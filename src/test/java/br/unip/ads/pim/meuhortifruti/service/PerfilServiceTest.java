package br.unip.ads.pim.meuhortifruti.service;

import br.unip.ads.pim.meuhortifruti.dto.PerfilRequestDTO;
import br.unip.ads.pim.meuhortifruti.dto.PerfilResponseDTO;
import br.unip.ads.pim.meuhortifruti.entity.Perfil;
import br.unip.ads.pim.meuhortifruti.exception.RecursoDuplicadoException;
import br.unip.ads.pim.meuhortifruti.exception.RecursoNaoEncontradoException;
import br.unip.ads.pim.meuhortifruti.repository.PerfilRepository;
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
@DisplayName("Testes do PerfilService")
public class PerfilServiceTest {

    @Mock
    private PerfilRepository perfilRepository;

    @InjectMocks
    private PerfilService perfilService;

    private Perfil perfil;
    private PerfilRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        perfil = Perfil.builder()
                .idPerfil(1)
                .nome("ADMIN")
                .descricao("Perfil de administrador")
                .build();

        requestDTO = PerfilRequestDTO.builder()
                .nome("ADMIN")
                .descricao("Perfil de administrador")
                .build();
    }

    // ========== TESTES DE BUSCA POR ID ==========

    @Test
    @DisplayName("Deve buscar perfil por ID com sucesso")
    void deveBuscarPerfilPorId() {
        // Arrange
        when(perfilRepository.findById(1)).thenReturn(Optional.of(perfil));

        // Act
        PerfilResponseDTO resultado = perfilService.buscarPorId(1);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdPerfil()).isEqualTo(1);
        assertThat(resultado.getNome()).isEqualTo("ADMIN");
        assertThat(resultado.getDescricao()).isEqualTo("Perfil de administrador");

        verify(perfilRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar perfil inexistente")
    void deveLancarExcecaoAoBuscarPerfilInexistente() {
        // Arrange
        when(perfilRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> perfilService.buscarPorId(999))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Perfil")
                .hasMessageContaining("id")
                .hasMessageContaining("999");

        verify(perfilRepository, times(1)).findById(999);
    }

    // ========== TESTES DE CRIAÇÃO ==========

    @Test
    @DisplayName("Deve criar perfil com sucesso")
    void deveCriarPerfilComSucesso() {
        // Arrange
        when(perfilRepository.existsByNome("ADMIN")).thenReturn(false);
        when(perfilRepository.save(any(Perfil.class))).thenReturn(perfil);

        // Act
        PerfilResponseDTO resultado = perfilService.criar(requestDTO);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdPerfil()).isEqualTo(1);
        assertThat(resultado.getNome()).isEqualTo("ADMIN");
        assertThat(resultado.getDescricao()).isEqualTo("Perfil de administrador");

        verify(perfilRepository, times(1)).existsByNome("ADMIN");
        verify(perfilRepository, times(1)).save(any(Perfil.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar perfil com nome duplicado")
    void deveLancarExcecaoAoCriarPerfilComNomeDuplicado() {
        // Arrange
        when(perfilRepository.existsByNome("ADMIN")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> perfilService.criar(requestDTO))
                .isInstanceOf(RecursoDuplicadoException.class)
                .hasMessageContaining("Perfil")
                .hasMessageContaining("nome")
                .hasMessageContaining("ADMIN");

        verify(perfilRepository, times(1)).existsByNome("ADMIN");
        verify(perfilRepository, never()).save(any(Perfil.class));
    }

    @Test
    @DisplayName("Deve criar perfil USER")
    void deveCriarPerfilUser() {
        // Arrange
        PerfilRequestDTO requestUser = PerfilRequestDTO.builder()
                .nome("USER")
                .descricao("Perfil de usuário comum")
                .build();

        Perfil perfilUser = Perfil.builder()
                .idPerfil(2)
                .nome("USER")
                .descricao("Perfil de usuário comum")
                .build();

        when(perfilRepository.existsByNome("USER")).thenReturn(false);
        when(perfilRepository.save(any(Perfil.class))).thenReturn(perfilUser);

        // Act
        PerfilResponseDTO resultado = perfilService.criar(requestUser);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("USER");
        assertThat(resultado.getDescricao()).isEqualTo("Perfil de usuário comum");

        verify(perfilRepository, times(1)).existsByNome("USER");
        verify(perfilRepository, times(1)).save(any(Perfil.class));
    }

    @Test
    @DisplayName("Deve criar perfil GERENTE")
    void deveCriarPerfilGerente() {
        // Arrange
        PerfilRequestDTO requestGerente = PerfilRequestDTO.builder()
                .nome("GERENTE")
                .descricao("Perfil de gerente")
                .build();

        Perfil perfilGerente = Perfil.builder()
                .idPerfil(3)
                .nome("GERENTE")
                .descricao("Perfil de gerente")
                .build();

        when(perfilRepository.existsByNome("GERENTE")).thenReturn(false);
        when(perfilRepository.save(any(Perfil.class))).thenReturn(perfilGerente);

        // Act
        PerfilResponseDTO resultado = perfilService.criar(requestGerente);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("GERENTE");
        assertThat(resultado.getDescricao()).isEqualTo("Perfil de gerente");

        verify(perfilRepository, times(1)).existsByNome("GERENTE");
        verify(perfilRepository, times(1)).save(any(Perfil.class));
    }

    // ========== TESTES DE ATUALIZAÇÃO ==========

    @Test
    @DisplayName("Deve atualizar perfil com sucesso")
    void deveAtualizarPerfilComSucesso() {
        // Arrange
        PerfilRequestDTO requestAtualizado = PerfilRequestDTO.builder()
                .nome("ADMIN_MASTER")
                .descricao("Perfil de administrador master")
                .build();

        Perfil perfilAtualizado = Perfil.builder()
                .idPerfil(1)
                .nome("ADMIN_MASTER")
                .descricao("Perfil de administrador master")
                .build();

        when(perfilRepository.findById(1)).thenReturn(Optional.of(perfil));
        when(perfilRepository.findByNome("ADMIN_MASTER")).thenReturn(Optional.empty());
        when(perfilRepository.save(any(Perfil.class))).thenReturn(perfilAtualizado);

        // Act
        PerfilResponseDTO resultado = perfilService.atualizar(1, requestAtualizado);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdPerfil()).isEqualTo(1);
        assertThat(resultado.getNome()).isEqualTo("ADMIN_MASTER");
        assertThat(resultado.getDescricao()).isEqualTo("Perfil de administrador master");

        verify(perfilRepository, times(1)).findById(1);
        verify(perfilRepository, times(1)).findByNome("ADMIN_MASTER");
        verify(perfilRepository, times(1)).save(any(Perfil.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar perfil inexistente")
    void deveLancarExcecaoAoAtualizarPerfilInexistente() {
        // Arrange
        when(perfilRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> perfilService.atualizar(999, requestDTO))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Perfil")
                .hasMessageContaining("id")
                .hasMessageContaining("999");

        verify(perfilRepository, times(1)).findById(999);
        verify(perfilRepository, never()).save(any(Perfil.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar com nome já utilizado por outro perfil")
    void deveLancarExcecaoAoAtualizarComNomeDuplicado() {
        // Arrange
        Perfil outroPerfil = Perfil.builder()
                .idPerfil(2)
                .nome("USER")
                .descricao("Perfil de usuário")
                .build();

        PerfilRequestDTO requestComOutroNome = PerfilRequestDTO.builder()
                .nome("USER")
                .descricao("Perfil de administrador")
                .build();

        when(perfilRepository.findById(1)).thenReturn(Optional.of(perfil));
        when(perfilRepository.findByNome("USER")).thenReturn(Optional.of(outroPerfil));

        // Act & Assert
        assertThatThrownBy(() -> perfilService.atualizar(1, requestComOutroNome))
                .isInstanceOf(RecursoDuplicadoException.class)
                .hasMessageContaining("Perfil")
                .hasMessageContaining("nome")
                .hasMessageContaining("USER");

        verify(perfilRepository, times(1)).findById(1);
        verify(perfilRepository, times(1)).findByNome("USER");
        verify(perfilRepository, never()).save(any(Perfil.class));
    }

    @Test
    @DisplayName("Deve atualizar perfil mantendo o mesmo nome")
    void deveAtualizarPerfilMantendoMesmoNome() {
        // Arrange
        PerfilRequestDTO requestMesmoNome = PerfilRequestDTO.builder()
                .nome("ADMIN")
                .descricao("Nova descrição do administrador")
                .build();

        Perfil perfilAtualizado = Perfil.builder()
                .idPerfil(1)
                .nome("ADMIN")
                .descricao("Nova descrição do administrador")
                .build();

        when(perfilRepository.findById(1)).thenReturn(Optional.of(perfil));
        when(perfilRepository.findByNome("ADMIN")).thenReturn(Optional.of(perfil));
        when(perfilRepository.save(any(Perfil.class))).thenReturn(perfilAtualizado);

        // Act
        PerfilResponseDTO resultado = perfilService.atualizar(1, requestMesmoNome);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("ADMIN");
        assertThat(resultado.getDescricao()).isEqualTo("Nova descrição do administrador");

        verify(perfilRepository, times(1)).findById(1);
        verify(perfilRepository, times(1)).findByNome("ADMIN");
        verify(perfilRepository, times(1)).save(any(Perfil.class));
    }

    @Test
    @DisplayName("Deve atualizar apenas a descrição do perfil")
    void deveAtualizarApenasDescricaoPerfil() {
        // Arrange
        PerfilRequestDTO requestNovaDescricao = PerfilRequestDTO.builder()
                .nome("ADMIN")
                .descricao("Descrição atualizada do administrador")
                .build();

        Perfil perfilComNovaDescricao = Perfil.builder()
                .idPerfil(1)
                .nome("ADMIN")
                .descricao("Descrição atualizada do administrador")
                .build();

        when(perfilRepository.findById(1)).thenReturn(Optional.of(perfil));
        when(perfilRepository.findByNome("ADMIN")).thenReturn(Optional.of(perfil));
        when(perfilRepository.save(any(Perfil.class))).thenReturn(perfilComNovaDescricao);

        // Act
        PerfilResponseDTO resultado = perfilService.atualizar(1, requestNovaDescricao);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getDescricao()).isEqualTo("Descrição atualizada do administrador");

        verify(perfilRepository, times(1)).findById(1);
        verify(perfilRepository, times(1)).findByNome("ADMIN");
        verify(perfilRepository, times(1)).save(any(Perfil.class));
    }

    // ========== TESTES DE EXCLUSÃO ==========

    @Test
    @DisplayName("Deve excluir perfil com sucesso")
    void deveExcluirPerfilComSucesso() {
        // Arrange
        when(perfilRepository.findById(1)).thenReturn(Optional.of(perfil));
        doNothing().when(perfilRepository).delete(perfil);

        // Act
        perfilService.excluir(1);

        // Assert
        verify(perfilRepository, times(1)).findById(1);
        verify(perfilRepository, times(1)).delete(perfil);
    }

    @Test
    @DisplayName("Deve lançar exceção ao excluir perfil inexistente")
    void deveLancarExcecaoAoExcluirPerfilInexistente() {
        // Arrange
        when(perfilRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> perfilService.excluir(999))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Perfil")
                .hasMessageContaining("id")
                .hasMessageContaining("999");

        verify(perfilRepository, times(1)).findById(999);
        verify(perfilRepository, never()).delete(any(Perfil.class));
    }
}