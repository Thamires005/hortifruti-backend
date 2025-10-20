package br.unip.ads.pim.meuhortifruti.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.unip.ads.pim.meuhortifruti.entity.Perfil;

@Repository
public interface PerfilRepository extends JpaRepository<Perfil, Integer> {
    
}
