package br.unip.ads.pim.meuhortifruti.repository;

import br.unip.ads.pim.meuhortifruti.entity.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Integer> {

    @Query("SELECT p FROM Pagamento p WHERE p.pedido.idPedido = :idPedido")
    Optional<Pagamento> buscarPorIdPedido(@Param("idPedido") Integer idPedido);

    List<Pagamento> findByStatusPagamento(String statusPagamento);

    List<Pagamento> findByMetodoPagamento(String metodoPagamento);
}
