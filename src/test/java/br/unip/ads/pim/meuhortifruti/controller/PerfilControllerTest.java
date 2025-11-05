package br.unip.ads.pim.meuhortifruti.controller;

import br.unip.ads.pim.meuhortifruti.dto.PerfilRequestDTO;
import br.unip.ads.pim.meuhortifruti.dto.PerfilResponseDTO;
import br.unip.ads.pim.meuhortifruti.service.PerfilService;
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

@WebMvcTest(PerfilController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Testes do PerfilController")
public class PerfilControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PerfilService perfilService;

    private PerfilResponseDTO perfilResponse;
    private PerfilRequestDTO perfilRequest;

    @BeforeEach
    void setUp() {
        perfilResponse = PerfilResponseDTO.builder()
                .idPerfil(1)
                .nome("ADMIN")
                .descricao("Perfil de administrador")
                .build();

        perfilRequest = PerfilRequestDTO.builder()
                .nome("ADMIN")
                .descricao("Perfil de administrador")
                .build();
    }

    // ========== TESTES DE BUSCA POR ID ==========

    @Test
    @DisplayName("Deve buscar perfil por ID com sucesso")
    void deveBuscarPerfilPorId() throws Exception {
        // Arrange
        when(perfilService.buscarPorId(1)).thenReturn(perfilResponse);

        // Act & Assert
        mockMvc.perform(get("/v1/perfils/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPerfil").value(1))
                .andExpect(jsonPath("$.nome").value("ADMIN"))
                .andExpect(jsonPath("$.descricao").value("Perfil de administrador"));

        verify(perfilService, times(1)).buscarPorId(1);
    }

    @Test
    @DisplayName("Deve retornar erro quando perfil não for encontrado")
    void deveRetornarErroQuandoPerfilNaoForEncontrado() throws Exception {
        // Arrange
        when(perfilService.buscarPorId(999))
                .thenThrow(new RuntimeException("Perfil não encontrado com ID: 999"));

        // Act & Assert
        mockMvc.perform(get("/v1/perfils/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(perfilService, times(1)).buscarPorId(999);
    }

    // ========== TESTES DE CRIAÇÃO ==========

    @Test
    @DisplayName("Deve criar perfil com sucesso")
    void deveCriarPerfilComSucesso() throws Exception {
        // Arrange
        when(perfilService.criar(any(PerfilRequestDTO.class))).thenReturn(perfilResponse);

        // Act & Assert
        mockMvc.perform(post("/v1/perfils")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(perfilRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idPerfil").value(1))
                .andExpect(jsonPath("$.nome").value("ADMIN"))
                .andExpect(jsonPath("$.descricao").value("Perfil de administrador"));

        verify(perfilService, times(1)).criar(any(PerfilRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar perfil com nome vazio")
    void deveRetornar400AoCriarPerfilComNomeVazio() throws Exception {
        // Arrange
        PerfilRequestDTO requestInvalido = PerfilRequestDTO.builder()
                .nome("")
                .descricao("Perfil de administrador")
                .build();

        // Act & Assert
        mockMvc.perform(post("/v1/perfils")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(perfilService, never()).criar(any(PerfilRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar perfil com nome nulo")
    void deveRetornar400AoCriarPerfilComNomeNulo() throws Exception {
        // Arrange
        PerfilRequestDTO requestInvalido = PerfilRequestDTO.builder()
                .nome(null)
                .descricao("Perfil de administrador")
                .build();

        // Act & Assert
        mockMvc.perform(post("/v1/perfils")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(perfilService, never()).criar(any(PerfilRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar perfil com descrição vazia")
    void deveRetornar400AoCriarPerfilComDescricaoVazia() throws Exception {
        // Arrange
        PerfilRequestDTO requestInvalido = PerfilRequestDTO.builder()
                .nome("ADMIN")
                .descricao("")
                .build();

        // Act & Assert
        mockMvc.perform(post("/v1/perfils")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(perfilService, never()).criar(any(PerfilRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar perfil com descrição nula")
    void deveRetornar400AoCriarPerfilComDescricaoNula() throws Exception {
        // Arrange
        PerfilRequestDTO requestInvalido = PerfilRequestDTO.builder()
                .nome("ADMIN")
                .descricao(null)
                .build();

        // Act & Assert
        mockMvc.perform(post("/v1/perfils")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(perfilService, never()).criar(any(PerfilRequestDTO.class));
    }

    @Test
    @DisplayName("Deve criar perfil USER com sucesso")
    void deveCriarPerfilUserComSucesso() throws Exception {
        // Arrange
        PerfilRequestDTO requestUser = PerfilRequestDTO.builder()
                .nome("USER")
                .descricao("Perfil de usuário comum")
                .build();

        PerfilResponseDTO responseUser = PerfilResponseDTO.builder()
                .idPerfil(2)
                .nome("USER")
                .descricao("Perfil de usuário comum")
                .build();

        when(perfilService.criar(any(PerfilRequestDTO.class))).thenReturn(responseUser);

        // Act & Assert
        mockMvc.perform(post("/v1/perfils")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("USER"));

        verify(perfilService, times(1)).criar(any(PerfilRequestDTO.class));
    }

    @Test
    @DisplayName("Deve criar perfil GERENTE com sucesso")
    void deveCriarPerfilGerenteComSucesso() throws Exception {
        // Arrange
        PerfilRequestDTO requestGerente = PerfilRequestDTO.builder()
                .nome("GERENTE")
                .descricao("Perfil de gerente")
                .build();

        PerfilResponseDTO responseGerente = PerfilResponseDTO.builder()
                .idPerfil(3)
                .nome("GERENTE")
                .descricao("Perfil de gerente")
                .build();

        when(perfilService.criar(any(PerfilRequestDTO.class))).thenReturn(responseGerente);

        // Act & Assert
        mockMvc.perform(post("/v1/perfils")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestGerente)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("GERENTE"));

        verify(perfilService, times(1)).criar(any(PerfilRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar erro ao criar perfil com nome duplicado")
    void deveRetornarErroAoCriarPerfilComNomeDuplicado() throws Exception {
        // Arrange
        when(perfilService.criar(any(PerfilRequestDTO.class)))
                .thenThrow(new RuntimeException("Perfil com nome 'ADMIN' já existe"));

        // Act & Assert
        mockMvc.perform(post("/v1/perfils")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(perfilRequest)))
                .andExpect(status().is5xxServerError());

        verify(perfilService, times(1)).criar(any(PerfilRequestDTO.class));
    }

    // ========== TESTES DE ATUALIZAÇÃO ==========

    @Test
    @DisplayName("Deve atualizar perfil com sucesso")
    void deveAtualizarPerfilComSucesso() throws Exception {
        // Arrange
        PerfilRequestDTO requestAtualizado = PerfilRequestDTO.builder()
                .nome("ADMIN_MASTER")
                .descricao("Perfil de administrador master")
                .build();

        PerfilResponseDTO responseAtualizado = PerfilResponseDTO.builder()
                .idPerfil(1)
                .nome("ADMIN_MASTER")
                .descricao("Perfil de administrador master")
                .build();

        when(perfilService.atualizar(eq(1), any(PerfilRequestDTO.class)))
                .thenReturn(responseAtualizado);

        // Act & Assert
        mockMvc.perform(put("/v1/perfils/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestAtualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPerfil").value(1))
                .andExpect(jsonPath("$.nome").value("ADMIN_MASTER"))
                .andExpect(jsonPath("$.descricao").value("Perfil de administrador master"));

        verify(perfilService, times(1)).atualizar(eq(1), any(PerfilRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao atualizar perfil com dados inválidos")
    void deveRetornar400AoAtualizarPerfilComDadosInvalidos() throws Exception {
        // Arrange
        PerfilRequestDTO requestInvalido = PerfilRequestDTO.builder()
                .nome("")
                .descricao("Descrição")
                .build();

        // Act & Assert
        mockMvc.perform(put("/v1/perfils/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(perfilService, never()).atualizar(any(Integer.class), any(PerfilRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar erro ao atualizar perfil inexistente")
    void deveRetornarErroAoAtualizarPerfilInexistente() throws Exception {
        // Arrange
        when(perfilService.atualizar(eq(999), any(PerfilRequestDTO.class)))
                .thenThrow(new RuntimeException("Perfil não encontrado com ID: 999"));

        // Act & Assert
        mockMvc.perform(put("/v1/perfils/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(perfilRequest)))
                .andExpect(status().is5xxServerError());

        verify(perfilService, times(1)).atualizar(eq(999), any(PerfilRequestDTO.class));
    }

    // ========== TESTES DE EXCLUSÃO ==========

    @Test
    @DisplayName("Deve excluir perfil com sucesso")
    void deveExcluirPerfilComSucesso() throws Exception {
        // Arrange
        doNothing().when(perfilService).excluir(1);

        // Act & Assert
        mockMvc.perform(delete("/v1/perfils/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(perfilService, times(1)).excluir(1);
    }

    @Test
    @DisplayName("Deve retornar erro ao excluir perfil inexistente")
    void deveRetornarErroAoExcluirPerfilInexistente() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Perfil não encontrado com ID: 999"))
                .when(perfilService).excluir(999);

        // Act & Assert
        mockMvc.perform(delete("/v1/perfils/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(perfilService, times(1)).excluir(999);
    }
}