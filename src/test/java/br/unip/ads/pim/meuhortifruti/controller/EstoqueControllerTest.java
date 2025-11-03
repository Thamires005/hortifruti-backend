package br.unip.ads.pim.meuhortifruti.controller;

import br.unip.ads.pim.meuhortifruti.dto.EstoqueRequestDTO;
import br.unip.ads.pim.meuhortifruti.dto.EstoqueResponseDTO;
import br.unip.ads.pim.meuhortifruti.service.EstoqueService;
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

@WebMvcTest(EstoqueController.class)
@AutoConfigureMockMvc(addFilters = false)  // Desabilita filtros de segurança
@DisplayName("Testes do EstoqueController")
public class EstoqueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EstoqueService estoqueService;

    private EstoqueResponseDTO estoqueResponse;
    private EstoqueRequestDTO estoqueRequest;

    @BeforeEach
    void setUp() {
        estoqueResponse = EstoqueResponseDTO.builder()
                .idEstoque(1)
                .idProduto(10)
                .quantidadeProdutos(100)
                .build();

        estoqueRequest = EstoqueRequestDTO.builder()
                .idProduto(10)
                .quantidadeProdutos(100)
                .build();
    }

    // ========== TESTES DE BUSCA POR ID ==========

    @Test
    @DisplayName("Deve buscar estoque por ID com sucesso")
    void deveBuscarEstoquePorId() throws Exception {
        // Arrange
        when(estoqueService.buscarPorId(1)).thenReturn(estoqueResponse);

        // Act & Assert
        mockMvc.perform(get("/v1/estoques/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEstoque").value(1))
                .andExpect(jsonPath("$.idProduto").value(10))
                .andExpect(jsonPath("$.quantidadeProdutos").value(100));

        verify(estoqueService, times(1)).buscarPorId(1);
    }

    @Test
    @DisplayName("Deve retornar erro quando estoque não for encontrado")
    void deveRetornarErroQuandoEstoqueNaoForEncontrado() throws Exception {
        // Arrange
        when(estoqueService.buscarPorId(999))
                .thenThrow(new RuntimeException("Estoque não encontrado com ID: 999"));

        // Act & Assert
        mockMvc.perform(get("/v1/estoques/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(estoqueService, times(1)).buscarPorId(999);
    }

    // ========== TESTES DE CRIAÇÃO ==========

    @Test
    @DisplayName("Deve criar estoque com sucesso")
    void deveCriarEstoqueComSucesso() throws Exception {
        // Arrange
        when(estoqueService.criar(any(EstoqueRequestDTO.class))).thenReturn(estoqueResponse);

        // Act & Assert
        mockMvc.perform(post("/v1/estoques")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(estoqueRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idEstoque").value(1))
                .andExpect(jsonPath("$.idProduto").value(10))
                .andExpect(jsonPath("$.quantidadeProdutos").value(100));

        verify(estoqueService, times(1)).criar(any(EstoqueRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar estoque com idProduto nulo")
    void deveRetornar400AoCriarEstoqueComIdProdutoNulo() throws Exception {
        // Arrange
        EstoqueRequestDTO requestInvalido = EstoqueRequestDTO.builder()
                .idProduto(null)
                .quantidadeProdutos(100)
                .build();

        // Act & Assert
        mockMvc.perform(post("/v1/estoques")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(estoqueService, never()).criar(any(EstoqueRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar estoque com quantidade nula")
    void deveRetornar400AoCriarEstoqueComQuantidadeNula() throws Exception {
        // Arrange
        EstoqueRequestDTO requestInvalido = EstoqueRequestDTO.builder()
                .idProduto(10)
                .quantidadeProdutos(null)
                .build();

        // Act & Assert
        mockMvc.perform(post("/v1/estoques")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(estoqueService, never()).criar(any(EstoqueRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar estoque com quantidade negativa")
    void deveRetornar400AoCriarEstoqueComQuantidadeNegativa() throws Exception {
        // Arrange
        EstoqueRequestDTO requestInvalido = EstoqueRequestDTO.builder()
                .idProduto(10)
                .quantidadeProdutos(-5)
                .build();

        // Act & Assert
        mockMvc.perform(post("/v1/estoques")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(estoqueService, never()).criar(any(EstoqueRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar erro ao criar estoque com produto inexistente")
    void deveRetornarErroAoCriarEstoqueComProdutoInexistente() throws Exception {
        // Arrange
        when(estoqueService.criar(any(EstoqueRequestDTO.class)))
                .thenThrow(new RuntimeException("Produto não encontrado com ID: 10"));

        // Act & Assert
        mockMvc.perform(post("/v1/estoques")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(estoqueRequest)))
                .andExpect(status().is5xxServerError());

        verify(estoqueService, times(1)).criar(any(EstoqueRequestDTO.class));
    }

    // ========== TESTES DE ATUALIZAÇÃO ==========

    @Test
    @DisplayName("Deve atualizar estoque com sucesso")
    void deveAtualizarEstoqueComSucesso() throws Exception {
        // Arrange
        EstoqueRequestDTO requestAtualizado = EstoqueRequestDTO.builder()
                .idProduto(10)
                .quantidadeProdutos(150)
                .build();

        EstoqueResponseDTO responseAtualizado = EstoqueResponseDTO.builder()
                .idEstoque(1)
                .idProduto(10)
                .quantidadeProdutos(150)
                .build();

        when(estoqueService.atualizar(eq(1), any(EstoqueRequestDTO.class)))
                .thenReturn(responseAtualizado);

        // Act & Assert
        mockMvc.perform(put("/v1/estoques/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestAtualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEstoque").value(1))
                .andExpect(jsonPath("$.idProduto").value(10))
                .andExpect(jsonPath("$.quantidadeProdutos").value(150));

        verify(estoqueService, times(1)).atualizar(eq(1), any(EstoqueRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao atualizar estoque com idProduto nulo")
    void deveRetornar400AoAtualizarEstoqueComIdProdutoNulo() throws Exception {
        // Arrange
        EstoqueRequestDTO requestInvalido = EstoqueRequestDTO.builder()
                .idProduto(null)
                .quantidadeProdutos(100)
                .build();

        // Act & Assert
        mockMvc.perform(put("/v1/estoques/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(estoqueService, never()).atualizar(any(Integer.class), any(EstoqueRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao atualizar estoque com quantidade negativa")
    void deveRetornar400AoAtualizarEstoqueComQuantidadeNegativa() throws Exception {
        // Arrange
        EstoqueRequestDTO requestInvalido = EstoqueRequestDTO.builder()
                .idProduto(10)
                .quantidadeProdutos(-10)
                .build();

        // Act & Assert
        mockMvc.perform(put("/v1/estoques/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(estoqueService, never()).atualizar(any(Integer.class), any(EstoqueRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar erro ao atualizar estoque inexistente")
    void deveRetornarErroAoAtualizarEstoqueInexistente() throws Exception {
        // Arrange
        when(estoqueService.atualizar(eq(999), any(EstoqueRequestDTO.class)))
                .thenThrow(new RuntimeException("Estoque não encontrado com ID: 999"));

        // Act & Assert
        mockMvc.perform(put("/v1/estoques/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(estoqueRequest)))
                .andExpect(status().is5xxServerError());

        verify(estoqueService, times(1)).atualizar(eq(999), any(EstoqueRequestDTO.class));
    }

    // ========== TESTES DE EXCLUSÃO ==========

    @Test
    @DisplayName("Deve excluir estoque com sucesso")
    void deveExcluirEstoqueComSucesso() throws Exception {
        // Arrange
        doNothing().when(estoqueService).excluir(1);

        // Act & Assert
        mockMvc.perform(delete("/v1/estoques/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(estoqueService, times(1)).excluir(1);
    }

    @Test
    @DisplayName("Deve retornar erro ao excluir estoque inexistente")
    void deveRetornarErroAoExcluirEstoqueInexistente() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Estoque não encontrado com ID: 999"))
                .when(estoqueService).excluir(999);

        // Act & Assert
        mockMvc.perform(delete("/v1/estoques/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(estoqueService, times(1)).excluir(999);
    }
}