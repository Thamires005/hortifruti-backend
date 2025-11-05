package br.unip.ads.pim.meuhortifruti.service;

import br.unip.ads.pim.meuhortifruti.dto.ItemCompraRequestDTO;
import br.unip.ads.pim.meuhortifruti.dto.ItemCompraResponseDTO;
import br.unip.ads.pim.meuhortifruti.entity.ItemCompra;
import br.unip.ads.pim.meuhortifruti.entity.Produto;
import br.unip.ads.pim.meuhortifruti.exception.RecursoDuplicadoException;
import br.unip.ads.pim.meuhortifruti.exception.RecursoNaoEncontradoException;
import br.unip.ads.pim.meuhortifruti.repository.ItemCompraRepository;
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
@DisplayName("Testes do ItemCompraService")
public class ItemCompraServiceTest {

    @Mock
    private ItemCompraRepository itemCompraRepository;

    @InjectMocks
    private ItemCompraService itemCompraService;

    private ItemCompra itemCompra;
    private ItemCompraRequestDTO requestDTO;
    private Produto produto;

    @BeforeEach
    void setUp() {
        produto = Produto.builder()
                .idProduto(1)
                .nome("Maçã")
                .preco(new BigDecimal("5.50"))
                .build();

        itemCompra = ItemCompra.builder()
                .idItemCompra(1)
                .produto(produto)
                .preco(new BigDecimal("5.50"))
                .quantidade(10)
                .build();

        requestDTO = ItemCompraRequestDTO.builder()
                .produto(produto)
                .preco(new BigDecimal("5.50"))
                .quantidade(10)
                .build();
    }

    // ========== TESTES DE BUSCA POR ID ==========

    @Test
    @DisplayName("Deve buscar item de compra por ID com sucesso")
    void deveBuscarItemCompraPorId() {
        // Arrange
        when(itemCompraRepository.findById(1)).thenReturn(Optional.of(itemCompra));

        // Act
        ItemCompraResponseDTO resultado = itemCompraService.buscarPorId(1);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdItemCompra()).isEqualTo(1);
        assertThat(resultado.getPreco()).isEqualByComparingTo(new BigDecimal("5.50"));
        assertThat(resultado.getQuantidade()).isEqualTo(10);

