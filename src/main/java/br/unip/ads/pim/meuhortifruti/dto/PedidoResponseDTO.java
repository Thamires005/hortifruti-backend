package br.unip.ads.pim.meuhortifruti.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponseDTO {

    private Integer idPedido;
    private Integer idCliente;
    private String nomeCliente;
    private String statusPedido;
    private BigDecimal valorTotal;
    private List<ItemPedidoDTO> itens;
    private PagamentoDTO pagamento;
    private EntregaDTO entrega;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemPedidoDTO {
        private Integer idItemPedido;
        private Integer idProduto;
        private String nomeProduto;
        private BigDecimal preco;
        private Integer quantidade;
        private BigDecimal subtotal;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PagamentoDTO {
        private Integer idPagamento;
        private BigDecimal valor;
        private String metodoPagamento;
        private String statusPagamento;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EntregaDTO {
        private Integer idEntrega;
        private String endereco;
        private String statusEntrega;
        private LocalDateTime dtEntrega;
    }
}
