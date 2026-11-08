package br.edu.sebo.service;

import br.edu.sebo.config.Conexao;
import br.edu.sebo.dao.*;
import br.edu.sebo.model.*;
import java.sql.*;
import java.util.List;

public class SolicitacaoService {
    private final Conexao conexao;

    public SolicitacaoService(Conexao conexao) { this.conexao = conexao; }

    public Solicitacao solicitar(int usuarioId, int livroId) throws SQLException {
        try (Connection c = conexao.abrir()) {
            c.setAutoCommit(false);
            try {
                LivroDAO livros = new LivroDAO(c);
                Livro livro = livros.buscar(livroId, true);
                if (livro == null) throw new IllegalArgumentException("Livro não encontrado.");
                if (new UsuarioDAO(c).buscar(usuarioId) == null) throw new IllegalArgumentException("Usuário não encontrado.");
                if (livro.getUsuarioId() == usuarioId) throw new IllegalArgumentException("Não é permitido solicitar seu próprio livro.");
                if (livro.getStatus() != StatusLivro.DISPONIVEL || livros.temSolicitacao(livroId, true)) {
                    throw new IllegalArgumentException("Livro indisponível para novos pedidos: existe negociação ativa ou indisponibilidade.");
                }
                Solicitacao pedido = new SolicitacaoDAO(c).inserir(livro, usuarioId);
                c.commit();
                return pedido;
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        }
    }

    public List<Solicitacao> historico(int usuarioId) throws SQLException {
        try (Connection c = conexao.abrir()) {
            if (new UsuarioDAO(c).buscar(usuarioId) == null) throw new IllegalArgumentException("Usuário não encontrado.");
            return new SolicitacaoDAO(c).historico(usuarioId);
        }
    }

    public void aceitar(int usuarioId, int pedidoId) throws SQLException {
        alterar(usuarioId, pedidoId, StatusSolicitacao.ACEITA);
    }

    public void recusar(int usuarioId, int pedidoId) throws SQLException {
        alterar(usuarioId, pedidoId, StatusSolicitacao.RECUSADA);
    }

    public void finalizar(int usuarioId, int pedidoId) throws SQLException {
        alterar(usuarioId, pedidoId, StatusSolicitacao.FINALIZADA);
    }

    private void alterar(int usuarioId, int pedidoId, StatusSolicitacao destino) throws SQLException {
        try (Connection c = conexao.abrir()) {
            // A consulta inicial só localiza o livro; não decide estado nem autorização.
            SolicitacaoDAO pedidos = new SolicitacaoDAO(c);
            Solicitacao referencia = pedidos.buscar(pedidoId, false);
            if (referencia == null) throw new IllegalArgumentException("Solicitação não encontrada.");
            c.setAutoCommit(false);
            try {
                // Mesma ordem do CRUD: primeiro livro, depois pedido, sempre relidos sob bloqueio.
                LivroDAO livros = new LivroDAO(c);
                Livro livro = livros.buscar(referencia.livroId(), true);
                Solicitacao pedido = pedidos.buscar(pedidoId, true);
                validarAlteracao(usuarioId, pedido, livro, destino);
                if (destino == StatusSolicitacao.FINALIZADA) {
                    Usuario solicitante = new UsuarioDAO(c).buscar(pedido.solicitanteId());
                    livro.finalizarNegociacao(solicitante);
                    livros.salvarNegociacao(livro.getId(), livro.getUsuarioId(), livro.getStatus());
                    pedidos.finalizar(pedidoId);
                } else {
                    if (destino == StatusSolicitacao.ACEITA) {
                        livros.salvarNegociacao(livro.getId(), livro.getUsuarioId(), StatusLivro.INDISPONIVEL);
                    }
                    pedidos.decidir(pedidoId, destino);
                }
                c.commit();
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        }
    }

    private void validarAlteracao(int usuarioId, Solicitacao pedido, Livro livro, StatusSolicitacao destino) {
        if (pedido == null || livro == null) throw new IllegalArgumentException("Negociação não encontrada.");
        if (pedido.ofertanteOriginalId() != usuarioId) {
            throw new IllegalArgumentException("Somente o proprietário original pode decidir ou finalizar.");
        }
        StatusSolicitacao esperado = destino == StatusSolicitacao.FINALIZADA
                ? StatusSolicitacao.ACEITA : StatusSolicitacao.PENDENTE;
        if (pedido.status() != esperado) throw new IllegalArgumentException("Ação inválida para solicitação " + pedido.status() + ".");
        StatusLivro statusEsperado = esperado == StatusSolicitacao.ACEITA ? StatusLivro.INDISPONIVEL : StatusLivro.DISPONIVEL;
        if (livro.getUsuarioId() != pedido.ofertanteOriginalId() || livro.getTipoOferta() != pedido.tipoOferta()
                || livro.getStatus() != statusEsperado) {
            throw new IllegalArgumentException("Estado do livro incompatível com a negociação.");
        }
    }
}