        verify(itemCompraRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar item de compra inexistente")
    void deveLancarExcecaoAoBuscarItemCompraInexistente() {
        // Arrange
        when(itemCompraRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> itemCompraService.buscarPorId(999))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Item Compra")
                .hasMessageContaining("id")
                .hasMessageContaining("999");

        verify(itemCompraRepository, times(1)).findById(999);
    }

    // ========== TESTES DE CRIAÇÃO ==========

    @Test
    @DisplayName("Deve criar item de compra com sucesso")
    void deveCriarItemCompraComSucesso() {
        // Arrange
        when(itemCompraRepository.existsById(produto.getIdProduto())).thenReturn(false);
        when(itemCompraRepository.save(any(ItemCompra.class))).thenReturn(itemCompra);

        // Act
        ItemCompraResponseDTO resultado = itemCompraService.criar(requestDTO);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdItemCompra()).isEqualTo(1);
        assertThat(resultado.getPreco()).isEqualByComparingTo(new BigDecimal("5.50"));
        assertThat(resultado.getQuantidade()).isEqualTo(10);

        verify(itemCompraRepository, times(1)).existsById(produto.getIdProduto());
        verify(itemCompraRepository, times(1)).save(any(ItemCompra.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar item de compra com ID de produto duplicado")
    void deveLancarExcecaoAoCriarItemCompraComIdProdutoDuplicado() {
        // Arrange
        when(itemCompraRepository.existsById(produto.getIdProduto())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> itemCompraService.criar(requestDTO))
                .isInstanceOf(RecursoDuplicadoException.class)
                .hasMessageContaining("Item compra")
                .hasMessageContaining("id")
                .hasMessageContaining("1");

        verify(itemCompraRepository, times(1)).existsById(produto.getIdProduto());
        verify(itemCompraRepository, never()).save(any(ItemCompra.class));
    }

    @Test
    @DisplayName("Deve criar item de compra com quantidade diferente")
    void deveCriarItemCompraComQuantidadeDiferente() {
        // Arrange
        ItemCompraRequestDTO requestComQuantidade = ItemCompraRequestDTO.builder()
                .produto(produto)
                .preco(new BigDecimal("5.50"))
                .quantidade(25)
                .build();

        ItemCompra itemCompraComQuantidade = ItemCompra.builder()
                .idItemCompra(2)
                .produto(produto)
                .preco(new BigDecimal("5.50"))
                .quantidade(25)
                .build();

        when(itemCompraRepository.existsById(produto.getIdProduto())).thenReturn(false);
        when(itemCompraRepository.save(any(ItemCompra.class))).thenReturn(itemCompraComQuantidade);

        // Act
        ItemCompraResponseDTO resultado = itemCompraService.criar(requestComQuantidade);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getQuantidade()).isEqualTo(25);

        verify(itemCompraRepository, times(1)).existsById(produto.getIdProduto());
        verify(itemCompraRepository, times(1)).save(any(ItemCompra.class));
    }

    @Test
    @DisplayName("Deve criar item de compra com preço diferente")
    void deveCriarItemCompraComPrecoDiferente() {
        // Arrange
        ItemCompraRequestDTO requestComPreco = ItemCompraRequestDTO.builder()
                .produto(produto)
                .preco(new BigDecimal("10.00"))
                .quantidade(10)
                .build();

        ItemCompra itemCompraComPreco = ItemCompra.builder()
                .idItemCompra(3)
                .produto(produto)
                .preco(new BigDecimal("10.00"))
                .quantidade(10)
                .build();

        when(itemCompraRepository.existsById(produto.getIdProduto())).thenReturn(false);
        when(itemCompraRepository.save(any(ItemCompra.class))).thenReturn(itemCompraComPreco);

        // Act
        ItemCompraResponseDTO resultado = itemCompraService.criar(requestComPreco);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getPreco()).isEqualByComparingTo(new BigDecimal("10.00"));

        verify(itemCompraRepository, times(1)).existsById(produto.getIdProduto());
        verify(itemCompraRepository, times(1)).save(any(ItemCompra.class));
    }

    // ========== TESTES DE ATUALIZAÇÃO ==========

    @Test
    @DisplayName("Deve atualizar item de compra com sucesso")
    void deveAtualizarItemCompraComSucesso() {
        // Arrange
        ItemCompraRequestDTO requestAtualizado = ItemCompraRequestDTO.builder()
                .produto(produto)
                .preco(new BigDecimal("6.00"))
                .quantidade(15)
                .build();

        ItemCompra itemCompraAtualizado = ItemCompra.builder()
                .idItemCompra(1)
                .produto(produto)
                .preco(new BigDecimal("6.00"))
                .quantidade(15)
                .build();

        when(itemCompraRepository.findById(1)).thenReturn(Optional.of(itemCompra));
        when(itemCompraRepository.save(any(ItemCompra.class))).thenReturn(itemCompraAtualizado);

        // Act
        ItemCompraResponseDTO resultado = itemCompraService.atualizar(1, requestAtualizado);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdItemCompra()).isEqualTo(1);
        assertThat(resultado.getPreco()).isEqualByComparingTo(new BigDecimal("6.00"));
        assertThat(resultado.getQuantidade()).isEqualTo(15);

        verify(itemCompraRepository, times(2)).findById(1); // Uma no buscarItemCompraPorId e outra na validação
        verify(itemCompraRepository, times(1)).save(any(ItemCompra.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar item de compra inexistente")
    void deveLancarExcecaoAoAtualizarItemCompraInexistente() {
        // Arrange
        when(itemCompraRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> itemCompraService.atualizar(999, requestDTO))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Item Compra")
                .hasMessageContaining("id")
                .hasMessageContaining("999");

        verify(itemCompraRepository, times(1)).findById(999);
        verify(itemCompraRepository, never()).save(any(ItemCompra.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar com ID de produto já utilizado por outro item")
    void deveLancarExcecaoAoAtualizarComIdProdutoDuplicado() {
        // Arrange
        Produto outroProduto = Produto.builder()
                .idProduto(2)
                .nome("Banana")
                .build();

        ItemCompra outroItemCompra = ItemCompra.builder()
                .idItemCompra(2)
                .produto(outroProduto)
                .preco(new BigDecimal("3.00"))
                .quantidade(5)
                .build();

        ItemCompraRequestDTO requestComOutroProduto = ItemCompraRequestDTO.builder()
                .produto(outroProduto)
                .preco(new BigDecimal("3.00"))
                .quantidade(5)
                .build();

        when(itemCompraRepository.findById(1)).thenReturn(Optional.of(itemCompra));
        when(itemCompraRepository.findById(2)).thenReturn(Optional.of(outroItemCompra));

        // Act & Assert
        assertThatThrownBy(() -> itemCompraService.atualizar(1, requestComOutroProduto))
                .isInstanceOf(RecursoDuplicadoException.class)
                .hasMessageContaining("Item Compra")
                .hasMessageContaining("id")
                .hasMessageContaining("2");

        verify(itemCompraRepository, times(1)).findById(1);
        verify(itemCompraRepository, times(1)).findById(2);
        verify(itemCompraRepository, never()).save(any(ItemCompra.class));
    }

    @Test
    @DisplayName("Deve atualizar item de compra mantendo o mesmo produto")
    void deveAtualizarItemCompraMantendoMesmoProduto() {
        // Arrange
        ItemCompraRequestDTO requestMesmoProduto = ItemCompraRequestDTO.builder()
                .produto(produto)
                .preco(new BigDecimal("7.00"))
                .quantidade(20)
                .build();

        ItemCompra itemCompraAtualizado = ItemCompra.builder()
                .idItemCompra(1)
                .produto(produto)
                .preco(new BigDecimal("7.00"))
                .quantidade(20)
                .build();

        when(itemCompraRepository.findById(1)).thenReturn(Optional.of(itemCompra));
        when(itemCompraRepository.save(any(ItemCompra.class))).thenReturn(itemCompraAtualizado);

        // Act
        ItemCompraResponseDTO resultado = itemCompraService.atualizar(1, requestMesmoProduto);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdItemCompra()).isEqualTo(1);
        assertThat(resultado.getPreco()).isEqualByComparingTo(new BigDecimal("7.00"));
        assertThat(resultado.getQuantidade()).isEqualTo(20);

        verify(itemCompraRepository, times(2)).findById(1);
        verify(itemCompraRepository, times(1)).save(any(ItemCompra.class));
    }

    @Test
    @DisplayName("Deve atualizar apenas o preço do item de compra")
    void deveAtualizarApenasPrecoItemCompra() {
        // Arrange
        ItemCompraRequestDTO requestNovoPreco = ItemCompraRequestDTO.builder()
                .produto(produto)
                .preco(new BigDecimal("8.50"))
                .quantidade(10) // Mesma quantidade
                .build();

        ItemCompra itemCompraComNovoPreco = ItemCompra.builder()
                .idItemCompra(1)
                .produto(produto)
                .preco(new BigDecimal("8.50"))
                .quantidade(10)
                .build();

        when(itemCompraRepository.findById(1)).thenReturn(Optional.of(itemCompra));
        when(itemCompraRepository.save(any(ItemCompra.class))).thenReturn(itemCompraComNovoPreco);

        // Act
        ItemCompraResponseDTO resultado = itemCompraService.atualizar(1, requestNovoPreco);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getPreco()).isEqualByComparingTo(new BigDecimal("8.50"));
        assertThat(resultado.getQuantidade()).isEqualTo(10);

        verify(itemCompraRepository, times(2)).findById(1);
        verify(itemCompraRepository, times(1)).save(any(ItemCompra.class));
    }

    @Test
    @DisplayName("Deve atualizar apenas a quantidade do item de compra")
    void deveAtualizarApenasQuantidadeItemCompra() {
        // Arrange
        ItemCompraRequestDTO requestNovaQuantidade = ItemCompraRequestDTO.builder()
                .produto(produto)
                .preco(new BigDecimal("5.50")) // Mesmo preço
                .quantidade(30)
                .build();

        ItemCompra itemCompraComNovaQuantidade = ItemCompra.builder()
                .idItemCompra(1)
                .produto(produto)
                .preco(new BigDecimal("5.50"))
                .quantidade(30)
                .build();

        when(itemCompraRepository.findById(1)).thenReturn(Optional.of(itemCompra));
        when(itemCompraRepository.save(any(ItemCompra.class))).thenReturn(itemCompraComNovaQuantidade);

        // Act
        ItemCompraResponseDTO resultado = itemCompraService.atualizar(1, requestNovaQuantidade);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getPreco()).isEqualByComparingTo(new BigDecimal("5.50"));
        assertThat(resultado.getQuantidade()).isEqualTo(30);

        verify(itemCompraRepository, times(2)).findById(1);
        verify(itemCompraRepository, times(1)).save(any(ItemCompra.class));
    }

    // ========== TESTES DE EXCLUSÃO ==========

    @Test
    @DisplayName("Deve excluir item de compra com sucesso")
    void deveExcluirItemCompraComSucesso() {
        // Arrange
        when(itemCompraRepository.findById(1)).thenReturn(Optional.of(itemCompra));
        doNothing().when(itemCompraRepository).delete(itemCompra);

        // Act
        itemCompraService.excluir(1);

        // Assert
        verify(itemCompraRepository, times(1)).findById(1);
        verify(itemCompraRepository, times(1)).delete(itemCompra);
    }

    @Test
    @DisplayName("Deve lançar exceção ao excluir item de compra inexistente")
    void deveLancarExcecaoAoExcluirItemCompraInexistente() {
        // Arrange
        when(itemCompraRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> itemCompraService.excluir(999))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Item Compra")
                .hasMessageContaining("id")
                .hasMessageContaining("999");

        verify(itemCompraRepository, times(1)).findById(999);
        verify(itemCompraRepository, never()).delete(any(ItemCompra.class));
    }
}