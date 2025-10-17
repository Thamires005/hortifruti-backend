package br.unip.ads.pim.meuhortifruti.service;

import br.unip.ads.pim.meuhortifruti.dto.UsuarioRequestDTO;
import br.unip.ads.pim.meuhortifruti.dto.UsuarioResponseDTO;
import br.unip.ads.pim.meuhortifruti.entity.Usuario;
import br.unip.ads.pim.meuhortifruti.exception.RecursoDuplicadoException;
import br.unip.ads.pim.meuhortifruti.exception.RecursoNaoEncontradoException;
import br.unip.ads.pim.meuhortifruti.repository.UsuarioRepository;
import org.springframework.stereotype.Usuario;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstrutor

public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos(){
        return usuarioRepository.findAll()
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId (Integer id){
        Usuario usuario = buscarUsuarioPorId(id);
        return converterParaDTO(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO criar(UsuarioRequestDTO dto){
        if (UsuarioRepository.existsByNome(dto.getNome())){
            throw new RecursoDuplicadoException("Usuário ", "nome ", dto.getNome());
        }
        Usuario usuario = Usuario.builder()
                .nome(dto.getNome())
                .build();

        usuario = usuarioRepository.save(usuario);
        return converterParaDTO(usuario);
    }
    @Transactional
    public UsuarioResponseDTO atualizar(Integer id, UsuarioRequestDTO dto ){
        Usuario usuario = buscarUsuarioPorId(id);

        usuarioRepository.findByNome(dto.getNome()).ifPresent( Usuario usuarioExistente -> {
                if (!usuarioExistente.getIdUsuario().equals(id)){
                throw new RecursoDuplicadoException("Usuário ", "nome ", dto.getNome());
                }
        });

        usuario.setNome(dto.getNome());
        usuario = usuarioRepository.save(usuario);
        return converterParaDTO(usuario);
    }

    @Transactional
    public void excluir(Integer id){
        Usuario usuario = buscarUsuarioPorId(id);
        usuarioRepository.delete(usuario);
    }

    private Usuario buscarUsuarioPorId(Integer id){
        return usuarioRepository.findId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário ", "id ", id));
    }
    private UsuarioResponseDTO converterParaDTO (Usuario usuario){
        return UsuarioResponseDTO.builder()
                .IdUsuario(usuario.getIdUsuario())
                .nome(Usuario.getNome())
                .builder();
    }
}
