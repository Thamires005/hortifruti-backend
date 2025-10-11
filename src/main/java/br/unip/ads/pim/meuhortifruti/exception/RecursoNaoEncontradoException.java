package br.unip.ads.pim.meuhortifruti.exception;

public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }

    public RecursoNaoEncontradoException(String recurso, String campo, Object valor) {
        super(String.format("%s não encontrado(a) com %s: %s", recurso, campo, valor));
    }
}