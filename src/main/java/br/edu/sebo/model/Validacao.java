package br.edu.sebo.model;

public final class Validacao {
    private Validacao() { }

    public static String texto(String valor, String campo, int limite) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " é obrigatório.");
        }
        String texto = valor.strip();
        if (texto.length() > limite) {
            throw new IllegalArgumentException(campo + " deve ter até " + limite + " caracteres.");
        }
        return texto;
    }
}
