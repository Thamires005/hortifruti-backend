package br.unip.ads.pim.meuhortifruti.service;

import br.unip.ads.pim.meuhortifruti.dto.PagamentoRequestDTO;
import br.unip.ads.pim.meuhortifruti.dto.PagamentoResponseDTO;
import br.unip.ads.pim.meuhortifruti.entity.Pagamento;
import br.unip.ads.pim.meuhortifruti.entity.Compra;
import br.unip.ads.pim.meuhortifruti.exception.RecursoDuplicadoException;
import br.unip.ads.pim.meuhortifruti.exception.RecursoNaoEncontradoException;
import br.unip.ads.pim.meuhortifruti.repository.PagamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do PagamentoService")
public class PagamentoServiceTest {

    @Mock
    private PagamentoRepository pagamentoRepository;

    @InjectMocks
    private PagamentoService pagamentoService;

    private Pagamento pagamento;
    private PagamentoRequestDTO requestDTO;
    private Compra compra;

    @BeforeEach
    void setUp() {
        compra = Compra.builder()
                .idCompra(100)
                .statusCompra("PENDENTE")
                .build();

        pagamento = Pagamento.builder()
                .idPagamento(1)
                .compra(compra)
                .valor(new BigDecimal("150.00"))
                .formaPagamento("CARTAO_CREDITO")
                .statusPagamento("APROVADO")
                .build();

        requestDTO = PagamentoRequestDTO.builder()
                .idCompra(100)
                .valor(new BigDecimal("150.00"))
                .formaPagamento("CARTAO_CREDITO")
                .statusPagamento("APROVADO")
                .build();
    }

    // ========== TESTES DE BUSCA POR ID ==========

    @Test
    @DisplayName("Deve buscar pagamento por ID com sucesso")
    void deveBuscarPagamentoPorId() {
        // Arrange
        when(pagamentoRepository.findById(1)).thenReturn(Optional.of(pagamento));

        // Act
        PagamentoResponseDTO resultado = pagamentoService.buscarPorId(1);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdPagamento()).isEqualTo(1);
        assertThat(resultado.getValor()).isEqualByComparingTo(new BigDecimal("150.00"));
        assertThat(resultado.getFormaPagamento()).isEqualTo("CARTAO_CREDITO");
        assertThat(resultado.getStatusPagamento()).isEqualTo("APROVADO");

