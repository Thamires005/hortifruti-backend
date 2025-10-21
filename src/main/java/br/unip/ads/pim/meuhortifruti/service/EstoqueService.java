package br.unip.ads.pim.meuhortifruti.service;

import br.unip.ads.pim.meuhortifruti.dto.EstoqueResponseDTO;
import br.unip.ads.pim.meuhortifruti.dto.EstoqueResquestDTO;
import br.unip.ads.pim.meuhortifruti.entity.Estoque;
import br.unip.ads.pim.meuhortifruti.exception.RecursoDuplicadoException;
import br.unip.ads.pim.meuhortifruti.exception.RecursoNaoEncontradoException;
import br.unip.ads.pim.meuhortifruti.repository.EstoqueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;

    public EstoqueService(EstoqueRepository estoqueRepository) {
        this.estoqueRepository = estoqueRepository;
    }

    @Transactional(readOnly = true)
    public List<EstoqueResponseDTO> listarTodas() {
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

    @Transactional
    public EstoqueResponseDTO criar(EstoqueResquestDTO dto) {
        if (estoqueRepository.existsByNome(dto.getNome())) {
            throw new RecursoDuplicadoException("Estoque", "nome", dto.getNome());
        }

        Estoque estoque = Estoque.builder()
                .nome(dto.getNome())
                .build();

        estoque = estoqueRepository.save(estoque);
        return converterParaDTO(estoque);
    }

    @Transactional
    public EstoqueResponseDTO atualizar(Integer id, EstoqueResquestDTO dto) {
        Estoque estoque = buscarEstoquePorId(id);

        estoqueRepository.findByNome(dto.getNome()).ifPresent(estoqueExistente -> {
            if (!estoqueExistente.getIdEstoque().equals(id)) {
                throw new RecursoDuplicadoException("Estoque", "nome", dto.getNome());
            }
        });

        estoque.setNome(dto.getNome());
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
                .nome(estoque.getNome())
                .build();
    }

}
