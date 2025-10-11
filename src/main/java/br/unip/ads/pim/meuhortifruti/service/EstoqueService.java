package br.unip.ads.pim.meuhortifruti.service;

import br.unip.ads.pim.meuhortifruti.dto.EstoqueRequestDTO;
import br.unip.ads.pim.meuhortifruti.dto.EstoqueResponseDTO;
import br.unip.ads.pim.meuhortifruti.entity.Estoque;
import br.unip.ads.pim.meuhortifruti.entity.Produto;
import br.unip.ads.pim.meuhortifruti.exception.RecursoNaoEncontradoException;
import br.unip.ads.pim.meuhortifruti.exception.RegraNegocioException;
import br.unip.ads.pim.meuhortifruti.repository.EstoqueRepository;
import br.unip.ads.pim.meuhortifruti.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;
    private final ProdutoRepository produtoRepository;

    @Transactional(readOnly = true)
    public List<EstoqueResponseDTO> listarTodos() {
        return estoqueRepository.findAll()
            .stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EstoqueResponseDTO buscarPorId(Integer id) {
        Estoque estoque = buscarEstoquePorId(id);
        return converterParaDTO(estoque);
    }

    @Transactional(readOnly = true)
    public EstoqueResponseDTO buscarPorProduto(Integer idProduto) {
        Estoque estoque = estoqueRepository.buscarPorIdProduto(idProduto)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Estoque do produto", "idProduto", idProduto));
        return converterParaDTO(estoque);
    }

    @Transactional
    public EstoqueResponseDTO criar(EstoqueRequestDTO dto) {
        Produto produto = produtoRepository.findById(dto.getIdProduto())
            .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", "id", dto.getIdProduto()));

        if (estoqueRepository.buscarPorIdProduto(dto.getIdProduto()).isPresent()) {
            throw new RegraNegocioException("Já existe um registro de estoque para este produto");
        }

        Estoque estoque = Estoque.builder()
            .produto(produto)
            .quantProdutos(dto.getQuantProdutos())
            .build();

        estoque = estoqueRepository.save(estoque);
        return converterParaDTO(estoque);
    }

    @Transactional
    public EstoqueResponseDTO atualizar(Integer id, EstoqueRequestDTO dto) {
        Estoque estoque = buscarEstoquePorId(id);

        estoque.setQuantProdutos(dto.getQuantProdutos());
        estoque = estoqueRepository.save(estoque);
        return converterParaDTO(estoque);
    }

    @Transactional
    public EstoqueResponseDTO adicionarQuantidade(Integer idProduto, Integer quantidade) {
        Estoque estoque = estoqueRepository.buscarPorIdProduto(idProduto)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Estoque do produto", "idProduto", idProduto));

        estoque.setQuantProdutos(estoque.getQuantProdutos() + quantidade);
        estoque = estoqueRepository.save(estoque);
        return converterParaDTO(estoque);
    }

    @Transactional
    public EstoqueResponseDTO removerQuantidade(Integer idProduto, Integer quantidade) {
        Estoque estoque = estoqueRepository.buscarPorIdProduto(idProduto)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Estoque do produto", "idProduto", idProduto));

        int novaQuantidade = estoque.getQuantProdutos() - quantidade;
        if (novaQuantidade < 0) {
            throw new RegraNegocioException("Quantidade insuficiente em estoque");
        }

        estoque.setQuantProdutos(novaQuantidade);
        estoque = estoqueRepository.save(estoque);
        return converterParaDTO(estoque);
    }

    @Transactional
    public void excluir(Integer id) {
        Estoque estoque = buscarEstoquePorId(id);
        estoqueRepository.delete(estoque);
    }

    private Estoque buscarEstoquePorId(Integer id) {
        return estoqueRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Estoque", "id", id));
    }

    private EstoqueResponseDTO converterParaDTO(Estoque estoque) {
        return EstoqueResponseDTO.builder()
            .idEstoque(estoque.getIdEstoque())
            .idProduto(estoque.getProduto().getIdProduto())
            .nomeProduto(estoque.getProduto().getNome())
            .quantProdutos(estoque.getQuantProdutos())
            .build();
    }
}
