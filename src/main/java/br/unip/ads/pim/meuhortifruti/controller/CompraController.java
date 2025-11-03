package br.unip.ads.pim.meuhortifruti.controller;

import br.unip.ads.pim.meuhortifruti.dto.CompraRequestDTO;
import br.unip.ads.pim.meuhortifruti.dto.CompraResponseDTO;
import br.unip.ads.pim.meuhortifruti.service.CompraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/compras")
@RequiredArgsConstructor
public class CompraController {

    private final CompraService compraService;

    @GetMapping
    public ResponseEntity<List<CompraResponseDTO>> listarTodas() {
        List<CompraResponseDTO> compras = compraService.listarTodas();
        return ResponseEntity.ok(compras);
    }
    @GetMapping("/{id}")
    public ResponseEntity<CompraResponseDTO> buscarPorId(@PathVariable Integer id) {
        CompraResponseDTO compra = compraService.buscarPorId(id);
        return ResponseEntity.ok(compra);
    }
    @PostMapping
    @Secured("ROLE_ADMIN")
    public ResponseEntity<CompraResponseDTO> criar(@Valid @RequestBody CompraRequestDTO dto) {
        CompraResponseDTO compra = compraService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(compra);
    }
    @PutMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<CompraResponseDTO> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody CompraRequestDTO dto) {
        CompraResponseDTO compra = compraService.atualizar(id, dto);
        return ResponseEntity.ok(compra);
    }
    @DeleteMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        compraService.excluir(id);
        return ResponseEntity.noContent().build();
    }

}
