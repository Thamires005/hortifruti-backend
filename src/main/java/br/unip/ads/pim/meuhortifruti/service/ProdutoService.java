package br.unip.ads.pim.meuhortifruti.service;

import br.unip.ads.pim.meuhortifruti.dto.ProdutoRequestDTO;
import br.unip.ads.pim.meuhortifruti.dto.ProdutoResponseDTO;
import br.unip.ads.pim.meuhortifruti.entity.Categoria;
import br.unip.ads.pim.meuhortifruti.entity.Produto;
import br.unip.ads.pim.meuhortifruti.exception.RecursoNaoEncontradoException;
import br.unip.ads.pim.meuhortifruti.repository.CategoriaRepository;
import br.unip.ads.pim.meuhortifruti.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> listarTodos() {
        return produtoRepository.findAll()
            .stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProdutoResponseDTO buscarPorId(Integer id) {
        Produto produto = buscarProdutoPorId(id);
        return converterParaDTO(produto);
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> buscarPorNome(String nome) {
        return produtoRepository.findByNomeContainingIgnoreCase(nome)
            .stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> buscarPorCategoria(Integer idCategoria) {
        Categoria categoria = categoriaRepository.findById(idCategoria)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria", "id", idCategoria));
        
        return produtoRepository.findByCategoria(categoria)
            .stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> buscarProdutosComEstoqueBaixo(Integer quantidadeMinima) {
        return produtoRepository.buscarProdutosComEstoqueBaixo(quantidadeMinima)
            .stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> buscarProdutosProximosDoVencimento(LocalDate data) {
        return produtoRepository.buscarProdutosProximosDoVencimento(data)
            .stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }

    @Transactional
    public ProdutoResponseDTO criar(ProdutoRequestDTO dto) {
        Categoria categoria = categoriaRepository.findById(dto.getIdCategoria())
            .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria", "id", dto.getIdCategoria()));

        Produto produto = Produto.builder()
            .nome(dto.getNome())
            .preco(dto.getPreco())
            .quantidade(dto.getQuantidade())
            .dtValidade(dto.getDtValidade())
            .categoria(categoria)
            .build();

        produto = produtoRepository.save(produto);
        return converterParaDTO(produto);
    }

    @Transactional
    public ProdutoResponseDTO atualizar(Integer id, ProdutoRequestDTO dto) {
        Produto produto = buscarProdutoPorId(id);
        
        Categoria categoria = categoriaRepository.findById(dto.getIdCategoria())
            .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria", "id", dto.getIdCategoria()));

        produto.setNome(dto.getNome());
        produto.setPreco(dto.getPreco());
        produto.setQuantidade(dto.getQuantidade());
        produto.setDtValidade(dto.getDtValidade());
        produto.setCategoria(categoria);

        produto = produtoRepository.save(produto);
        return converterParaDTO(produto);
    }

    @Transactional
    public void excluir(Integer id) {
        Produto produto = buscarProdutoPorId(id);
        produtoRepository.delete(produto);
    }

    private Produto buscarProdutoPorId(Integer id) {
        return produtoRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", "id", id));
    }

    private ProdutoResponseDTO converterParaDTO(Produto produto) {
        Categoria categoria = produto.getCategoria();
        
        return ProdutoResponseDTO.builder()
            .idProduto(produto.getIdProduto())
            .nome(produto.getNome())
            .preco(produto.getPreco())
            .quantidade(produto.getQuantidade())
            .dtValidade(produto.getDtValidade())
            .idCategoria(categoria.getIdCategoria())
            .nomeCategoria(categoria.getNome())
            .build();
    }
}
