package br.unip.ads.pim.meuhortifruti.service;

import br.unip.ads.pim.meuhortifruti.dto.ForneceRequestDTO;
import br.unip.ads.pim.meuhortifruti.dto.ForneceResponseDTO;
import br.unip.ads.pim.meuhortifruti.entity.Fornece;
import br.unip.ads.pim.meuhortifruti.repository.ForneceRepository;
import br.unip.ads.pim.meuhortifruti.exception.RecursoDuplicadoException;
import br.unip.ads.pim.meuhortifruti.exception.RecursoNaoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ForneceService {

    private final ForneceRepository forneceRepository;

    @Transactional(readOnly = true)
    public List<ForneceResponseDTO> listarTodas(){
        return ForneceRepository.findAll()
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public ForneceResponseDTO buscarPorId(Integer id) {
        Fornece fornece = buscarFornecePorId(id);
        return converterParaDTO(Fornece);
    }

    @Transactional
    public ForneceResponseDTO criar(ForneceRequestDTO dto) {
        if (ForneceRepository.existsByNome(dto.getNome())) {
            throw new RecursoDuplicadoException("Fornece", "nome", dto.getNome());
        }

        Fornece fornece = Fornece.builder()
                .nome(dto.getNome())
                .build();

        Fornece = forneceRepository.save(fornece);
        return converterParaDTO(fornece);
    }

    @Transactional
    public ForneceResponseDTO atualizar(Integer id, ForneceRequestDTO dto) {
        Fornece fornece = buscarFornecePorId(id);

        forneceRepository.findByNome(dto.getNome()).ifPresent(forneceExistente -> {
            if (!forneceExistente.getIdFornece().equals(id)) {
                throw new RecursoDuplicadoException("Fornece", "nome", dto.getNome());
            }
        });

        fornece.setNome(dto.getNome());
        fornece = forneceRepository.save(fornece);
        return converterParaDTO(fornece);
    }

    @Transactional
    public void excluir(Integer id) {
        Fornece fornece = buscarFornecePorId(id);
        forneceRepository.delete(fornece);
    }

    private Fornece buscarCategoriaPorId(Integer id) {
        return forneceRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("fornece", "id", id));
    }

    private ForneceResponseDTO converterParaDTO(Fornece fornece) {
        return ForneceResponseDTO.builder()
                .idFornece(fornece.getIdFornece())
                .nome(fornece.getNome())
                .build();
    }
}
