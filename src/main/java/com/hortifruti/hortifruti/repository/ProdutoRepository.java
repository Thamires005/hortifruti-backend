package com.hortifruti.hortifruti.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hortifruti.hortifruti.model.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long>{
    
}
