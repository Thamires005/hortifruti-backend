package br.unip.ads.pim.meuhortifruti.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequestDTO {

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 5)
    private String senha;

    @NotBlank(message = "O nome do usuário é obrigatório")
    private String nome;
}