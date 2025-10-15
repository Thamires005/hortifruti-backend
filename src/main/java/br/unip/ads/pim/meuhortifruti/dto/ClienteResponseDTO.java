package br.unip.ads.pim.meuhortifruti.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponseDTO {
    
    private Integer idCliente;
    private String nome;
    private String cpf; 
    private String endereco;
    private String telefone;
    private String email;
    private LocalDate dtNascimento;
}
