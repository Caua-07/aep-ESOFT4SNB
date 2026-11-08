package br.edu.sebo;

import br.edu.sebo.config.Conexao;
import br.edu.sebo.model.*;
import br.edu.sebo.service.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import java.sql.*;
import java.lang.reflect.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

@EnabledIfEnvironmentVariable(named = "SEBO_TEST_URL", matches = ".+")
class NegociacaoMySqlTest {
    private Conexao conexao;
    private UsuarioService usuarios;
    private LivroService livros;
    private SolicitacaoService pedidos;
    private Usuario dono, outro, terceiro;

    @BeforeEach
    void preparar() throws Exception {
        String url = System.getenv("SEBO_TEST_URL");
        if (!url.matches("jdbc:mysql://[^/]+/sebo_test(\\?.*)?")) {
            throw new IllegalStateException("Somente banco descartável sebo_test.");
        }
        conexao = new Conexao(url, System.getenv("SEBO_TEST_USER"), System.getenv("SEBO_TEST_PASSWORD"));
        try (Connection c = conexao.abrir(); Statement s = c.createStatement()) {
            s.executeUpdate("DELETE FROM solicitacoes");
            s.executeUpdate("DELETE FROM livros");
            s.executeUpdate("DELETE FROM usuarios");
        }
        usuarios = new UsuarioService(conexao);
        livros = new LivroService(conexao);
        pedidos = new SolicitacaoService(conexao);
        dono = usuarios.cadastrar("Ana", "ana@exemplo.br", "Software", "111");
        outro = usuarios.cadastrar("Bruno", "bruno@exemplo.br", "Software", "222");
        terceiro = usuarios.cadastrar("Carla", "carla@exemplo.br", "Software", "333");
    }

    private Livro livro(TipoOferta tipo) throws Exception {
        return livros.cadastrar(dono.getId(), "Java", "Autor", "POO", "Bom", tipo);
    }

    private Solicitacao reler(int id) throws Exception {
        return pedidos.historico(outro.getId()).stream().filter(p -> p.id() == id).findFirst().orElseThrow();
    }

    @Test
    void solicitaComFotografiaEImpedePedidosInvalidos() throws Exception {
        Livro livro = livro(TipoOferta.TROCA);
        assertThrows(IllegalArgumentException.class, () -> pedidos.solicitar(dono.getId(), livro.getId()));
        assertThrows(IllegalArgumentException.class, () -> pedidos.solicitar(Integer.MAX_VALUE, livro.getId()));
        assertThrows(IllegalArgumentException.class, () -> pedidos.solicitar(outro.getId(), Integer.MAX_VALUE));
        Solicitacao p = pedidos.solicitar(outro.getId(), livro.getId());
        assertEquals(StatusSolicitacao.PENDENTE, p.status());
        assertEquals(dono.getId(), p.ofertanteOriginalId());
        assertEquals("Java", p.tituloLivro());
        assertNotNull(p.dataSolicitacao());
        assertNull(p.dataDecisao());
        assertNull(p.dataFinalizacao());
        assertEquals(StatusLivro.DISPONIVEL, livros.buscar(livro.getId()).getStatus());
        assertThrows(IllegalArgumentException.class, () -> pedidos.solicitar(terceiro.getId(), livro.getId()));
        assertThrows(IllegalArgumentException.class, () -> livros.atualizar(dono.getId(), livro.getId(), "Novo", "Autor", "POO", "Bom", TipoOferta.TROCA));
        assertThrows(IllegalArgumentException.class, () -> livros.excluir(dono.getId(), livro.getId()));
        assertEquals(1, pedidos.historico(dono.getId()).size());
    }

    @Test
    void recusaSomentePeloDonoELiberaNovoPedido() throws Exception {
        Livro livro = livro(TipoOferta.EMPRESTIMO);
        Solicitacao p = pedidos.solicitar(outro.getId(), livro.getId());
        assertThrows(IllegalArgumentException.class, () -> pedidos.recusar(outro.getId(), p.id()));
        assertThrows(IllegalArgumentException.class, () -> pedidos.aceitar(terceiro.getId(), p.id()));
        assertThrows(IllegalArgumentException.class, () -> pedidos.finalizar(dono.getId(), p.id()));
        pedidos.recusar(dono.getId(), p.id());
        Solicitacao recusada = reler(p.id());
        assertEquals(StatusSolicitacao.RECUSADA, recusada.status());
        assertNotNull(recusada.dataDecisao());
        assertThrows(IllegalArgumentException.class, () -> pedidos.recusar(dono.getId(), p.id()));
        assertThrows(IllegalArgumentException.class, () -> pedidos.aceitar(dono.getId(), p.id()));
        assertThrows(IllegalArgumentException.class, () -> pedidos.finalizar(dono.getId(), p.id()));
        assertEquals(recusada, reler(p.id()));
        assertTrue(pedidos.solicitar(terceiro.getId(), livro.getId()).id() > p.id());
    }

