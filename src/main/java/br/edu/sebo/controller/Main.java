package br.edu.sebo.controller;

import br.edu.sebo.config.Conexao;
import br.edu.sebo.service.*;
import java.sql.Connection;
import java.sql.SQLException;
import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;

public class Main {
    public static void main(String[] args) {
        try {
            Conexao conexao = Conexao.doAmbiente();
            try (Connection teste = conexao.abrir()) {
                if (!teste.isValid(5)) throw new SQLException("Conexão indisponível.");
            }
            System.out.println("Conectado ao MySQL. Cadastros, livros e negociações.");
            System.out.println("Seleção de usuário apenas para demonstração acadêmica, sem autenticação.");
            new Menu(System.in, System.out, new UsuarioService(conexao), new LivroService(conexao), new SolicitacaoService(conexao)).executar();
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (SQLException e) {
            System.err.println("Não foi possível iniciar: confira MySQL, credenciais e banco. SQLState: " + e.getSQLState());
            System.exit(1);
        } finally {
            // Encerra a tarefa interna do driver ao sair pelo Maven ou pela IDE.
            AbandonedConnectionCleanupThread.checkedShutdown();
        }
    }
}
