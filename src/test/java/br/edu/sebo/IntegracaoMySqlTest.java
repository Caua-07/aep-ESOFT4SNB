package br.edu.sebo;

import br.edu.sebo.config.Conexao;
import br.edu.sebo.model.*;
import br.edu.sebo.service.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import java.sql.*;
import static org.junit.jupiter.api.Assertions.*;

// Usa somente o banco descartável sebo_test. Nunca executar em dados reais.
@EnabledIfEnvironmentVariable(named = "SEBO_TEST_URL", matches = ".+")
class IntegracaoMySqlTest {
    private Conexao conexao;
    private UsuarioService usuarios;
    private LivroService livros;
    private Usuario dono;
    private Usuario outro;

    @BeforeEach
    void prepararBancoDeTeste() throws Exception {
        String url = System.getenv("SEBO_TEST_URL");
        if (!url.matches("jdbc:mysql://[^/]+/sebo_test(\\?.*)?")) {
            throw new IllegalStateException("Os testes exigem um banco chamado sebo_test.");
        }
        conexao = new Conexao(url, System.getenv("SEBO_TEST_USER"), System.getenv("SEBO_TEST_PASSWORD"));
        try (Connection c = conexao.abrir(); Statement s = c.createStatement()) {
            s.executeUpdate("DELETE FROM solicitacoes");
            s.executeUpdate("DELETE FROM livros");
            s.executeUpdate("DELETE FROM usuarios");
        }
        usuarios = new UsuarioService(conexao);
        livros = new LivroService(conexao);
        dono = usuarios.cadastrar("Ana Silva", "ana@alunos.exemplo.br", "Software", "044900000001");
        outro = usuarios.cadastrar("Bruno Silva", "bruno@alunos.exemplo.br", "Software", "044900000002");
    }

    private Livro novoLivro() throws SQLException {
        return livros.cadastrar(dono.getId(), "Java", "Autor", "POO", "Bom", TipoOferta.TROCA);
    }

    @Test
    void persisteUsuarioERecusaEmailDuplicado() throws Exception {
        Usuario lido = new UsuarioService(conexao).buscar(dono.getId());
        assertEquals("Ana Silva", lido.getNomeCompleto());
        assertEquals("044900000001", lido.getTelefone());
        assertThrows(IllegalArgumentException.class, () -> usuarios.cadastrar("Outra", " ANA@ALUNOS.EXEMPLO.BR ", "Curso", "123"));
        assertEquals(2, usuarios.listar().size());
    }

    @Test
    void fazCrudPersistenteIncluindoMudancaDeTipo() throws Exception {
        Livro salvo = novoLivro();
        assertTrue(salvo.getId() > 0);
        assertEquals(StatusLivro.DISPONIVEL, salvo.getStatus());
        assertEquals("Java", new LivroService(conexao).buscar(salvo.getId()).getTitulo());
        livros.atualizar(dono.getId(), salvo.getId(), "Java 17", "Outro Autor", "Banco", "Usado", TipoOferta.EMPRESTIMO);
        Livro alterado = new LivroService(conexao).buscar(salvo.getId());
        assertEquals("Java 17", alterado.getTitulo());
        assertEquals("Outro Autor", alterado.getAutor());
        assertEquals("Banco", alterado.getDisciplina());
        assertEquals("Usado", alterado.getEstadoConservacao());
        assertInstanceOf(LivroEmprestimo.class, alterado);
        assertEquals(dono.getId(), alterado.getUsuarioId());
        livros.excluir(dono.getId(), salvo.getId());
        assertTrue(livros.listar().isEmpty());
        assertThrows(IllegalArgumentException.class, () -> livros.buscar(salvo.getId()));
    }

    @Test
    void permiteExemplaresComMesmoTitulo() throws Exception {
        Livro primeiro = novoLivro();
        Livro segundo = novoLivro();
        assertNotEquals(primeiro.getId(), segundo.getId());
        assertEquals(2, livros.listar().size());
    }

