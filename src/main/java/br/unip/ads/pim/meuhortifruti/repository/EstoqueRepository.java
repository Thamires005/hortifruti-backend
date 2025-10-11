package br.unip.ads.pim.meuhortifruti.repository;

import br.unip.ads.pim.meuhortifruti.entity.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstoqueRepository extends JpaRepository<Estoque, Integer> {

    @Query("SELECT e FROM Estoque e WHERE e.produto.idProduto = :idProduto")
    Optional<Estoque> buscarPorIdProduto(@Param("idProduto") Integer idProduto);
}
