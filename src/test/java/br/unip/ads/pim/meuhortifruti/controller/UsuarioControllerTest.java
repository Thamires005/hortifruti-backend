package br.unip.ads.pim.meuhortifruti.controller;

import br.unip.ads.pim.meuhortifruti.dto.UsuarioRequestDTO;
import br.unip.ads.pim.meuhortifruti.dto.UsuarioResponseDTO;
import br.unip.ads.pim.meuhortifruti.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Testes do UsuarioController")
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService usuarioService;

    private UsuarioResponseDTO usuarioResponse;
    private UsuarioRequestDTO usuarioRequest;

    @BeforeEach
    void setUp() {
        usuarioResponse = UsuarioResponseDTO.builder()
                .idUsuario(1)
                .nome("João Silva")
                .perfil(null)
                .build();

        usuarioRequest = UsuarioRequestDTO.builder()
                .nome("João Silva")
                .senha("senha123")
                .build();
    }

    // ========== TESTES DE BUSCA POR ID ==========

    @Test
    @DisplayName("Deve buscar usuário por ID com sucesso")
    void deveBuscarUsuarioPorId() throws Exception {
        // Arrange
        when(usuarioService.buscarPorId(1)).thenReturn(usuarioResponse);

        // Act & Assert
        mockMvc.perform(get("/v1/usuarios/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(1))
                .andExpect(jsonPath("$.nome").value("João Silva"));

        verify(usuarioService, times(1)).buscarPorId(1);
    }

    @Test
    @DisplayName("Deve retornar erro quando usuário não for encontrado")
    void deveRetornarErroQuandoUsuarioNaoForEncontrado() throws Exception {
        // Arrange
        when(usuarioService.buscarPorId(999))
                .thenThrow(new RuntimeException("Usuário não encontrado com ID: 999"));

        // Act & Assert
        mockMvc.perform(get("/v1/usuarios/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(usuarioService, times(1)).buscarPorId(999);
    }

    // ========== TESTES DE CRIAÇÃO ==========

    @Test
    @DisplayName("Deve criar usuário com sucesso")
    void deveCriarUsuarioComSucesso() throws Exception {
        // Arrange
        when(usuarioService.criar(any(UsuarioRequestDTO.class))).thenReturn(usuarioResponse);

        // Act & Assert
        mockMvc.perform(post("/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idUsuario").value(1))
                .andExpect(jsonPath("$.nome").value("João Silva"));

        verify(usuarioService, times(1)).criar(any(UsuarioRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar usuário com nome vazio")
    void deveRetornar400AoCriarUsuarioComNomeVazio() throws Exception {
        // Arrange
        UsuarioRequestDTO requestInvalido = UsuarioRequestDTO.builder()
                .nome("")
                .senha("senha123")
                .build();

        // Act & Assert
        mockMvc.perform(post("/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(usuarioService, never()).criar(any(UsuarioRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar usuário com nome nulo")
    void deveRetornar400AoCriarUsuarioComNomeNulo() throws Exception {
        // Arrange
        UsuarioRequestDTO requestInvalido = UsuarioRequestDTO.builder()
                .nome(null)
                .senha("senha123")
                .build();

        // Act & Assert
        mockMvc.perform(post("/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(usuarioService, never()).criar(any(UsuarioRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar usuário com senha vazia")
    void deveRetornar400AoCriarUsuarioComSenhaVazia() throws Exception {
        // Arrange
        UsuarioRequestDTO requestInvalido = UsuarioRequestDTO.builder()
                .nome("João Silva")
                .senha("")
                .build();

        // Act & Assert
        mockMvc.perform(post("/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(usuarioService, never()).criar(any(UsuarioRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar usuário com senha nula")
    void deveRetornar400AoCriarUsuarioComSenhaNula() throws Exception {
        // Arrange
        UsuarioRequestDTO requestInvalido = UsuarioRequestDTO.builder()
                .nome("João Silva")
                .senha(null)
                .build();

        // Act & Assert
        mockMvc.perform(post("/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(usuarioService, never()).criar(any(UsuarioRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar usuário com senha menor que 5 caracteres")
    void deveRetornar400AoCriarUsuarioComSenhaCurta() throws Exception {
        // Arrange
        UsuarioRequestDTO requestInvalido = UsuarioRequestDTO.builder()
                .nome("João Silva")
                .senha("1234")  // Menos de 5 caracteres
                .build();

        // Act & Assert
        mockMvc.perform(post("/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(usuarioService, never()).criar(any(UsuarioRequestDTO.class));
    }

    @Test
    @DisplayName("Deve criar usuário com senha de 5 caracteres")
    void deveCriarUsuarioComSenhaMinima() throws Exception {
        // Arrange
        UsuarioRequestDTO requestSenhaMinima = UsuarioRequestDTO.builder()
                .nome("Maria Santos")
                .senha("12345")  // Exatamente 5 caracteres
                .build();

        UsuarioResponseDTO responseSenhaMinima = UsuarioResponseDTO.builder()
                .idUsuario(2)
                .nome("Maria Santos")
                .build();

        when(usuarioService.criar(any(UsuarioRequestDTO.class))).thenReturn(responseSenhaMinima);

        // Act & Assert
        mockMvc.perform(post("/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestSenhaMinima)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Maria Santos"));

        verify(usuarioService, times(1)).criar(any(UsuarioRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar erro ao criar usuário com nome duplicado")
    void deveRetornarErroAoCriarUsuarioComNomeDuplicado() throws Exception {
        // Arrange
        when(usuarioService.criar(any(UsuarioRequestDTO.class)))
                .thenThrow(new RuntimeException("Usuário com nome 'João Silva' já existe"));

        // Act & Assert
        mockMvc.perform(post("/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioRequest)))
                .andExpect(status().is5xxServerError());

        verify(usuarioService, times(1)).criar(any(UsuarioRequestDTO.class));
    }

    // ========== TESTES DE ATUALIZAÇÃO ==========

    @Test
    @DisplayName("Deve atualizar usuário com sucesso")
    void deveAtualizarUsuarioComSucesso() throws Exception {
        // Arrange
        UsuarioRequestDTO requestAtualizado = UsuarioRequestDTO.builder()
                .nome("João Silva Atualizado")
                .senha("novaSenha123")
                .build();

        UsuarioResponseDTO responseAtualizado = UsuarioResponseDTO.builder()
                .idUsuario(1)
                .nome("João Silva Atualizado")
                .build();

        when(usuarioService.atualizar(eq(1), any(UsuarioRequestDTO.class)))
                .thenReturn(responseAtualizado);

        // Act & Assert
        mockMvc.perform(put("/v1/usuarios/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestAtualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(1))
                .andExpect(jsonPath("$.nome").value("João Silva Atualizado"));

        verify(usuarioService, times(1)).atualizar(eq(1), any(UsuarioRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao atualizar usuário com dados inválidos")
    void deveRetornar400AoAtualizarUsuarioComDadosInvalidos() throws Exception {
        // Arrange
        UsuarioRequestDTO requestInvalido = UsuarioRequestDTO.builder()
                .nome("")
                .senha("senha123")
                .build();

        // Act & Assert
        mockMvc.perform(put("/v1/usuarios/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(usuarioService, never()).atualizar(any(Integer.class), any(UsuarioRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar erro ao atualizar usuário inexistente")
    void deveRetornarErroAoAtualizarUsuarioInexistente() throws Exception {
        // Arrange
        when(usuarioService.atualizar(eq(999), any(UsuarioRequestDTO.class)))
                .thenThrow(new RuntimeException("Usuário não encontrado com ID: 999"));

        // Act & Assert
        mockMvc.perform(put("/v1/usuarios/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioRequest)))
                .andExpect(status().is5xxServerError());

        verify(usuarioService, times(1)).atualizar(eq(999), any(UsuarioRequestDTO.class));
    }

    // ========== TESTES DE EXCLUSÃO ==========

    @Test
    @DisplayName("Deve excluir usuário com sucesso")
    void deveExcluirUsuarioComSucesso() throws Exception {
        // Arrange
        doNothing().when(usuarioService).excluir(1);

        // Act & Assert
        mockMvc.perform(delete("/v1/usuarios/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(usuarioService, times(1)).excluir(1);
    }

    @Test
    @DisplayName("Deve retornar erro ao excluir usuário inexistente")
    void deveRetornarErroAoExcluirUsuarioInexistente() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Usuário não encontrado com ID: 999"))
                .when(usuarioService).excluir(999);

        // Act & Assert
        mockMvc.perform(delete("/v1/usuarios/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(usuarioService, times(1)).excluir(999);
    }
}