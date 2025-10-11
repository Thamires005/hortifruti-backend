package br.unip.ads.pim.meuhortifruti.controller;

import br.unip.ads.pim.meuhortifruti.dto.PedidoRequestDTO;
import br.unip.ads.pim.meuhortifruti.dto.PedidoResponseDTO;
import br.unip.ads.pim.meuhortifruti.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/pedidos")
@RequiredArgsConstructor
@Secured("ROLE_CLIENTE")
public class PedidoController {

    private final PedidoService pedidoService;

    @GetMapping
    @Secured("ROLE_ADMIN")
    public ResponseEntity<List<PedidoResponseDTO>> listarTodos() {
        List<PedidoResponseDTO> pedidos = pedidoService.listarTodos();
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> buscarPorId(@PathVariable Integer id) {
        PedidoResponseDTO pedido = pedidoService.buscarPorId(id);
        return ResponseEntity.ok(pedido);
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<PedidoResponseDTO>> buscarPorCliente(@PathVariable Integer idCliente) {
        List<PedidoResponseDTO> pedidos = pedidoService.buscarPorCliente(idCliente);
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/status/{status}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<List<PedidoResponseDTO>> buscarPorStatus(@PathVariable String status) {
        List<PedidoResponseDTO> pedidos = pedidoService.buscarPorStatus(status);
        return ResponseEntity.ok(pedidos);
    }

    @PostMapping
    public ResponseEntity<PedidoResponseDTO> criar(@Valid @RequestBody PedidoRequestDTO dto) {
        PedidoResponseDTO pedido = pedidoService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
    }

    @PatchMapping("/{id}/status")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<PedidoResponseDTO> atualizarStatus(
            @PathVariable Integer id,
            @RequestParam String status) {
        PedidoResponseDTO pedido = pedidoService.atualizarStatus(id, status);
        return ResponseEntity.ok(pedido);
    }

    @PatchMapping("/{id}/pagamento/status")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<PedidoResponseDTO> atualizarStatusPagamento(
            @PathVariable Integer id,
            @RequestParam String status) {
        PedidoResponseDTO pedido = pedidoService.atualizarStatusPagamento(id, status);
        return ResponseEntity.ok(pedido);
    }

    @PatchMapping("/{id}/entrega/status")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<PedidoResponseDTO> atualizarStatusEntrega(
            @PathVariable Integer id,
            @RequestParam String status) {
        PedidoResponseDTO pedido = pedidoService.atualizarStatusEntrega(id, status);
        return ResponseEntity.ok(pedido);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Integer id) {
        pedidoService.cancelar(id);
        return ResponseEntity.noContent().build();
    }
}
