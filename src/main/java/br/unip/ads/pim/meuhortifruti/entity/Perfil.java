package br.unip.ads.pim.meuhortifruti.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "perfil")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Perfil implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_perfil")
    private Integer idPerfil;

    @NotBlank(message = "O nome é obrigatório")
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @NotBlank(message = "Descrição é obrigatória")
    @Column(name = "descricao", nullable = false, length = 100)
    private String descricao;

    @Builder.Default
    @OneToMany(mappedBy = "perfil", cascade = CascadeType.ALL)
    private List<Usuario> usuarios = new ArrayList<>();
}
