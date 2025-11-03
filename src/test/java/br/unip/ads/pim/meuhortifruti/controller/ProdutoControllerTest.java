package br.unip.ads.pim.meuhortifruti.controller;

import br.unip.ads.pim.meuhortifruti.dto.ProdutoRequestDTO;
import br.unip.ads.pim.meuhortifruti.dto.ProdutoResponseDTO;
import br.unip.ads.pim.meuhortifruti.service.ProdutoService;
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
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProdutoController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Testes do ProdutoController")
public class ProdutoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProdutoService produtoService;

    private ProdutoResponseDTO produtoResponse;
    private ProdutoRequestDTO produtoRequest;

    @BeforeEach
    void setUp() {
        produtoResponse = ProdutoResponseDTO.builder()
                .idProduto(1)
                .nome("Maçã")
                .preco(new BigDecimal("5.50"))
                .quantidadeEstoque(100)
                .dtValidade(LocalDate.now().plusMonths(2))
                .dataEntrega(LocalDate.now())
                .build();

        produtoRequest = ProdutoRequestDTO.builder()
                .nome("Maçã")
                .preco(new BigDecimal("5.50"))
                .quantidadeEstoque(100)
                .dtValidade(LocalDate.now().plusMonths(2))
                .dataEntrega(LocalDate.now())
                .build();
    }

    // ========== TESTES DE LISTAGEM ==========

    @Test
    @DisplayName("Deve listar todos os produtos com sucesso")
    void deveListarTodosProdutos() throws Exception {
        // Arrange
        ProdutoResponseDTO produto2 = ProdutoResponseDTO.builder()
                .idProduto(2)
                .nome("Banana")
                .preco(new BigDecimal("3.20"))
                .quantidadeEstoque(150)
                .dtValidade(LocalDate.now().plusMonths(1))
                .dataEntrega(LocalDate.now())
                .build();

        List<ProdutoResponseDTO> produtos = Arrays.asList(produtoResponse, produto2);
        when(produtoService.listarTodas()).thenReturn(produtos);

        // Act & Assert
        mockMvc.perform(get("/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].idProduto").value(1))
                .andExpect(jsonPath("$[0].nome").value("Maçã"))
                .andExpect(jsonPath("$[1].idProduto").value(2))
                .andExpect(jsonPath("$[1].nome").value("Banana"));

        verify(produtoService, times(1)).listarTodas();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver produtos")
    void deveRetornarListaVaziaQuandoNaoHouverProdutos() throws Exception {
        // Arrange
        when(produtoService.listarTodas()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(produtoService, times(1)).listarTodas();
    }

    // ========== TESTES DE BUSCA POR ID ==========

    @Test
    @DisplayName("Deve buscar produto por ID com sucesso")
    void deveBuscarProdutoPorId() throws Exception {
        // Arrange
        when(produtoService.buscarPorId(1)).thenReturn(produtoResponse);

        // Act & Assert
        mockMvc.perform(get("/v1/produtos/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProduto").value(1))
                .andExpect(jsonPath("$.nome").value("Maçã"))
                .andExpect(jsonPath("$.preco").value(5.50))
                .andExpect(jsonPath("$.quantidadeEstoque").value(100));

        verify(produtoService, times(1)).buscarPorId(1);
    }

    @Test
    @DisplayName("Deve retornar erro quando produto não for encontrado")
    void deveRetornarErroQuandoProdutoNaoForEncontrado() throws Exception {
        // Arrange
        when(produtoService.buscarPorId(999))
                .thenThrow(new RuntimeException("Produto não encontrado com ID: 999"));

        // Act & Assert
        mockMvc.perform(get("/v1/produtos/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(produtoService, times(1)).buscarPorId(999);
    }

    // ========== TESTES DE CRIAÇÃO ==========

    @Test
    @DisplayName("Deve criar produto com sucesso")
    void deveCriarProdutoComSucesso() throws Exception {
        // Arrange
        when(produtoService.criar(any(ProdutoRequestDTO.class))).thenReturn(produtoResponse);

        // Act & Assert
        mockMvc.perform(post("/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(produtoRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idProduto").value(1))
                .andExpect(jsonPath("$.nome").value("Maçã"))
                .andExpect(jsonPath("$.preco").value(5.50));

        verify(produtoService, times(1)).criar(any(ProdutoRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar produto com nome nulo")
    void deveRetornar400AoCriarProdutoComNomeNulo() throws Exception {
        // Arrange - nome nulo viola @NotBlank
        ProdutoRequestDTO requestInvalido = ProdutoRequestDTO.builder()
                .nome(null)
                .preco(new BigDecimal("5.50"))
                .quantidadeEstoque(100)
                .dtValidade(LocalDate.now().plusMonths(2))
                .dataEntrega(LocalDate.now())
                .build();

        // Act & Assert
        mockMvc.perform(post("/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(produtoService, never()).criar(any(ProdutoRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar produto com nome muito curto")
    void deveRetornar400AoCriarProdutoComNomeCurto() throws Exception {
        // Arrange - nome com menos de 3 caracteres viola @Size
        ProdutoRequestDTO requestInvalido = ProdutoRequestDTO.builder()
                .nome("AB")
                .preco(new BigDecimal("5.50"))
                .quantidadeEstoque(100)
                .dtValidade(LocalDate.now().plusMonths(2))
                .dataEntrega(LocalDate.now())
                .build();

        // Act & Assert
        mockMvc.perform(post("/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(produtoService, never()).criar(any(ProdutoRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar produto com preço nulo")
    void deveRetornar400AoCriarProdutoComPrecoNulo() throws Exception {
        // Arrange - preço nulo viola @NotNull
        ProdutoRequestDTO requestInvalido = ProdutoRequestDTO.builder()
                .nome("Maçã")
                .preco(null)
                .quantidadeEstoque(100)
                .dtValidade(LocalDate.now().plusMonths(2))
                .dataEntrega(LocalDate.now())
                .build();

        // Act & Assert
        mockMvc.perform(post("/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(produtoService, never()).criar(any(ProdutoRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar produto com preço zero")
    void deveRetornar400AoCriarProdutoComPrecoZero() throws Exception {
        // Arrange - preço zero viola @DecimalMin("0.01")
        ProdutoRequestDTO requestInvalido = ProdutoRequestDTO.builder()
                .nome("Maçã")
                .preco(BigDecimal.ZERO)
                .quantidadeEstoque(100)
                .dtValidade(LocalDate.now().plusMonths(2))
                .dataEntrega(LocalDate.now())
                .build();

        // Act & Assert
        mockMvc.perform(post("/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(produtoService, never()).criar(any(ProdutoRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar produto com data de validade passada")
    void deveRetornar400AoCriarProdutoComDataValidadePassada() throws Exception {
        // Arrange - data passada viola @Future
        ProdutoRequestDTO requestInvalido = ProdutoRequestDTO.builder()
                .nome("Maçã")
                .preco(new BigDecimal("5.50"))
                .quantidadeEstoque(100)
                .dtValidade(LocalDate.now().minusDays(1))
                .dataEntrega(LocalDate.now())
                .build();

        // Act & Assert
        mockMvc.perform(post("/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(produtoService, never()).criar(any(ProdutoRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar erro ao criar produto com nome duplicado")
    void deveRetornarErroAoCriarProdutoComNomeDuplicado() throws Exception {
        // Arrange
        when(produtoService.criar(any(ProdutoRequestDTO.class)))
                .thenThrow(new RuntimeException("Produto com nome 'Maçã' já existe"));

        // Act & Assert
        mockMvc.perform(post("/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(produtoRequest)))
                .andExpect(status().is5xxServerError());

        verify(produtoService, times(1)).criar(any(ProdutoRequestDTO.class));
    }

    // ========== TESTES DE ATUALIZAÇÃO ==========

    @Test
    @DisplayName("Deve atualizar produto com sucesso")
    void deveAtualizarProdutoComSucesso() throws Exception {
        // Arrange
        ProdutoRequestDTO requestAtualizado = ProdutoRequestDTO.builder()
                .nome("Maçã Gala")
                .preco(new BigDecimal("6.00"))
                .quantidadeEstoque(120)
                .dtValidade(LocalDate.now().plusMonths(3))
                .dataEntrega(LocalDate.now())
                .build();

        ProdutoResponseDTO responseAtualizado = ProdutoResponseDTO.builder()
                .idProduto(1)
                .nome("Maçã Gala")
                .preco(new BigDecimal("6.00"))
                .quantidadeEstoque(120)
                .dtValidade(LocalDate.now().plusMonths(3))
                .dataEntrega(LocalDate.now())
                .build();

        when(produtoService.atualizar(eq(1), any(ProdutoRequestDTO.class)))
                .thenReturn(responseAtualizado);

        // Act & Assert
        mockMvc.perform(put("/v1/produtos/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestAtualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProduto").value(1))
                .andExpect(jsonPath("$.nome").value("Maçã Gala"))
                .andExpect(jsonPath("$.preco").value(6.00))
                .andExpect(jsonPath("$.quantidadeEstoque").value(120));

        verify(produtoService, times(1)).atualizar(eq(1), any(ProdutoRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao atualizar produto com dados inválidos")
    void deveRetornar400AoAtualizarProdutoComDadosInvalidos() throws Exception {
        // Arrange - nome vazio viola @NotBlank
        ProdutoRequestDTO requestInvalido = ProdutoRequestDTO.builder()
                .nome("")
                .preco(new BigDecimal("5.50"))
                .quantidadeEstoque(100)
                .dtValidade(LocalDate.now().plusMonths(2))
                .dataEntrega(LocalDate.now())
                .build();

        // Act & Assert
        mockMvc.perform(put("/v1/produtos/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(produtoService, never()).atualizar(any(Integer.class), any(ProdutoRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar erro ao atualizar produto inexistente")
    void deveRetornarErroAoAtualizarProdutoInexistente() throws Exception {
        // Arrange
        when(produtoService.atualizar(eq(999), any(ProdutoRequestDTO.class)))
                .thenThrow(new RuntimeException("Produto não encontrado com ID: 999"));

        // Act & Assert
        mockMvc.perform(put("/v1/produtos/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(produtoRequest)))
                .andExpect(status().is5xxServerError());

        verify(produtoService, times(1)).atualizar(eq(999), any(ProdutoRequestDTO.class));
    }

    // ========== TESTES DE EXCLUSÃO ==========

    @Test
    @DisplayName("Deve excluir produto com sucesso")
    void deveExcluirProdutoComSucesso() throws Exception {
        // Arrange
        doNothing().when(produtoService).excluir(1);

        // Act & Assert
        mockMvc.perform(delete("/v1/produtos/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(produtoService, times(1)).excluir(1);
    }

    @Test
    @DisplayName("Deve retornar erro ao excluir produto inexistente")
    void deveRetornarErroAoExcluirProdutoInexistente() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Produto não encontrado com ID: 999"))
                .when(produtoService).excluir(999);

        // Act & Assert
        mockMvc.perform(delete("/v1/produtos/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(produtoService, times(1)).excluir(999);
    }
}