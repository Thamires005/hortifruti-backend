package br.unip.ads.pim.meuhortifruti.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.unip.ads.pim.meuhortifruti.entity.Fornecedor;

@Repository
public interface FornecedorRepository extends JpaRepository<Fornecedor, Integer>{
    
}
