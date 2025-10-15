package br.unip.ads.pim.meuhortifruti.dto;

import java.math.BigDecimal;
import java.time.LocalDate;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForneceRequestDTO {
    
    @NotNull
    private LocalDate data;

    @NotNull(message = "A quantidade de fornecimentos é obrigatória")
    @Min(value = 0, message = "A quantidade não pode ser negativa")
    private Integer quantidade;

    @NotNull(message = "O valor dos produtos fornecidos é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
    private BigDecimal valor; 
}
