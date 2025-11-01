package br.unip.ads.pim.meuhortifruti.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "produto")

public class Produto implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produto")
    private Integer idProduto;

    @NotBlank(message = "Nome do produto é obrigatório")
    @Column(name = "nome", nullable = false, length = 200)
    private String nome;

    @NotNull
    @Column(name= "data", nullable = false)
    private LocalDate dataEntrega;

    @NotNull(message = "O preço do produto é obrigatório")
    @DecimalMin(value = "0.01", message = "O preço do produto deve ser maior que zero")
    @Column(name = "preço", nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @NotNull(message = "A quantidade do produto é obrigatório")
    @Min(value = 0, message = "A quantidade não pode ser negativa")
    @Column(name = "quantidade_Estoque", nullable = false)
    private Integer quantidadeEstoque;

    @Future(message = "Data da validade deverá ser futura")
    @Column(name = "dt_validade")
    private LocalDate dtValidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    /*@ManyToMany
    Set<Fornecedor> fornecimentos;
    private Fornecedor fornecedor;
     */

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estoque", nullable = false)
    private Estoque estoque;

}
    /*
    @Builder.Default
    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL)
    private List<ItemCompra> itensCompra = new ArrayList<>();
     */