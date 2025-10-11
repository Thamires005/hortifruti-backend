package com.hortifruti.hortifruti.dto;

public class ProdutoDTO {
    private Long idProduto;
    private String nome;
    private Double preco;
    private String descricao;

    //construtor sem paramêtros
    public ProdutoDTO(){}

    //construtor com paramêtros
    public ProdutoDTO(Long idProduto, String nome, Double preco, String descricao){
        this.idProduto = idProduto;
        this.nome = nome;
        this.preco = preco;
        this.descricao = descricao;
    }
    //getters e setters 
    // get idProduto
    public Long getIdProduto(){
        return idProduto;
    }
    //set idProduto
    public void setIdProduto(Long idProduto){
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
    //get descrição
    public String getDescricao(){
        return descricao;
    }
    //set descrição 
    public void setDescricao(String descricao){
        this.descricao = descricao;
    }
}
