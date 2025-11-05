package br.unip.ads.pim.meuhortifruti.controller;

import br.unip.ads.pim.meuhortifruti.dto.CompraRequestDTO;
import br.unip.ads.pim.meuhortifruti.dto.CompraResponseDTO;
import br.unip.ads.pim.meuhortifruti.entity.ItemCompra;
import br.unip.ads.pim.meuhortifruti.service.CompraService;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CompraController.class)
@AutoConfigureMockMvc(addFilters = false)  // Desabilita filtros de segurança
@DisplayName("Testes do CompraController")
public class CompraControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CompraService compraService;

    private CompraResponseDTO compraResponse;
    private CompraRequestDTO compraRequest;
    private List<ItemCompra> itensCompra;

    @BeforeEach
    void setUp() {
        itensCompra = new ArrayList<>();

        compraResponse = CompraResponseDTO.builder()
                .idCompra(1)
                .statusCompra("PENDENTE")
                .itensCompra(new ArrayList<>())
                .pagamento(null)
                .build();

        compraRequest = CompraRequestDTO.builder()
                .idCompra(null)
                .statusCompra("PENDENTE")
                .itensCompra(itensCompra)
                .build();
    }

    // ========== TESTES DE LISTAGEM ==========

    @Test
    @DisplayName("Deve listar todas as compras com sucesso")
    void deveListarTodasCompras() throws Exception {
        // Arrange
        CompraResponseDTO compra2 = CompraResponseDTO.builder()
                .idCompra(2)
                .statusCompra("FINALIZADA")
                .itensCompra(new ArrayList<>())
                .pagamento(null)
                .build();

        List<CompraResponseDTO> compras = Arrays.asList(compraResponse, compra2);
        when(compraService.listarTodas()).thenReturn(compras);

        // Act & Assert
        mockMvc.perform(get("/v1/compras")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].idCompra").value(1))
                .andExpect(jsonPath("$[0].statusCompra").value("PENDENTE"))
                .andExpect(jsonPath("$[1].idCompra").value(2))
                .andExpect(jsonPath("$[1].statusCompra").value("FINALIZADA"));

        verify(compraService, times(1)).listarTodas();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver compras")
    void deveRetornarListaVaziaQuandoNaoHouverCompras() throws Exception {
        // Arrange
        when(compraService.listarTodas()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/v1/compras")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(compraService, times(1)).listarTodas();
    }

    // ========== TESTES DE BUSCA POR ID ==========

    @Test
    @DisplayName("Deve buscar compra por ID com sucesso")
    void deveBuscarCompraPorId() throws Exception {
        // Arrange
        when(compraService.buscarPorId(1)).thenReturn(compraResponse);

        // Act & Assert
        mockMvc.perform(get("/v1/compras/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCompra").value(1))
                .andExpect(jsonPath("$.statusCompra").value("PENDENTE"));

        verify(compraService, times(1)).buscarPorId(1);
    }

    @Test
    @DisplayName("Deve retornar erro quando compra não for encontrada")
    void deveRetornarErroQuandoCompraNaoForEncontrada() throws Exception {
        // Arrange
        when(compraService.buscarPorId(999))
                .thenThrow(new RuntimeException("Compra não encontrada com ID: 999"));

        // Act & Assert
        mockMvc.perform(get("/v1/compras/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(compraService, times(1)).buscarPorId(999);
    }

    // ========== TESTES DE CRIAÇÃO ==========

    @Test
    @DisplayName("Deve criar compra com sucesso")
    void deveCriarCompraComSucesso() throws Exception {
        // Arrange
        CompraResponseDTO responseCreated = CompraResponseDTO.builder()
                .idCompra(1)
                .statusCompra("PENDENTE")
                .itensCompra(new ArrayList<>())
                .pagamento(null)
                .build();

        when(compraService.criar(any(CompraRequestDTO.class))).thenReturn(responseCreated);

        // Act & Assert
        mockMvc.perform(post("/v1/compras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(compraRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCompra").value(1))
                .andExpect(jsonPath("$.statusCompra").value("PENDENTE"));

        verify(compraService, times(1)).criar(any(CompraRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar compra com status vazio")
    void deveRetornar400AoCriarCompraComStatusVazio() throws Exception {
        // Arrange
        CompraRequestDTO requestInvalido = CompraRequestDTO.builder()
                .idCompra(null)
                .statusCompra("")  // Status vazio
                .itensCompra(itensCompra)
                .build();

        // Act & Assert
        mockMvc.perform(post("/v1/compras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(compraService, never()).criar(any(CompraRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar compra com status nulo")
    void deveRetornar400AoCriarCompraComStatusNulo() throws Exception {
        // Arrange
        CompraRequestDTO requestInvalido = CompraRequestDTO.builder()
                .idCompra(null)
                .statusCompra(null)  // Status nulo
                .itensCompra(itensCompra)
                .build();

        // Act & Assert
        mockMvc.perform(post("/v1/compras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(compraService, never()).criar(any(CompraRequestDTO.class));
    }

    @Test
    @DisplayName("Deve criar compra com lista de itens vazia")
    void deveCriarCompraComListaItensVazia() throws Exception {
        // Arrange
        CompraRequestDTO requestSemItens = CompraRequestDTO.builder()
                .idCompra(null)
                .statusCompra("PENDENTE")
                .itensCompra(new ArrayList<>())
                .build();

        CompraResponseDTO responseSemItens = CompraResponseDTO.builder()
                .idCompra(2)
                .statusCompra("PENDENTE")
                .itensCompra(new ArrayList<>())
                .pagamento(null)
                .build();

        when(compraService.criar(any(CompraRequestDTO.class))).thenReturn(responseSemItens);

        // Act & Assert
        mockMvc.perform(post("/v1/compras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestSemItens)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCompra").value(2))
                .andExpect(jsonPath("$.statusCompra").value("PENDENTE"))
                .andExpect(jsonPath("$.itensCompra").isEmpty());

        verify(compraService, times(1)).criar(any(CompraRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar erro ao criar compra com ID duplicado")
    void deveRetornarErroAoCriarCompraComIdDuplicado() throws Exception {
        // Arrange
        CompraRequestDTO requestComId = CompraRequestDTO.builder()
                .idCompra(1)
                .statusCompra("PENDENTE")
                .itensCompra(itensCompra)
                .build();

        when(compraService.criar(any(CompraRequestDTO.class)))
                .thenThrow(new RuntimeException("Compra com ID '1' já existe"));

        // Act & Assert
        mockMvc.perform(post("/v1/compras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestComId)))
                .andExpect(status().is5xxServerError());

        verify(compraService, times(1)).criar(any(CompraRequestDTO.class));
    }

    // ========== TESTES DE ATUALIZAÇÃO ==========

    @Test
    @DisplayName("Deve atualizar compra com sucesso")
    void deveAtualizarCompraComSucesso() throws Exception {
        // Arrange
        CompraRequestDTO requestAtualizado = CompraRequestDTO.builder()
                .idCompra(1)
                .statusCompra("FINALIZADA")
                .itensCompra(itensCompra)
                .build();

        CompraResponseDTO responseAtualizado = CompraResponseDTO.builder()
                .idCompra(1)
                .statusCompra("FINALIZADA")
                .itensCompra(new ArrayList<>())
                .pagamento(null)
                .build();

        when(compraService.atualizar(eq(1), any(CompraRequestDTO.class)))
                .thenReturn(responseAtualizado);

        // Act & Assert
        mockMvc.perform(put("/v1/compras/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestAtualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCompra").value(1))
                .andExpect(jsonPath("$.statusCompra").value("FINALIZADA"));

        verify(compraService, times(1)).atualizar(eq(1), any(CompraRequestDTO.class));
    }

    @Test
    @DisplayName("Deve atualizar status da compra de PENDENTE para FINALIZADA")
    void deveAtualizarStatusCompraPendenteParaFinalizada() throws Exception {
        // Arrange
        CompraRequestDTO requestFinalizada = CompraRequestDTO.builder()
                .idCompra(1)
                .statusCompra("FINALIZADA")
                .itensCompra(itensCompra)
                .build();

        CompraResponseDTO responseFinalizada = CompraResponseDTO.builder()
                .idCompra(1)
                .statusCompra("FINALIZADA")
                .itensCompra(new ArrayList<>())
                .pagamento(null)
                .build();

        when(compraService.atualizar(eq(1), any(CompraRequestDTO.class)))
                .thenReturn(responseFinalizada);

        // Act & Assert
        mockMvc.perform(put("/v1/compras/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestFinalizada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCompra").value("FINALIZADA"));

        verify(compraService, times(1)).atualizar(eq(1), any(CompraRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao atualizar compra com status vazio")
    void deveRetornar400AoAtualizarCompraComStatusVazio() throws Exception {
        // Arrange
        CompraRequestDTO requestInvalido = CompraRequestDTO.builder()
                .idCompra(1)
                .statusCompra("")  // Status vazio
                .itensCompra(itensCompra)
                .build();

        // Act & Assert
        mockMvc.perform(put("/v1/compras/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(compraService, never()).atualizar(any(Integer.class), any(CompraRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar erro ao atualizar compra inexistente")
    void deveRetornarErroAoAtualizarCompraInexistente() throws Exception {
        // Arrange
        when(compraService.atualizar(eq(999), any(CompraRequestDTO.class)))
                .thenThrow(new RuntimeException("Compra não encontrada com ID: 999"));

        // Act & Assert
        mockMvc.perform(put("/v1/compras/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(compraRequest)))
                .andExpect(status().is5xxServerError());

        verify(compraService, times(1)).atualizar(eq(999), any(CompraRequestDTO.class));
    }

    // ========== TESTES DE EXCLUSÃO ==========

    @Test
    @DisplayName("Deve excluir compra com sucesso")
    void deveExcluirCompraComSucesso() throws Exception {
        // Arrange
        doNothing().when(compraService).excluir(1);

        // Act & Assert
        mockMvc.perform(delete("/v1/compras/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(compraService, times(1)).excluir(1);
    }

    @Test
    @DisplayName("Deve retornar erro ao excluir compra inexistente")
    void deveRetornarErroAoExcluirCompraInexistente() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Compra não encontrada com ID: 999"))
                .when(compraService).excluir(999);

        // Act & Assert
        mockMvc.perform(delete("/v1/compras/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(compraService, times(1)).excluir(999);
    }

    // ========== TESTES DE DIFERENTES STATUS ==========

    @Test
    @DisplayName("Deve criar compra com status CANCELADA")
    void deveCriarCompraComStatusCancelada() throws Exception {
        // Arrange
        CompraRequestDTO requestCancelada = CompraRequestDTO.builder()
                .idCompra(null)
                .statusCompra("CANCELADA")
                .itensCompra(itensCompra)
                .build();

        CompraResponseDTO responseCancelada = CompraResponseDTO.builder()
                .idCompra(3)
                .statusCompra("CANCELADA")
                .itensCompra(new ArrayList<>())
                .pagamento(null)
                .build();

        when(compraService.criar(any(CompraRequestDTO.class))).thenReturn(responseCancelada);

        // Act & Assert
        mockMvc.perform(post("/v1/compras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCancelada)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCompra").value("CANCELADA"));

        verify(compraService, times(1)).criar(any(CompraRequestDTO.class));
    }

    @Test
    @DisplayName("Deve criar compra com status FINALIZADA")
    void deveCriarCompraComStatusFinalizada() throws Exception {
        // Arrange
        CompraRequestDTO requestFinalizada = CompraRequestDTO.builder()
                .idCompra(null)
                .statusCompra("FINALIZADA")
                .itensCompra(itensCompra)
                .build();

        CompraResponseDTO responseFinalizada = CompraResponseDTO.builder()
                .idCompra(4)
                .statusCompra("FINALIZADA")
                .itensCompra(new ArrayList<>())
                .pagamento(null)
                .build();

        when(compraService.criar(any(CompraRequestDTO.class))).thenReturn(responseFinalizada);

        // Act & Assert
        mockMvc.perform(post("/v1/compras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestFinalizada)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCompra").value("FINALIZADA"));

        verify(compraService, times(1)).criar(any(CompraRequestDTO.class));
    }
}