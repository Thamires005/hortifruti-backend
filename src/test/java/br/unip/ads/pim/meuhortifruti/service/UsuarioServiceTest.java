package br.unip.ads.pim.meuhortifruti.service;

import br.unip.ads.pim.meuhortifruti.dto.UsuarioRequestDTO;
import br.unip.ads.pim.meuhortifruti.dto.UsuarioResponseDTO;
import br.unip.ads.pim.meuhortifruti.entity.Usuario;
import br.unip.ads.pim.meuhortifruti.exception.RecursoDuplicadoException;
import br.unip.ads.pim.meuhortifruti.exception.RecursoNaoEncontradoException;
import br.unip.ads.pim.meuhortifruti.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do UsuarioService")
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;
    private UsuarioRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .idUsuario(1)
                .nome("João Silva")
                .senha("senha123")
                .build();

        requestDTO = UsuarioRequestDTO.builder()
                .nome("João Silva")
                .senha("senha123")
                .build();
    }

    // ========== TESTES DE BUSCA POR ID ==========

    @Test
    @DisplayName("Deve buscar usuário por ID com sucesso")
    void deveBuscarUsuarioPorId() {
        // Arrange
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));

        // Act
        UsuarioResponseDTO resultado = usuarioService.buscarPorId(1);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdUsuario()).isEqualTo(1);
        assertThat(resultado.getNome()).isEqualTo("João Silva");

        verify(usuarioRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar usuário inexistente")
    void deveLancarExcecaoAoBuscarUsuarioInexistente() {
        // Arrange
        when(usuarioRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.buscarPorId(999))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Usuário")
                .hasMessageContaining("id")
                .hasMessageContaining("999");

        verify(usuarioRepository, times(1)).findById(999);
    }

    // ========== TESTES DE CRIAÇÃO ==========

    @Test
    @DisplayName("Deve criar usuário com sucesso")
    void deveCriarUsuarioComSucesso() {
        // Arrange
        when(usuarioRepository.existsByNome("João Silva")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        UsuarioResponseDTO resultado = usuarioService.criar(requestDTO);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdUsuario()).isEqualTo(1);
        assertThat(resultado.getNome()).isEqualTo("João Silva");

        verify(usuarioRepository, times(1)).existsByNome("João Silva");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar usuário com nome duplicado")
    void deveLancarExcecaoAoCriarUsuarioComNomeDuplicado() {
        // Arrange
        when(usuarioRepository.existsByNome("João Silva")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.criar(requestDTO))
                .isInstanceOf(RecursoDuplicadoException.class)
                .hasMessageContaining("Usuário")
                .hasMessageContaining("nome")
                .hasMessageContaining("João Silva");

        verify(usuarioRepository, times(1)).existsByNome("João Silva");
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve criar usuário com senha longa")
    void deveCriarUsuarioComSenhaLonga() {
        // Arrange
        UsuarioRequestDTO requestSenhaLonga = UsuarioRequestDTO.builder()
                .nome("Maria Santos")
                .senha("senhamuito123segura456")
                .build();

        Usuario usuarioSenhaLonga = Usuario.builder()
                .idUsuario(2)
                .nome("Maria Santos")
                .senha("senhamuito123segura456")
                .build();

        when(usuarioRepository.existsByNome("Maria Santos")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioSenhaLonga);

        // Act
        UsuarioResponseDTO resultado = usuarioService.criar(requestSenhaLonga);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("Maria Santos");

        verify(usuarioRepository, times(1)).existsByNome("Maria Santos");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    // ========== TESTES DE ATUALIZAÇÃO ==========

    @Test
    @DisplayName("Deve atualizar usuário com sucesso")
    void deveAtualizarUsuarioComSucesso() {
        // Arrange
        UsuarioRequestDTO requestAtualizado = UsuarioRequestDTO.builder()
                .nome("João Silva Atualizado")
                .senha("novaSenha123")
                .build();

        Usuario usuarioAtualizado = Usuario.builder()
                .idUsuario(1)
                .nome("João Silva Atualizado")
                .senha("novaSenha123")
                .build();

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByNome("João Silva Atualizado")).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioAtualizado);

        // Act
        UsuarioResponseDTO resultado = usuarioService.atualizar(1, requestAtualizado);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdUsuario()).isEqualTo(1);
        assertThat(resultado.getNome()).isEqualTo("João Silva Atualizado");

        verify(usuarioRepository, times(1)).findById(1);
        verify(usuarioRepository, times(1)).findByNome("João Silva Atualizado");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar usuário inexistente")
    void deveLancarExcecaoAoAtualizarUsuarioInexistente() {
        // Arrange
        when(usuarioRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.atualizar(999, requestDTO))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Usuário")
                .hasMessageContaining("id")
                .hasMessageContaining("999");

        verify(usuarioRepository, times(1)).findById(999);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar com nome já utilizado por outro usuário")
    void deveLancarExcecaoAoAtualizarComNomeDuplicado() {
        // Arrange
        Usuario outroUsuario = Usuario.builder()
                .idUsuario(2)
                .nome("Maria Santos")
                .senha("senha456")
                .build();

        UsuarioRequestDTO requestComOutroNome = UsuarioRequestDTO.builder()
                .nome("Maria Santos")
                .senha("senha123")
                .build();

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByNome("Maria Santos")).thenReturn(Optional.of(outroUsuario));

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.atualizar(1, requestComOutroNome))
                .isInstanceOf(RecursoDuplicadoException.class)
                .hasMessageContaining("Usuário")
                .hasMessageContaining("id")
                .hasMessageContaining("1");

        verify(usuarioRepository, times(1)).findById(1);
        verify(usuarioRepository, times(1)).findByNome("Maria Santos");
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve atualizar usuário mantendo o mesmo nome")
    void deveAtualizarUsuarioMantendoMesmoNome() {
        // Arrange
        UsuarioRequestDTO requestMesmoNome = UsuarioRequestDTO.builder()
                .nome("João Silva")
                .senha("novaSenha456")
                .build();

        Usuario usuarioAtualizado = Usuario.builder()
                .idUsuario(1)
                .nome("João Silva")
                .senha("novaSenha456")
                .build();

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByNome("João Silva")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioAtualizado);

        // Act
        UsuarioResponseDTO resultado = usuarioService.atualizar(1, requestMesmoNome);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("João Silva");

        verify(usuarioRepository, times(1)).findById(1);
        verify(usuarioRepository, times(1)).findByNome("João Silva");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve atualizar apenas a senha do usuário")
    void deveAtualizarApenasSenhaUsuario() {
        // Arrange
        UsuarioRequestDTO requestNovaSenha = UsuarioRequestDTO.builder()
                .nome("João Silva")
                .senha("senhaNova789")
                .build();

        Usuario usuarioComNovaSenha = Usuario.builder()
                .idUsuario(1)
                .nome("João Silva")
                .senha("senhaNova789")
                .build();

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByNome("João Silva")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioComNovaSenha);

        // Act
        UsuarioResponseDTO resultado = usuarioService.atualizar(1, requestNovaSenha);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("João Silva");

        verify(usuarioRepository, times(1)).findById(1);
        verify(usuarioRepository, times(1)).findByNome("João Silva");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    // ========== TESTES DE EXCLUSÃO ==========

    @Test
    @DisplayName("Deve excluir usuário com sucesso")
    void deveExcluirUsuarioComSucesso() {
        // Arrange
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        doNothing().when(usuarioRepository).delete(usuario);

        // Act
        usuarioService.excluir(1);

        // Assert
        verify(usuarioRepository, times(1)).findById(1);
        verify(usuarioRepository, times(1)).delete(usuario);
    }

    @Test
    @DisplayName("Deve lançar exceção ao excluir usuário inexistente")
    void deveLancarExcecaoAoExcluirUsuarioInexistente() {
        // Arrange
        when(usuarioRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.excluir(999))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Usuário")
                .hasMessageContaining("id")
                .hasMessageContaining("999");

        verify(usuarioRepository, times(1)).findById(999);
        verify(usuarioRepository, never()).delete(any(Usuario.class));
    }
}