package br.unip.ads.pim.meuhortifruti.service;

import br.unip.ads.pim.meuhortifruti.entity.Estoque;
import br.unip.ads.pim.meuhortifruti.dto.EstoqueRequestDTO;
import br.unip.ads.pim.meuhortifruti.dto.EstoqueResponseDTO;
import br.unip.ads.pim.meuhortifruti.exception.RecursoNaoEncontradoException;
import br.unip.ads.pim.meuhortifruti.repository.EstoqueRepository;
import br.unip.ads.pim.meuhortifruti.exception.RecursoDuplicadoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;

    @Transactional
    public EstoqueResponseDTO buscarPorId(Integer id){
        Estoque estoque = buscarEstoquePorId(id);
        return converterParaDTO(estoque);
    }
    @Transactional
    public EstoqueResponseDTO criar(EstoqueRequestDTO dto){
        if (estoqueRepository.existsById(dto.getIdProduto())){
            throw new RecursoDuplicadoException("Estoque", "id", dto.getIdProduto());
        }
        Estoque estoque = Estoque.builder()
                .quantidadeProdutos(dto.getQuantidadeProdutos())
                .build();
        estoque = estoqueRepository.save(estoque);
        return converterParaDTO(estoque);
    }
    @Transactional
    public EstoqueResponseDTO atualizar(Integer id, EstoqueRequestDTO dto){
        Estoque estoque = buscarEstoquePorId(id);
        estoqueRepository.findById(dto.getIdProduto()).ifPresent(estoqueExistente -> {
            if (!estoqueExistente.getIdEstoque().equals(id)){
                throw new RecursoDuplicadoException("Estoque", "id", dto.getIdProduto());
            }
        });
        estoque.setQuantidadeProdutos(dto.getQuantidadeProdutos());
        estoque = estoqueRepository.save(estoque);
        return converterParaDTO(estoque);
    }
    @Transactional
    public void excluir(Integer id){
        Estoque estoque = buscarEstoquePorId(id);
        estoqueRepository.delete(estoque);
    }
    private Estoque buscarEstoquePorId(Integer id){
        return estoqueRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Estoque","id", id ));
    }
    private EstoqueResponseDTO converterParaDTO (Estoque estoque){
        return EstoqueResponseDTO.builder()
                .quantidadeProdutos(estoque.getQuantidadeProdutos())
                .build();
    }

}
