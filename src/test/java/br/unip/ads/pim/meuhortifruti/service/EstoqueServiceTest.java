package br.unip.ads.pim.meuhortifruti.service;

import br.unip.ads.pim.meuhortifruti.dto.EstoqueRequestDTO;
import br.unip.ads.pim.meuhortifruti.dto.EstoqueResponseDTO;
import br.unip.ads.pim.meuhortifruti.entity.Estoque;
import br.unip.ads.pim.meuhortifruti.exception.RecursoDuplicadoException;
import br.unip.ads.pim.meuhortifruti.exception.RecursoNaoEncontradoException;
import br.unip.ads.pim.meuhortifruti.repository.EstoqueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do EstoqueService")
public class EstoqueServiceTest {

    @Mock
    private EstoqueRepository estoqueRepository;

    @InjectMocks
    private EstoqueService estoqueService;

    private Estoque estoque;
    private EstoqueRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        estoque = Estoque.builder()
                .idEstoque(1)
                .quantidadeProdutos(100)
                .build();

        requestDTO = EstoqueRequestDTO.builder()
                .idProduto(1)
                .quantidadeProdutos(100)
                .build();
    }

    /**
     * Início da execução dos testes
     * */

    // ========== TESTES DE BUSCA POR ID ==========

    @Test
    @DisplayName("Deve buscar estoque por ID com sucesso")
    void deveBuscarEstoquePorId() {
        // Arrange
        when(estoqueRepository.findById(1)).thenReturn(Optional.of(estoque));

        // Act
        EstoqueResponseDTO resultado = estoqueService.buscarPorId(1);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getQuantidadeProdutos()).isEqualTo(100);

        verify(estoqueRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Deve lançar exceção quando estoque não for encontrado")
    void deveLancarExcecaoQuandoEstoqueNaoForEncontrado() {
        // Arrange
        when(estoqueRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> estoqueService.buscarPorId(999))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Estoque")
                .hasMessageContaining("999");

        verify(estoqueRepository, times(1)).findById(999);
    }

    // ========== TESTES DE CRIAÇÃO ==========

    @Test
    @DisplayName("Deve criar estoque com sucesso")
    void deveCriarEstoque() {
        // Arrange
        when(estoqueRepository.existsById(1)).thenReturn(false);
        when(estoqueRepository.save(any(Estoque.class))).thenReturn(estoque);

        // Act
        EstoqueResponseDTO resultado = estoqueService.criar(requestDTO);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getQuantidadeProdutos()).isEqualTo(100);

        verify(estoqueRepository, times(1)).existsById(1);
        verify(estoqueRepository, times(1)).save(any(Estoque.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar estoque com ID duplicado")
    void deveLancarExcecaoAoCriarEstoqueComIdDuplicado() {
        // Arrange
        when(estoqueRepository.existsById(1)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> estoqueService.criar(requestDTO))
                .isInstanceOf(RecursoDuplicadoException.class)
                .hasMessageContaining("Estoque")
                .hasMessageContaining("id")
                .hasMessageContaining("1");

        verify(estoqueRepository, times(1)).existsById(1);
        verify(estoqueRepository, never()).save(any(Estoque.class));
    }

    // ========== TESTES DE ATUALIZAÇÃO ==========

    @Test
    @DisplayName("Deve atualizar estoque com sucesso")
    void deveAtualizarEstoque() {
        // Arrange
        EstoqueRequestDTO novoRequestDTO = EstoqueRequestDTO.builder()
                .idProduto(1)
                .quantidadeProdutos(150)
                .build();

        Estoque estoqueAtualizado = Estoque.builder()
                .idEstoque(1)
                .quantidadeProdutos(150)
                .build();

        when(estoqueRepository.findById(1)).thenReturn(Optional.of(estoque));
        when(estoqueRepository.save(any(Estoque.class))).thenReturn(estoqueAtualizado);

        // Act
        EstoqueResponseDTO resultado = estoqueService.atualizar(1, novoRequestDTO);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getQuantidadeProdutos()).isEqualTo(150);

        verify(estoqueRepository, times(2)).findById(1);
        verify(estoqueRepository, times(1)).save(any(Estoque.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar estoque inexistente")
    void deveLancarExcecaoAoAtualizarEstoqueInexistente() {
        // Arrange
        when(estoqueRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> estoqueService.atualizar(999, requestDTO))
                .isInstanceOf(RecursoNaoEncontradoException.class);

        verify(estoqueRepository, times(1)).findById(999);
        verify(estoqueRepository, never()).save(any(Estoque.class));
    }

    // ========== TESTES DE EXCLUSÃO ==========

    @Test
    @DisplayName("Deve excluir estoque com sucesso")
    void deveExcluirEstoque() {
        // Arrange
        when(estoqueRepository.findById(1)).thenReturn(Optional.of(estoque));
        doNothing().when(estoqueRepository).delete(estoque);

        // Act
        estoqueService.excluir(1);

        // Assert
        verify(estoqueRepository, times(1)).findById(1);
        verify(estoqueRepository, times(1)).delete(estoque);
    }

    @Test
    @DisplayName("Deve lançar exceção ao excluir estoque inexistente")
    void deveLancarExcecaoAoExcluirEstoqueInexistente() {
        // Arrange
        when(estoqueRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> estoqueService.excluir(999))
                .isInstanceOf(RecursoNaoEncontradoException.class);

        verify(estoqueRepository, times(1)).findById(999);
        verify(estoqueRepository, never()).delete(any(Estoque.class));
    }
}