package br.unip.ads.pim.meuhortifruti.repository;

import br.unip.ads.pim.meuhortifruti.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.unip.ads.pim.meuhortifruti.entity.Pagamento;

import java.util.Optional;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Integer>{


}
