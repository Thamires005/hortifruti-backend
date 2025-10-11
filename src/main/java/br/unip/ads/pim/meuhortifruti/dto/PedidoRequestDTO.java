package br.unip.ads.pim.meuhortifruti.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoRequestDTO {

    @NotNull(message = "ID do cliente é obrigatório")
    private Integer idCliente;

    @NotBlank(message = "Endereço de entrega é obrigatório")
    private String enderecoEntrega;

    @NotBlank(message = "Método de pagamento é obrigatório")
    private String metodoPagamento;

    @NotEmpty(message = "Pedido deve conter ao menos um item")
    @Valid
    private List<ItemPedidoDTO> itens;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemPedidoDTO {
        
        @NotNull(message = "ID do produto é obrigatório")
        private Integer idProduto;
        
        @NotNull(message = "Quantidade é obrigatória")
        private Integer quantidade;
    }
}
