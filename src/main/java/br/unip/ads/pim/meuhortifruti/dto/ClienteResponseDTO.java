package br.unip.ads.pim.meuhortifruti.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponseDTO {

    private Integer idUsuario;
    private String nome;
    private String cpf;
    private String endereco;
    private String telefone;
    private String email;
    private LocalDate dtNascimento;
}
