package br.unip.ads.pim.meuhortifruti.repository;

import br.unip.ads.pim.meuhortifruti.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    @Query("SELECT p FROM Pedido p WHERE p.cliente.idUsuario = :idCliente")
    List<Pedido> buscarPorIdCliente(@Param("idCliente") Integer idCliente);

    List<Pedido> findByStatusPedido(String statusPedido);

    @Query("SELECT p FROM Pedido p WHERE p.cliente.idUsuario = :idCliente AND p.statusPedido = :status")
    List<Pedido> buscarPorClienteEStatus(
        @Param("idCliente") Integer idCliente,
        @Param("status") String status
    );
}
