package br.unip.ads.pim.meuhortifruti.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "fornece")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Fornece implements Serializable {
    
    @NotNull
    @Column(name = "data", nullable = false)
    private LocalDate data;

    @NotNull(message = "A quantidade de fornecimentos é obrigatória")
    @Min(value = 0, message = "A quantidade não pode ser negativa")
    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @NotNull(message = "O valor dos produtos fornecidos é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
    @Column(name = "valor", nullable = false, precision = 10, scale = 2)
    private BigDecimal valor; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_fornecedor", nullable = false)
    private Fornecedor fornecedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_produto", nullable = false)
    private Produto produto;
}
