package br.unip.ads.pim.meuhortifruti.controller;

import br.unip.ads.pim.meuhortifruti.dto.ItemCompraRequestDTO;
import br.unip.ads.pim.meuhortifruti.dto.ItemCompraResponseDTO;
import br.unip.ads.pim.meuhortifruti.service.ItemPedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/itens-pedido")
@RequiredArgsConstructor
public class ItemPedidoController {
    
    private final ItemPedidoService itemPedidoService;

    @GetMapping
    public ResponseEntity<List<ItemCompraResponseDTO>> listarTodos() {
        List<ItemCompraResponseDTO> itensPedido = itemPedidoService.listarTodos();
        return ResponseEntity.ok(itensPedido);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemCompraResponseDTO> buscarPorId(@PathVariable Integer id) {
        ItemCompraResponseDTO itemPedido = itemPedidoService.buscarPorId(id);
        return ResponseEntity.ok(itemPedido);
    }

    @PostMapping
    @Secured("ROLE_USER")
    public ResponseEntity<ItemCompraResponseDTO> criar(@Valid @RequestBody ItemCompraRequestDTO dto) {
        ItemCompraResponseDTO itemPedido = itemPedidoService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(itemPedido);
    }

    @PutMapping("/{id}")
    @Secured("ROLE_USER")
    public ResponseEntity<ItemCompraResponseDTO> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody ItemCompraRequestDTO dto) {
        ItemCompraResponseDTO itemPedido = itemPedidoService.atualizar(id, dto);
        return ResponseEntity.ok(itemPedido);
    }

    @DeleteMapping("/{id}")
    @Secured("ROLE_USER")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        itemPedidoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

}
