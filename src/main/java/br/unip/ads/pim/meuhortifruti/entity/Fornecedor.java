package br.unip.ads.pim.meuhortifruti.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;

@Entity
@Data
@Builder
@Table(name = "fornecedor")
@NoArgsConstructor
@AllArgsConstructor
public class Fornecedor {


    @ManyToMany
    Set<Produto> produtos;
    private Produto produto;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_fornecedor")
    private Integer idFornecedor;

    @NotBlank(message = "Nome do Fornecedor é obrigatório")
    @Column(name = "nome", nullable = false, unique = true, length = 100)
    private String nome;

    @NotBlank(message = "CNPJ é obrigatório")
    @Pattern(regexp = "\\d{14}", message = "O CNPJ deve conter 14 dígitos")
    @Column(name = "cnpj", nullable = false, unique = true, length = 14)
    private String cnpj;

    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(regexp = "\\d{9,11}", message = "Telefone deve conter de 9 ou 11 dígitos")
    @Column(name = "telefone", nullable = false, length = 11)
    private String telefone;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail deve ser válido")
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank(message = "Endereço é obrigatório")
    @Column(name = "endereco", nullable = false, length = 300)
    private String endereco;

    @NotBlank(message = "Produtos fornecidos é obrigatório")
    @Column(name = "prod_fornecidos", columnDefinition = "TEXT")
    private String produtosFornecidos;

}
