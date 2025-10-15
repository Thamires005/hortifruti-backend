package br.unip.ads.pim.meuhortifruti.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.unip.ads.pim.meuhortifruti.entity.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    
}
