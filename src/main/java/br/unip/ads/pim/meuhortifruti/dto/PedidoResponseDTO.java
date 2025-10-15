package br.unip.ads.pim.meuhortifruti.dto;

import java.util.List;

import br.unip.ads.pim.meuhortifruti.entity.Cliente;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponseDTO {
     
    private Integer idPedido;
    private Cliente cliente;
    private String StatusPedido;
    private List<ItemPedidoRequestDTO> itensPedidos;
    private PagamentoResponseDTO pagamento;
    private EntregaResponseDTO entrega;
}
