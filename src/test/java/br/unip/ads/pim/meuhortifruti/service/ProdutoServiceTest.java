package br.unip.ads.pim.meuhortifruti.service;

import br.unip.ads.pim.meuhortifruti.dto.ProdutoRequestDTO;
import br.unip.ads.pim.meuhortifruti.dto.ProdutoResponseDTO;
import br.unip.ads.pim.meuhortifruti.entity.Produto;
import br.unip.ads.pim.meuhortifruti.exception.RecursoDuplicadoException;
import br.unip.ads.pim.meuhortifruti.exception.RecursoNaoEncontradoException;
import br.unip.ads.pim.meuhortifruti.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do ProdutoService")
public class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ProdutoService produtoService;

    private Produto produto;
    private ProdutoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        produto = Produto.builder()
                .idProduto(1)
                .nome("Maçã")
                .preco(new BigDecimal("5.50"))
                .quantidadeEstoque(100)
                .dtValidade(LocalDate.now().plusMonths(1))
                .dataEntrega(LocalDate.now())
                .build();

        requestDTO = ProdutoRequestDTO.builder()
                .nome("Maçã")
                .preco(new BigDecimal("5.50"))
                .quantidadeEstoque(100)
                .dtValidade(LocalDate.now().plusMonths(1))
                .dataEntrega(LocalDate.now())
                .build();
    }

    /**
     * Início da execução dos testes
     * */
    @Test
    @DisplayName("Deve listar todos os produtos com sucesso")
    void deveListarTodosProdutos() {
        // Arrange
        Produto produto2 = Produto.builder()
                .idProduto(2)
                .nome("Banana")
                .preco(new BigDecimal("3.20"))
                .quantidadeEstoque(150)
                .dtValidade(LocalDate.now().plusMonths(1))
                .dataEntrega(LocalDate.now())
                .build();

        List<Produto> produtos = Arrays.asList(produto, produto2);
        when(produtoRepository.findAll()).thenReturn(produtos);

        // Act
        List<ProdutoResponseDTO> resultado = produtoService.listarTodas();

        // Assert
        assertThat(resultado)
                .isNotNull()
                .hasSize(2);
        assertThat(resultado.get(0).getNome()).isEqualTo("Maçã");
        assertThat(resultado.get(0).getPreco()).isEqualByComparingTo(new BigDecimal("5.50"));
        assertThat(resultado.get(1).getNome()).isEqualTo("Banana");

        verify(produtoRepository, times(1)).findAll();
    }

    // ========== TESTES DE BUSCA POR ID ==========

    @Test
    @DisplayName("Deve buscar produto por ID com sucesso")
    void deveBuscarProdutoPorId() {
        // Arrange
        when(produtoRepository.findById(1)).thenReturn(Optional.of(produto));

        // Act
        ProdutoResponseDTO resultado = produtoService.buscarPorId(1);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdProduto()).isEqualTo(1);
        assertThat(resultado.getNome()).isEqualTo("Maçã");
        assertThat(resultado.getPreco()).isEqualByComparingTo(new BigDecimal("5.50"));
        assertThat(resultado.getQuantidadeEstoque()).isEqualTo(100);

        verify(produtoRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Deve lançar exceção quando produto não for encontrado")
    void deveLancarExcecaoQuandoProdutoNaoForEncontrado() {
        // Arrange
        when(produtoRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> produtoService.buscarPorId(999))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Produto")
                .hasMessageContaining("999");

        verify(produtoRepository, times(1)).findById(999);
    }

    // ========== TESTES DE CRIAÇÃO ==========

    @Test
    @DisplayName("Deve criar produto com sucesso")
    void deveCriarProduto() {
        // Arrange
        when(produtoRepository.existsByNome(anyString())).thenReturn(false);
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

        // Act
        ProdutoResponseDTO resultado = produtoService.criar(requestDTO);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdProduto()).isEqualTo(1);
        assertThat(resultado.getNome()).isEqualTo("Maçã");
        assertThat(resultado.getPreco()).isEqualByComparingTo(new BigDecimal("5.50"));
        assertThat(resultado.getQuantidadeEstoque()).isEqualTo(100);

        verify(produtoRepository, times(1)).existsByNome("Maçã");
        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar produto com nome duplicado")
    void deveLancarExcecaoAoCriarProdutoComNomeDuplicado() {
        // Arrange
        when(produtoRepository.existsByNome("Maçã")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> produtoService.criar(requestDTO))
                .isInstanceOf(RecursoDuplicadoException.class)
                .hasMessageContaining("Produto")
                .hasMessageContaining("nome")
                .hasMessageContaining("Maçã");

        verify(produtoRepository, times(1)).existsByNome("Maçã");
        verify(produtoRepository, never()).save(any(Produto.class));
    }

    // ========== TESTES DE ATUALIZAÇÃO ==========

    @Test
    @DisplayName("Deve atualizar produto com sucesso")
    void deveAtualizarProduto() {
        // Arrange
        ProdutoRequestDTO novoRequestDTO = ProdutoRequestDTO.builder()
                .nome("Maçã Gala")
                .preco(new BigDecimal("6.00"))
                .quantidadeEstoque(120)
                .dtValidade(LocalDate.now().plusMonths(2))
                .dataEntrega(LocalDate.now())
                .build();

        Produto produtoAtualizado = Produto.builder()
                .idProduto(1)
                .nome("Maçã Gala")
                .preco(new BigDecimal("6.00"))
                .quantidadeEstoque(120)
                .dtValidade(LocalDate.now().plusMonths(2))
                .dataEntrega(LocalDate.now())
                .build();

        when(produtoRepository.findById(1)).thenReturn(Optional.of(produto));
        when(produtoRepository.findByNome("Maçã Gala")).thenReturn(Optional.empty());
        when(produtoRepository.save(any(Produto.class))).thenReturn(produtoAtualizado);

        // Act
        ProdutoResponseDTO resultado = produtoService.atualizar(1, novoRequestDTO);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdProduto()).isEqualTo(1);
        assertThat(resultado.getNome()).isEqualTo("Maçã Gala");
        assertThat(resultado.getPreco()).isEqualByComparingTo(new BigDecimal("6.00"));
        assertThat(resultado.getQuantidadeEstoque()).isEqualTo(120);

        verify(produtoRepository, times(1)).findById(1);
        verify(produtoRepository, times(1)).findByNome("Maçã Gala");
        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    @DisplayName("Deve permitir atualizar produto mantendo o mesmo nome")
    void devePermitirAtualizarProdutoComMesmoNome() {
        // Arrange
        when(produtoRepository.findById(1)).thenReturn(Optional.of(produto));
        when(produtoRepository.findByNome("Maçã")).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

        // Act
        ProdutoResponseDTO resultado = produtoService.atualizar(1, requestDTO);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("Maçã");

        verify(produtoRepository, times(1)).findById(1);
        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar para nome já existente em outro produto")
    void deveLancarExcecaoAoAtualizarParaNomeJaExistente() {
        // Arrange
        Produto outroProduto = Produto.builder()
                .idProduto(2)
                .nome("Banana")
                .preco(new BigDecimal("3.20"))
                .quantidadeEstoque(150)
                .dtValidade(LocalDate.now().plusMonths(1))
                .dataEntrega(LocalDate.now())
                .build();

        ProdutoRequestDTO novoRequestDTO = ProdutoRequestDTO.builder()
                .nome("Banana")
                .preco(new BigDecimal("5.50"))
                .quantidadeEstoque(100)
                .dtValidade(LocalDate.now().plusMonths(1))
                .dataEntrega(LocalDate.now())
                .build();

        when(produtoRepository.findById(1)).thenReturn(Optional.of(produto));
        when(produtoRepository.findByNome("Banana")).thenReturn(Optional.of(outroProduto));

        // Act & Assert
        assertThatThrownBy(() -> produtoService.atualizar(1, novoRequestDTO))
                .isInstanceOf(RecursoDuplicadoException.class)
                .hasMessageContaining("Produto")
                .hasMessageContaining("nome")
                .hasMessageContaining("Banana");

        verify(produtoRepository, times(1)).findById(1);
        verify(produtoRepository, never()).save(any(Produto.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar produto inexistente")
    void deveLancarExcecaoAoAtualizarProdutoInexistente() {
        // Arrange
        when(produtoRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> produtoService.atualizar(999, requestDTO))
                .isInstanceOf(RecursoNaoEncontradoException.class);

        verify(produtoRepository, times(1)).findById(999);
        verify(produtoRepository, never()).save(any(Produto.class));
    }

    // ========== TESTES DE EXCLUSÃO ==========

    @Test
    @DisplayName("Deve excluir produto com sucesso")
    void deveExcluirProduto() {
        // Arrange
        when(produtoRepository.findById(1)).thenReturn(Optional.of(produto));
        doNothing().when(produtoRepository).delete(produto);

        // Act
        produtoService.excluir(1);

        // Assert
        verify(produtoRepository, times(1)).findById(1);
        verify(produtoRepository, times(1)).delete(produto);
    }

    @Test
    @DisplayName("Deve lançar exceção ao excluir produto inexistente")
    void deveLancarExcecaoAoExcluirProdutoInexistente() {
        // Arrange
        when(produtoRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> produtoService.excluir(999))
                .isInstanceOf(RecursoNaoEncontradoException.class);

        verify(produtoRepository, times(1)).findById(999);
        verify(produtoRepository, never()).delete(any(Produto.class));
    }
}