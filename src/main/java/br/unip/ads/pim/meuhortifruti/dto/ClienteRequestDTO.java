package br.unip.ads.pim.meuhortifruti.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteRequestDTO {
    
    @NotBlank(message = "Nome do cliente é obrigatório")
    private String nome;

    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "\\d{10,11}", message = "CPF deve conter 11 dígitos")
    private String cpf; 

    @NotBlank(message = "Endereço é obrigatório")
    private String endereco;

    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(regexp = "\\d{9,11}", message = "Telefone deve conter de 9 ou 11 dígitos")
    private String telefone;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail deve ser válido")
    private String email;

    @Past(message = "A data de nascimento deve ser no passado")
    private LocalDate dtNascimento;
}
