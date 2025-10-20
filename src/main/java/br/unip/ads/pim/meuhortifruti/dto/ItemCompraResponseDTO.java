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
public class ItemCompraResponseDTO {
    
     private Integer idItemCompra;
     private ProdutoResponseDTO produto;
     private BigDecimal preco;
     private Integer quantidade;
}
