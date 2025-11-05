package br.unip.ads.pim.meuhortifruti.service;

import br.unip.ads.pim.meuhortifruti.dto.FornecedorRequestDTO;
import br.unip.ads.pim.meuhortifruti.dto.FornecedorResponseDTO;
import br.unip.ads.pim.meuhortifruti.entity.Fornecedor;
import br.unip.ads.pim.meuhortifruti.exception.RecursoDuplicadoException;
import br.unip.ads.pim.meuhortifruti.exception.RecursoNaoEncontradoException;
import br.unip.ads.pim.meuhortifruti.repository.FornecedorRepository;
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
@DisplayName("Testes do FornecedorService")
public class FornecedorServiceTest {

    @Mock
    private FornecedorRepository fornecedorRepository;

    @InjectMocks
    private FornecedorService fornecedorService;

    private Fornecedor fornecedor;
    private FornecedorRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        fornecedor = Fornecedor.builder()
                .idFornecedor(1)
                .nome("Fornecedor Frutas Ltda")
                .cnpj("12345678901234")
                .telefone("11987654321")
                .email("contato@frutas.com")
                .endereco("Rua das Frutas, 123")
                .produtosFornecidos("Laranjas, Maçãs, Bananas")
                .build();

        requestDTO = FornecedorRequestDTO.builder()
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
    void deveListarTodosFornecedores() {
        // Arrange
        Fornecedor fornecedor2 = Fornecedor.builder()
                .idFornecedor(2)
                .nome("Fornecedor Verduras Ltda")
                .cnpj("98765432109876")
                .telefone("11912345678")
                .email("contato@verduras.com")
                .endereco("Rua das Verduras, 456")
                .produtosFornecidos("Alface, Tomate, Cenoura")
                .build();

        List<Fornecedor> fornecedores = Arrays.asList(fornecedor, fornecedor2);
        when(fornecedorRepository.findAll()).thenReturn(fornecedores);

        // Act
        List<FornecedorResponseDTO> resultado = fornecedorService.listarTodas();

        // Assert
        assertThat(resultado)
                .isNotNull()
                .hasSize(2);
        assertThat(resultado.get(0).getNome()).isEqualTo("Fornecedor Frutas Ltda");
        assertThat(resultado.get(0).getEmail()).isEqualTo("contato@frutas.com");
        assertThat(resultado.get(1).getNome()).isEqualTo("Fornecedor Verduras Ltda");
        assertThat(resultado.get(1).getEmail()).isEqualTo("contato@verduras.com");

        verify(fornecedorRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver fornecedores")
    void deveRetornarListaVaziaQuandoNaoHouverFornecedores() {
        // Arrange
        when(fornecedorRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<FornecedorResponseDTO> resultado = fornecedorService.listarTodas();

        // Assert
        assertThat(resultado)
                .isNotNull()
                .isEmpty();

        verify(fornecedorRepository, times(1)).findAll();
    }

    // ========== TESTES DE BUSCA POR ID ==========

    @Test
    @DisplayName("Deve buscar fornecedor por ID com sucesso")
    void deveBuscarFornecedorPorId() {
        // Arrange
        when(fornecedorRepository.findById(1)).thenReturn(Optional.of(fornecedor));

        // Act
        FornecedorResponseDTO resultado = fornecedorService.buscarPorId(1);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdFornecedor()).isEqualTo(1);
        assertThat(resultado.getNome()).isEqualTo("Fornecedor Frutas Ltda");
        assertThat(resultado.getCnpj()).isEqualTo("12345678901234");
        assertThat(resultado.getEmail()).isEqualTo("contato@frutas.com");
        assertThat(resultado.getTelefone()).isEqualTo("11987654321");
        assertThat(resultado.getEndereco()).isEqualTo("Rua das Frutas, 123");
        assertThat(resultado.getProdutosFornecidos()).isEqualTo("Laranjas, Maçãs, Bananas");

        verify(fornecedorRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar fornecedor inexistente")
    void deveLancarExcecaoAoBuscarFornecedorInexistente() {
        // Arrange
        when(fornecedorRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> fornecedorService.buscarPorId(999))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Fornecedor")
                .hasMessageContaining("id")
                .hasMessageContaining("999");

        verify(fornecedorRepository, times(1)).findById(999);
    }

    // ========== TESTES DE CRIAÇÃO ==========

    @Test
    @DisplayName("Deve criar fornecedor com sucesso")
    void deveCriarFornecedorComSucesso() {
        // Arrange
        when(fornecedorRepository.existsByEmail(requestDTO.getEmail())).thenReturn(false);
        when(fornecedorRepository.save(any(Fornecedor.class))).thenReturn(fornecedor);

        // Act
        FornecedorResponseDTO resultado = fornecedorService.criar(requestDTO);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdFornecedor()).isEqualTo(1);
        assertThat(resultado.getNome()).isEqualTo("Fornecedor Frutas Ltda");
        assertThat(resultado.getCnpj()).isEqualTo("12345678901234");
        assertThat(resultado.getEmail()).isEqualTo("contato@frutas.com");
        assertThat(resultado.getTelefone()).isEqualTo("11987654321");
        assertThat(resultado.getEndereco()).isEqualTo("Rua das Frutas, 123");
        assertThat(resultado.getProdutosFornecidos()).isEqualTo("Laranjas, Maçãs, Bananas");

        verify(fornecedorRepository, times(1)).existsByEmail(requestDTO.getEmail());
        verify(fornecedorRepository, times(1)).save(any(Fornecedor.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar fornecedor com email duplicado")
    void deveLancarExcecaoAoCriarFornecedorComEmailDuplicado() {
        // Arrange
        when(fornecedorRepository.existsByEmail(requestDTO.getEmail())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> fornecedorService.criar(requestDTO))
                .isInstanceOf(RecursoDuplicadoException.class)
                .hasMessageContaining("Fornecedor")
                .hasMessageContaining("email")
                .hasMessageContaining("contato@frutas.com");

        verify(fornecedorRepository, times(1)).existsByEmail(requestDTO.getEmail());
        verify(fornecedorRepository, never()).save(any(Fornecedor.class));
    }

    // ========== TESTES DE ATUALIZAÇÃO ==========

    @Test
    @DisplayName("Deve atualizar fornecedor com sucesso")
    void deveAtualizarFornecedorComSucesso() {
        // Arrange
        FornecedorRequestDTO requestAtualizado = FornecedorRequestDTO.builder()
                .nome("Fornecedor Frutas Tropicais Ltda")
                .cnpj("12345678901234")
                .telefone("11999887766")
                .email("contato@frutas.com")
                .endereco("Avenida das Frutas, 999")
                .produtosFornecidos("Manga, Abacaxi, Coco")
                .build();

        Fornecedor fornecedorAtualizado = Fornecedor.builder()
                .idFornecedor(1)
                .nome("Fornecedor Frutas Tropicais Ltda")
                .cnpj("12345678901234")
                .telefone("11999887766")
                .email("contato@frutas.com")
                .endereco("Avenida das Frutas, 999")
                .produtosFornecidos("Manga, Abacaxi, Coco")
                .build();

        when(fornecedorRepository.findById(1)).thenReturn(Optional.of(fornecedor));
        when(fornecedorRepository.findByEmail(requestAtualizado.getEmail()))
                .thenReturn(Optional.of(fornecedor));
        when(fornecedorRepository.save(any(Fornecedor.class))).thenReturn(fornecedorAtualizado);

        // Act
        FornecedorResponseDTO resultado = fornecedorService.atualizar(1, requestAtualizado);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdFornecedor()).isEqualTo(1);
        assertThat(resultado.getNome()).isEqualTo("Fornecedor Frutas Tropicais Ltda");
        assertThat(resultado.getTelefone()).isEqualTo("11999887766");
        assertThat(resultado.getEndereco()).isEqualTo("Avenida das Frutas, 999");
        assertThat(resultado.getProdutosFornecidos()).isEqualTo("Manga, Abacaxi, Coco");

        verify(fornecedorRepository, times(1)).findById(1);
        verify(fornecedorRepository, times(1)).findByEmail(requestAtualizado.getEmail());
        verify(fornecedorRepository, times(1)).save(any(Fornecedor.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar fornecedor inexistente")
    void deveLancarExcecaoAoAtualizarFornecedorInexistente() {
        // Arrange
        when(fornecedorRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> fornecedorService.atualizar(999, requestDTO))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Fornecedor")
                .hasMessageContaining("id")
                .hasMessageContaining("999");

        verify(fornecedorRepository, times(1)).findById(999);
        verify(fornecedorRepository, never()).save(any(Fornecedor.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar com email já utilizado por outro fornecedor")
    void deveLancarExcecaoAoAtualizarComEmailDuplicado() {
        // Arrange
        Fornecedor outroFornecedor = Fornecedor.builder()
                .idFornecedor(2)
                .nome("Outro Fornecedor")
                .email("contato@frutas.com")
                .build();

        when(fornecedorRepository.findById(1)).thenReturn(Optional.of(fornecedor));
        when(fornecedorRepository.findByEmail(requestDTO.getEmail()))
                .thenReturn(Optional.of(outroFornecedor));

        // Act & Assert
        assertThatThrownBy(() -> fornecedorService.atualizar(1, requestDTO))
                .isInstanceOf(RecursoDuplicadoException.class)
                .hasMessageContaining("Fornecedor")
                .hasMessageContaining("email")
                .hasMessageContaining("contato@frutas.com");

        verify(fornecedorRepository, times(1)).findById(1);
        verify(fornecedorRepository, times(1)).findByEmail(requestDTO.getEmail());
        verify(fornecedorRepository, never()).save(any(Fornecedor.class));
    }

    @Test
    @DisplayName("Deve atualizar fornecedor mantendo o mesmo email")
    void deveAtualizarFornecedorMantendoMesmoEmail() {
        // Arrange
        when(fornecedorRepository.findById(1)).thenReturn(Optional.of(fornecedor));
        when(fornecedorRepository.findByEmail(requestDTO.getEmail()))
                .thenReturn(Optional.of(fornecedor)); // Mesmo fornecedor
        when(fornecedorRepository.save(any(Fornecedor.class))).thenReturn(fornecedor);

        // Act
        FornecedorResponseDTO resultado = fornecedorService.atualizar(1, requestDTO);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getEmail()).isEqualTo("contato@frutas.com");

        verify(fornecedorRepository, times(1)).findById(1);
        verify(fornecedorRepository, times(1)).findByEmail(requestDTO.getEmail());
        verify(fornecedorRepository, times(1)).save(any(Fornecedor.class));
    }

    // ========== TESTES DE EXCLUSÃO ==========

    @Test
    @DisplayName("Deve excluir fornecedor com sucesso")
    void deveExcluirFornecedorComSucesso() {
        // Arrange
        when(fornecedorRepository.findById(1)).thenReturn(Optional.of(fornecedor));
        doNothing().when(fornecedorRepository).delete(fornecedor);

        // Act
        fornecedorService.excluir(1);

        // Assert
        verify(fornecedorRepository, times(1)).findById(1);
        verify(fornecedorRepository, times(1)).delete(fornecedor);
    }

    @Test
    @DisplayName("Deve lançar exceção ao excluir fornecedor inexistente")
    void deveLancarExcecaoAoExcluirFornecedorInexistente() {
        // Arrange
        when(fornecedorRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> fornecedorService.excluir(999))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Fornecedor")
                .hasMessageContaining("id")
                .hasMessageContaining("999");

        verify(fornecedorRepository, times(1)).findById(999);
        verify(fornecedorRepository, never()).delete(any(Fornecedor.class));
    }
}