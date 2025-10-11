package br.unip.ads.pim.meuhortifruti.repository;

import br.unip.ads.pim.meuhortifruti.entity.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FornecedorRepository extends JpaRepository<Fornecedor, Integer> {

    Optional<Fornecedor> findByCnpj(String cnpj);

    Optional<Fornecedor> findByEmail(String email);

    List<Fornecedor> findByNomeContainingIgnoreCase(String nome);

    boolean existsByCnpj(String cnpj);

    boolean existsByEmail(String email);
}
