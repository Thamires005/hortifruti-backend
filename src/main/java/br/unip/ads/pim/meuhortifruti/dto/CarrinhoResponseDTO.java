package br.unip.ads.pim.meuhortifruti.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarrinhoResponseDTO {

    private Integer idCarrinho;
    private Integer idCliente;
    private Integer quantProdutos;
    private BigDecimal valorTotal;
    private List<ProdutoCarrinhoDTO> produtos;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProdutoCarrinhoDTO {
        private Integer idProduto;
        private String nome;
        private BigDecimal preco;
        private Integer quantidade;
        private BigDecimal subtotal;
    }
}
