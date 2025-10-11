package br.unip.ads.pim.meuhortifruti.service;

import br.unip.ads.pim.meuhortifruti.dto.CarrinhoItemDTO;
import br.unip.ads.pim.meuhortifruti.dto.CarrinhoResponseDTO;
import br.unip.ads.pim.meuhortifruti.entity.Carrinho;
import br.unip.ads.pim.meuhortifruti.entity.Cliente;
import br.unip.ads.pim.meuhortifruti.entity.Produto;
import br.unip.ads.pim.meuhortifruti.exception.EstoqueInsuficienteException;
import br.unip.ads.pim.meuhortifruti.exception.RecursoNaoEncontradoException;
import br.unip.ads.pim.meuhortifruti.exception.RegraNegocioException;
import br.unip.ads.pim.meuhortifruti.repository.CarrinhoRepository;
import br.unip.ads.pim.meuhortifruti.repository.ClienteRepository;
import br.unip.ads.pim.meuhortifruti.repository.ProdutoRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarrinhoService {

    private final CarrinhoRepository carrinhoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public CarrinhoResponseDTO buscarPorCliente(Integer idCliente) {
        Carrinho carrinho = carrinhoRepository.buscarPorIdCliente(idCliente)
            .orElseGet(() -> criarCarrinhoVazio(idCliente));
        return converterParaDTO(carrinho);
    }

    @Transactional
    public CarrinhoResponseDTO adicionarItem(Integer idCliente, CarrinhoItemDTO itemDTO) {
        Carrinho carrinho = obterOuCriarCarrinho(idCliente);
        Produto produto = buscarProduto(itemDTO.getIdProduto());

        validarEstoque(produto, itemDTO.getQuantidade());

        Map<Integer, Integer> itens = desserializarItens(carrinho);
        itens.put(itemDTO.getIdProduto(), itens.getOrDefault(itemDTO.getIdProduto(), 0) + itemDTO.getQuantidade());

        atualizarCarrinho(carrinho, itens);
        carrinho = carrinhoRepository.save(carrinho);
        return converterParaDTO(carrinho);
    }

    @Transactional
    public CarrinhoResponseDTO removerItem(Integer idCliente, Integer idProduto) {
        Carrinho carrinho = buscarCarrinhoPorCliente(idCliente);
        Map<Integer, Integer> itens = desserializarItens(carrinho);

        if (!itens.containsKey(idProduto)) {
            throw new RegraNegocioException("Produto não está no carrinho");
        }

        itens.remove(idProduto);
        atualizarCarrinho(carrinho, itens);
        carrinho = carrinhoRepository.save(carrinho);
        return converterParaDTO(carrinho);
    }

    @Transactional
    public CarrinhoResponseDTO atualizarQuantidade(Integer idCliente, CarrinhoItemDTO itemDTO) {
        Carrinho carrinho = buscarCarrinhoPorCliente(idCliente);
        Produto produto = buscarProduto(itemDTO.getIdProduto());

        validarEstoque(produto, itemDTO.getQuantidade());

        Map<Integer, Integer> itens = desserializarItens(carrinho);
        itens.put(itemDTO.getIdProduto(), itemDTO.getQuantidade());

        atualizarCarrinho(carrinho, itens);
        carrinho = carrinhoRepository.save(carrinho);
        return converterParaDTO(carrinho);
    }

    @Transactional
    public void limpar(Integer idCliente) {
        Carrinho carrinho = buscarCarrinhoPorCliente(idCliente);
        atualizarCarrinho(carrinho, new HashMap<>());
        carrinhoRepository.save(carrinho);
    }

    private Carrinho obterOuCriarCarrinho(Integer idCliente) {
        return carrinhoRepository.buscarPorIdCliente(idCliente)
            .orElseGet(() -> criarCarrinhoVazio(idCliente));
    }

    private Carrinho buscarCarrinhoPorCliente(Integer idCliente) {
        return carrinhoRepository.buscarPorIdCliente(idCliente)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Carrinho do cliente", "idCliente", idCliente));
    }

    private Carrinho criarCarrinhoVazio(Integer idCliente) {
        Cliente cliente = clienteRepository.findById(idCliente)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente", "id", idCliente));

        Carrinho carrinho = Carrinho.builder()
            .cliente(cliente)
            .listaProdutos("{}")
            .quantProdutos(0)
            .valorTotal(BigDecimal.ZERO)
            .build();

        return carrinhoRepository.save(carrinho);
    }

    private Produto buscarProduto(Integer idProduto) {
        return produtoRepository.findById(idProduto)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", "id", idProduto));
    }

    private void validarEstoque(Produto produto, Integer quantidadeSolicitada) {
        if (produto.getQuantidade() < quantidadeSolicitada) {
            throw new EstoqueInsuficienteException(
                produto.getNome(),
                produto.getQuantidade(),
                quantidadeSolicitada
            );
        }
    }

    private Map<Integer, Integer> desserializarItens(Carrinho carrinho) {
        try {
            String json = carrinho.getListaProdutos();
            if (json == null || json.isEmpty() || json.equals("{}")) {
                return new HashMap<>();
            }
            return objectMapper.readValue(json, new TypeReference<Map<Integer, Integer>>() {});
        } catch (JsonProcessingException e) {
            return new HashMap<>();
        }
    }

    private void atualizarCarrinho(Carrinho carrinho, Map<Integer, Integer> itens) {
        try {
            carrinho.setListaProdutos(objectMapper.writeValueAsString(itens));
            carrinho.setQuantProdutos(calcularQuantidadeTotal(itens));
            carrinho.setValorTotal(calcularValorTotal(itens));
        } catch (JsonProcessingException e) {
            throw new RegraNegocioException("Erro ao processar itens do carrinho");
        }
    }

    private Integer calcularQuantidadeTotal(Map<Integer, Integer> itens) {
        return itens.values().stream()
            .mapToInt(Integer::intValue)
            .sum();
    }

    private BigDecimal calcularValorTotal(Map<Integer, Integer> itens) {
        if (itens.isEmpty()) {
            return BigDecimal.ZERO;
        }

        List<Produto> produtos = produtoRepository.findAllById(itens.keySet());
        Map<Integer, Produto> produtosPorId = produtos.stream()
            .collect(Collectors.toMap(Produto::getIdProduto, p -> p));

        return itens.entrySet().stream()
            .map(entry -> {
                Produto produto = produtosPorId.get(entry.getKey());
                if (produto == null) return BigDecimal.ZERO;
                return produto.getPreco().multiply(BigDecimal.valueOf(entry.getValue()));
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private CarrinhoResponseDTO converterParaDTO(Carrinho carrinho) {
        Map<Integer, Integer> itens = desserializarItens(carrinho);
        List<CarrinhoResponseDTO.ProdutoCarrinhoDTO> produtos = converterProdutosParaDTO(itens);

        return CarrinhoResponseDTO.builder()
            .idCarrinho(carrinho.getIdCarrinho())
            .idCliente(carrinho.getCliente().getIdUsuario())
            .quantProdutos(carrinho.getQuantProdutos())
            .valorTotal(carrinho.getValorTotal())
            .produtos(produtos)
            .build();
    }

    private List<CarrinhoResponseDTO.ProdutoCarrinhoDTO> converterProdutosParaDTO(Map<Integer, Integer> itens) {
        if (itens.isEmpty()) {
            return List.of();
        }

        List<Produto> produtos = produtoRepository.findAllById(itens.keySet());

        return produtos.stream()
            .map(produto -> {
                Integer quantidade = itens.get(produto.getIdProduto());
                BigDecimal subtotal = produto.getPreco().multiply(BigDecimal.valueOf(quantidade));

                return CarrinhoResponseDTO.ProdutoCarrinhoDTO.builder()
                    .idProduto(produto.getIdProduto())
                    .nome(produto.getNome())
                    .preco(produto.getPreco())
                    .quantidade(quantidade)
                    .subtotal(subtotal)
                    .build();
            })
            .collect(Collectors.toList());
    }
}