        verify(pagamentoRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar pagamento inexistente")
    void deveLancarExcecaoAoBuscarPagamentoInexistente() {
        // Arrange
        when(pagamentoRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> pagamentoService.buscarPorId(999))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Pagamento")
                .hasMessageContaining("id")
                .hasMessageContaining("999");

        verify(pagamentoRepository, times(1)).findById(999);
    }

    // ========== TESTES DE CRIAÇÃO ==========

    @Test
    @DisplayName("Deve criar pagamento com sucesso")
    void deveCriarPagamentoComSucesso() {
        // Arrange
        when(pagamentoRepository.existsByIdCompra(100)).thenReturn(false);
        when(pagamentoRepository.save(any(Pagamento.class))).thenReturn(pagamento);

        // Act
        PagamentoResponseDTO resultado = pagamentoService.criar(requestDTO);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdPagamento()).isEqualTo(1);
        assertThat(resultado.getValor()).isEqualByComparingTo(new BigDecimal("150.00"));
        assertThat(resultado.getFormaPagamento()).isEqualTo("CARTAO_CREDITO");
        assertThat(resultado.getStatusPagamento()).isEqualTo("APROVADO");

        verify(pagamentoRepository, times(1)).existsByIdCompra(100);
        verify(pagamentoRepository, times(1)).save(any(Pagamento.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar pagamento com idCompra duplicado")
    void deveLancarExcecaoAoCriarPagamentoComIdCompraDuplicado() {
        // Arrange
        when(pagamentoRepository.existsByIdCompra(100)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> pagamentoService.criar(requestDTO))
                .isInstanceOf(RecursoDuplicadoException.class)
                .hasMessageContaining("Pagamento")
                .hasMessageContaining("id")
                .hasMessageContaining("100");

        verify(pagamentoRepository, times(1)).existsByIdCompra(100);
        verify(pagamentoRepository, never()).save(any(Pagamento.class));
    }

    @Test
    @DisplayName("Deve criar pagamento com forma de pagamento PIX")
    void deveCriarPagamentoComFormaPagamentoPix() {
        // Arrange
        Compra compra2 = Compra.builder()
                .idCompra(101)
                .statusCompra("PENDENTE")
                .build();

        PagamentoRequestDTO requestPix = PagamentoRequestDTO.builder()
                .idCompra(101)
                .valor(new BigDecimal("200.00"))
                .formaPagamento("PIX")
                .statusPagamento("PENDENTE")
                .build();

        Pagamento pagamentoPix = Pagamento.builder()
                .idPagamento(2)
                .compra(compra2)
                .valor(new BigDecimal("200.00"))
                .formaPagamento("PIX")
                .statusPagamento("PENDENTE")
                .build();

        when(pagamentoRepository.existsByIdCompra(101)).thenReturn(false);
        when(pagamentoRepository.save(any(Pagamento.class))).thenReturn(pagamentoPix);

        // Act
        PagamentoResponseDTO resultado = pagamentoService.criar(requestPix);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getFormaPagamento()).isEqualTo("PIX");
        assertThat(resultado.getStatusPagamento()).isEqualTo("PENDENTE");

        verify(pagamentoRepository, times(1)).existsByIdCompra(101);
        verify(pagamentoRepository, times(1)).save(any(Pagamento.class));
    }

    @Test
    @DisplayName("Deve criar pagamento com valor mínimo")
    void deveCriarPagamentoComValorMinimo() {
        // Arrange
        Compra compra3 = Compra.builder()
                .idCompra(102)
                .statusCompra("PENDENTE")
                .build();

        PagamentoRequestDTO requestValorMinimo = PagamentoRequestDTO.builder()
                .idCompra(102)
                .valor(new BigDecimal("0.01"))
                .formaPagamento("DINHEIRO")
                .statusPagamento("APROVADO")
                .build();

        Pagamento pagamentoValorMinimo = Pagamento.builder()
                .idPagamento(3)
                .compra(compra3)
                .valor(new BigDecimal("0.01"))
                .formaPagamento("DINHEIRO")
                .statusPagamento("APROVADO")
                .build();

        when(pagamentoRepository.existsByIdCompra(102)).thenReturn(false);
        when(pagamentoRepository.save(any(Pagamento.class))).thenReturn(pagamentoValorMinimo);

        // Act
        PagamentoResponseDTO resultado = pagamentoService.criar(requestValorMinimo);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getValor()).isEqualByComparingTo(new BigDecimal("0.01"));

        verify(pagamentoRepository, times(1)).existsByIdCompra(102);
        verify(pagamentoRepository, times(1)).save(any(Pagamento.class));
    }

    // ========== TESTES DE ATUALIZAÇÃO ==========

    @Test
    @DisplayName("Deve atualizar pagamento com sucesso")
    void deveAtualizarPagamentoComSucesso() {
        // Arrange
        Compra compraAtualizada = Compra.builder()
                .idCompra(100)
                .statusCompra("PENDENTE")
                .build();

        PagamentoRequestDTO requestAtualizado = PagamentoRequestDTO.builder()
                .idCompra(100)
                .valor(new BigDecimal("180.00"))
                .formaPagamento("CARTAO_DEBITO")
                .statusPagamento("CANCELADO")
                .build();

        Pagamento pagamentoAtualizado = Pagamento.builder()
                .idPagamento(1)
                .compra(compraAtualizada)
                .valor(new BigDecimal("180.00"))
                .formaPagamento("CARTAO_DEBITO")
                .statusPagamento("CANCELADO")
                .build();

        when(pagamentoRepository.findById(1)).thenReturn(Optional.of(pagamento));
        when(pagamentoRepository.findByIdCompra(100)).thenReturn(Optional.of(pagamento));
        when(pagamentoRepository.save(any(Pagamento.class))).thenReturn(pagamentoAtualizado);

        // Act
        PagamentoResponseDTO resultado = pagamentoService.atualizar(1, requestAtualizado);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdPagamento()).isEqualTo(1);
        assertThat(resultado.getValor()).isEqualByComparingTo(new BigDecimal("180.00"));
        assertThat(resultado.getFormaPagamento()).isEqualTo("CARTAO_DEBITO");
        assertThat(resultado.getStatusPagamento()).isEqualTo("CANCELADO");

        verify(pagamentoRepository, times(1)).findById(1);
        verify(pagamentoRepository, times(1)).findByIdCompra(100);
        verify(pagamentoRepository, times(1)).save(any(Pagamento.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar pagamento inexistente")
    void deveLancarExcecaoAoAtualizarPagamentoInexistente() {
        // Arrange
        when(pagamentoRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> pagamentoService.atualizar(999, requestDTO))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Pagamento")
                .hasMessageContaining("id")
                .hasMessageContaining("999");

        verify(pagamentoRepository, times(1)).findById(999);
        verify(pagamentoRepository, never()).save(any(Pagamento.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar com idCompra já utilizado por outro pagamento")
    void deveLancarExcecaoAoAtualizarComIdCompraDuplicado() {
        // Arrange
        Compra compra2 = Compra.builder()
                .idCompra(200)
                .statusCompra("PENDENTE")
                .build();

        Pagamento outroPagamento = Pagamento.builder()
                .idPagamento(2)
                .compra(compra2)
                .valor(new BigDecimal("100.00"))
                .formaPagamento("PIX")
                .statusPagamento("APROVADO")
                .build();

        PagamentoRequestDTO requestComOutroIdCompra = PagamentoRequestDTO.builder()
                .idCompra(200)
                .valor(new BigDecimal("150.00"))
                .formaPagamento("CARTAO_CREDITO")
                .statusPagamento("APROVADO")
                .build();

        when(pagamentoRepository.findById(1)).thenReturn(Optional.of(pagamento));
        when(pagamentoRepository.findByIdCompra(200)).thenReturn(Optional.of(outroPagamento));

        // Act & Assert
        assertThatThrownBy(() -> pagamentoService.atualizar(1, requestComOutroIdCompra))
                .isInstanceOf(RecursoDuplicadoException.class)
                .hasMessageContaining("Pagamento")
                .hasMessageContaining("idCompra")
                .hasMessageContaining("1");

        verify(pagamentoRepository, times(1)).findById(1);
        verify(pagamentoRepository, times(1)).findByIdCompra(200);
        verify(pagamentoRepository, never()).save(any(Pagamento.class));
    }

    @Test
    @DisplayName("Deve atualizar pagamento mantendo o mesmo idCompra")
    void deveAtualizarPagamentoMantendoMesmoIdCompra() {
        // Arrange
        Compra compraMesmo = Compra.builder()
                .idCompra(100)
                .statusCompra("PENDENTE")
                .build();

        PagamentoRequestDTO requestMesmoIdCompra = PagamentoRequestDTO.builder()
                .idCompra(100)
                .valor(new BigDecimal("200.00"))
                .formaPagamento("PIX")
                .statusPagamento("APROVADO")
                .build();

        Pagamento pagamentoAtualizado = Pagamento.builder()
                .idPagamento(1)
                .compra(compraMesmo)
                .valor(new BigDecimal("200.00"))
                .formaPagamento("PIX")
                .statusPagamento("APROVADO")
                .build();

        when(pagamentoRepository.findById(1)).thenReturn(Optional.of(pagamento));
        when(pagamentoRepository.findByIdCompra(100)).thenReturn(Optional.of(pagamento));
        when(pagamentoRepository.save(any(Pagamento.class))).thenReturn(pagamentoAtualizado);

        // Act
        PagamentoResponseDTO resultado = pagamentoService.atualizar(1, requestMesmoIdCompra);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdPagamento()).isEqualTo(1);
        assertThat(resultado.getValor()).isEqualByComparingTo(new BigDecimal("200.00"));

        verify(pagamentoRepository, times(1)).findById(1);
        verify(pagamentoRepository, times(1)).findByIdCompra(100);
        verify(pagamentoRepository, times(1)).save(any(Pagamento.class));
    }

    @Test
    @DisplayName("Deve atualizar apenas o status do pagamento")
    void deveAtualizarApenasStatusPagamento() {
        // Arrange
        Compra compraStatus = Compra.builder()
                .idCompra(100)
                .statusCompra("PENDENTE")
                .build();

        PagamentoRequestDTO requestNovoStatus = PagamentoRequestDTO.builder()
                .idCompra(100)
                .valor(new BigDecimal("150.00"))
                .formaPagamento("CARTAO_CREDITO")
                .statusPagamento("RECUSADO")
                .build();

        Pagamento pagamentoComNovoStatus = Pagamento.builder()
                .idPagamento(1)
                .compra(compraStatus)
                .valor(new BigDecimal("150.00"))
                .formaPagamento("CARTAO_CREDITO")
                .statusPagamento("RECUSADO")
                .build();

        when(pagamentoRepository.findById(1)).thenReturn(Optional.of(pagamento));
        when(pagamentoRepository.findByIdCompra(100)).thenReturn(Optional.of(pagamento));
        when(pagamentoRepository.save(any(Pagamento.class))).thenReturn(pagamentoComNovoStatus);

        // Act
        PagamentoResponseDTO resultado = pagamentoService.atualizar(1, requestNovoStatus);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getStatusPagamento()).isEqualTo("RECUSADO");

        verify(pagamentoRepository, times(1)).findById(1);
        verify(pagamentoRepository, times(1)).findByIdCompra(100);
        verify(pagamentoRepository, times(1)).save(any(Pagamento.class));
    }

    // ========== TESTES DE EXCLUSÃO ==========

    @Test
    @DisplayName("Deve excluir pagamento com sucesso")
    void deveExcluirPagamentoComSucesso() {
        // Arrange
        when(pagamentoRepository.findById(1)).thenReturn(Optional.of(pagamento));
        doNothing().when(pagamentoRepository).delete(pagamento);

        // Act
        pagamentoService.excluir(1);

        // Assert
        verify(pagamentoRepository, times(1)).findById(1);
        verify(pagamentoRepository, times(1)).delete(pagamento);
    }

    @Test
    @DisplayName("Deve lançar exceção ao excluir pagamento inexistente")
    void deveLancarExcecaoAoExcluirPagamentoInexistente() {
        // Arrange
        when(pagamentoRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> pagamentoService.excluir(999))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Pagamento")
                .hasMessageContaining("id")
                .hasMessageContaining("999");

        verify(pagamentoRepository, times(1)).findById(999);
        verify(pagamentoRepository, never()).delete(any(Pagamento.class));
    }
}
