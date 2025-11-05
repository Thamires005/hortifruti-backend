package br.unip.ads.pim.meuhortifruti.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.unip.ads.pim.meuhortifruti.entity.Pagamento;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Integer>{

    Optional<Pagamento> findByIdCompra(Integer idCompra);
    
    boolean existsByIdCompra(Integer idCompra);
}