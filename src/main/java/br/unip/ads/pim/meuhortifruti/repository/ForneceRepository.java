package br.unip.ads.pim.meuhortifruti.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.unip.ads.pim.meuhortifruti.entity.Fornece;

@Repository
public interface ForneceRepository extends JpaRepository<Fornece, Integer> {
    
}