    @Test
    void rejeitaOutroProprietarioSemAlterarLivro() throws Exception {
        Livro livro = novoLivro();
        assertThrows(IllegalArgumentException.class, () -> livros.atualizar(outro.getId(), livro.getId(), "Novo", "Autor", "POO", "Bom", TipoOferta.TROCA));
        assertThrows(IllegalArgumentException.class, () -> livros.excluir(outro.getId(), livro.getId()));
        assertEquals("Java", livros.buscar(livro.getId()).getTitulo());
    }

    @Test
    void rejeitaUsuarioInexistenteETipoNulo() {
        assertThrows(IllegalArgumentException.class, () -> livros.cadastrar(Integer.MAX_VALUE, "Java", "Autor", "POO", "Bom", TipoOferta.TROCA));
        assertThrows(IllegalArgumentException.class, () -> livros.cadastrar(dono.getId(), "Java", "Autor", "POO", "Bom", null));
    }

    @Test
    void rejeitaDadosInvalidosSemGravacaoParcial() throws Exception {
        Livro livro = novoLivro();
        assertThrows(IllegalArgumentException.class, () -> livros.atualizar(dono.getId(), livro.getId(), " ", "Autor", "POO", "Bom", TipoOferta.TROCA));
        assertEquals("Java", livros.buscar(livro.getId()).getTitulo());
    }

    @Test
    void bloqueiaEdicaoComPedidoPendente() throws Exception {
        Livro livro = novoLivro();
        inserirPedido(livro, "PENDENTE");
        assertTrue(livros.temSolicitacaoAtiva(livro.getId()));
        assertThrows(IllegalArgumentException.class, () -> livros.atualizar(dono.getId(), livro.getId(), "Novo", "Autor", "POO", "Bom", TipoOferta.TROCA));
        assertThrows(IllegalArgumentException.class, () -> livros.excluir(dono.getId(), livro.getId()));
        assertEquals("Java", livros.buscar(livro.getId()).getTitulo());
    }

    @Test
    void bloqueiaEdicaoComPedidoAceito() throws Exception {
        Livro livro = novoLivro();
        inserirPedido(livro, "ACEITA");
        assertTrue(livros.temSolicitacaoAtiva(livro.getId()));
        assertThrows(IllegalArgumentException.class, () -> livros.atualizar(dono.getId(), livro.getId(), "Novo", "Autor", "POO", "Bom", TipoOferta.TROCA));
    }

    @Test
    void permiteEdicaoMasPreservaHistoricoEncerrado() throws Exception {
        Livro livro = novoLivro();
        inserirPedido(livro, "FINALIZADA");
        assertFalse(livros.temSolicitacaoAtiva(livro.getId()));
        livros.atualizar(dono.getId(), livro.getId(), "Novo", "Autor", "POO", "Bom", TipoOferta.EMPRESTIMO);
        assertThrows(IllegalArgumentException.class, () -> livros.excluir(dono.getId(), livro.getId()));
        try (Connection c = conexao.abrir(); Statement s = c.createStatement();
             ResultSet r = s.executeQuery("SELECT titulo_livro_na_solicitacao, tipo_oferta_na_solicitacao FROM solicitacoes")) {
            assertTrue(r.next());
            assertEquals("Java", r.getString(1));
            assertEquals("TROCA", r.getString(2));
        }
    }

    private void inserirPedido(Livro livro, String status) throws SQLException {
        String sql = "INSERT INTO solicitacoes (livro_id, usuario_id, ofertante_original_id, titulo_livro_na_solicitacao, tipo_oferta_na_solicitacao, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = conexao.abrir(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, livro.getId()); s.setInt(2, outro.getId()); s.setInt(3, dono.getId());
            s.setString(4, "Java"); s.setString(5, "TROCA"); s.setString(6, status);
            s.executeUpdate();
        }
    }
}
