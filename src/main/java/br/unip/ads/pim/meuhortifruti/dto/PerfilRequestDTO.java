package br.unip.ads.pim.meuhortifruti.dto;

import jakarta.validation.constraints.NotBlank;

public class PerfilRequestDTO {
    
    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "Descrição é obrigatória")
    private String descricao;
}
