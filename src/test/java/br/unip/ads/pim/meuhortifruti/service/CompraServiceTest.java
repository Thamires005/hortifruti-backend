package br.unip.ads.pim.meuhortifruti.service;

import br.unip.ads.pim.meuhortifruti.dto.CompraRequestDTO;
import br.unip.ads.pim.meuhortifruti.dto.CompraResponseDTO;
import br.unip.ads.pim.meuhortifruti.entity.Compra;
import br.unip.ads.pim.meuhortifruti.entity.ItemCompra;
import br.unip.ads.pim.meuhortifruti.exception.RecursoDuplicadoException;
import br.unip.ads.pim.meuhortifruti.exception.RecursoNaoEncontradoException;
import br.unip.ads.pim.meuhortifruti.repository.CompraRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do CompraService")
public class CompraServiceTest {

    @Mock
    private CompraRepository compraRepository;

    @InjectMocks
    private CompraService compraService;

    private Compra compra;
    private CompraRequestDTO requestDTO;
    private List<ItemCompra> itensCompra;

    @BeforeEach
    void setUp() {
        itensCompra = new ArrayList<>();
        
        compra = Compra.builder()
                .idCompra(1)
                .statusCompra("PENDENTE")
                .itensCompra(itensCompra)
                .build();

        requestDTO = CompraRequestDTO.builder()
                .idCompra(1)
                .statusCompra("PENDENTE")
                .itensCompra(itensCompra)
                .build();
    }

    // ========== TESTES DE LISTAGEM ==========

