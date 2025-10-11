package br.unip.ads.pim.meuhortifruti.controller;

import br.unip.ads.pim.meuhortifruti.dto.EstoqueRequestDTO;
import br.unip.ads.pim.meuhortifruti.dto.EstoqueResponseDTO;
import br.unip.ads.pim.meuhortifruti.service.EstoqueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/estoque")
@RequiredArgsConstructor
@Secured("ROLE_ADMIN")
public class EstoqueController {

    private final EstoqueService estoqueService;

    @GetMapping
    public ResponseEntity<List<EstoqueResponseDTO>> listarTodos() {
        List<EstoqueResponseDTO> estoques = estoqueService.listarTodos();
        return ResponseEntity.ok(estoques);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstoqueResponseDTO> buscarPorId(@PathVariable Integer id) {
        EstoqueResponseDTO estoque = estoqueService.buscarPorId(id);
        return ResponseEntity.ok(estoque);
    }

    @GetMapping("/produto/{idProduto}")
    public ResponseEntity<EstoqueResponseDTO> buscarPorProduto(@PathVariable Integer idProduto) {
        EstoqueResponseDTO estoque = estoqueService.buscarPorProduto(idProduto);
        return ResponseEntity.ok(estoque);
    }

    @PostMapping
    public ResponseEntity<EstoqueResponseDTO> criar(@Valid @RequestBody EstoqueRequestDTO dto) {
        EstoqueResponseDTO estoque = estoqueService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(estoque);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstoqueResponseDTO> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody EstoqueRequestDTO dto) {
        EstoqueResponseDTO estoque = estoqueService.atualizar(id, dto);
        return ResponseEntity.ok(estoque);
    }

    @PatchMapping("/adicionar/{idProduto}")
    public ResponseEntity<EstoqueResponseDTO> adicionarQuantidade(
            @PathVariable Integer idProduto,
            @RequestParam Integer quantidade) {
        EstoqueResponseDTO estoque = estoqueService.adicionarQuantidade(idProduto, quantidade);
        return ResponseEntity.ok(estoque);
    }

    @PatchMapping("/remover/{idProduto}")
    public ResponseEntity<EstoqueResponseDTO> removerQuantidade(
            @PathVariable Integer idProduto,
            @RequestParam Integer quantidade) {
        EstoqueResponseDTO estoque = estoqueService.removerQuantidade(idProduto, quantidade);
        return ResponseEntity.ok(estoque);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        estoqueService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
