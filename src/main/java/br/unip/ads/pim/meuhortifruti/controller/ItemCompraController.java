package br.unip.ads.pim.meuhortifruti.controller;

import br.unip.ads.pim.meuhortifruti.dto.ItemCompraRequestDTO;
import br.unip.ads.pim.meuhortifruti.dto.ItemCompraResponseDTO;
import br.unip.ads.pim.meuhortifruti.service.ItemCompraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/itemCompras")
@RequiredArgsConstructor
public class ItemCompraController {

    private final ItemCompraService itemCompraService;

    @GetMapping("/{id}")
    public ResponseEntity<ItemCompraResponseDTO> buscarPorId(@PathVariable Integer id) {
        ItemCompraResponseDTO itemCompra = itemCompraService.buscarPorId(id);
        return ResponseEntity.ok(itemCompra);
    }

    @PostMapping
    @Secured("ROLE_ADMIN")
    public ResponseEntity<ItemCompraResponseDTO> criar(@Valid @RequestBody ItemCompraRequestDTO dto) {
        ItemCompraResponseDTO itemCompra = itemCompraService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(itemCompra);
    }

    @PutMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<ItemCompraResponseDTO> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody ItemCompraRequestDTO dto) {
        ItemCompraResponseDTO itemCompra = itemCompraService.atualizar(id, dto);
        return ResponseEntity.ok(itemCompra);
    }

    @DeleteMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        itemCompraService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
