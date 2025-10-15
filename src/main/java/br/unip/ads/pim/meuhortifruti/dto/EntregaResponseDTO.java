package br.unip.ads.pim.meuhortifruti.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntregaResponseDTO {
    
    private Integer idEntrega;
    private String endereco;
    private String statusEntrega;
    private LocalDateTime dtEntrega;
}
