package br.unip.ads.pim.meuhortifruti.controller;

import br.unip.ads.pim.meuhortifruti.dto.CategoriaRequestDTO;
import br.unip.ads.pim.meuhortifruti.dto.CategoriaResponseDTO;
import br.unip.ads.pim.meuhortifruti.service.CategoriaService;
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

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoriaController.class)
@AutoConfigureMockMvc(addFilters = false)  // Desabilita filtros de segurança
@DisplayName("Testes do CategoriaController")
public class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoriaService categoriaService;

    private CategoriaResponseDTO categoriaResponse;
    private CategoriaRequestDTO categoriaRequest;

    @BeforeEach
    void setUp() {
        categoriaResponse = CategoriaResponseDTO.builder()
                .idCategoria(1)
                .nome("Frutas")
                .build();

        categoriaRequest = CategoriaRequestDTO.builder()
                .nome("Frutas")
                .build();
    }

    // ========== TESTES DE LISTAGEM ==========

    @Test
    @DisplayName("Deve listar todas as categorias com sucesso")
    void deveListarTodasCategorias() throws Exception {
        // Arrange
        CategoriaResponseDTO categoria2 = CategoriaResponseDTO.builder()
                .idCategoria(2)
                .nome("Verduras")
                .build();

        List<CategoriaResponseDTO> categorias = Arrays.asList(categoriaResponse, categoria2);
        when(categoriaService.listarTodas()).thenReturn(categorias);

        // Act & Assert
        mockMvc.perform(get("/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].idCategoria").value(1))
                .andExpect(jsonPath("$[0].nome").value("Frutas"))
                .andExpect(jsonPath("$[1].idCategoria").value(2))
                .andExpect(jsonPath("$[1].nome").value("Verduras"));

        verify(categoriaService, times(1)).listarTodas();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver categorias")
    void deveRetornarListaVaziaQuandoNaoHouverCategorias() throws Exception {
        // Arrange
        when(categoriaService.listarTodas()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(categoriaService, times(1)).listarTodas();
    }

    // ========== TESTES DE BUSCA POR ID ==========

    @Test
    @DisplayName("Deve buscar categoria por ID com sucesso")
    void deveBuscarCategoriaPorId() throws Exception {
        // Arrange
        when(categoriaService.buscarPorId(1)).thenReturn(categoriaResponse);

        // Act & Assert
        mockMvc.perform(get("/v1/categorias/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCategoria").value(1))
                .andExpect(jsonPath("$.nome").value("Frutas"));

        verify(categoriaService, times(1)).buscarPorId(1);
    }

    @Test
    @DisplayName("Deve retornar erro quando categoria não for encontrada")
    void deveRetornarErroQuandoCategoriaNaoForEncontrada() throws Exception {
        // Arrange
        when(categoriaService.buscarPorId(999))
                .thenThrow(new RuntimeException("Categoria não encontrada com ID: 999"));

        // Act & Assert
        mockMvc.perform(get("/v1/categorias/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(categoriaService, times(1)).buscarPorId(999);
    }

    // ========== TESTES DE CRIAÇÃO ==========

    @Test
    @DisplayName("Deve criar categoria com sucesso")
    void deveCriarCategoriaComSucesso() throws Exception {
        // Arrange
        when(categoriaService.criar(any(CategoriaRequestDTO.class))).thenReturn(categoriaResponse);

        // Act & Assert
        mockMvc.perform(post("/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoriaRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCategoria").value(1))
                .andExpect(jsonPath("$.nome").value("Frutas"));

        verify(categoriaService, times(1)).criar(any(CategoriaRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar categoria com dados inválidos")
    void deveRetornar400AoCriarCategoriaComDadosInvalidos() throws Exception {
        // Arrange
        CategoriaRequestDTO requestInvalido = CategoriaRequestDTO.builder()
                .nome(null)
                .build();

        // Act & Assert
        mockMvc.perform(post("/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(categoriaService, never()).criar(any(CategoriaRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar erro ao criar categoria com nome duplicado")
    void deveRetornarErroAoCriarCategoriaComNomeDuplicado() throws Exception {
        // Arrange
        when(categoriaService.criar(any(CategoriaRequestDTO.class)))
                .thenThrow(new RuntimeException("Categoria com nome 'Frutas' já existe"));

        // Act & Assert
        mockMvc.perform(post("/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoriaRequest)))
                .andExpect(status().is5xxServerError());

        verify(categoriaService, times(1)).criar(any(CategoriaRequestDTO.class));
    }

    // ========== TESTES DE ATUALIZAÇÃO ==========

    @Test
    @DisplayName("Deve atualizar categoria com sucesso")
    void deveAtualizarCategoriaComSucesso() throws Exception {
        // Arrange
        CategoriaRequestDTO requestAtualizado = CategoriaRequestDTO.builder()
                .nome("Frutas Tropicais")
                .build();

        CategoriaResponseDTO responseAtualizado = CategoriaResponseDTO.builder()
                .idCategoria(1)
                .nome("Frutas Tropicais")
                .build();

        when(categoriaService.atualizar(eq(1), any(CategoriaRequestDTO.class)))
                .thenReturn(responseAtualizado);

        // Act & Assert
        mockMvc.perform(put("/v1/categorias/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestAtualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCategoria").value(1))
                .andExpect(jsonPath("$.nome").value("Frutas Tropicais"));

        verify(categoriaService, times(1)).atualizar(eq(1), any(CategoriaRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao atualizar categoria com dados inválidos")
    void deveRetornar400AoAtualizarCategoriaComDadosInvalidos() throws Exception {
        // Arrange
        CategoriaRequestDTO requestInvalido = CategoriaRequestDTO.builder()
                .nome("")
                .build();

        // Act & Assert
        mockMvc.perform(put("/v1/categorias/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(categoriaService, never()).atualizar(any(Integer.class), any(CategoriaRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar erro ao atualizar categoria inexistente")
    void deveRetornarErroAoAtualizarCategoriaInexistente() throws Exception {
        // Arrange
        when(categoriaService.atualizar(eq(999), any(CategoriaRequestDTO.class)))
                .thenThrow(new RuntimeException("Categoria não encontrada com ID: 999"));

        // Act & Assert
        mockMvc.perform(put("/v1/categorias/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoriaRequest)))
                .andExpect(status().is5xxServerError());

        verify(categoriaService, times(1)).atualizar(eq(999), any(CategoriaRequestDTO.class));
    }

    // ========== TESTES DE EXCLUSÃO ==========

    @Test
    @DisplayName("Deve excluir categoria com sucesso")
    void deveExcluirCategoriaComSucesso() throws Exception {
        // Arrange
        doNothing().when(categoriaService).excluir(1);

        // Act & Assert
        mockMvc.perform(delete("/v1/categorias/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(categoriaService, times(1)).excluir(1);
    }

    @Test
    @DisplayName("Deve retornar erro ao excluir categoria inexistente")
    void deveRetornarErroAoExcluirCategoriaInexistente() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Categoria não encontrada com ID: 999"))
                .when(categoriaService).excluir(999);

        // Act & Assert
        mockMvc.perform(delete("/v1/categorias/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(categoriaService, times(1)).excluir(999);
    }
}