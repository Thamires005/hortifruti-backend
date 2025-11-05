package br.unip.ads.pim.meuhortifruti.controller;

import br.unip.ads.pim.meuhortifruti.dto.PagamentoRequestDTO;
import br.unip.ads.pim.meuhortifruti.dto.PagamentoResponseDTO;
import br.unip.ads.pim.meuhortifruti.service.PagamentoService;
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

@WebMvcTest(PagamentoController.class)
@AutoConfigureMockMvc(addFilters = false)  // Desabilita filtros de segurança
@DisplayName("Testes do PagamentoController")
public class PagamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PagamentoService pagamentoService;

    private PagamentoResponseDTO pagamentoResponse;
    private PagamentoRequestDTO pagamentoRequest;

    @BeforeEach
    void setUp() {
        pagamentoResponse = PagamentoResponseDTO.builder()
                .idPagamento(1)
                .valor(new BigDecimal("150.00"))
                .formaPagamento("CARTAO_CREDITO")
                .statusPagamento("APROVADO")
                .compra(null)
                .build();

        pagamentoRequest = PagamentoRequestDTO.builder()
                .idCompra(100)
                .valor(new BigDecimal("150.00"))
                .formaPagamento("CARTAO_CREDITO")
                .statusPagamento("APROVADO")
                .build();
    }

    // ========== TESTES DE BUSCA POR ID ==========

    @Test
    @DisplayName("Deve buscar pagamento por ID com sucesso")
    void deveBuscarPagamentoPorId() throws Exception {
        // Arrange
        when(pagamentoService.buscarPorId(1)).thenReturn(pagamentoResponse);

        // Act & Assert
        mockMvc.perform(get("/v1/pagamentos/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPagamento").value(1))
                .andExpect(jsonPath("$.valor").value(150.00))
                .andExpect(jsonPath("$.formaPagamento").value("CARTAO_CREDITO"))
                .andExpect(jsonPath("$.statusPagamento").value("APROVADO"));

        verify(pagamentoService, times(1)).buscarPorId(1);
    }

    @Test
    @DisplayName("Deve retornar erro quando pagamento não for encontrado")
    void deveRetornarErroQuandoPagamentoNaoForEncontrado() throws Exception {
        // Arrange
        when(pagamentoService.buscarPorId(999))
                .thenThrow(new RuntimeException("Pagamento não encontrado com ID: 999"));

        // Act & Assert
        mockMvc.perform(get("/v1/pagamentos/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(pagamentoService, times(1)).buscarPorId(999);
    }

    // ========== TESTES DE CRIAÇÃO ==========

    @Test
    @DisplayName("Deve criar pagamento com sucesso")
    void deveCriarPagamentoComSucesso() throws Exception {
        // Arrange
        when(pagamentoService.criar(any(PagamentoRequestDTO.class))).thenReturn(pagamentoResponse);

        // Act & Assert
        mockMvc.perform(post("/v1/pagamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pagamentoRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idPagamento").value(1))
                .andExpect(jsonPath("$.valor").value(150.00))
                .andExpect(jsonPath("$.formaPagamento").value("CARTAO_CREDITO"))
                .andExpect(jsonPath("$.statusPagamento").value("APROVADO"));

        verify(pagamentoService, times(1)).criar(any(PagamentoRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar pagamento com idCompra nulo")
    void deveRetornar400AoCriarPagamentoComIdCompraNulo() throws Exception {
        // Arrange
        PagamentoRequestDTO requestInvalido = PagamentoRequestDTO.builder()
                .idCompra(null)  // idCompra nulo
                .valor(new BigDecimal("150.00"))
                .formaPagamento("CARTAO_CREDITO")
                .statusPagamento("APROVADO")
                .build();

        // Act & Assert
        mockMvc.perform(post("/v1/pagamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(pagamentoService, never()).criar(any(PagamentoRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar pagamento com valor nulo")
    void deveRetornar400AoCriarPagamentoComValorNulo() throws Exception {
        // Arrange
        PagamentoRequestDTO requestInvalido = PagamentoRequestDTO.builder()
                .idCompra(100)
                .valor(null)  // Valor nulo
                .formaPagamento("CARTAO_CREDITO")
                .statusPagamento("APROVADO")
                .build();

        // Act & Assert
        mockMvc.perform(post("/v1/pagamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(pagamentoService, never()).criar(any(PagamentoRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar pagamento com valor zero")
    void deveRetornar400AoCriarPagamentoComValorZero() throws Exception {
        // Arrange
        PagamentoRequestDTO requestInvalido = PagamentoRequestDTO.builder()
                .idCompra(100)
                .valor(new BigDecimal("0.00"))  // Valor zero
                .formaPagamento("CARTAO_CREDITO")
                .statusPagamento("APROVADO")
                .build();

        // Act & Assert
        mockMvc.perform(post("/v1/pagamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(pagamentoService, never()).criar(any(PagamentoRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar pagamento com valor negativo")
    void deveRetornar400AoCriarPagamentoComValorNegativo() throws Exception {
        // Arrange
        PagamentoRequestDTO requestInvalido = PagamentoRequestDTO.builder()
                .idCompra(100)
                .valor(new BigDecimal("-50.00"))  // Valor negativo
                .formaPagamento("CARTAO_CREDITO")
                .statusPagamento("APROVADO")
                .build();

        // Act & Assert
        mockMvc.perform(post("/v1/pagamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(pagamentoService, never()).criar(any(PagamentoRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar pagamento com forma de pagamento nula")
    void deveRetornar400AoCriarPagamentoComFormaPagamentoNula() throws Exception {
        // Arrange
        PagamentoRequestDTO requestInvalido = PagamentoRequestDTO.builder()
                .idCompra(100)
                .valor(new BigDecimal("150.00"))
                .formaPagamento(null)  // Forma de pagamento nula
                .statusPagamento("APROVADO")
                .build();

        // Act & Assert
        mockMvc.perform(post("/v1/pagamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(pagamentoService, never()).criar(any(PagamentoRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar pagamento com status de pagamento vazio")
    void deveRetornar400AoCriarPagamentoComStatusPagamentoVazio() throws Exception {
        // Arrange
        PagamentoRequestDTO requestInvalido = PagamentoRequestDTO.builder()
                .idCompra(100)
                .valor(new BigDecimal("150.00"))
                .formaPagamento("CARTAO_CREDITO")
                .statusPagamento("")  // Status vazio
                .build();

        // Act & Assert
        mockMvc.perform(post("/v1/pagamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(pagamentoService, never()).criar(any(PagamentoRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar pagamento com status de pagamento nulo")
    void deveRetornar400AoCriarPagamentoComStatusPagamentoNulo() throws Exception {
        // Arrange
        PagamentoRequestDTO requestInvalido = PagamentoRequestDTO.builder()
                .idCompra(100)
                .valor(new BigDecimal("150.00"))
                .formaPagamento("CARTAO_CREDITO")
                .statusPagamento(null)  // Status nulo
                .build();

        // Act & Assert
        mockMvc.perform(post("/v1/pagamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(pagamentoService, never()).criar(any(PagamentoRequestDTO.class));
    }

    @Test
    @DisplayName("Deve criar pagamento com valor mínimo válido")
    void deveCriarPagamentoComValorMinimoValido() throws Exception {
        // Arrange
        PagamentoRequestDTO requestValorMinimo = PagamentoRequestDTO.builder()
                .idCompra(100)
                .valor(new BigDecimal("0.01"))  // Valor mínimo válido
                .formaPagamento("PIX")
                .statusPagamento("PENDENTE")
                .build();

        PagamentoResponseDTO responseValorMinimo = PagamentoResponseDTO.builder()
                .idPagamento(2)
                .valor(new BigDecimal("0.01"))
                .formaPagamento("PIX")
                .statusPagamento("PENDENTE")
                .build();

        when(pagamentoService.criar(any(PagamentoRequestDTO.class))).thenReturn(responseValorMinimo);

        // Act & Assert
        mockMvc.perform(post("/v1/pagamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValorMinimo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.valor").value(0.01));

        verify(pagamentoService, times(1)).criar(any(PagamentoRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar erro ao criar pagamento com idCompra duplicado")
    void deveRetornarErroAoCriarPagamentoComIdCompraDuplicado() throws Exception {
        // Arrange
        when(pagamentoService.criar(any(PagamentoRequestDTO.class)))
                .thenThrow(new RuntimeException("Pagamento com idCompra '100' já existe"));

        // Act & Assert
        mockMvc.perform(post("/v1/pagamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pagamentoRequest)))
                .andExpect(status().is5xxServerError());

        verify(pagamentoService, times(1)).criar(any(PagamentoRequestDTO.class));
    }

    // ========== TESTES DE ATUALIZAÇÃO ==========

    @Test
    @DisplayName("Deve atualizar pagamento com sucesso")
    void deveAtualizarPagamentoComSucesso() throws Exception {
        // Arrange
        PagamentoRequestDTO requestAtualizado = PagamentoRequestDTO.builder()
                .idCompra(100)
                .valor(new BigDecimal("180.00"))
                .formaPagamento("CARTAO_DEBITO")
                .statusPagamento("RECUSADO")
                .build();

        PagamentoResponseDTO responseAtualizado = PagamentoResponseDTO.builder()
                .idPagamento(1)
                .valor(new BigDecimal("180.00"))
                .formaPagamento("CARTAO_DEBITO")
                .statusPagamento("RECUSADO")
                .build();

        when(pagamentoService.atualizar(eq(1), any(PagamentoRequestDTO.class)))
                .thenReturn(responseAtualizado);

        // Act & Assert
        mockMvc.perform(put("/v1/pagamentos/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestAtualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPagamento").value(1))
                .andExpect(jsonPath("$.valor").value(180.00))
                .andExpect(jsonPath("$.formaPagamento").value("CARTAO_DEBITO"))
                .andExpect(jsonPath("$.statusPagamento").value("RECUSADO"));

        verify(pagamentoService, times(1)).atualizar(eq(1), any(PagamentoRequestDTO.class));
    }

    @Test
    @DisplayName("Deve atualizar apenas o status do pagamento")
    void deveAtualizarApenasStatusPagamento() throws Exception {
        // Arrange
        PagamentoRequestDTO requestNovoStatus = PagamentoRequestDTO.builder()
                .idCompra(100)
                .valor(new BigDecimal("150.00"))
                .formaPagamento("CARTAO_CREDITO")
                .statusPagamento("CANCELADO")
                .build();

        PagamentoResponseDTO responseNovoStatus = PagamentoResponseDTO.builder()
                .idPagamento(1)
                .valor(new BigDecimal("150.00"))
                .formaPagamento("CARTAO_CREDITO")
                .statusPagamento("CANCELADO")
                .build();

        when(pagamentoService.atualizar(eq(1), any(PagamentoRequestDTO.class)))
                .thenReturn(responseNovoStatus);

        // Act & Assert
        mockMvc.perform(put("/v1/pagamentos/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestNovoStatus)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusPagamento").value("CANCELADO"));

        verify(pagamentoService, times(1)).atualizar(eq(1), any(PagamentoRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao atualizar pagamento com dados inválidos")
    void deveRetornar400AoAtualizarPagamentoComDadosInvalidos() throws Exception {
        // Arrange
        PagamentoRequestDTO requestInvalido = PagamentoRequestDTO.builder()
                .idCompra(100)
                .valor(new BigDecimal("-100.00"))  // Valor negativo
                .formaPagamento("CARTAO_CREDITO")
                .statusPagamento("APROVADO")
                .build();

        // Act & Assert
        mockMvc.perform(put("/v1/pagamentos/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(pagamentoService, never()).atualizar(any(Integer.class), any(PagamentoRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar erro ao atualizar pagamento inexistente")
    void deveRetornarErroAoAtualizarPagamentoInexistente() throws Exception {
        // Arrange
        when(pagamentoService.atualizar(eq(999), any(PagamentoRequestDTO.class)))
                .thenThrow(new RuntimeException("Pagamento não encontrado com ID: 999"));

        // Act & Assert
        mockMvc.perform(put("/v1/pagamentos/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pagamentoRequest)))
                .andExpect(status().is5xxServerError());

        verify(pagamentoService, times(1)).atualizar(eq(999), any(PagamentoRequestDTO.class));
    }

    // ========== TESTES DE EXCLUSÃO ==========

    @Test
    @DisplayName("Deve excluir pagamento com sucesso")
    void deveExcluirPagamentoComSucesso() throws Exception {
        // Arrange
        doNothing().when(pagamentoService).excluir(1);

        // Act & Assert
        mockMvc.perform(delete("/v1/pagamentos/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(pagamentoService, times(1)).excluir(1);
    }

    @Test
    @DisplayName("Deve retornar erro ao excluir pagamento inexistente")
    void deveRetornarErroAoExcluirPagamentoInexistente() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Pagamento não encontrado com ID: 999"))
                .when(pagamentoService).excluir(999);

        // Act & Assert
        mockMvc.perform(delete("/v1/pagamentos/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(pagamentoService, times(1)).excluir(999);
    }

    // ========== TESTES COM DIFERENTES FORMAS DE PAGAMENTO ==========

    @Test
    @DisplayName("Deve criar pagamento com forma de pagamento PIX")
    void deveCriarPagamentoComFormaPagamentoPix() throws Exception {
        // Arrange
        PagamentoRequestDTO requestPix = PagamentoRequestDTO.builder()
                .idCompra(101)
                .valor(new BigDecimal("200.00"))
                .formaPagamento("PIX")
                .statusPagamento("APROVADO")
                .build();

        PagamentoResponseDTO responsePix = PagamentoResponseDTO.builder()
                .idPagamento(3)
                .valor(new BigDecimal("200.00"))
                .formaPagamento("PIX")
                .statusPagamento("APROVADO")
                .build();

        when(pagamentoService.criar(any(PagamentoRequestDTO.class))).thenReturn(responsePix);

        // Act & Assert
        mockMvc.perform(post("/v1/pagamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestPix)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.formaPagamento").value("PIX"));

        verify(pagamentoService, times(1)).criar(any(PagamentoRequestDTO.class));
    }

    @Test
    @DisplayName("Deve criar pagamento com forma de pagamento DINHEIRO")
    void deveCriarPagamentoComFormaPagamentoDinheiro() throws Exception {
        // Arrange
        PagamentoRequestDTO requestDinheiro = PagamentoRequestDTO.builder()
                .idCompra(102)
                .valor(new BigDecimal("50.00"))
                .formaPagamento("DINHEIRO")
                .statusPagamento("APROVADO")
                .build();

        PagamentoResponseDTO responseDinheiro = PagamentoResponseDTO.builder()
                .idPagamento(4)
                .valor(new BigDecimal("50.00"))
                .formaPagamento("DINHEIRO")
                .statusPagamento("APROVADO")
                .build();

        when(pagamentoService.criar(any(PagamentoRequestDTO.class))).thenReturn(responseDinheiro);

        // Act & Assert
        mockMvc.perform(post("/v1/pagamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDinheiro)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.formaPagamento").value("DINHEIRO"));

        verify(pagamentoService, times(1)).criar(any(PagamentoRequestDTO.class));
    }
}