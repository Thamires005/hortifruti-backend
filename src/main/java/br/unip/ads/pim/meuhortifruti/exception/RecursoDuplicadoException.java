package br.unip.ads.pim.meuhortifruti.exception;

public class RecursoDuplicadoException extends RuntimeException {

    public RecursoDuplicadoException(String mensagem) {
        super(mensagem);
    }

    public RecursoDuplicadoException(String recurso, String campo, Object valor) {
        super(String.format("%s já existe com %s: %s", recurso, campo, valor));
    }
}