package br.unip.ads.pim.meuhortifruti.exception;

public class AcessoNegadoException extends RuntimeException {

    public AcessoNegadoException(String mensagem) {
        super(mensagem);
    }

    public AcessoNegadoException() {
        super("Você não tem permissão para acessar este recurso");
    }
}