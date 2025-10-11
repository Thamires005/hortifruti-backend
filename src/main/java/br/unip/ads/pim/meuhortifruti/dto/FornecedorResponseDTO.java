package br.unip.ads.pim.meuhortifruti.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FornecedorResponseDTO {

    private Integer idFornecedor;
    private String nome;
    private String cnpj;
    private String telefone;
    private String email;
    private String endereco;
    private String prodFornecidos;
}