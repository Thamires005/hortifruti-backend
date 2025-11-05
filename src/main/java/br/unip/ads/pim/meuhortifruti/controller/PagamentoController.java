package br.unip.ads.pim.meuhortifruti.controller;

import br.unip.ads.pim.meuhortifruti.dto.PagamentoRequestDTO;
import br.unip.ads.pim.meuhortifruti.dto.PagamentoResponseDTO;
import br.unip.ads.pim.meuhortifruti.service.PagamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/pagamentos")
@RequiredArgsConstructor
public class PagamentoController {

    private final PagamentoService pagamentoService;

    @GetMapping("/{id}")
    public ResponseEntity<PagamentoResponseDTO> buscarPorId(@PathVariable Integer id) {
        PagamentoResponseDTO pagamento = pagamentoService.buscarPorId(id);
        return ResponseEntity.ok(pagamento);
    }

    @PostMapping
    @Secured("ROLE_ADMIN")
    public ResponseEntity<PagamentoResponseDTO> criar(@Valid @RequestBody PagamentoRequestDTO dto) {
        PagamentoResponseDTO pagamento = pagamentoService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(pagamento);
    }

    @PutMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<PagamentoResponseDTO> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody PagamentoRequestDTO dto) {
        PagamentoResponseDTO pagamento = pagamentoService.atualizar(id, dto);
        return ResponseEntity.ok(pagamento);
    }

    @DeleteMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        pagamentoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
