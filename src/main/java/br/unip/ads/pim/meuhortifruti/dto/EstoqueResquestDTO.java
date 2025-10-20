package br.unip.ads.pim.meuhortifruti.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstoqueResquestDTO {
    
    @NotNull
    private Integer idproduto;

    @NotNull(message = "A quantidade de produtos no estoque é obrigatória")
    @Min(value = 0, message = "A quantidade não pode ser negativa")
    private Integer quantProdutos;
}
