package br.edu.sebo.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ModeloTest {
    @Test
    void normalizaEmailENomeSemPerderZeroDoTelefone() {
        Usuario usuario = new Usuario(0, " Ana Silva ", " ANA@ALUNOS.EXEMPLO.BR ", "Software", "044900000000");
        assertEquals("Ana Silva", usuario.getNomeCompleto());
        assertEquals("ana@alunos.exemplo.br", usuario.getEmailInstitucional());
        assertEquals("044900000000", usuario.getTelefone());
    }

    @Test
    void rejeitaCamposInvalidosAntesDeSalvar() {
        assertThrows(IllegalArgumentException.class, () -> new Usuario(0, " ", "a@b.br", "Curso", "123"));
        assertThrows(IllegalArgumentException.class, () -> new Usuario(0, "Ana", "sem-arroba", "Curso", "123"));
        assertThrows(IllegalArgumentException.class, () -> new Usuario(0, "Ana", "a@b.br", null, "123"));
        assertThrows(IllegalArgumentException.class, () -> new Usuario(0, "Ana", "a@b.br", "Curso", "1".repeat(21)));
    }

    @Test
    void rejeitaDominioMalformadoNoEmail() {
        for (String email : new String[]{"ana@alunos..br", "ana@-alunos.br", "ana@alunos-.br", "ana@alunos.br.", ".ana@alunos.br", "ana..silva@alunos.br"}) {
            assertThrows(IllegalArgumentException.class,
                    () -> new Usuario(0, "Ana", email, "Curso", "123"), email);
        }
    }

    @Test
    void distingueOfertaPorPolimorfismo() {
        Livro troca = new LivroTroca(0, 1, "Java", "Autor", "POO", "Bom", StatusLivro.DISPONIVEL);
        Livro emprestimo = new LivroEmprestimo(0, 1, "Java", "Autor", "POO", "Bom", StatusLivro.DISPONIVEL);
        assertEquals(TipoOferta.TROCA, troca.getTipoOferta());
        assertEquals(TipoOferta.EMPRESTIMO, emprestimo.getTipoOferta());
    }

    @Test
    void rejeitaLivroSemProprietarioOuDadosObrigatorios() {
        assertThrows(IllegalArgumentException.class, () -> new LivroTroca(0, 0, "Java", "Autor", "POO", "Bom", StatusLivro.DISPONIVEL));
        assertThrows(IllegalArgumentException.class, () -> new LivroTroca(0, 1, " ", "Autor", "POO", "Bom", StatusLivro.DISPONIVEL));
        assertThrows(IllegalArgumentException.class, () -> new LivroTroca(0, 1, "A".repeat(151), "Autor", "POO", "Bom", StatusLivro.DISPONIVEL));
        assertThrows(IllegalArgumentException.class, () -> new LivroTroca(0, 1, "Java", "Autor", "POO", "Bom", null));
    }
}
