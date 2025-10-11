package br.unip.ads.pim.meuhortifruti.repository;

import br.unip.ads.pim.meuhortifruti.entity.Carrinho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarrinhoRepository extends JpaRepository<Carrinho, Integer> {

    @Query("SELECT c FROM Carrinho c WHERE c.cliente.idUsuario = :idCliente")
    Optional<Carrinho> buscarPorIdCliente(@Param("idCliente") Integer idCliente);
}
