package br.unip.ads.pim.meuhortifruti.dto;

import java.util.ArrayList;
import java.util.List;

import br.unip.ads.pim.meuhortifruti.entity.Cliente;
import br.unip.ads.pim.meuhortifruti.entity.ItemPedido;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoRequestDTO {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @NotBlank(message = "Status do pedido é obrigatório")
    private String StatusPedido;

    @Builder.Default
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<ItemPedido> itensPedidos = new ArrayList<>();
}
