package com.hortifruti.hortifruti.model;

import java.time.LocalDate;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "produto")
public class Produto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProduto; //chave primária

    private String nome;
    private double preco;
    private int quantEstoque;
    private LocalDate dataValidade;
    private String descricao;   
    
     //contrutor sem paramêtros
    public Produto(){}

    //construtor com paramêtros
    public Produto(String nome, double preco, int quantEstoque, LocalDate dataValidade, String descricao){
        this.nome = nome;
        this.preco = preco;
        this.quantEstoque = quantEstoque;
        this.dataValidade = dataValidade;
        this.descricao = descricao;
    }
    // Getters e Setters
    // get de idProduto
    public Long getIdProduto() {
        return idProduto;
    }
    // set de idProduto
    public void setIdProduto(Long idProduto) {
        this.idProduto = idProduto;
    }
    // get de Nome
    public String getNome(){
        return nome;
    }
    // set de Nome
    public void setNome(String nome){
        this.nome = nome; 
    }
    // get de preco
    public double getPreco(){
        return preco;
    }
    //set de preco
    public void setPreco(double preco){
        this.preco = preco;
    }
    // get de quantEstoque
    public int getQuantEstoque(){
        return quantEstoque;
    }
    // set de quantEstoque
    public void setQuantEstoque(int quantEstoque){
        this.quantEstoque = quantEstoque;
    }
    // get dataValidade
    public LocalDate getDataValidade(){
        return dataValidade;
    }
    // set dataValidade
    public void setDataValidade(LocalDate dataValidade){
        this.dataValidade = dataValidade;
    }
    //get descrição
    public String getDescricao(){
        return descricao;
    }
    //set descrição 
    public void setDescricao(String descricao){
        this.descricao = descricao;
    }


}

