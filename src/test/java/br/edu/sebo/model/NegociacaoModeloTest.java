package br.edu.sebo.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NegociacaoModeloTest {
    private Usuario usuario(int id) { return new Usuario(id, "Pessoa", "pessoa" + id + "@exemplo.br", "Curso", "123"); }

    @Test
    void polimorfismoTransfereMesmaInstanciaEntreColecoes() {
        Usuario dono = usuario(1), novo = usuario(2);
        Livro livro = new LivroTroca(1, 1, "Java", "Autor", "POO", "Bom", StatusLivro.INDISPONIVEL);
        livro.vincularProprietario(dono);
        livro.finalizarNegociacao(novo);
        assertTrue(dono.getLivros().isEmpty());
        assertSame(livro, novo.getLivros().get(0));
        assertSame(novo, livro.getProprietario());
        assertEquals(2, livro.getUsuarioId());
        assertEquals(StatusLivro.DISPONIVEL, livro.getStatus());
        assertThrows(UnsupportedOperationException.class, () -> novo.getLivros().clear());
        assertThrows(IllegalArgumentException.class, () -> livro.finalizarNegociacao(dono));
    }

    @Test
    void emprestimoPreservaColecaoEProprietario() {
        Usuario dono = usuario(1), outro = usuario(2);
        Livro livro = new LivroEmprestimo(1, 1, "Java", "Autor", "POO", "Bom", StatusLivro.INDISPONIVEL);
        livro.vincularProprietario(dono);
        livro.finalizarNegociacao(outro);
        assertSame(livro, dono.getLivros().get(0));
        assertSame(dono, livro.getProprietario());
        assertTrue(outro.getLivros().isEmpty());
        assertEquals(StatusLivro.DISPONIVEL, livro.getStatus());
    }

    @Test
    void impedeVinculoIncompativelEFinalizacaoSemSolicitante() {
        Livro livro = new LivroTroca(1, 1, "Java", "Autor", "POO", "Bom", StatusLivro.INDISPONIVEL);
        assertThrows(IllegalArgumentException.class, () -> livro.vincularProprietario(usuario(2)));
        livro.vincularProprietario(usuario(1));
        assertThrows(IllegalArgumentException.class, () -> livro.finalizarNegociacao(null));
        assertThrows(IllegalArgumentException.class, () -> livro.finalizarNegociacao(usuario(1)));
        assertEquals(1, livro.getProprietario().getLivros().size());
    }
}
