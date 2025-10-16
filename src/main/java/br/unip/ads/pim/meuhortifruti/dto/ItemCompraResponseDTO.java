package br.unip.ads.pim.meuhortifruti.dto;

import java.math.BigDecimal;

import br.unip.ads.pim.meuhortifruti.entity.Produto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemCompraResponseDTO {
    
     private Integer idItemCompra;
     private Produto produto;
     private BigDecimal preco;
     private Integer quantidade;
}
