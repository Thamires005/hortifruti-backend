
package br.unip.ads.pim.meuhortifruti.controller;

import br.unip.ads.pim.meuhortifruti.dto.ForneceRequestDTO;
import br.unip.ads.pim.meuhortifruti.dto.ForneceResponseDTO;
import br.unip.ads.pim.meuhortifruti.service.ForneceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/forneces")
@RequiredArgsConstructor
public class ForneceController {

    private final ForneceService forneceService;

    public ForneceController(ForneceService forneceService) {
        this.forneceService = forneceService;
    }

    @GetMapping
    public ResponseEntity<List<ForneceResponseDTO>> listarTodas() {
        List<ForneceResponseDTO> forneces = forneceService.listarTodas();
        return ResponseEntity.ok(forneces);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ForneceResponseDTO> buscarPorId(@PathVariable Integer id) {
        ForneceResponseDTO fornece = forneceService.buscarPorId(id);
        return ResponseEntity.ok(fornece);
    }
    @PostMapping
    @Secured("ROLE_ADMIN")
    public ResponseEntity<ForneceResponseDTO> criar(@Valid @RequestBody ForneceRequestDTO dto) {
        ForneceResponseDTO fornece = forneceService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(fornece);
    }
    @PutMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<ForneceResponseDTO> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody ForneceRequestDTO dto) {
        ForneceResponseDTO fornece = forneceService.atualizar(id, dto);
        return ResponseEntity.ok(fornece);
    }
    @DeleteMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        forneceService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