    @Test
    @DisplayName("Deve listar todas as compras com sucesso")
    void deveListarTodasCompras() {
        // Arrange
        Compra compra2 = Compra.builder()
                .idCompra(2)
                .statusCompra("APROVADA")
                .itensCompra(new ArrayList<>())
                .build();

        List<Compra> compras = Arrays.asList(compra, compra2);
        when(compraRepository.findAll()).thenReturn(compras);

        // Act
        List<CompraResponseDTO> resultado = compraService.listarTodas();

        // Assert
        assertThat(resultado)
                .isNotNull()
                .hasSize(2);
        assertThat(resultado.get(0).getIdCompra()).isEqualTo(1);
        assertThat(resultado.get(0).getStatusCompra()).isEqualTo("PENDENTE");
        assertThat(resultado.get(1).getIdCompra()).isEqualTo(2);
        assertThat(resultado.get(1).getStatusCompra()).isEqualTo("APROVADA");

        verify(compraRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver compras")
    void deveRetornarListaVaziaQuandoNaoHouverCompras() {
        // Arrange
        when(compraRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<CompraResponseDTO> resultado = compraService.listarTodas();

        // Assert
        assertThat(resultado)
                .isNotNull()
                .isEmpty();

        verify(compraRepository, times(1)).findAll();
    }

    // ========== TESTES DE BUSCA POR ID ==========

    @Test
    @DisplayName("Deve buscar compra por ID com sucesso")
    void deveBuscarCompraPorId() {
        // Arrange
        when(compraRepository.findById(1)).thenReturn(Optional.of(compra));

        // Act
        CompraResponseDTO resultado = compraService.buscarPorId(1);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdCompra()).isEqualTo(1);
        assertThat(resultado.getStatusCompra()).isEqualTo("PENDENTE");

        verify(compraRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar compra inexistente")
    void deveLancarExcecaoAoBuscarCompraInexistente() {
        // Arrange
        when(compraRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> compraService.buscarPorId(999))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Compra")  
                .hasMessageContaining("id")
                .hasMessageContaining("999");

        verify(compraRepository, times(1)).findById(999);
    }

    // ========== TESTES DE CRIAÇÃO ==========

    @Test
    @DisplayName("Deve criar compra com sucesso")
    void deveCriarCompraComSucesso() {
        // Arrange
        CompraRequestDTO novaCompraRequest = CompraRequestDTO.builder()
                .idCompra(null)  // ← ID null na criação (será gerado pelo banco)
                .statusCompra("PENDENTE")
                .itensCompra(itensCompra)
                .build();

        Compra novaCompra = Compra.builder()
                .idCompra(2)  // ← ID gerado pelo banco
                .statusCompra("PENDENTE")
                .itensCompra(itensCompra)
                .build();

        when(compraRepository.existsById(null)).thenReturn(false);
        when(compraRepository.save(any(Compra.class))).thenReturn(novaCompra);

        // Act
        CompraResponseDTO resultado = compraService.criar(novaCompraRequest);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdCompra()).isEqualTo(2);
        assertThat(resultado.getStatusCompra()).isEqualTo("PENDENTE");

        verify(compraRepository, times(1)).existsById(null);
        verify(compraRepository, times(1)).save(any(Compra.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar compra com ID que já existe")
    void deveLancarExcecaoAoCriarCompraComIdExistente() {
        // Arrange
        when(compraRepository.existsById(1)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> compraService.criar(requestDTO))
                .isInstanceOf(RecursoDuplicadoException.class)
                .hasMessageContaining("Compra")
                .hasMessageContaining("id")
                .hasMessageContaining("1");

        verify(compraRepository, times(1)).existsById(1);
        verify(compraRepository, never()).save(any(Compra.class));
    }

    @Test
    @DisplayName("Deve criar compra com lista de itens vazia")
    void deveCriarCompraComListaItensVazia() {
        // Arrange
        CompraRequestDTO requestSemItens = CompraRequestDTO.builder()
                .idCompra(null)
                .statusCompra("PENDENTE")
                .itensCompra(new ArrayList<>())
                .build();

        Compra compraSemItens = Compra.builder()
                .idCompra(3)
                .statusCompra("PENDENTE")
                .itensCompra(new ArrayList<>())
                .build();

        when(compraRepository.existsById(null)).thenReturn(false);
        when(compraRepository.save(any(Compra.class))).thenReturn(compraSemItens);

        // Act
        CompraResponseDTO resultado = compraService.criar(requestSemItens);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdCompra()).isEqualTo(3);
        assertThat(resultado.getStatusCompra()).isEqualTo("PENDENTE");

        verify(compraRepository, times(1)).existsById(null);
        verify(compraRepository, times(1)).save(any(Compra.class));
    }

    // ========== TESTES DE ATUALIZAÇÃO ==========

    @Test
    @DisplayName("Deve atualizar compra com sucesso")
    void deveAtualizarCompraComSucesso() {
        // Arrange
        CompraRequestDTO requestAtualizado = CompraRequestDTO.builder()
                .idCompra(1)
                .statusCompra("APROVADA")
                .itensCompra(itensCompra)
                .build();

        Compra compraAtualizada = Compra.builder()
                .idCompra(1)
                .statusCompra("APROVADA")
                .itensCompra(itensCompra)
                .build();

        when(compraRepository.findById(1)).thenReturn(Optional.of(compra));
        when(compraRepository.save(any(Compra.class))).thenReturn(compraAtualizada);

        // Act
        CompraResponseDTO resultado = compraService.atualizar(1, requestAtualizado);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdCompra()).isEqualTo(1);
        assertThat(resultado.getStatusCompra()).isEqualTo("APROVADA");

        verify(compraRepository, times(2)).findById(1); // Uma no buscarCompraPorId e outra na validação
        verify(compraRepository, times(1)).save(any(Compra.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar compra inexistente")
    void deveLancarExcecaoAoAtualizarCompraInexistente() {
        // Arrange
        when(compraRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> compraService.atualizar(999, requestDTO))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Compra")  // ← Corrigido
                .hasMessageContaining("id")
                .hasMessageContaining("999");

        verify(compraRepository, times(1)).findById(999);
        verify(compraRepository, never()).save(any(Compra.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar com ID já utilizado por outra compra")
    void deveLancarExcecaoAoAtualizarComIdDuplicado() {
        // Arrange
        Compra outraCompra = Compra.builder()
                .idCompra(2)
                .statusCompra("APROVADA")
                .build();

        CompraRequestDTO requestComOutroId = CompraRequestDTO.builder()
                .idCompra(2)
                .statusCompra("CANCELADA")
                .itensCompra(itensCompra)
                .build();

        when(compraRepository.findById(1)).thenReturn(Optional.of(compra));
        when(compraRepository.findById(2)).thenReturn(Optional.of(outraCompra));

        // Act & Assert
        assertThatThrownBy(() -> compraService.atualizar(1, requestComOutroId))
                .isInstanceOf(RecursoDuplicadoException.class)
                .hasMessageContaining("Compra")
                .hasMessageContaining("id")
                .hasMessageContaining("2");

        verify(compraRepository, times(1)).findById(1);
        verify(compraRepository, times(1)).findById(2);
        verify(compraRepository, never()).save(any(Compra.class));
    }

    @Test
    @DisplayName("Deve atualizar compra mantendo o mesmo ID")
    void deveAtualizarCompraMantendoMesmoId() {
        // Arrange
        CompraRequestDTO requestMesmoId = CompraRequestDTO.builder()
                .idCompra(1)
                .statusCompra("ENTREGUE")
                .itensCompra(itensCompra)
                .build();

        Compra compraAtualizada = Compra.builder()
                .idCompra(1)
                .statusCompra("ENTREGUE")
                .itensCompra(itensCompra)
                .build();

        when(compraRepository.findById(1)).thenReturn(Optional.of(compra));
        when(compraRepository.save(any(Compra.class))).thenReturn(compraAtualizada);

        // Act
        CompraResponseDTO resultado = compraService.atualizar(1, requestMesmoId);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdCompra()).isEqualTo(1);
        assertThat(resultado.getStatusCompra()).isEqualTo("ENTREGUE");

        verify(compraRepository, times(2)).findById(1);
        verify(compraRepository, times(1)).save(any(Compra.class));
    }

    @Test
    @DisplayName("Deve atualizar status da compra de PENDENTE para APROVADA")
    void deveAtualizarStatusCompraPendenteParaAprovada() {
        // Arrange
        CompraRequestDTO requestAprovada = CompraRequestDTO.builder()
                .idCompra(1)
                .statusCompra("APROVADA")
                .itensCompra(itensCompra)
                .build();

        Compra compraAprovada = Compra.builder()
                .idCompra(1)
                .statusCompra("APROVADA")
                .itensCompra(itensCompra)
                .build();

        when(compraRepository.findById(1)).thenReturn(Optional.of(compra));
        when(compraRepository.save(any(Compra.class))).thenReturn(compraAprovada);

        // Act
        CompraResponseDTO resultado = compraService.atualizar(1, requestAprovada);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getStatusCompra()).isEqualTo("APROVADA");

        verify(compraRepository, times(2)).findById(1);
        verify(compraRepository, times(1)).save(any(Compra.class));
    }

    // ========== TESTES DE EXCLUSÃO ==========

    @Test
    @DisplayName("Deve excluir compra com sucesso")
    void deveExcluirCompraComSucesso() {
        // Arrange
        when(compraRepository.findById(1)).thenReturn(Optional.of(compra));
        doNothing().when(compraRepository).delete(compra);

        // Act
        compraService.excluir(1);

        // Assert
        verify(compraRepository, times(1)).findById(1);
        verify(compraRepository, times(1)).delete(compra);  // ← Agora verifica o delete
    }

    @Test
    @DisplayName("Deve lançar exceção ao excluir compra inexistente")
    void deveLancarExcecaoAoExcluirCompraInexistente() {
        // Arrange
        when(compraRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> compraService.excluir(999))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Compra")  
                .hasMessageContaining("id")
                .hasMessageContaining("999");

        verify(compraRepository, times(1)).findById(999);
        verify(compraRepository, never()).delete(any(Compra.class));  // ← Verifica que delete não foi chamado
    }
}