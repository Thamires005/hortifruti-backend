package br.unip.ads.pim.meuhortifruti.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.unip.ads.pim.meuhortifruti.entity.Produto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarrinhoRequestDTO {
    
    @Builder.Default
    @OneToMany(mappedBy = "carrinho", cascade = CascadeType.ALL)
    private List<Produto> produtos = new ArrayList<>();
    
    private Integer quantProdutos;
    private BigDecimal valorTotal;
}
