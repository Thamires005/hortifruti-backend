package br.unip.ads.pim.meuhortifruti.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraResponseDTO {
     
    private Integer idCompra;
    private String statusCompra;
    private List<ItemCompraResponseDTO> itensCompra;
    private PagamentoResponseDTO pagamento;
    
}
