package br.unip.ads.pim.meuhortifruti.dto;

import java.util.ArrayList;
import java.util.List;

import br.unip.ads.pim.meuhortifruti.entity.ItemCompra;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraRequestDTO {

    @NotBlank(message = "Status da compra é obrigatório")
    private String statusCompra;

    @Builder.Default
    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL)
    private List<ItemCompra> itensCompra = new ArrayList<>();
}
