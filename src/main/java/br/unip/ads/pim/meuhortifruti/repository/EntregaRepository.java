package br.unip.ads.pim.meuhortifruti.repository;

import br.unip.ads.pim.meuhortifruti.entity.Entrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EntregaRepository extends JpaRepository<Entrega, Integer> {

    @Query("SELECT e FROM Entrega e WHERE e.pedido.idPedido = :idPedido")
    Optional<Entrega> buscarPorIdPedido(@Param("idPedido") Integer idPedido);

    List<Entrega> findByStatusEntrega(String statusEntrega);
}
