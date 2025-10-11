package br.unip.ads.pim.meuhortifruti.service;

import br.unip.ads.pim.meuhortifruti.dto.PedidoRequestDTO;
import br.unip.ads.pim.meuhortifruti.dto.PedidoResponseDTO;
import br.unip.ads.pim.meuhortifruti.entity.*;
import br.unip.ads.pim.meuhortifruti.exception.EstoqueInsuficienteException;
import br.unip.ads.pim.meuhortifruti.exception.RecursoNaoEncontradoException;
import br.unip.ads.pim.meuhortifruti.exception.RegraNegocioException;
import br.unip.ads.pim.meuhortifruti.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;

    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> listarTodos() {
        return pedidoRepository.findAll()
            .stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PedidoResponseDTO buscarPorId(Integer id) {
        Pedido pedido = buscarPedidoPorId(id);
        return converterParaDTO(pedido);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> buscarPorCliente(Integer idCliente) {
        return pedidoRepository.buscarPorIdCliente(idCliente)
            .stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> buscarPorStatus(String status) {
        return pedidoRepository.findByStatusPedido(status)
            .stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }

    @Transactional
    public PedidoResponseDTO criar(PedidoRequestDTO dto) {
        Cliente cliente = buscarCliente(dto.getIdCliente());
        Map<Integer, Produto> produtosPorId = buscarEValidarProdutos(dto.getItens());

        Pedido pedido = criarPedidoBase(cliente);
        adicionarItensPedido(pedido, dto.getItens(), produtosPorId);
        atualizarEstoque(dto.getItens(), produtosPorId);

        BigDecimal valorTotal = calcularValorTotal(dto.getItens(), produtosPorId);
        adicionarPagamento(pedido, valorTotal, dto.getMetodoPagamento());
        adicionarEntrega(pedido, dto.getEnderecoEntrega());

        pedido = pedidoRepository.save(pedido);
        return converterParaDTO(pedido);
    }

    @Transactional
    public PedidoResponseDTO atualizarStatus(Integer id, String novoStatus) {
        Pedido pedido = buscarPedidoPorId(id);
        pedido.setStatusPedido(novoStatus);
        pedido = pedidoRepository.save(pedido);
        return converterParaDTO(pedido);
    }

    @Transactional
    public PedidoResponseDTO atualizarStatusPagamento(Integer id, String novoStatus) {
        Pedido pedido = buscarPedidoPorId(id);
        pedido.getPagamento().setStatusPagamento(novoStatus);
        pedido = pedidoRepository.save(pedido);
        return converterParaDTO(pedido);
    }

    @Transactional
    public PedidoResponseDTO atualizarStatusEntrega(Integer id, String novoStatus) {
        Pedido pedido = buscarPedidoPorId(id);
        pedido.getEntrega().setStatusEntrega(novoStatus);
        pedido = pedidoRepository.save(pedido);
        return converterParaDTO(pedido);
    }

    @Transactional
    public void cancelar(Integer id) {
        Pedido pedido = buscarPedidoPorId(id);
        validarPedidoPendente(pedido);
        devolverEstoque(pedido);
        atualizarStatusCancelamento(pedido);
        pedidoRepository.save(pedido);
    }

    private Cliente buscarCliente(Integer idCliente) {
        return clienteRepository.findById(idCliente)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente", "id", idCliente));
    }

    private Map<Integer, Produto> buscarEValidarProdutos(List<PedidoRequestDTO.ItemPedidoDTO> itens) {
        List<Integer> idsProdutos = itens.stream()
            .map(PedidoRequestDTO.ItemPedidoDTO::getIdProduto)
            .collect(Collectors.toList());

        List<Produto> produtos = produtoRepository.findAllById(idsProdutos);

        if (produtos.size() != idsProdutos.size()) {
            throw new RecursoNaoEncontradoException("Um ou mais produtos não foram encontrados");
        }

        Map<Integer, Produto> produtosPorId = produtos.stream()
            .collect(Collectors.toMap(Produto::getIdProduto, p -> p));

        validarEstoque(itens, produtosPorId);

        return produtosPorId;
    }

    private void validarEstoque(List<PedidoRequestDTO.ItemPedidoDTO> itens, Map<Integer, Produto> produtosPorId) {
        for (PedidoRequestDTO.ItemPedidoDTO item : itens) {
            Produto produto = produtosPorId.get(item.getIdProduto());
            if (produto.getQuantidade() < item.getQuantidade()) {
                throw new EstoqueInsuficienteException(
                    produto.getNome(),
                    produto.getQuantidade(),
                    item.getQuantidade()
                );
            }
        }
    }

    private Pedido criarPedidoBase(Cliente cliente) {
        Pedido pedido = Pedido.builder()
            .cliente(cliente)
            .statusPedido("PENDENTE")
            .build();
        return pedidoRepository.save(pedido);
    }

    private void adicionarItensPedido(Pedido pedido, List<PedidoRequestDTO.ItemPedidoDTO> itens, 
                                      Map<Integer, Produto> produtosPorId) {
        for (PedidoRequestDTO.ItemPedidoDTO itemDTO : itens) {
            Produto produto = produtosPorId.get(itemDTO.getIdProduto());

            ItemPedido itemPedido = ItemPedido.builder()
                .pedido(pedido)
                .produto(produto)
                .preco(produto.getPreco())
                .quantidade(itemDTO.getQuantidade())
                .build();

            pedido.getItens().add(itemPedido);
        }
    }

    private void atualizarEstoque(List<PedidoRequestDTO.ItemPedidoDTO> itens, Map<Integer, Produto> produtosPorId) {
        for (PedidoRequestDTO.ItemPedidoDTO item : itens) {
            Produto produto = produtosPorId.get(item.getIdProduto());
            produto.setQuantidade(produto.getQuantidade() - item.getQuantidade());
        }
        produtoRepository.saveAll(produtosPorId.values());
    }

    private BigDecimal calcularValorTotal(List<PedidoRequestDTO.ItemPedidoDTO> itens, 
                                          Map<Integer, Produto> produtosPorId) {
        return itens.stream()
            .map(item -> {
                Produto produto = produtosPorId.get(item.getIdProduto());
                return produto.getPreco().multiply(BigDecimal.valueOf(item.getQuantidade()));
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void adicionarPagamento(Pedido pedido, BigDecimal valorTotal, String metodoPagamento) {
        Pagamento pagamento = Pagamento.builder()
            .pedido(pedido)
            .valor(valorTotal)
            .metodoPagamento(metodoPagamento)
            .statusPagamento("PENDENTE")
            .build();
        pedido.setPagamento(pagamento);
    }

    private void adicionarEntrega(Pedido pedido, String endereco) {
        Entrega entrega = Entrega.builder()
            .pedido(pedido)
            .endereco(endereco)
            .statusEntrega("PENDENTE")
            .build();
        pedido.setEntrega(entrega);
    }

    private void validarPedidoPendente(Pedido pedido) {
        if (!"PENDENTE".equals(pedido.getStatusPedido())) {
            throw new RegraNegocioException("Apenas pedidos pendentes podem ser cancelados");
        }
    }

    private void devolverEstoque(Pedido pedido) {
        for (ItemPedido item : pedido.getItens()) {
            Produto produto = item.getProduto();
            produto.setQuantidade(produto.getQuantidade() + item.getQuantidade());
            produtoRepository.save(produto);
        }
    }

    private void atualizarStatusCancelamento(Pedido pedido) {
        pedido.setStatusPedido("CANCELADO");
        pedido.getPagamento().setStatusPagamento("CANCELADO");
        pedido.getEntrega().setStatusEntrega("CANCELADO");
    }

    private Pedido buscarPedidoPorId(Integer id) {
        return pedidoRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido", "id", id));
    }

    private PedidoResponseDTO converterParaDTO(Pedido pedido) {
        return PedidoResponseDTO.builder()
            .idPedido(pedido.getIdPedido())
            .idCliente(pedido.getCliente().getIdUsuario())
            .nomeCliente(pedido.getCliente().getNome())
            .statusPedido(pedido.getStatusPedido())
            .valorTotal(calcularValorTotalPedido(pedido))
            .itens(converterItensParaDTO(pedido.getItens()))
            .pagamento(converterPagamentoParaDTO(pedido.getPagamento()))
            .entrega(converterEntregaParaDTO(pedido.getEntrega()))
            .build();
    }

    private BigDecimal calcularValorTotalPedido(Pedido pedido) {
        return pedido.getItens().stream()
            .map(item -> item.getPreco().multiply(BigDecimal.valueOf(item.getQuantidade())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<PedidoResponseDTO.ItemPedidoDTO> converterItensParaDTO(List<ItemPedido> itens) {
        return itens.stream()
            .map(item -> {
                BigDecimal subtotal = item.getPreco().multiply(BigDecimal.valueOf(item.getQuantidade()));
                return PedidoResponseDTO.ItemPedidoDTO.builder()
                    .idItemPedido(item.getIdItemPedido())
                    .idProduto(item.getProduto().getIdProduto())
                    .nomeProduto(item.getProduto().getNome())
                    .preco(item.getPreco())
                    .quantidade(item.getQuantidade())
                    .subtotal(subtotal)
                    .build();
            })
            .collect(Collectors.toList());
    }

    private PedidoResponseDTO.PagamentoDTO converterPagamentoParaDTO(Pagamento pagamento) {
        if (pagamento == null) {
            return null;
        }

        return PedidoResponseDTO.PagamentoDTO.builder()
            .idPagamento(pagamento.getIdPagamento())
            .valor(pagamento.getValor())
            .metodoPagamento(pagamento.getMetodoPagamento())
            .statusPagamento(pagamento.getStatusPagamento())
            .build();
    }

    private PedidoResponseDTO.EntregaDTO converterEntregaParaDTO(Entrega entrega) {
        if (entrega == null) {
            return null;
        }

        return PedidoResponseDTO.EntregaDTO.builder()
            .idEntrega(entrega.getIdEntrega())
            .endereco(entrega.getEndereco())
            .statusEntrega(entrega.getStatusEntrega())
            .dtEntrega(entrega.getDtEntrega())
            .build();
    }
}
