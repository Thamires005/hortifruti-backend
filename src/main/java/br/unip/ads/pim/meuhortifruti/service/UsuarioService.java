package br.unip.ads.pim.meuhortifruti.service;

import br.unip.ads.pim.meuhortifruti.dto.UsuarioRequestDTO;
import br.unip.ads.pim.meuhortifruti.dto.UsuarioResponseDTO;
import br.unip.ads.pim.meuhortifruti.entity.Usuario;
import br.unip.ads.pim.meuhortifruti.repository.UsuarioRepository;
import br.unip.ads.pim.meuhortifruti.exception.RecursoDuplicadoException;
import br.unip.ads.pim.meuhortifruti.exception.RecursoNaoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Integer id){
        Usuario usuario = buscarUsuarioPorId(id);
        return converterParaDTO(usuario);
    }

    @Transactional
    public UsuarioResponseDTO criar(UsuarioRequestDTO dto){
        if(usuarioRepository.existsByNome(dto.getNome())){
            throw new RecursoDuplicadoException("Usuário", "nome", dto.getNome());
        }
        Usuario usuario = Usuario.builder()
                .nome(dto.getNome())
                .senha(dto.getSenha())
                .build();

        usuario = usuarioRepository.save(usuario);
        return converterParaDTO(usuario);
    }

    @Transactional
    public UsuarioResponseDTO atualizar(Integer id, UsuarioRequestDTO dto){
        Usuario usuario = buscarUsuarioPorId(id);

        usuarioRepository.findByNome(dto.getNome()).ifPresent(usuarioExistente ->{
            if(!usuarioExistente.getIdUsuario().equals(id)){
                throw new RecursoDuplicadoException("Usuário", "id", id);
            }
        });
        usuario.setNome(dto.getNome());
        usuario.setSenha(dto.getSenha());
        usuario = usuarioRepository.save(usuario);
        return converterParaDTO(usuario);
    }

    @Transactional
    public void excluir (Integer id){
        Usuario usuario = buscarUsuarioPorId(id);
        usuarioRepository.delete(usuario);
    }

    private Usuario buscarUsuarioPorId(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário", "id", id));
    }

    private UsuarioResponseDTO converterParaDTO(Usuario usuario) {
        return UsuarioResponseDTO.builder()
                .idUsuario(usuario.getIdUsuario())
                .nome(usuario.getNome())
                .build();
    }
}
