package br.edu.sebo.service;

import br.edu.sebo.config.Conexao;
import br.edu.sebo.dao.*;
import br.edu.sebo.model.*;
import java.sql.*;
import java.util.List;

public class LivroService {
    private final Conexao conexao;

    public LivroService(Conexao conexao) { this.conexao = conexao; }

    public Livro cadastrar(int usuarioId, String titulo, String autor, String disciplina,
                           String conservacao, TipoOferta tipo) throws SQLException {
        Livro livro = criarLivro(0, usuarioId, titulo, autor, disciplina, conservacao, tipo, StatusLivro.DISPONIVEL);
        try (Connection c = conexao.abrir()) {
            if (new UsuarioDAO(c).buscar(usuarioId) == null) {
                throw new IllegalArgumentException("Usuário não encontrado.");
            }
            return new LivroDAO(c).inserir(livro);
        }
    }

    public Livro buscar(int id) throws SQLException {
        try (Connection c = conexao.abrir()) {
            Livro livro = new LivroDAO(c).buscar(id, false);
            if (livro == null) throw new IllegalArgumentException("Livro não encontrado.");
            return livro;
        }
    }

    public List<Livro> listar() throws SQLException {
        try (Connection c = conexao.abrir()) { return new LivroDAO(c).listar(); }
    }

    public boolean temSolicitacaoAtiva(int id) throws SQLException {
        try (Connection c = conexao.abrir()) { return new LivroDAO(c).temSolicitacao(id, true); }
    }

    public void atualizar(int usuarioId, int livroId, String titulo, String autor,
                          String disciplina, String conservacao, TipoOferta tipo) throws SQLException {
        try (Connection c = conexao.abrir()) {
            c.setAutoCommit(false);
            try {
                LivroDAO dao = new LivroDAO(c);
                // O bloqueio mantém a verificação e a alteração na mesma transação.
                Livro atual = dao.buscar(livroId, true);
                conferirProprietario(atual, usuarioId);
                if (dao.temSolicitacao(livroId, true)) {
                    throw new IllegalArgumentException("Livro com solicitação ativa não pode ser editado.");
                }
                Livro editado = criarLivro(livroId, usuarioId, titulo, autor, disciplina, conservacao, tipo, atual.getStatus());
                dao.atualizar(editado);
                c.commit();
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        }
    }

    public void excluir(int usuarioId, int livroId) throws SQLException {
        try (Connection c = conexao.abrir()) {
            c.setAutoCommit(false);
            try {
                LivroDAO dao = new LivroDAO(c);
                conferirProprietario(dao.buscar(livroId, true), usuarioId);
                if (dao.temSolicitacao(livroId, false)) {
                    throw new IllegalArgumentException("Livro com solicitações não pode ser excluído: o histórico deve ser preservado.");
                }
                dao.excluir(livroId);
                c.commit();
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        }
    }

    private void conferirProprietario(Livro livro, int usuarioId) {
        if (livro == null) throw new IllegalArgumentException("Livro não encontrado.");
        if (livro.getUsuarioId() != usuarioId) {
            throw new IllegalArgumentException("Somente o proprietário pode alterar este livro.");
        }
    }

    private Livro criarLivro(int id, int usuarioId, String titulo, String autor,
                             String disciplina, String conservacao, TipoOferta tipo, StatusLivro status) {
        if (tipo == null) throw new IllegalArgumentException("Tipo de oferta é obrigatório.");
        if (tipo == TipoOferta.TROCA) {
            return new LivroTroca(id, usuarioId, titulo, autor, disciplina, conservacao, status);
        }
        return new LivroEmprestimo(id, usuarioId, titulo, autor, disciplina, conservacao, status);
    }
}
