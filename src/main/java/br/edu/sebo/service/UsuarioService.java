package br.edu.sebo.service;

import br.edu.sebo.config.Conexao;
import br.edu.sebo.dao.UsuarioDAO;
import br.edu.sebo.dao.LivroDAO;
import br.edu.sebo.model.Livro;
import br.edu.sebo.model.Usuario;
import java.sql.*;
import java.util.List;

public class UsuarioService {
    private final Conexao conexao;

    public UsuarioService(Conexao conexao) { this.conexao = conexao; }

    public Usuario cadastrar(String nome, String email, String curso, String telefone) throws SQLException {
        Usuario usuario = new Usuario(0, nome, email, curso, telefone);
        try (Connection c = conexao.abrir()) {
            return new UsuarioDAO(c).inserir(usuario);
        } catch (SQLException e) {
            // O índice UNIQUE também protege contra cadastros simultâneos.
            if (e.getErrorCode() == 1062) throw new IllegalArgumentException("E-mail já cadastrado.");
            throw e;
        }
    }

    public Usuario buscar(int id) throws SQLException {
        try (Connection c = conexao.abrir()) {
            Usuario usuario = new UsuarioDAO(c).buscar(id);
            if (usuario == null) throw new IllegalArgumentException("Usuário não encontrado.");
            return usuario;
        }
    }

    public Usuario buscarComLivros(int id) throws SQLException {
        try (Connection c = conexao.abrir()) {
            Usuario usuario = new UsuarioDAO(c).buscar(id);
            if (usuario == null) throw new IllegalArgumentException("Usuário não encontrado.");
            for (Livro livro : new LivroDAO(c).listarPorProprietario(id)) {
                livro.vincularProprietario(usuario);
            }
            return usuario;
        }
    }

    public List<Usuario> listar() throws SQLException {
        try (Connection c = conexao.abrir()) { return new UsuarioDAO(c).listar(); }
    }
}
