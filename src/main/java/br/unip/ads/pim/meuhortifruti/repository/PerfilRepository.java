package br.unip.ads.pim.meuhortifruti.repository;

import br.unip.ads.pim.meuhortifruti.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.unip.ads.pim.meuhortifruti.entity.Perfil;

import java.util.Optional;

@Repository
public interface PerfilRepository extends JpaRepository<Perfil, Integer> {

    Optional<Perfil> findByNome(String nome);

    boolean existsByNome(String nome);
}