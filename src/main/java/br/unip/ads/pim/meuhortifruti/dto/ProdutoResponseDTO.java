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
public class ProdutoResponseDTO {
    
    private Integer idProduto;
    private String nome;
    private BigDecimal preco;
    private Integer quantEstoque;

}
