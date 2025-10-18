package br.unip.ads.pim.meuhortifruti.service;

import br.unip.ads.pim.meuhortifruti.dto.ProdutoRequestDTO;
import br.unip.ads.pim.meuhortifruti.dto.ProdutoResponseDTO;
import br.unip.ads.pim.meuhortifruti.entity.Produto;
import br.unip.ads.pim.meuhortifruti.exception.RecursoDuplicadoException;
import br.unip.ads.pim.meuhortifruti.exception.RecursoNaoEncontradoException;
import br.unip.ads.pim.meuhortifruti.repository.ProdutoRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class ProdutoService {
    private final ProdutoRepository produtoRepository;

    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> listarTodas() {
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
    @Transactional
    public ProdutoResponseDTO criar(@Valid ProdutoRequestDTO dto) {
        if(produtoRepository.existsByNome(dto.getNome())){
            throw new RecursoDuplicadoException("Produto ", "nome ", dto.getNome());
        }
        Produto produto = produto.builder()
                .nome(dto.getNome())
                .build();

        produto = produtoRepository.save(produto);
        return converterParaDTO(produto);
    }

    @Transactional
    public ProdutoResponseDTO atualizar(Integer id, ProdutoRequestDTO dto) {
        Produto produto = buscarProdutoPorId(id);

        produtoRepository.findByNome(dto.getNome()).ifPresent(produtoExistente -> {
            if (!produtoExistente.getIdProduto().equals(id)) {
                throw new RecursoDuplicadoException("Produto ", "nome ", dto.getNome());
            }
        });

        produto.setNome(dto.getNome());
        produto = produtoRepository.save(produto);
        return converterParaDTO(produto);
    }
    @Transactional
    public void excluir(Integer id){
        Produto produto = buscarProdutoPorId(id);
        produtoRepository.delete(produto);
    }
    private Produto buscarProdutoPorId(Integer id){
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto ", "id ", id));
    }

    private ProdutoResponseDTO converterParaDTO(Produto produto){
        return ProdutoResponseDTO.builder()
                .idProduto(produto.getIdProduto())
                .nome(produto.getNome())
                .build();
    }
}
