package br.unip.ads.pim.meuhortifruti.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
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
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Table(name = "produto")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Produto implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produto")
    private Integer idProduto;

    @NotBlank(message = "O nome de produto é obrigatório")
    @Column(name = "nome", nullable = false, length = 200)
    private String nome;

    @NotNull(message = "O preço do produto é obrigatório")
    @DecimalMin(value = "0.01", message = "O preço do produto deve ser maior que zero")
    @Column(name = "preco", nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @NotNull(message = "A quantidade do produto é obrigatória")
    @Min(value = 0, message = "A quantidade não pode ser negativa")
    @Column(name = "quant_estoque", nullable = false)
    private Integer quantEstoque;

    @Future(message = "Data de validade deverá ser futura")
    @Column(name = "dt_validade")
    private LocalDate dtvalidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estoque", nullable = false)
    private Estoque estoque;

    @Builder.Default
    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL)
    private List<Fornece> fornecimetos = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL)
    private List<ItemPedido> itensPedido = new ArrayList<>();
    
    @Builder.Default
    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL)
    private List<Carrinho> carrinhos = new ArrayList<>();
}
