package br.unip.ads.pim.meuhortifruti.controller;

import br.unip.ads.pim.meuhortifruti.dto.UsuarioRequestDTO;
import br.unip.ads.pim.meuhortifruti.dto.UsuarioResponseDTO;
import br.unip.ads.pim.meuhortifruti.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    
    private final UsuarioService usuarioService;


    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodas() {
        List<UsuarioResponseDTO> usuarios = usuarioService.listarTodas();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Integer id) {
        UsuarioResponseDTO usuario = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(usuario);
    }

    @PostMapping
    @Secured("ROLE_ADMIN")
    public ResponseEntity<UsuarioResponseDTO> criar(@Valid @RequestBody UsuarioRequestDTO  dto) {
        UsuarioResponseDTO usuario = usuarioService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    @PutMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<UsuarioResponseDTO> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody UsuarioRequestDTO  dto) {
        UsuarioResponseDTO usuario = usuarioService.atualizar(id, dto);
        return ResponseEntity.ok(usuario);
    }

    @DeleteMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        usuarioService.excluir(id);
        return ResponseEntity.noContent().build();
    }

}
