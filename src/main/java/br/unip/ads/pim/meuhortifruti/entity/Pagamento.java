package br.unip.ads.pim.meuhortifruti.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "pagamento")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pagamento implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pagamento")
    private Integer idPagamento;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido", nullable = false, unique = true)
    private Pedido pedido;

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    @Column(name = "valor", nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @NotBlank(message = "Método de pagamento é obrigatório")
    @Column(name = "metodo_pagamento", nullable = false, length = 50)
    private String metodoPagamento;

    @NotBlank(message = "Status do pagamento é obrigatório")
    @Column(name = "status_pagamento", nullable = false, length = 50)
    private String statusPagamento;
}