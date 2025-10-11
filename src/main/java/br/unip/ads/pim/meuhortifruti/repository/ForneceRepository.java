package br.unip.ads.pim.meuhortifruti.repository;

import br.unip.ads.pim.meuhortifruti.entity.Fornece;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ForneceRepository extends JpaRepository<Fornece, Integer> {

    @Query("SELECT f FROM Fornece f WHERE f.fornecedor.idFornecedor = :idFornecedor")
    List<Fornece> buscarPorIdFornecedor(@Param("idFornecedor") Integer idFornecedor);

    @Query("SELECT f FROM Fornece f WHERE f.produto.idProduto = :idProduto")
    List<Fornece> buscarPorIdProduto(@Param("idProduto") Integer idProduto);

    List<Fornece> findByDataBetween(LocalDateTime dataInicio, LocalDateTime dataFim);

    @Query("SELECT f FROM Fornece f WHERE f.fornecedor.idFornecedor = :idFornecedor AND f.produto.idProduto = :idProduto")
    List<Fornece> buscarPorFornecedorEProduto(
        @Param("idFornecedor") Integer idFornecedor,
        @Param("idProduto") Integer idProduto
    );
}
