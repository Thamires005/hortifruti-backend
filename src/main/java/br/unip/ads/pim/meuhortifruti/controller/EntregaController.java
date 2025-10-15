package br.unip.ads.pim.meuhortifruti.controller;

import br.unip.ads.pim.meuhortifruti.dto.EntregaRequestDTO;
import br.unip.ads.pim.meuhortifruti.dto.EntregaResponseDTO;
import br.unip.ads.pim.meuhortifruti.service.EntregaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/entregas")
@RequiredArgsConstructor
public class EntregaController {

    private final EntregaService entregaService;

    @GetMapping
    public ResponseEntity<List<EntregaResponseDTO>> listarTodas() {
        List<EntregaResponseDTO> entregas = entregaService.listarTodas();
        return ResponseEntity.ok(entregas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntregaResponseDTO> buscarPorId(@PathVariable Integer id) {
        EntregaResponseDTO entrega = entregaService.buscarPorId(id);
        return ResponseEntity.ok(entrega);
    }

    @PostMapping
    @Secured("ROLE_USER")
    public ResponseEntity<EntregaResponseDTO> criar(@Valid @RequestBody EntregaRequestDTO dto) {
        EntregaResponseDTO entrega = entregaService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(entrega);
    }

    @PutMapping("/{id}")
    @Secured("ROLE_USER")
    public ResponseEntity<EntregaResponseDTO> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody EntregaRequestDTO dto) {
        EntregaResponseDTO entrega = entregaService.atualizar(id, dto);
        return ResponseEntity.ok(entrega);
    }

    @DeleteMapping("/{id}")
    @Secured("ROLE_USER")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        entregaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
    
}
