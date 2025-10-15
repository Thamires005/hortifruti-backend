package br.unip.ads.pim.meuhortifruti.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "entrega")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Entrega implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_entrega")
    private Integer idEntrega;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;

    @NotBlank(message = "Endereço é obrigatório")
    @Column(name = "endereco", nullable = false, length = 300)
    private String endereco;

    @NotBlank(message = "Status da entrega é obrigatório")
    @Column(name = "status_entrega", nullable = false, length = 100)
    private String statusEntrega;

    @NotNull
    @Column(name = "dt_entrega", nullable = false)
    private LocalDateTime dtEntrega;
}

