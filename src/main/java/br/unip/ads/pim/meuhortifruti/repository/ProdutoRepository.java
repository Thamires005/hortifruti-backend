package br.unip.ads.pim.meuhortifruti.repository;

import br.unip.ads.pim.meuhortifruti.entity.Categoria;
import br.unip.ads.pim.meuhortifruti.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Integer> {

    List<Produto> findByCategoria(Categoria categoria);

    List<Produto> findByNomeContainingIgnoreCase(String nome);

    List<Produto> findByQuantidadeGreaterThan(Integer quantidade);

    List<Produto> findByDtValidadeBefore(LocalDate data);

    @Query("SELECT p FROM Produto p WHERE p.quantidade <= :quantidadeMinima")
    List<Produto> buscarProdutosComEstoqueBaixo(@Param("quantidadeMinima") Integer quantidadeMinima);

    @Query("SELECT p FROM Produto p WHERE p.dtValidade <= :data")
    List<Produto> buscarProdutosProximosDoVencimento(@Param("data") LocalDate data);
}
