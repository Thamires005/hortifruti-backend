package br.unip.ads.pim.meuhortifruti.dto;

import jakarta.validation.constraints.*;
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
public class ProdutoRequestDTO {

    @NotBlank(message = "O nome do produto é obrigatório")
    @Size(min = 3, max = 300, message = "O nome deve ter entre 3 até 100 caracteres")
    private String nome;

    @NotNull(message = "O preço do produto é obrigatório")
    @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero")
    private BigDecimal preco;

    @NotNull(message = "A quantidade de produto é obrigatório")
    @DecimalMin(value = "A quantidade não pode ser negativa")
    private Integer quantidadeEstoque;

    @NotNull
    private LocalDate dataEntrega;

    @Future(message = "Data de validade deverá ser futura")
    private LocalDate dtValidade;
}