    @Test
    void aceitaUmaVezEBloqueiaOutroPedido() throws Exception {
        Livro livro = livro(TipoOferta.TROCA);
        Solicitacao p = pedidos.solicitar(outro.getId(), livro.getId());
        pedidos.aceitar(dono.getId(), p.id());
        Solicitacao aceita = reler(p.id());
        assertEquals(StatusSolicitacao.ACEITA, aceita.status());
        assertNotNull(aceita.dataDecisao());
        assertEquals(StatusLivro.INDISPONIVEL, livros.buscar(livro.getId()).getStatus());
        assertThrows(IllegalArgumentException.class, () -> pedidos.aceitar(dono.getId(), p.id()));
        assertThrows(IllegalArgumentException.class, () -> pedidos.recusar(dono.getId(), p.id()));
        assertThrows(IllegalArgumentException.class, () -> pedidos.solicitar(terceiro.getId(), livro.getId()));
        assertThrows(IllegalArgumentException.class, () -> pedidos.finalizar(outro.getId(), p.id()));
        assertThrows(IllegalArgumentException.class, () -> pedidos.finalizar(terceiro.getId(), p.id()));
        assertEquals(aceita, reler(p.id()));
    }

    @Test
    void devolucaoMantemDonoEDisponibiliza() throws Exception {
        Livro livro = livro(TipoOferta.EMPRESTIMO);
        Solicitacao p = pedidos.solicitar(outro.getId(), livro.getId());
        pedidos.aceitar(dono.getId(), p.id());
        pedidos.finalizar(dono.getId(), p.id());
        Livro devolvido = livros.buscar(livro.getId());
        assertEquals(dono.getId(), devolvido.getUsuarioId());
        assertEquals(StatusLivro.DISPONIVEL, devolvido.getStatus());
        assertEquals(StatusSolicitacao.FINALIZADA, reler(p.id()).status());
        assertNotNull(reler(p.id()).dataFinalizacao());
        assertEquals(1, usuarios.buscarComLivros(dono.getId()).getLivros().size());
        assertTrue(usuarios.buscarComLivros(outro.getId()).getLivros().isEmpty());
        assertTrue(pedidos.solicitar(terceiro.getId(), livro.getId()).id() > p.id());
    }

    @Test
    void trocaTransfereERepeticaoNaoAlteraDatasNemDono() throws Exception {
        Livro livro = livro(TipoOferta.TROCA);
        Solicitacao p = pedidos.solicitar(outro.getId(), livro.getId());
        pedidos.aceitar(dono.getId(), p.id());
        pedidos.finalizar(dono.getId(), p.id());
        Solicitacao finalizada = reler(p.id());
        Livro trocado = livros.buscar(livro.getId());
        assertEquals(outro.getId(), trocado.getUsuarioId());
        assertEquals(StatusLivro.DISPONIVEL, trocado.getStatus());
        assertEquals(TipoOferta.TROCA, trocado.getTipoOferta());
        assertTrue(usuarios.buscarComLivros(dono.getId()).getLivros().isEmpty());
        Usuario novoDono = usuarios.buscarComLivros(outro.getId());
        assertSame(novoDono, novoDono.getLivros().get(0).getProprietario());
        assertThrows(IllegalArgumentException.class, () -> pedidos.finalizar(dono.getId(), p.id()));
        assertThrows(IllegalArgumentException.class, () -> pedidos.finalizar(outro.getId(), p.id()));
        assertEquals(finalizada, reler(p.id()));
        assertEquals(outro.getId(), livros.buscar(livro.getId()).getUsuarioId());
    }

