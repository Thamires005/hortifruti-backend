package br.unip.ads.pim.meuhortifruti.entity;

import java.io.Serializable;
import java.math.BigDecimal;
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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "carrinho")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Carrinho implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_carrinho")
    private Integer idCarrinho;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @Builder.Default
    @OneToMany(mappedBy = "carrinho", cascade = CascadeType.ALL)
    private List<Produto> produtos = new ArrayList<>();

    @Column(name = "quant_produtos")
    private Integer quantProdutos;

    @Column(name = "valor_total")
    private BigDecimal valorTotal;
}
