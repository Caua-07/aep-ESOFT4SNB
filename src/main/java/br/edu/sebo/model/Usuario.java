package br.edu.sebo.model;

import java.util.Locale;

public class Usuario {
    private final int id;
    private final String nomeCompleto;
    private final String emailInstitucional;
    private final String curso;
    private final String telefone;

    public Usuario(int id, String nome, String email, String curso, String telefone) {
        this.id = id;
        this.nomeCompleto = Validacao.texto(nome, "Nome", 150);
        this.emailInstitucional = Validacao.texto(email, "E-mail", 150).toLowerCase(Locale.ROOT);
        if (!emailValido(emailInstitucional)) {
            throw new IllegalArgumentException("Informe um e-mail válido.");
        }
        this.curso = Validacao.texto(curso, "Curso", 100);
        this.telefone = Validacao.texto(telefone, "Telefone", 20);
    }

    private static boolean emailValido(String email) {
        if (!email.matches("[^\\s@]+@[^\\s@]+")) return false;
        String[] partes = email.split("@");
        String nome = partes[0];
        if (nome.startsWith(".") || nome.endsWith(".") || nome.contains("..")) return false;
        String[] dominio = partes[1].split("\\.", -1);
        if (dominio.length < 2) return false;
        for (String rotulo : dominio) {
            if (rotulo.length() > 63 || !rotulo.matches("[a-z0-9](?:[a-z0-9-]*[a-z0-9])?")) return false;
        }
        return true;
    }

    public int getId() { return id; }
    public String getNomeCompleto() { return nomeCompleto; }
    public String getEmailInstitucional() { return emailInstitucional; }
    public String getCurso() { return curso; }
    public String getTelefone() { return telefone; }
}
