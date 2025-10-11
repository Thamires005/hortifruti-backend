package br.unip.ads.pim.meuhortifruti.dto;

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
    private String nome;
    private BigDecimal preco;
    private Integer quantidade;
    private LocalDate dtValidade;
    private Integer idCategoria;
    private String nomeCategoria;
}
