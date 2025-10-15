package br.unip.ads.pim.meuhortifruti.dto;

import br.unip.ads.pim.meuhortifruti.entity.Pedido;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntregaRequestDTO {
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;

    @NotBlank(message = "Endereço é obrigatório")
    private String endereco;

    @NotBlank(message = "Status da entrega é obrigatório")
    private String statusEntrega;
}
