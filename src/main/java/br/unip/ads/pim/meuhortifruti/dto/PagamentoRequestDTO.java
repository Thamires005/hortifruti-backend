package br.unip.ads.pim.meuhortifruti.dto;

import java.math.BigDecimal;

import br.unip.ads.pim.meuhortifruti.entity.Compra;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagamentoRequestDTO {

    private Compra compra;

    @NotNull(message = "O valor é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
    private BigDecimal valor;

    @NotNull(message = "A forma de pagamento é obrigatória")
    private String formaPagamento;

    @NotBlank(message = "Status do pagamento é obrigatório")
    private String statusPagamento;
}
