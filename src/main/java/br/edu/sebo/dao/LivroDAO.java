package br.edu.sebo.dao;

import br.edu.sebo.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LivroDAO {
    private final Connection conexao;

    public LivroDAO(Connection conexao) { this.conexao = conexao; }

    public Livro inserir(Livro livro) throws SQLException {
        String sql = "INSERT INTO livros (titulo, autor, disciplina, estado_conservacao, tipo_oferta, usuario_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement s = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencherDados(s, livro);
            s.setInt(6, livro.getUsuarioId());
            s.executeUpdate();
            try (ResultSet chaves = s.getGeneratedKeys()) {
                if (!chaves.next()) throw new SQLException("Não foi possível obter o ID do livro.");
                return buscar(chaves.getInt(1), false);
            }
        }
    }

    public Livro buscar(int id, boolean bloquear) throws SQLException {
        String sql = "SELECT * FROM livros WHERE id = ?";
        if (bloquear) sql += " FOR UPDATE";
        try (PreparedStatement s = conexao.prepareStatement(sql)) {
            s.setInt(1, id);
            try (ResultSet r = s.executeQuery()) { return r.next() ? lerLivro(r) : null; }
        }
    }

    public List<Livro> listar() throws SQLException {
        List<Livro> livros = new ArrayList<>();
        try (PreparedStatement s = conexao.prepareStatement("SELECT * FROM livros ORDER BY id");
             ResultSet r = s.executeQuery()) {
            while (r.next()) livros.add(lerLivro(r));
        }
        return livros;
    }

    public void atualizar(Livro livro) throws SQLException {
        String sql = "UPDATE livros SET titulo = ?, autor = ?, disciplina = ?, estado_conservacao = ?, tipo_oferta = ? WHERE id = ?";
        try (PreparedStatement s = conexao.prepareStatement(sql)) {
            preencherDados(s, livro);
            s.setInt(6, livro.getId());
            s.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        try (PreparedStatement s = conexao.prepareStatement("DELETE FROM livros WHERE id = ?")) {
            s.setInt(1, id);
            s.executeUpdate();
        }
    }

    public boolean temSolicitacao(int livroId, boolean somenteAtivas) throws SQLException {
        String sql = "SELECT id FROM solicitacoes WHERE livro_id = ?";
        if (somenteAtivas) sql += " AND status IN ('PENDENTE', 'ACEITA')";
        sql += " LIMIT 1";
        try (PreparedStatement s = conexao.prepareStatement(sql)) {
            s.setInt(1, livroId);
            try (ResultSet r = s.executeQuery()) { return r.next(); }
        }
    }

    private void preencherDados(PreparedStatement s, Livro livro) throws SQLException {
        s.setString(1, livro.getTitulo());
        s.setString(2, livro.getAutor());
        s.setString(3, livro.getDisciplina());
        s.setString(4, livro.getEstadoConservacao());
        s.setString(5, livro.getTipoOferta().name());
    }

    private Livro lerLivro(ResultSet r) throws SQLException {
        int id = r.getInt("id");
        int dono = r.getInt("usuario_id");
        String titulo = r.getString("titulo");
        String autor = r.getString("autor");
        String disciplina = r.getString("disciplina");
        String conservacao = r.getString("estado_conservacao");
        StatusLivro status = StatusLivro.valueOf(r.getString("status"));
        if (TipoOferta.valueOf(r.getString("tipo_oferta")) == TipoOferta.TROCA) {
            return new LivroTroca(id, dono, titulo, autor, disciplina, conservacao, status);
        }
        return new LivroEmprestimo(id, dono, titulo, autor, disciplina, conservacao, status);
    }
}
