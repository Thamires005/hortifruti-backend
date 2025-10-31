package br.unip.ads.pim.meuhortifruti.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstoqueResponseDTO {

    private Integer iEstoque;
    private ProdutoResponseDTO produto;
    private Integer quantidadeProdutos;
}
