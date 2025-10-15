package br.unip.ads.pim.meuhortifruti.controller;

import br.unip.ads.pim.meuhortifruti.dto.CarrinhoRequestDTO;
import br.unip.ads.pim.meuhortifruti.dto.CarrinhoResponseDTO;
import br.unip.ads.pim.meuhortifruti.service.CarrinhoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/carrinhos")
@RequiredArgsConstructor

public class CarrinhoController {

    private final carrinhoService carrinhoSevice;

    @GetMapping
    public ResponseEntity<List<CarrinhoResponseDTO>> listarTodos() {
        List<CarrinhoResponseDTO> carrinhos = carrinhoSevice.listarTodos();
        return ResponseEntity.ok(carrinhos);
    }

    @GetMapping("/{id}")    
    public ResponseEntity<CarrinhoResponseDTO> buscarPorId(@PathVariable Integer id) {
        CarrinhoResponseDTO carrinho = carrinhoSevice.buscarPorId(id);
        return ResponseEntity.ok(carrinho);
    }
    @PostMapping
    @Secured("ROLE_USER")
    public ResponseEntity<CarrinhoResponseDTO> criar(@Valid @RequestBody CarrinhoRequestDTO dto) {
        CarrinhoResponseDTO carrinho = carrinhoSevice.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(carrinho);
    }
    @PutMapping("/{id}")
    @Secured("ROLE_USER")
    public ResponseEntity<CarrinhoResponseDTO> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody CarrinhoRequestDTO dto) {
        CarrinhoResponseDTO carrinho = carrinhoSevice.atualizar(id, dto);
        return ResponseEntity.ok(carrinho);
    }
    @DeleteMapping("/{id}")
    @Secured("ROLE_USER")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        carrinhoSevice.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
