package br.unip.ads.pim.meuhortifruti.controller;

import br.unip.ads.pim.meuhortifruti.dto.CarrinhoItemDTO;
import br.unip.ads.pim.meuhortifruti.dto.CarrinhoResponseDTO;
import br.unip.ads.pim.meuhortifruti.service.CarrinhoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/carrinho")
@RequiredArgsConstructor
@Secured("ROLE_CLIENTE")
public class CarrinhoController {

    private final CarrinhoService carrinhoService;

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<CarrinhoResponseDTO> buscarPorCliente(@PathVariable Integer idCliente) {
        CarrinhoResponseDTO carrinho = carrinhoService.buscarPorCliente(idCliente);
        return ResponseEntity.ok(carrinho);
    }

    @PostMapping("/cliente/{idCliente}/item")
    public ResponseEntity<CarrinhoResponseDTO> adicionarItem(
            @PathVariable Integer idCliente,
            @Valid @RequestBody CarrinhoItemDTO itemDTO) {
        CarrinhoResponseDTO carrinho = carrinhoService.adicionarItem(idCliente, itemDTO);
        return ResponseEntity.ok(carrinho);
    }

    @DeleteMapping("/cliente/{idCliente}/item/{idProduto}")
    public ResponseEntity<CarrinhoResponseDTO> removerItem(
            @PathVariable Integer idCliente,
            @PathVariable Integer idProduto) {
        CarrinhoResponseDTO carrinho = carrinhoService.removerItem(idCliente, idProduto);
        return ResponseEntity.ok(carrinho);
    }

    @PutMapping("/cliente/{idCliente}/item")
    public ResponseEntity<CarrinhoResponseDTO> atualizarQuantidade(
            @PathVariable Integer idCliente,
            @Valid @RequestBody CarrinhoItemDTO itemDTO) {
        CarrinhoResponseDTO carrinho = carrinhoService.atualizarQuantidade(idCliente, itemDTO);
        return ResponseEntity.ok(carrinho);
    }

    @DeleteMapping("/cliente/{idCliente}")
    public ResponseEntity<Void> limpar(@PathVariable Integer idCliente) {
        carrinhoService.limpar(idCliente);
        return ResponseEntity.noContent().build();
    }
}
