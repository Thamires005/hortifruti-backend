package br.unip.ads.pim.meuhortifruti.controller;

import br.unip.ads.pim.meuhortifruti.dto.ProdutoRequestDTO;
import br.unip.ads.pim.meuhortifruti.dto.ProdutoResponseDTO;
import br.unip.ads.pim.meuhortifruti.service.ProdutoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/v1/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;

    @GetMapping
    public ResponseEntity<List<ProdutoResponseDTO>> listarTodos() {
        List<ProdutoResponseDTO> produtos = produtoService.listarTodos();
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(@PathVariable Integer id) {
        ProdutoResponseDTO produto = produtoService.buscarPorId(id);
        return ResponseEntity.ok(produto);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarPorNome(@RequestParam String nome) {
        List<ProdutoResponseDTO> produtos = produtoService.buscarPorNome(nome);
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/categoria/{idCategoria}")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarPorCategoria(@PathVariable Integer idCategoria) {
        List<ProdutoResponseDTO> produtos = produtoService.buscarPorCategoria(idCategoria);
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/estoque-baixo")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarComEstoqueBaixo(
            @RequestParam(defaultValue = "10") Integer quantidade) {
        List<ProdutoResponseDTO> produtos = produtoService.buscarProdutosComEstoqueBaixo(quantidade);
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/proximo-vencimento")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarProximosDoVencimento(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        List<ProdutoResponseDTO> produtos = produtoService.buscarProdutosProximosDoVencimento(data);
        return ResponseEntity.ok(produtos);
    }

    @PostMapping
    @Secured("ROLE_ADMIN")
    public ResponseEntity<ProdutoResponseDTO> criar(@Valid @RequestBody ProdutoRequestDTO dto) {
        ProdutoResponseDTO produto = produtoService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(produto);
    }

    @PutMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<ProdutoResponseDTO> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody ProdutoRequestDTO dto) {
        ProdutoResponseDTO produto = produtoService.atualizar(id, dto);
        return ResponseEntity.ok(produto);
    }

    @DeleteMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        produtoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
