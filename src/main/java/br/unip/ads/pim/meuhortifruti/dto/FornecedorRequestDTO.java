package br.unip.ads.pim.meuhortifruti.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FornecedorRequestDTO {
    
    @NotBlank(message = "Nome do Fornecedor é obrigatório")
    private String nome;

    @NotBlank(message = "CNPJ é obrigatório")
    @Pattern(regexp = "\\d{14}", message = "O CNPJ deve conter 14 dígitos")
    private String cnpj; 

    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(regexp = "\\d{9,11}", message = "Telefone deve conter de 9 ou 11 dígitos")
    private String telefone;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail deve ser válido")
    private String email;

    @NotBlank(message = "Endereço é obrigatório")
    private String endereco;

    @NotBlank(message = "Produtos fornecidos é obrigatório")
    private String prodFornecidos;
}
