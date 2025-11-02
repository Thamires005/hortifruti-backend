package br.unip.ads.pim.meuhortifruti.service;

import br.unip.ads.pim.meuhortifruti.dto.PerfilResponseDTO;
import br.unip.ads.pim.meuhortifruti.dto.PerfilRequestDTO;
import br.unip.ads.pim.meuhortifruti.entity.Perfil;
import br.unip.ads.pim.meuhortifruti.exception.RecursoDuplicadoException;
import br.unip.ads.pim.meuhortifruti.exception.RecursoNaoEncontradoException;
import br.unip.ads.pim.meuhortifruti.repository.PerfilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor

public class PerfilService {

    private final PerfilRepository perfilRepository;

    @Transactional(readOnly = true)
    public PerfilResponseDTO buscarPorId(Integer id){
        Perfil perfil = buscarPerfilPorId(id);
        return converterParaDTO(perfil);
    }

    @Transactional
    public PerfilResponseDTO criar(PerfilRequestDTO dto){
        if(perfilRepository.existsByNome(dto.getNome())){
            throw new RecursoDuplicadoException("Perfil", "nome", dto.getNome());
        }

        Perfil perfil = Perfil.builder()
                .nome(dto.getNome())
                .descricao(dto.getDescricao())
                .build();
        perfil = perfilRepository.save(perfil);
        return converterParaDTO(perfil);
    }

    @Transactional
    public PerfilResponseDTO atualizar(Integer id, PerfilRequestDTO dto){
        Perfil perfil = buscarPerfilPorId(id);

        perfilRepository.findByNome(dto.getNome()).ifPresent(perfilExistente -> {
            if(!perfilExistente.getIdPerfil().equals(id)){
                throw new RecursoDuplicadoException("Perfil", "nome", dto.getNome());
            }
        });
        perfil.setDescricao(dto.getDescricao());
        perfil.setNome(dto.getNome());
        perfil = perfilRepository.save(perfil);
        return converterParaDTO(perfil);
    }

    @Transactional
    public void excluir(Integer id){
        Perfil perfil = buscarPerfilPorId(id);
        perfilRepository.delete(perfil);
    }

    private Perfil buscarPerfilPorId(Integer id) {
        return perfilRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Perfil", "id", id));
    }

    private PerfilResponseDTO converterParaDTO(Perfil perfil) {
        return PerfilResponseDTO.builder()
                .idPerfil(perfil.getIdPerfil())
                .nome(perfil.getNome())
                .descricao(perfil.getDescricao())
                .build();
    }
}

