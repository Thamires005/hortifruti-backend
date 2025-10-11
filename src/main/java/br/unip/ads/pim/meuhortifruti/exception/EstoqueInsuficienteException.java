package br.unip.ads.pim.meuhortifruti.exception;

public class EstoqueInsuficienteException extends RuntimeException {

    public EstoqueInsuficienteException(String mensagem) {
        super(mensagem);
    }

    public EstoqueInsuficienteException(String produto, Integer quantidadeDisponivel, Integer quantidadeSolicitada) {
        super(String.format("Estoque insuficiente para o produto %s. Disponível: %d, Solicitado: %d",
                produto, quantidadeDisponivel, quantidadeSolicitada));
    }
}