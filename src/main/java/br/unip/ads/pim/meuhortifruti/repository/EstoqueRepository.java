package br.unip.ads.pim.meuhortifruti.repository;

import br.unip.ads.pim.meuhortifruti.entity.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstoqueRepository extends JpaRepository <Estoque, Integer> {
}
