package br.unip.ads.pim.meuhortifruti.dto;

import br.unip.ads.pim.meuhortifruti.entity.Produto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstoqueResponseDTO {
    
    private Integer idEstoque;
    private Produto produto;
    private Integer quantProdutos;
}
