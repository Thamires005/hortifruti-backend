package br.unip.ads.pim.meuhortifruti.dto;

import java.math.BigDecimal;
import java.util.List;

import br.unip.ads.pim.meuhortifruti.entity.Cliente;
import br.unip.ads.pim.meuhortifruti.entity.Produto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarrinhoResponseDTO {

    private Integer idCarrinho;
    private Cliente cliente;
    private List<Produto> produtos;
    private Integer quantProdutos;
    private BigDecimal valorTotal;
}
