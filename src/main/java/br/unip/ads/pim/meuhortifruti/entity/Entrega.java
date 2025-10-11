package br.unip.ads.pim.meuhortifruti.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "entrega")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Entrega implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_entrega")
    private Integer idEntrega;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido", nullable = false, unique = true)
    private Pedido pedido;

    @NotBlank(message = "Endereço de entrega é obrigatório")
    @Column(name = "endereco", nullable = false, length = 500)
    private String endereco;

    @NotBlank(message = "Status da entrega é obrigatório")
    @Column(name = "status_entrega", nullable = false, length = 50)
    private String statusEntrega;

    @Column(name = "dt_entrega")
    private LocalDateTime dtEntrega;
}
