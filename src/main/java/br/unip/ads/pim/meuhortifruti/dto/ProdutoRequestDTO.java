package br.unip.ads.pim.meuhortifruti.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoRequestDTO {
    @NotBlank(message = "O nome do produto é obrigatório")
    @Size(min = 3, max = 300, message = "O nome deve ter entre 3 até 100 caracteres")
    private String nome;

    @NotNull(message = "O preço do produto é obrigatório")
    @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero")
    private BigDecimal preco;

    @NotNull(message = "O preço do produto é obrigatório")
    @DecimalMin(value = "A quantidade não pode ser negativa")
    private Integer quantidadeEstoque;
}