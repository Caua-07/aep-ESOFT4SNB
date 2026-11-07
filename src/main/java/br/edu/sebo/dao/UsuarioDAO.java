package br.edu.sebo.dao;

import br.edu.sebo.model.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {
    private final Connection conexao;

    public UsuarioDAO(Connection conexao) { this.conexao = conexao; }

    public Usuario inserir(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (nome_completo, email_institucional, curso, telefone) VALUES (?, ?, ?, ?)";
        try (PreparedStatement s = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            s.setString(1, usuario.getNomeCompleto());
            s.setString(2, usuario.getEmailInstitucional());
            s.setString(3, usuario.getCurso());
            s.setString(4, usuario.getTelefone());
            s.executeUpdate();
            try (ResultSet chaves = s.getGeneratedKeys()) {
                if (!chaves.next()) throw new SQLException("Não foi possível obter o ID do usuário.");
                return new Usuario(chaves.getInt(1), usuario.getNomeCompleto(),
                        usuario.getEmailInstitucional(), usuario.getCurso(), usuario.getTelefone());
            }
        }
    }

    public Usuario buscar(int id) throws SQLException {
        try (PreparedStatement s = conexao.prepareStatement("SELECT * FROM usuarios WHERE id = ?")) {
            s.setInt(1, id);
            try (ResultSet r = s.executeQuery()) { return r.next() ? lerUsuario(r) : null; }
        }
    }

    public List<Usuario> listar() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        try (PreparedStatement s = conexao.prepareStatement("SELECT * FROM usuarios ORDER BY id");
             ResultSet r = s.executeQuery()) {
            while (r.next()) usuarios.add(lerUsuario(r));
        }
        return usuarios;
    }

    private Usuario lerUsuario(ResultSet r) throws SQLException {
        return new Usuario(r.getInt("id"), r.getString("nome_completo"),
                r.getString("email_institucional"), r.getString("curso"), r.getString("telefone"));
    }
}
