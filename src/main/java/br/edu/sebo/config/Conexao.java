package br.edu.sebo.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
    private final String url;
    private final String usuario;
    private final String senha;

    public Conexao(String url, String usuario, String senha) {
        if (url == null || usuario == null || senha == null) {
            throw new IllegalArgumentException("Configure SEBO_DB_URL, SEBO_DB_USER e SEBO_DB_PASSWORD.");
        }
        this.url = url;
        this.usuario = usuario;
        this.senha = senha;
    }

    public static Conexao doAmbiente() {
        return new Conexao(System.getenv("SEBO_DB_URL"), System.getenv("SEBO_DB_USER"),
                System.getenv("SEBO_DB_PASSWORD"));
    }

    public Connection abrir() throws SQLException {
        Connection conexao = DriverManager.getConnection(url, usuario, senha);
        try (var comando = conexao.createStatement()) {
            // DATETIME não armazena fuso; todas as datas da aplicação usam UTC.
            comando.execute("SET time_zone = '+00:00'");
            return conexao;
        } catch (SQLException e) {
            conexao.close();
            throw e;
        }
    }
}
