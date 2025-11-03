package br.unip.ads.pim.meuhortifruti.repository;

import br.unip.ads.pim.meuhortifruti.entity.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FornecedorRepository extends JpaRepository <Fornecedor, Integer> {

    Optional<Fornecedor> findByEmail(String email);

    boolean existsByEmail(String  email);
}