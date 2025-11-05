package br.unip.ads.pim.meuhortifruti.service;

import br.unip.ads.pim.meuhortifruti.dto.CategoriaRequestDTO;
import br.unip.ads.pim.meuhortifruti.dto.CategoriaResponseDTO;
import br.unip.ads.pim.meuhortifruti.entity.Categoria;
import br.unip.ads.pim.meuhortifruti.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do CategoriaService")
public class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoria;
    private CategoriaRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        categoria = Categoria.builder()
                .idCategoria(1)
                .nome("Frutas")
                .build();

        requestDTO = CategoriaRequestDTO.builder()
                .nome("Frutas")
                .build();
    }

    /**
     * Início da execução dos testes
     * */
    @Test
    @DisplayName("Deve listar todas as categorias com sucesso")
    void deveListarTodasCategorias() {
        // Arrange
        Categoria categoria2 = Categoria.builder()
                .idCategoria(2)
                .nome("Verduras")
                .build();

        List<Categoria> categorias = Arrays.asList(categoria, categoria2);
        when(categoriaRepository.findAll()).thenReturn(categorias);

        // Act
        List<CategoriaResponseDTO> resultado = categoriaService.listarTodas();

        // Assert
        assertThat(resultado)
                .isNotNull()
                .hasSize(2);
        assertThat(resultado.get(0).getNome()).isEqualTo("Frutas");
        assertThat(resultado.get(1).getNome()).isEqualTo("Verduras");

        verify(categoriaRepository, times(1)).findAll();
    }

    // ========== TESTES DE BUSCA POR ID ==========

    @Test
    @DisplayName("Deve buscar categoria por ID com sucesso")
    void deveBuscarCategoriaPorId() {
        // Arrange
        when(categoriaRepository.findById(1)).thenReturn(Optional.of(categoria));

        // Act
        CategoriaResponseDTO resultado = categoriaService.buscarPorId(1);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdCategoria()).isEqualTo(1);
        assertThat(resultado.getNome()).isEqualTo("Frutas");

        verify(categoriaRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Deve lançar exceção quando categoria não for encontrada")
    void deveLancarExcecaoQuandoCategoriaNaoForEncontrada() {
        // Arrange
        when(categoriaRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> categoriaService.buscarPorId(999))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Categoria")
                .hasMessageContaining("999");

        verify(categoriaRepository, times(1)).findById(999);
    }

    // ========== TESTES DE CRIAÇÃO ==========

    @Test
    @DisplayName("Deve criar categoria com sucesso")
    void deveCriarCategoria() {
        // Arrange
        when(categoriaRepository.existsByNome(anyString())).thenReturn(false);
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

        // Act
        CategoriaResponseDTO resultado = categoriaService.criar(requestDTO);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdCategoria()).isEqualTo(1);
        assertThat(resultado.getNome()).isEqualTo("Frutas");

        verify(categoriaRepository, times(1)).existsByNome("Frutas");
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar categoria com nome duplicado")
    void deveLancarExcecaoAoCriarCategoriaComNomeDuplicado() {
        // Arrange
        when(categoriaRepository.existsByNome("Frutas")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> categoriaService.criar(requestDTO))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Categoria")
                .hasMessageContaining("nome")
                .hasMessageContaining("Frutas");

        verify(categoriaRepository, times(1)).existsByNome("Frutas");
        verify(categoriaRepository, never()).save(any(Categoria.class));
    }

    // ========== TESTES DE ATUALIZAÇÃO ==========

    @Test
    @DisplayName("Deve atualizar categoria com sucesso")
    void deveAtualizarCategoria() {
        // Arrange
        CategoriaRequestDTO novoRequestDTO = CategoriaRequestDTO.builder()
                .nome("Frutas Tropicais")
                .build();

        Categoria categoriaAtualizada = Categoria.builder()
                .idCategoria(1)
                .nome("Frutas Tropicais")
                .build();

        when(categoriaRepository.findById(1)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.findByNome("Frutas Tropicais")).thenReturn(Optional.empty());
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaAtualizada);

        // Act
        CategoriaResponseDTO resultado = categoriaService.atualizar(1, novoRequestDTO);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdCategoria()).isEqualTo(1);
        assertThat(resultado.getNome()).isEqualTo("Frutas Tropicais");

        verify(categoriaRepository, times(1)).findById(1);
        verify(categoriaRepository, times(1)).findByNome("Frutas Tropicais");
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    @Test
    @DisplayName("Deve permitir atualizar categoria mantendo o mesmo nome")
    void devePermitirAtualizarCategoriaComMesmoNome() {
        // Arrange
        when(categoriaRepository.findById(1)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.findByNome("Frutas")).thenReturn(Optional.of(categoria));
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

        // Act
        CategoriaResponseDTO resultado = categoriaService.atualizar(1, requestDTO);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("Frutas");

        verify(categoriaRepository, times(1)).findById(1);
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar para nome já existente em outra categoria")
    void deveLancarExcecaoAoAtualizarParaNomeJaExistente() {
        // Arrange
        Categoria outraCategoria = Categoria.builder()
                .idCategoria(2)
                .nome("Verduras")
                .build();

        CategoriaRequestDTO novoRequestDTO = CategoriaRequestDTO.builder()
                .nome("Verduras")
                .build();

        when(categoriaRepository.findById(1)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.findByNome("Verduras")).thenReturn(Optional.of(outraCategoria));

        // Act & Assert
        assertThatThrownBy(() -> categoriaService.atualizar(1, novoRequestDTO))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Categoria")
                .hasMessageContaining("nome")
                .hasMessageContaining("Verduras");

        verify(categoriaRepository, times(1)).findById(1);
        verify(categoriaRepository, never()).save(any(Categoria.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar categoria inexistente")
    void deveLancarExcecaoAoAtualizarCategoriaInexistente() {
        // Arrange
        when(categoriaRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> categoriaService.atualizar(999, requestDTO))
                .isInstanceOf(Exception.class);

        verify(categoriaRepository, times(1)).findById(999);
        verify(categoriaRepository, never()).save(any(Categoria.class));
    }

    // ========== TESTES DE EXCLUSÃO ==========

    @Test
    @DisplayName("Deve excluir categoria com sucesso")
    void deveExcluirCategoria() {
        // Arrange
        when(categoriaRepository.findById(1)).thenReturn(Optional.of(categoria));
        doNothing().when(categoriaRepository).delete(categoria);

        // Act
        categoriaService.excluir(1);

        // Assert
        verify(categoriaRepository, times(1)).findById(1);
        verify(categoriaRepository, times(1)).delete(categoria);
    }

    @Test
    @DisplayName("Deve lançar exceção ao excluir categoria inexistente")
    void deveLancarExcecaoAoExcluirCategoriaInexistente() {
        // Arrange
        when(categoriaRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> categoriaService.excluir(999))
                .isInstanceOf(Exception.class);

        verify(categoriaRepository, times(1)).findById(999);
        verify(categoriaRepository, never()).delete(any(Categoria.class));
    }
}
