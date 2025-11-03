package br.unip.ads.pim.meuhortifruti.controller;

import br.unip.ads.pim.meuhortifruti.dto.PerfilRequestDTO;
import br.unip.ads.pim.meuhortifruti.dto.PerfilResponseDTO;
import br.unip.ads.pim.meuhortifruti.service.PerfilService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1/perfils")
@RequiredArgsConstructor
public class PerfilController {

    private final PerfilService perfilService;

    @GetMapping("/{id}")
    public ResponseEntity<PerfilResponseDTO> buscarPorId(@PathVariable Integer id) {
        PerfilResponseDTO perfil = perfilService.buscarPorId(id);
        return ResponseEntity.ok(perfil);
    }

    @PostMapping
    @Secured("ROLE_ADMIN")
    public ResponseEntity<PerfilResponseDTO> criar(@Valid @RequestBody PerfilRequestDTO dto) {
        PerfilResponseDTO perfil = perfilService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(perfil);
    }

    @PutMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<PerfilResponseDTO> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody PerfilRequestDTO dto) {
        PerfilResponseDTO perfil = perfilService.atualizar(id, dto);
        return ResponseEntity.ok(perfil);
    }

    @DeleteMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        perfilService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
