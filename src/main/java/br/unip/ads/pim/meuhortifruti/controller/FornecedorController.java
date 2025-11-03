package br.unip.ads.pim.meuhortifruti.controller;

import br.unip.ads.pim.meuhortifruti.dto.FornecedorResponseDTO;
import br.unip.ads.pim.meuhortifruti.dto.FornecedorRequestDTO;
import br.unip.ads.pim.meuhortifruti.service.FornecedorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/fornecedores")
@RequiredArgsConstructor
public class FornecedorController {

    private final FornecedorService fornecedorService;


    @GetMapping
    public ResponseEntity<List<FornecedorResponseDTO>> listarTodas() {
        List<FornecedorResponseDTO> fornecedores = fornecedorService.listarTodas();
        return ResponseEntity.ok(fornecedores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FornecedorResponseDTO> buscarPorId(@PathVariable Integer id) {
        FornecedorResponseDTO fornecedor = fornecedorService.buscarPorId(id);
        return ResponseEntity.ok(fornecedor);
    }

    @PostMapping
    @Secured("ROLE_ADMIN")
    public ResponseEntity<FornecedorResponseDTO> criar(@Valid @RequestBody FornecedorRequestDTO dto) {
        FornecedorResponseDTO fornecedor = fornecedorService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(fornecedor);
    }

    @PutMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<FornecedorResponseDTO> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody FornecedorRequestDTO dto) {
        FornecedorResponseDTO fornecedor = fornecedorService.atualizar(id, dto);
        return ResponseEntity.ok(fornecedor);
    }

    @DeleteMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        fornecedorService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}