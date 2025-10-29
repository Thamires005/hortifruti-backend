package br.unip.ads.pim.meuhortifruti.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoResponseDTO {

    private Integer idProduto;
    private Integer quantidadeEstoque;
    private Integer idCategoria;
    private String nome;
    private BigDecimal preco;
}