    @Test
    void historicoSobreviveAEdicaoEDuasTransferencias() throws Exception {
        Livro livro = livro(TipoOferta.TROCA);
        Solicitacao p1 = pedidos.solicitar(outro.getId(), livro.getId());
        pedidos.aceitar(dono.getId(), p1.id());
        pedidos.finalizar(dono.getId(), p1.id());
        livros.atualizar(outro.getId(), livro.getId(), "Java 17", "Autor", "POO", "Bom", TipoOferta.TROCA);
        Solicitacao p2 = pedidos.solicitar(terceiro.getId(), livro.getId());
        pedidos.aceitar(outro.getId(), p2.id());
        pedidos.finalizar(outro.getId(), p2.id());
        livros.atualizar(terceiro.getId(), livro.getId(), "Outro título", "Autor", "POO", "Bom", TipoOferta.EMPRESTIMO);
        Solicitacao antigo = pedidos.historico(dono.getId()).get(0);
        assertEquals("Java", antigo.tituloLivro());
        assertEquals(TipoOferta.TROCA, antigo.tipoOferta());
        assertEquals(dono.getId(), antigo.ofertanteOriginalId());
        assertEquals(outro.getId(), antigo.solicitanteId());
        assertEquals(1, pedidos.historico(dono.getId()).size());
        assertEquals(2, pedidos.historico(outro.getId()).size());
        assertEquals("Java 17", pedidos.historico(terceiro.getId()).get(0).tituloLivro());
        assertEquals(outro.getId(), pedidos.historico(terceiro.getId()).get(0).ofertanteOriginalId());
        assertThrows(IllegalArgumentException.class, () -> livros.excluir(terceiro.getId(), livro.getId()));
    }

    @Test
    void doisPedidosConcorrentesCriamApenasUmAtivo() throws Exception {
        Livro livro = livro(TipoOferta.TROCA);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch prontos = new CountDownLatch(2);
        CountDownLatch largada = new CountDownLatch(1);
        try {
            Future<Boolean> a = executor.submit(() -> tentarPedido(outro.getId(), livro.getId(), prontos, largada));
            Future<Boolean> b = executor.submit(() -> tentarPedido(terceiro.getId(), livro.getId(), prontos, largada));
            assertTrue(prontos.await(5, TimeUnit.SECONDS));
            largada.countDown();
            assertNotEquals(a.get(15, TimeUnit.SECONDS), b.get(15, TimeUnit.SECONDS));
            assertEquals(1, pedidos.historico(dono.getId()).size());
        } finally { largada.countDown(); executor.shutdownNow(); }
    }

    private boolean tentarPedido(int usuario, int livro, CountDownLatch prontos, CountDownLatch largada) throws Exception {
        prontos.countDown();
        if (!largada.await(5, TimeUnit.SECONDS)) throw new IllegalStateException("Tempo de largada excedido.");
        try { pedidos.solicitar(usuario, livro); return true; }
        catch (IllegalArgumentException e) { return false; }
    }

    @Test
    void rollbackReverteLivroQuandoGravacaoDoPedidoFalha() throws Exception {
        Livro livro = livro(TipoOferta.TROCA);
        Solicitacao p = pedidos.solicitar(outro.getId(), livro.getId());
        SolicitacaoService falho = new SolicitacaoService(conexaoComFalhaNoPedido());
        assertThrows(SQLException.class, () -> falho.aceitar(dono.getId(), p.id()));
        assertEquals(StatusLivro.DISPONIVEL, livros.buscar(livro.getId()).getStatus());
        assertEquals(StatusSolicitacao.PENDENTE, reler(p.id()).status());
        assertNull(reler(p.id()).dataDecisao());
        pedidos.aceitar(dono.getId(), p.id());
        assertThrows(SQLException.class, () -> falho.finalizar(dono.getId(), p.id()));
        assertEquals(dono.getId(), livros.buscar(livro.getId()).getUsuarioId());
        assertEquals(StatusLivro.INDISPONIVEL, livros.buscar(livro.getId()).getStatus());
        assertEquals(StatusSolicitacao.ACEITA, reler(p.id()).status());
        assertNull(reler(p.id()).dataFinalizacao());
    }

    // Injeta uma falha após UPDATE real do livro, mantendo a conexão com MySQL real.
    private Conexao conexaoComFalhaNoPedido() {
        return new Conexao("teste", "teste", "teste") {
            @Override public Connection abrir() throws SQLException {
                Connection real = conexao.abrir();
                return (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(),
                        new Class<?>[]{Connection.class}, (proxy, metodo, args) -> {
                            if (metodo.getName().equals("prepareStatement") && args[0] instanceof String sql
                                    && sql.startsWith("UPDATE solicitacoes")) throw new SQLException("Falha injetada para testar rollback.");
                            try { return metodo.invoke(real, args); }
                            catch (InvocationTargetException e) { throw e.getCause(); }
                        });
            }
        };
    }
}
