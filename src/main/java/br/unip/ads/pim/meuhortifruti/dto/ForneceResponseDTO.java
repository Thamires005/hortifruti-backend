package br.unip.ads.pim.meuhortifruti.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.unip.ads.pim.meuhortifruti.entity.Fornecedor;
import br.unip.ads.pim.meuhortifruti.entity.Produto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForneceResponseDTO {
    
    private LocalDate data;
    private Integer quantidade;
    private BigDecimal valor; 
    private Fornecedor fornecedor;
    private Produto produto;
}
