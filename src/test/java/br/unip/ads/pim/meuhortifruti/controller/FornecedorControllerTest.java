package br.unip.ads.pim.meuhortifruti.controller;

import br.unip.ads.pim.meuhortifruti.dto.FornecedorRequestDTO;
import br.unip.ads.pim.meuhortifruti.dto.FornecedorResponseDTO;
import br.unip.ads.pim.meuhortifruti.service.FornecedorService;
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

@WebMvcTest(FornecedorController.class)
@AutoConfigureMockMvc(addFilters = false)  // Desabilita filtros de segurança
@DisplayName("Testes do FornecedorController")
public class FornecedorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FornecedorService fornecedorService;

    private FornecedorResponseDTO fornecedorResponse;
    private FornecedorRequestDTO fornecedorRequest;

    @BeforeEach
    void setUp() {
        fornecedorResponse = FornecedorResponseDTO.builder()
                .idFornecedor(1)
                .nome("Fornecedor Frutas Ltda")
                .cnpj("12345678901234")
                .telefone("11987654321")
                .email("contato@frutas.com")
                .endereco("Rua das Frutas, 123")
                .produtosFornecidos("Laranjas, Maçãs, Bananas")
                .build();

        fornecedorRequest = FornecedorRequestDTO.builder()
                .nome("Fornecedor Frutas Ltda")
                .cnpj("12345678901234")
                .telefone("11987654321")
                .email("contato@frutas.com")
                .endereco("Rua das Frutas, 123")
                .produtosFornecidos("Laranjas, Maçãs, Bananas")
                .build();
    }

    // ========== TESTES DE LISTAGEM ==========

    @Test
    @DisplayName("Deve listar todos os fornecedores com sucesso")
    void deveListarTodosFornecedores() throws Exception {
        // Arrange
        FornecedorResponseDTO fornecedor2 = FornecedorResponseDTO.builder()
                .idFornecedor(2)
                .nome("Fornecedor Verduras Ltda")
                .cnpj("98765432109876")
                .telefone("11912345678")
                .email("contato@verduras.com")
                .endereco("Rua das Verduras, 456")
                .produtosFornecidos("Alface, Tomate, Cenoura")
                .build();

        List<FornecedorResponseDTO> fornecedores = Arrays.asList(fornecedorResponse, fornecedor2);
        when(fornecedorService.listarTodas()).thenReturn(fornecedores);

        // Act & Assert
        mockMvc.perform(get("/v1/fornecedores")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].idFornecedor").value(1))
                .andExpect(jsonPath("$[0].nome").value("Fornecedor Frutas Ltda"))
                .andExpect(jsonPath("$[0].email").value("contato@frutas.com"))
                .andExpect(jsonPath("$[1].idFornecedor").value(2))
                .andExpect(jsonPath("$[1].nome").value("Fornecedor Verduras Ltda"))
                .andExpect(jsonPath("$[1].email").value("contato@verduras.com"));

        verify(fornecedorService, times(1)).listarTodas();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver fornecedores")
    void deveRetornarListaVaziaQuandoNaoHouverFornecedores() throws Exception {
        // Arrange
        when(fornecedorService.listarTodas()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/v1/fornecedores")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(fornecedorService, times(1)).listarTodas();
    }

    // ========== TESTES DE BUSCA POR ID ==========

    @Test
    @DisplayName("Deve buscar fornecedor por ID com sucesso")
    void deveBuscarFornecedorPorId() throws Exception {
        // Arrange
        when(fornecedorService.buscarPorId(1)).thenReturn(fornecedorResponse);

        // Act & Assert
        mockMvc.perform(get("/v1/fornecedores/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idFornecedor").value(1))
                .andExpect(jsonPath("$.nome").value("Fornecedor Frutas Ltda"))
                .andExpect(jsonPath("$.cnpj").value("12345678901234"))
                .andExpect(jsonPath("$.telefone").value("11987654321"))
                .andExpect(jsonPath("$.email").value("contato@frutas.com"))
                .andExpect(jsonPath("$.endereco").value("Rua das Frutas, 123"))
                .andExpect(jsonPath("$.produtosFornecidos").value("Laranjas, Maçãs, Bananas"));

        verify(fornecedorService, times(1)).buscarPorId(1);
    }

    @Test
    @DisplayName("Deve retornar erro quando fornecedor não for encontrado")
    void deveRetornarErroQuandoFornecedorNaoForEncontrado() throws Exception {
        // Arrange
        when(fornecedorService.buscarPorId(999))
                .thenThrow(new RuntimeException("Fornecedor não encontrado com ID: 999"));

        // Act & Assert
        mockMvc.perform(get("/v1/fornecedores/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(fornecedorService, times(1)).buscarPorId(999);
    }

    // ========== TESTES DE CRIAÇÃO ==========

    @Test
    @DisplayName("Deve criar fornecedor com sucesso")
    void deveCriarFornecedorComSucesso() throws Exception {
        // Arrange
        when(fornecedorService.criar(any(FornecedorRequestDTO.class))).thenReturn(fornecedorResponse);

        // Act & Assert
        mockMvc.perform(post("/v1/fornecedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fornecedorRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idFornecedor").value(1))
                .andExpect(jsonPath("$.nome").value("Fornecedor Frutas Ltda"))
                .andExpect(jsonPath("$.cnpj").value("12345678901234"))
                .andExpect(jsonPath("$.telefone").value("11987654321"))
                .andExpect(jsonPath("$.email").value("contato@frutas.com"))
                .andExpect(jsonPath("$.endereco").value("Rua das Frutas, 123"))
                .andExpect(jsonPath("$.produtosFornecidos").value("Laranjas, Maçãs, Bananas"));

        verify(fornecedorService, times(1)).criar(any(FornecedorRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar fornecedor com nome nulo")
    void deveRetornar400AoCriarFornecedorComNomeNulo() throws Exception {
        // Arrange
        FornecedorRequestDTO requestInvalido = FornecedorRequestDTO.builder()
                .nome(null)
                .cnpj("12345678901234")
                .telefone("11987654321")
                .email("contato@frutas.com")
                .endereco("Rua das Frutas, 123")
                .produtosFornecidos("Laranjas, Maçãs, Bananas")
                .build();

        // Act & Assert
        mockMvc.perform(post("/v1/fornecedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(fornecedorService, never()).criar(any(FornecedorRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar fornecedor com CNPJ inválido")
    void deveRetornar400AoCriarFornecedorComCnpjInvalido() throws Exception {
        // Arrange
        FornecedorRequestDTO requestInvalido = FornecedorRequestDTO.builder()
                .nome("Fornecedor Frutas Ltda")
                .cnpj("123")  // CNPJ com menos de 14 dígitos
                .telefone("11987654321")
                .email("contato@frutas.com")
                .endereco("Rua das Frutas, 123")
                .produtosFornecidos("Laranjas, Maçãs, Bananas")
                .build();

        // Act & Assert
        mockMvc.perform(post("/v1/fornecedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(fornecedorService, never()).criar(any(FornecedorRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar fornecedor com telefone inválido")
    void deveRetornar400AoCriarFornecedorComTelefoneInvalido() throws Exception {
        // Arrange
        FornecedorRequestDTO requestInvalido = FornecedorRequestDTO.builder()
                .nome("Fornecedor Frutas Ltda")
                .cnpj("12345678901234")
                .telefone("123")  // Telefone com menos de 9 dígitos
                .email("contato@frutas.com")
                .endereco("Rua das Frutas, 123")
                .produtosFornecidos("Laranjas, Maçãs, Bananas")
                .build();

        // Act & Assert
        mockMvc.perform(post("/v1/fornecedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(fornecedorService, never()).criar(any(FornecedorRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar fornecedor com email inválido")
    void deveRetornar400AoCriarFornecedorComEmailInvalido() throws Exception {
        // Arrange
        FornecedorRequestDTO requestInvalido = FornecedorRequestDTO.builder()
                .nome("Fornecedor Frutas Ltda")
                .cnpj("12345678901234")
                .telefone("11987654321")
                .email("email-invalido")  // Email sem @
                .endereco("Rua das Frutas, 123")
                .produtosFornecidos("Laranjas, Maçãs, Bananas")
                .build();

        // Act & Assert
        mockMvc.perform(post("/v1/fornecedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(fornecedorService, never()).criar(any(FornecedorRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar fornecedor com endereço vazio")
    void deveRetornar400AoCriarFornecedorComEnderecoVazio() throws Exception {
        // Arrange
        FornecedorRequestDTO requestInvalido = FornecedorRequestDTO.builder()
                .nome("Fornecedor Frutas Ltda")
                .cnpj("12345678901234")
                .telefone("11987654321")
                .email("contato@frutas.com")
                .endereco("")  // Endereço vazio
                .produtosFornecidos("Laranjas, Maçãs, Bananas")
                .build();

        // Act & Assert
        mockMvc.perform(post("/v1/fornecedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(fornecedorService, never()).criar(any(FornecedorRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar fornecedor com produtos fornecidos vazio")
    void deveRetornar400AoCriarFornecedorComProdutosFornecidosVazio() throws Exception {
        // Arrange
        FornecedorRequestDTO requestInvalido = FornecedorRequestDTO.builder()
                .nome("Fornecedor Frutas Ltda")
                .cnpj("12345678901234")
                .telefone("11987654321")
                .email("contato@frutas.com")
                .endereco("Rua das Frutas, 123")
                .produtosFornecidos("")  // Produtos fornecidos vazio
                .build();

        // Act & Assert
        mockMvc.perform(post("/v1/fornecedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(fornecedorService, never()).criar(any(FornecedorRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar erro ao criar fornecedor com email duplicado")
    void deveRetornarErroAoCriarFornecedorComEmailDuplicado() throws Exception {
        // Arrange
        when(fornecedorService.criar(any(FornecedorRequestDTO.class)))
                .thenThrow(new RuntimeException("Fornecedor com email 'contato@frutas.com' já existe"));

        // Act & Assert
        mockMvc.perform(post("/v1/fornecedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fornecedorRequest)))
                .andExpect(status().is5xxServerError());

        verify(fornecedorService, times(1)).criar(any(FornecedorRequestDTO.class));
    }

    // ========== TESTES DE ATUALIZAÇÃO ==========

    @Test
    @DisplayName("Deve atualizar fornecedor com sucesso")
    void deveAtualizarFornecedorComSucesso() throws Exception {
        // Arrange
        FornecedorRequestDTO requestAtualizado = FornecedorRequestDTO.builder()
                .nome("Fornecedor Frutas Tropicais Ltda")
                .cnpj("12345678901234")
                .telefone("11999887766")
                .email("contato@frutas.com")
                .endereco("Avenida das Frutas, 999")
                .produtosFornecidos("Manga, Abacaxi, Coco")
                .build();

        FornecedorResponseDTO responseAtualizado = FornecedorResponseDTO.builder()
                .idFornecedor(1)
                .nome("Fornecedor Frutas Tropicais Ltda")
                .cnpj("12345678901234")
                .telefone("11999887766")
                .email("contato@frutas.com")
                .endereco("Avenida das Frutas, 999")
                .produtosFornecidos("Manga, Abacaxi, Coco")
                .build();

        when(fornecedorService.atualizar(eq(1), any(FornecedorRequestDTO.class)))
                .thenReturn(responseAtualizado);

        // Act & Assert
        mockMvc.perform(put("/v1/fornecedores/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestAtualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idFornecedor").value(1))
                .andExpect(jsonPath("$.nome").value("Fornecedor Frutas Tropicais Ltda"))
                .andExpect(jsonPath("$.telefone").value("11999887766"))
                .andExpect(jsonPath("$.endereco").value("Avenida das Frutas, 999"))
                .andExpect(jsonPath("$.produtosFornecidos").value("Manga, Abacaxi, Coco"));

        verify(fornecedorService, times(1)).atualizar(eq(1), any(FornecedorRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao atualizar fornecedor com dados inválidos")
    void deveRetornar400AoAtualizarFornecedorComDadosInvalidos() throws Exception {
        // Arrange
        FornecedorRequestDTO requestInvalido = FornecedorRequestDTO.builder()
                .nome("")  // Nome vazio
                .cnpj("12345678901234")
                .telefone("11987654321")
                .email("contato@frutas.com")
                .endereco("Rua das Frutas, 123")
                .produtosFornecidos("Laranjas, Maçãs, Bananas")
                .build();

        // Act & Assert
        mockMvc.perform(put("/v1/fornecedores/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(fornecedorService, never()).atualizar(any(Integer.class), any(FornecedorRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar erro ao atualizar fornecedor inexistente")
    void deveRetornarErroAoAtualizarFornecedorInexistente() throws Exception {
        // Arrange
        when(fornecedorService.atualizar(eq(999), any(FornecedorRequestDTO.class)))
                .thenThrow(new RuntimeException("Fornecedor não encontrado com ID: 999"));

        // Act & Assert
        mockMvc.perform(put("/v1/fornecedores/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fornecedorRequest)))
                .andExpect(status().is5xxServerError());

        verify(fornecedorService, times(1)).atualizar(eq(999), any(FornecedorRequestDTO.class));
    }

    // ========== TESTES DE EXCLUSÃO ==========

    @Test
    @DisplayName("Deve excluir fornecedor com sucesso")
    void deveExcluirFornecedorComSucesso() throws Exception {
        // Arrange
        doNothing().when(fornecedorService).excluir(1);

        // Act & Assert
        mockMvc.perform(delete("/v1/fornecedores/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(fornecedorService, times(1)).excluir(1);
    }

    @Test
    @DisplayName("Deve retornar erro ao excluir fornecedor inexistente")
    void deveRetornarErroAoExcluirFornecedorInexistente() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Fornecedor não encontrado com ID: 999"))
                .when(fornecedorService).excluir(999);

        // Act & Assert
        mockMvc.perform(delete("/v1/fornecedores/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(fornecedorService, times(1)).excluir(999);
    }
}