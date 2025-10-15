package br.unip.ads.pim.meuhortifruti.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pedido")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pedido implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Integer idPedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @NotBlank(message = "Status do pedido é obrigatório")
    @Column(name = "status_pedido", nullable = false, length = 100)
    private String StatusPedido;

    @Builder.Default
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<ItemPedido> itensPedidos = new ArrayList<>();

    @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pagamento", nullable = false)
    private Pagamento pagamento;

    @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_entrega", nullable = false)
    private Entrega entrega;
}
