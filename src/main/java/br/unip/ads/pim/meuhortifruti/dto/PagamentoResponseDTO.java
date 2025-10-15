package br.unip.ads.pim.meuhortifruti.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagamentoResponseDTO {
    
    private Integer idPagamento;
    private BigDecimal valor;
    private String formaPagamento;
    private String statusPagamento;
}
