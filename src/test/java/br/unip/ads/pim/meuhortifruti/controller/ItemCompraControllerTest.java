package br.unip.ads.pim.meuhortifruti.controller;

import br.unip.ads.pim.meuhortifruti.dto.ItemCompraRequestDTO;
import br.unip.ads.pim.meuhortifruti.dto.ItemCompraResponseDTO;
import br.unip.ads.pim.meuhortifruti.entity.Produto;
import br.unip.ads.pim.meuhortifruti.service.ItemCompraService;
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

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemCompraController.class)
@AutoConfigureMockMvc(addFilters = false)  // Desabilita filtros de segurança
@DisplayName("Testes do ItemCompraController")
public class ItemCompraControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemCompraService itemCompraService;

    private ItemCompraResponseDTO itemCompraResponse;
    private ItemCompraRequestDTO itemCompraRequest;
    private Produto produto;

    @BeforeEach
    void setUp() {
        produto = Produto.builder()
                .idProduto(1)
                .nome("Maçã")
                .preco(new BigDecimal("5.50"))
                .build();

        itemCompraResponse = ItemCompraResponseDTO.builder()
                .idItemCompra(1)
                .preco(new BigDecimal("5.50"))
                .quantidade(10)
                .build();

        itemCompraRequest = ItemCompraRequestDTO.builder()
                .produto(produto)
                .preco(new BigDecimal("5.50"))
                .quantidade(10)
                .build();
    }

    // ========== TESTES DE BUSCA POR ID ==========

    @Test
    @DisplayName("Deve buscar item de compra por ID com sucesso")
    void deveBuscarItemCompraPorId() throws Exception {
        // Arrange
        when(itemCompraService.buscarPorId(1)).thenReturn(itemCompraResponse);

        // Act & Assert
        mockMvc.perform(get("/v1/itemCompras/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idItemCompra").value(1))
                .andExpect(jsonPath("$.preco").value(5.50))
                .andExpect(jsonPath("$.quantidade").value(10));

        verify(itemCompraService, times(1)).buscarPorId(1);
    }

    @Test
    @DisplayName("Deve retornar erro quando item de compra não for encontrado")
    void deveRetornarErroQuandoItemCompraNaoForEncontrado() throws Exception {
        // Arrange
        when(itemCompraService.buscarPorId(999))
                .thenThrow(new RuntimeException("Item Compra não encontrado com ID: 999"));

        // Act & Assert
        mockMvc.perform(get("/v1/itemCompras/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(itemCompraService, times(1)).buscarPorId(999);
    }

    // ========== TESTES DE CRIAÇÃO ==========

    @Test
    @DisplayName("Deve criar item de compra com sucesso")
    void deveCriarItemCompraComSucesso() throws Exception {
        // Arrange
        when(itemCompraService.criar(any(ItemCompraRequestDTO.class))).thenReturn(itemCompraResponse);

        // Act & Assert
        mockMvc.perform(post("/v1/itemCompras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemCompraRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idItemCompra").value(1))
                .andExpect(jsonPath("$.preco").value(5.50))
                .andExpect(jsonPath("$.quantidade").value(10));

        verify(itemCompraService, times(1)).criar(any(ItemCompraRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar item de compra com preço nulo")
    void deveRetornar400AoCriarItemCompraComPrecoNulo() throws Exception {
        // Arrange
        ItemCompraRequestDTO requestInvalido = ItemCompraRequestDTO.builder()
                .produto(produto)
                .preco(null)  // Preço nulo
                .quantidade(10)
                .build();

        // Act & Assert
        mockMvc.perform(post("/v1/itemCompras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(itemCompraService, never()).criar(any(ItemCompraRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar item de compra com preço zero")
    void deveRetornar400AoCriarItemCompraComPrecoZero() throws Exception {
        // Arrange
        ItemCompraRequestDTO requestInvalido = ItemCompraRequestDTO.builder()
                .produto(produto)
                .preco(new BigDecimal("0.00"))  // Preço zero (deve ser maior que 0.01)
                .quantidade(10)
                .build();

        // Act & Assert
        mockMvc.perform(post("/v1/itemCompras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(itemCompraService, never()).criar(any(ItemCompraRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar item de compra com preço negativo")
    void deveRetornar400AoCriarItemCompraComPrecoNegativo() throws Exception {
        // Arrange
        ItemCompraRequestDTO requestInvalido = ItemCompraRequestDTO.builder()
                .produto(produto)
                .preco(new BigDecimal("-5.00"))  // Preço negativo
                .quantidade(10)
                .build();

        // Act & Assert
        mockMvc.perform(post("/v1/itemCompras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(itemCompraService, never()).criar(any(ItemCompraRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar item de compra com quantidade nula")
    void deveRetornar400AoCriarItemCompraComQuantidadeNula() throws Exception {
        // Arrange
        ItemCompraRequestDTO requestInvalido = ItemCompraRequestDTO.builder()
                .produto(produto)
                .preco(new BigDecimal("5.50"))
                .quantidade(null)  // Quantidade nula
                .build();

        // Act & Assert
        mockMvc.perform(post("/v1/itemCompras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(itemCompraService, never()).criar(any(ItemCompraRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar item de compra com quantidade zero")
    void deveRetornar400AoCriarItemCompraComQuantidadeZero() throws Exception {
        // Arrange
        ItemCompraRequestDTO requestInvalido = ItemCompraRequestDTO.builder()
                .produto(produto)
                .preco(new BigDecimal("5.50"))
                .quantidade(0)  // Quantidade zero (deve ser mínimo 1)
                .build();

        // Act & Assert
        mockMvc.perform(post("/v1/itemCompras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(itemCompraService, never()).criar(any(ItemCompraRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar item de compra com quantidade negativa")
    void deveRetornar400AoCriarItemCompraComQuantidadeNegativa() throws Exception {
        // Arrange
        ItemCompraRequestDTO requestInvalido = ItemCompraRequestDTO.builder()
                .produto(produto)
                .preco(new BigDecimal("5.50"))
                .quantidade(-5)  // Quantidade negativa
                .build();

        // Act & Assert
        mockMvc.perform(post("/v1/itemCompras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(itemCompraService, never()).criar(any(ItemCompraRequestDTO.class));
    }

    @Test
    @DisplayName("Deve criar item de compra com quantidade mínima válida")
    void deveCriarItemCompraComQuantidadeMinimaValida() throws Exception {
        // Arrange
        ItemCompraRequestDTO requestQuantidadeMinima = ItemCompraRequestDTO.builder()
                .produto(produto)
                .preco(new BigDecimal("5.50"))
                .quantidade(1)  // Quantidade mínima válida
                .build();

        ItemCompraResponseDTO responseQuantidadeMinima = ItemCompraResponseDTO.builder()
                .idItemCompra(2)
                .preco(new BigDecimal("5.50"))
                .quantidade(1)
                .build();

        when(itemCompraService.criar(any(ItemCompraRequestDTO.class))).thenReturn(responseQuantidadeMinima);

        // Act & Assert
        mockMvc.perform(post("/v1/itemCompras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestQuantidadeMinima)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quantidade").value(1));

        verify(itemCompraService, times(1)).criar(any(ItemCompraRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar erro ao criar item de compra com produto duplicado")
    void deveRetornarErroAoCriarItemCompraComProdutoDuplicado() throws Exception {
        // Arrange
        when(itemCompraService.criar(any(ItemCompraRequestDTO.class)))
                .thenThrow(new RuntimeException("Item compra com produto ID '1' já existe"));

        // Act & Assert
        mockMvc.perform(post("/v1/itemCompras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemCompraRequest)))
                .andExpect(status().is5xxServerError());

        verify(itemCompraService, times(1)).criar(any(ItemCompraRequestDTO.class));
    }

    // ========== TESTES DE ATUALIZAÇÃO ==========

    @Test
    @DisplayName("Deve atualizar item de compra com sucesso")
    void deveAtualizarItemCompraComSucesso() throws Exception {
        // Arrange
        ItemCompraRequestDTO requestAtualizado = ItemCompraRequestDTO.builder()
                .produto(produto)
                .preco(new BigDecimal("6.00"))
                .quantidade(15)
                .build();

        ItemCompraResponseDTO responseAtualizado = ItemCompraResponseDTO.builder()
                .idItemCompra(1)
                .preco(new BigDecimal("6.00"))
                .quantidade(15)
                .build();

        when(itemCompraService.atualizar(eq(1), any(ItemCompraRequestDTO.class)))
                .thenReturn(responseAtualizado);

        // Act & Assert
        mockMvc.perform(put("/v1/itemCompras/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestAtualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idItemCompra").value(1))
                .andExpect(jsonPath("$.preco").value(6.00))
                .andExpect(jsonPath("$.quantidade").value(15));

        verify(itemCompraService, times(1)).atualizar(eq(1), any(ItemCompraRequestDTO.class));
    }

    @Test
    @DisplayName("Deve atualizar apenas o preço do item de compra")
    void deveAtualizarApenasPrecoItemCompra() throws Exception {
        // Arrange
        ItemCompraRequestDTO requestNovoPreco = ItemCompraRequestDTO.builder()
                .produto(produto)
                .preco(new BigDecimal("8.50"))
                .quantidade(10)  // Mesma quantidade
                .build();

        ItemCompraResponseDTO responseNovoPreco = ItemCompraResponseDTO.builder()
                .idItemCompra(1)
                .preco(new BigDecimal("8.50"))
                .quantidade(10)
                .build();

        when(itemCompraService.atualizar(eq(1), any(ItemCompraRequestDTO.class)))
                .thenReturn(responseNovoPreco);

        // Act & Assert
        mockMvc.perform(put("/v1/itemCompras/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestNovoPreco)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preco").value(8.50))
                .andExpect(jsonPath("$.quantidade").value(10));

        verify(itemCompraService, times(1)).atualizar(eq(1), any(ItemCompraRequestDTO.class));
    }

    @Test
    @DisplayName("Deve atualizar apenas a quantidade do item de compra")
    void deveAtualizarApenasQuantidadeItemCompra() throws Exception {
        // Arrange
        ItemCompraRequestDTO requestNovaQuantidade = ItemCompraRequestDTO.builder()
                .produto(produto)
                .preco(new BigDecimal("5.50"))  // Mesmo preço
                .quantidade(30)
                .build();

        ItemCompraResponseDTO responseNovaQuantidade = ItemCompraResponseDTO.builder()
                .idItemCompra(1)
                .preco(new BigDecimal("5.50"))
                .quantidade(30)
                .build();

        when(itemCompraService.atualizar(eq(1), any(ItemCompraRequestDTO.class)))
                .thenReturn(responseNovaQuantidade);

        // Act & Assert
        mockMvc.perform(put("/v1/itemCompras/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestNovaQuantidade)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preco").value(5.50))
                .andExpect(jsonPath("$.quantidade").value(30));

        verify(itemCompraService, times(1)).atualizar(eq(1), any(ItemCompraRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao atualizar item de compra com preço inválido")
    void deveRetornar400AoAtualizarItemCompraComPrecoInvalido() throws Exception {
        // Arrange
        ItemCompraRequestDTO requestInvalido = ItemCompraRequestDTO.builder()
                .produto(produto)
                .preco(new BigDecimal("-10.00"))  // Preço negativo
                .quantidade(10)
                .build();

        // Act & Assert
        mockMvc.perform(put("/v1/itemCompras/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(itemCompraService, never()).atualizar(any(Integer.class), any(ItemCompraRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao atualizar item de compra com quantidade zero")
    void deveRetornar400AoAtualizarItemCompraComQuantidadeZero() throws Exception {
        // Arrange
        ItemCompraRequestDTO requestInvalido = ItemCompraRequestDTO.builder()
                .produto(produto)
                .preco(new BigDecimal("5.50"))
                .quantidade(0)  // Quantidade zero (deve ser mínimo 1)
                .build();

        // Act & Assert
        mockMvc.perform(put("/v1/itemCompras/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(itemCompraService, never()).atualizar(any(Integer.class), any(ItemCompraRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao atualizar item de compra com quantidade negativa")
    void deveRetornar400AoAtualizarItemCompraComQuantidadeNegativa() throws Exception {
        // Arrange
        ItemCompraRequestDTO requestInvalido = ItemCompraRequestDTO.builder()
                .produto(produto)
                .preco(new BigDecimal("5.50"))
                .quantidade(-10)  // Quantidade negativa
                .build();

        // Act & Assert
        mockMvc.perform(put("/v1/itemCompras/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(itemCompraService, never()).atualizar(any(Integer.class), any(ItemCompraRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar erro ao atualizar item de compra inexistente")
    void deveRetornarErroAoAtualizarItemCompraInexistente() throws Exception {
        // Arrange
        when(itemCompraService.atualizar(eq(999), any(ItemCompraRequestDTO.class)))
                .thenThrow(new RuntimeException("Item Compra não encontrado com ID: 999"));

        // Act & Assert
        mockMvc.perform(put("/v1/itemCompras/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemCompraRequest)))
                .andExpect(status().is5xxServerError());

        verify(itemCompraService, times(1)).atualizar(eq(999), any(ItemCompraRequestDTO.class));
    }

    // ========== TESTES DE EXCLUSÃO ==========

    @Test
    @DisplayName("Deve excluir item de compra com sucesso")
    void deveExcluirItemCompraComSucesso() throws Exception {
        // Arrange
        doNothing().when(itemCompraService).excluir(1);

        // Act & Assert
        mockMvc.perform(delete("/v1/itemCompras/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(itemCompraService, times(1)).excluir(1);
    }

    @Test
    @DisplayName("Deve retornar erro ao excluir item de compra inexistente")
    void deveRetornarErroAoExcluirItemCompraInexistente() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Item Compra não encontrado com ID: 999"))
                .when(itemCompraService).excluir(999);

        // Act & Assert
        mockMvc.perform(delete("/v1/itemCompras/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(itemCompraService, times(1)).excluir(999);
    }

    // ========== TESTES COM DIFERENTES VALORES ==========

    @Test
    @DisplayName("Deve criar item de compra com preço mínimo válido")
    void deveCriarItemCompraComPrecoMinimoValido() throws Exception {
        // Arrange
        ItemCompraRequestDTO requestPrecoMinimo = ItemCompraRequestDTO.builder()
                .produto(produto)
                .preco(new BigDecimal("0.01"))  // Preço mínimo válido
                .quantidade(10)
                .build();

        ItemCompraResponseDTO responsePrecoMinimo = ItemCompraResponseDTO.builder()
                .idItemCompra(3)
                .preco(new BigDecimal("0.01"))
                .quantidade(10)
                .build();

        when(itemCompraService.criar(any(ItemCompraRequestDTO.class))).thenReturn(responsePrecoMinimo);

        // Act & Assert
        mockMvc.perform(post("/v1/itemCompras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestPrecoMinimo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.preco").value(0.01));

        verify(itemCompraService, times(1)).criar(any(ItemCompraRequestDTO.class));
    }

    @Test
    @DisplayName("Deve criar item de compra com quantidade grande")
    void deveCriarItemCompraComQuantidadeGrande() throws Exception {
        // Arrange
        ItemCompraRequestDTO requestQuantidadeGrande = ItemCompraRequestDTO.builder()
                .produto(produto)
                .preco(new BigDecimal("5.50"))
                .quantidade(1000)  // Quantidade grande
                .build();

        ItemCompraResponseDTO responseQuantidadeGrande = ItemCompraResponseDTO.builder()
                .idItemCompra(4)
                .preco(new BigDecimal("5.50"))
                .quantidade(1000)
                .build();

        when(itemCompraService.criar(any(ItemCompraRequestDTO.class))).thenReturn(responseQuantidadeGrande);

        // Act & Assert
        mockMvc.perform(post("/v1/itemCompras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestQuantidadeGrande)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quantidade").value(1000));

        verify(itemCompraService, times(1)).criar(any(ItemCompraRequestDTO.class));
    }
}