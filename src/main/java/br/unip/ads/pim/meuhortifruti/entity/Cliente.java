package br.unip.ads.pim.meuhortifruti.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "cliente")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cliente implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Integer IdCliente;

    @NotBlank(message = "Nome do cliente é obrigatório")
    @Column(name = "nome", nullable = false, unique = true, length = 100)
    private String nome;

    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "\\d{10,11}", message = "CPF deve conter 11 dígitos")
    @Column(name = "cpf", nullable = false, unique = true, length = 11)
    private String cpf; 

    @NotBlank(message = "Endereço é obrigatório")
    @Column(name = "endereco", nullable = false, length = 300)
    private String endereco;

    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(regexp = "\\d{9,11}", message = "Telefone deve conter de 9 ou 11 dígitos")
    @Column(name = "telefone", nullable = false, length = 11)
    private String telefone;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail deve ser válido")
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Past(message = "A data de nascimento deve ser no passado")
    @Column(name = "dt_nascimento", nullable = false)
    private LocalDate dtNascimento;

    @Builder.Default
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Pedido> pedidos = new ArrayList<>();

    @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private Carrinho carrinho; 

}
