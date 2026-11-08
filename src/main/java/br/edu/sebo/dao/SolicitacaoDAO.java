package br.edu.sebo.dao;

import br.edu.sebo.model.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SolicitacaoDAO {
    private final Connection conexao;

    public SolicitacaoDAO(Connection conexao) { this.conexao = conexao; }

    public Solicitacao inserir(Livro livro, int solicitanteId) throws SQLException {
        String sql = "INSERT INTO solicitacoes (livro_id, usuario_id, ofertante_original_id, titulo_livro_na_solicitacao, tipo_oferta_na_solicitacao) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement s = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            s.setInt(1, livro.getId());
            s.setInt(2, solicitanteId);
            s.setInt(3, livro.getUsuarioId());
            s.setString(4, livro.getTitulo());
            s.setString(5, livro.getTipoOferta().name());
            s.executeUpdate();
            try (ResultSet r = s.getGeneratedKeys()) {
                if (!r.next()) throw new SQLException("ID da solicitação não retornado.");
                return buscar(r.getInt(1), false);
            }
        }
    }

    public Solicitacao buscar(int id, boolean bloquear) throws SQLException {
        String sql = "SELECT * FROM solicitacoes WHERE id = ?" + (bloquear ? " FOR UPDATE" : "");
        try (PreparedStatement s = conexao.prepareStatement(sql)) {
            s.setInt(1, id);
            try (ResultSet r = s.executeQuery()) { return r.next() ? ler(r) : null; }
        }
    }

    public List<Solicitacao> historico(int usuarioId) throws SQLException {
        List<Solicitacao> lista = new ArrayList<>();
        String sql = "SELECT * FROM solicitacoes WHERE usuario_id = ? OR ofertante_original_id = ? ORDER BY id DESC";
        try (PreparedStatement s = conexao.prepareStatement(sql)) {
            s.setInt(1, usuarioId);
            s.setInt(2, usuarioId);
            try (ResultSet r = s.executeQuery()) {
                while (r.next()) lista.add(ler(r));
            }
        }
        return lista;
    }

    public void decidir(int id, StatusSolicitacao status) throws SQLException {
        String sql = "UPDATE solicitacoes SET status = ?, data_decisao = CURRENT_TIMESTAMP WHERE id = ? AND status = 'PENDENTE'";
        try (PreparedStatement s = conexao.prepareStatement(sql)) {
            s.setString(1, status.name());
            s.setInt(2, id);
            if (s.executeUpdate() != 1) throw new SQLException("Solicitação não decidida.");
        }
    }

    public void finalizar(int id) throws SQLException {
        String sql = "UPDATE solicitacoes SET status = 'FINALIZADA', data_finalizacao = CURRENT_TIMESTAMP WHERE id = ? AND status = 'ACEITA'";
        try (PreparedStatement s = conexao.prepareStatement(sql)) {
            s.setInt(1, id);
            if (s.executeUpdate() != 1) throw new SQLException("Solicitação não finalizada.");
        }
    }

    private LocalDateTime data(ResultSet r, String coluna) throws SQLException {
        return r.getObject(coluna, LocalDateTime.class);
    }

    private Solicitacao ler(ResultSet r) throws SQLException {
        return new Solicitacao(r.getInt("id"), r.getInt("livro_id"), r.getInt("usuario_id"),
                r.getInt("ofertante_original_id"), r.getString("titulo_livro_na_solicitacao"),
                TipoOferta.valueOf(r.getString("tipo_oferta_na_solicitacao")),
                StatusSolicitacao.valueOf(r.getString("status")), data(r, "data_solicitacao"),
                data(r, "data_decisao"), data(r, "data_finalizacao"));
    }
}
