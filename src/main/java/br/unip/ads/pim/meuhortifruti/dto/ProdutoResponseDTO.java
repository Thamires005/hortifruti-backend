package br.unip.ads.pim.meuhortifruti.dto;

import br.unip.ads.pim.meuhortifruti.entity.Fornecedor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoResponseDTO {

    private Integer idProduto;
    private Integer quantidadeEstoque;
    private Integer idCategoria;
    private Fornecedor fornecedor;
    private LocalDate dataEntrega;
    private LocalDate dtValidade;
    private String nome;
    private BigDecimal preco;
}