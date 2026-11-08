package br.edu.sebo.controller;

import br.edu.sebo.model.*;
import br.edu.sebo.service.*;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Menu {
    private final Scanner entrada;
    private final PrintStream saida;
    private final UsuarioService usuarios;
    private final LivroService livros;
    private final SolicitacaoService solicitacoes;
    private Usuario usuarioAtual;

    public Menu(InputStream entrada, PrintStream saida, UsuarioService usuarios, LivroService livros,
                SolicitacaoService solicitacoes) {
        this.solicitacoes = solicitacoes;
        this.entrada = new Scanner(entrada, StandardCharsets.UTF_8);
        this.saida = saida;
        this.usuarios = usuarios;
        this.livros = livros;
    }

    public void executar() {
        while (true) {
            mostrarOpcoes();
            try {
                int opcao = numero("Opção: ");
                switch (opcao) {
                    case 0: saida.println("Até logo!"); return;
                    case 1: cadastrarUsuario(); break;
                    case 2: listarUsuarios(); break;
                    case 3: selecionarUsuario(); break;
                    case 4: cadastrarLivro(); break;
                    case 5: listarLivros(); break;
                    case 6: mostrarLivro(livros.buscar(numero("ID do livro: "))); break;
                    case 7: editarLivro(); break;
                    case 8: excluirLivro(); break;
                    case 9: solicitarLivro(); break;
                    case 10: listarSolicitacoes(1); break;
                    case 11: listarSolicitacoes(2); break;
                    case 12: decidirSolicitacao(true); break;
                    case 13: decidirSolicitacao(false); break;
                    case 14: finalizarSolicitacao(); break;
                    case 15: listarSolicitacoes(0); break;
                    case 16: listarMeusLivros(); break;
                    default: saida.println("Opção inválida.");
                }
            } catch (NoSuchElementException e) {
                saida.println("Entrada encerrada. Até logo!");
                return;
            } catch (IllegalArgumentException e) {
                saida.println(e.getMessage());
            } catch (SQLException e) {
                // Não mostrar credenciais nem a URL da conexão na mensagem.
                saida.println("Falha no banco (SQLState " + e.getSQLState() + "). Confira a conexão e o script SQL.");
            }
        }
    }

    private void mostrarOpcoes() {
        saida.println("\n=== SEBO COLABORATIVO ===");
        saida.println("Usuário: " + (usuarioAtual == null ? "nenhum selecionado" : usuarioAtual.getNomeCompleto()));
        saida.println("1 - Cadastrar usuário     2 - Listar usuários     3 - Selecionar usuário");
        saida.println("4 - Cadastrar livro       5 - Listar livros       6 - Consultar livro");
        saida.println("7 - Editar livro          8 - Excluir livro       0 - Sair");
        saida.println("9 - Solicitar livro       10 - Recebidas          11 - Feitas");
        saida.println("12 - Aceitar pedido       13 - Recusar pedido");
        saida.println("14 - Registrar devolução / confirmar troca      15 - Histórico");
        saida.println("16 - Meus livros atuais");
    }

    private void cadastrarUsuario() throws SQLException {
        Usuario usuario = usuarios.cadastrar(texto("Nome completo: "), texto("E-mail institucional: "),
                texto("Curso: "), texto("Telefone: "));
        saida.println("Usuário cadastrado. ID: " + usuario.getId());
    }

    private void listarUsuarios() throws SQLException {
        List<Usuario> lista = usuarios.listar();
        if (lista.isEmpty()) saida.println("Nenhum usuário cadastrado.");
        for (Usuario usuario : lista) {
            saida.println(usuario.getId() + " - " + usuario.getNomeCompleto() + " | " + usuario.getCurso());
        }
    }

    private void selecionarUsuario() throws SQLException {
        usuarioAtual = usuarios.buscar(numero("ID do usuário: "));
        saida.println("Usuário selecionado: " + usuarioAtual.getNomeCompleto());
    }

    private void cadastrarLivro() throws SQLException {
        exigirUsuario();
        Livro livro = livros.cadastrar(usuarioAtual.getId(), texto("Título: "), texto("Autor: "),
                texto("Disciplina: "), texto("Conservação: "), lerTipoOferta());
        saida.println("Livro cadastrado. ID: " + livro.getId());
    }

    private void listarLivros() throws SQLException {
        List<Livro> lista = livros.listar();
        if (lista.isEmpty()) saida.println("Nenhum livro cadastrado.");
        for (Livro livro : lista) mostrarLivro(livro);
    }

    private void mostrarLivro(Livro livro) throws SQLException {
        saida.println("\nID " + livro.getId() + " | " + livro.getTitulo() + " | " + livro.getAutor());
        saida.println("Disciplina: " + livro.getDisciplina() + " | Conservação: " + livro.getEstadoConservacao());
        saida.println("Oferta: " + livro.getTipoOferta() + " | Status: " + livro.getStatus()
                + " | Proprietário (ID): " + livro.getUsuarioId());
        if (livros.temSolicitacaoAtiva(livro.getId())) {
            saida.println(livro.getStatus() == StatusLivro.DISPONIVEL
                    ? "Solicitação pendente - novos pedidos e edição bloqueados, mesmo com status DISPONIVEL."
                    : "Solicitação aceita: novos pedidos e edição bloqueados.");
        }
    }

    private void editarLivro() throws SQLException {
        exigirUsuario();
        int id = numero("ID do livro: ");
        mostrarLivro(livros.buscar(id));
        saida.println("Informe todos os dados atualizados:");
        livros.atualizar(usuarioAtual.getId(), id, texto("Título: "), texto("Autor: "),
                texto("Disciplina: "), texto("Conservação: "), lerTipoOferta());
        saida.println("Livro atualizado.");
    }

    private void excluirLivro() throws SQLException {
        exigirUsuario();
        int id = numero("ID do livro: ");
        mostrarLivro(livros.buscar(id));
        if (!texto("Confirmar exclusão? Digite SIM: ").equalsIgnoreCase("SIM")) {
            saida.println("Exclusão cancelada.");
            return;
        }
        livros.excluir(usuarioAtual.getId(), id);
        saida.println("Livro excluído.");
    }

    private void solicitarLivro() throws SQLException {
        exigirUsuario();
        Solicitacao pedido = solicitacoes.solicitar(usuarioAtual.getId(), numero("ID do livro: "));
        saida.println("Solicitação criada. ID: " + pedido.id() + " | PENDENTE");
        saida.println("Livro permanece DISPONIVEL, mas novos pedidos ficam bloqueados até a decisão.");
    }

    private void listarSolicitacoes(int filtro) throws SQLException {
        exigirUsuario();
        boolean encontrou = false;
        for (Solicitacao pedido : solicitacoes.historico(usuarioAtual.getId())) {
            if (filtro == 1 && pedido.ofertanteOriginalId() != usuarioAtual.getId()) continue;
            if (filtro == 2 && pedido.solicitanteId() != usuarioAtual.getId()) continue;
            encontrou = true;
            saida.println("Pedido " + pedido.id() + " | Livro " + pedido.livroId() + " | " + pedido.tituloLivro()
                    + " | " + pedido.tipoOferta() + " | " + pedido.status());
            saida.println("Ofertante original: " + pedido.ofertanteOriginalId() + " | Solicitante: " + pedido.solicitanteId());
            saida.println("Datas em UTC - Solicitação: " + pedido.dataSolicitacao() + " | Decisão: " + pedido.dataDecisao()
                    + " | Finalização: " + pedido.dataFinalizacao());
        }
        if (!encontrou) saida.println("Nenhuma solicitação encontrada.");
    }

    private void decidirSolicitacao(boolean aceitar) throws SQLException {
        exigirUsuario();
        int id = numero("ID da solicitação: ");
        if (aceitar) solicitacoes.aceitar(usuarioAtual.getId(), id);
        else solicitacoes.recusar(usuarioAtual.getId(), id);
        saida.println(aceitar ? "Solicitação aceita; livro INDISPONIVEL." : "Solicitação recusada; novos pedidos liberados.");
    }

    private void finalizarSolicitacao() throws SQLException {
        exigirUsuario();
        int id = numero("ID da solicitação: ");
        if (!texto("Confirma que recebeu a devolução ou efetivou a troca física? Digite SIM: ").equalsIgnoreCase("SIM")) {
            saida.println("Finalização não realizada.");
            return;
        }
        solicitacoes.finalizar(usuarioAtual.getId(), id);
        saida.println("Negociação FINALIZADA; livro DISPONIVEL. Na troca, a propriedade passa ao solicitante.");
    }

    private void listarMeusLivros() throws SQLException {
        exigirUsuario();
        Usuario atualizado = usuarios.buscarComLivros(usuarioAtual.getId());
        if (atualizado.getLivros().isEmpty()) saida.println("Nenhum livro de sua propriedade.");
        for (Livro livro : atualizado.getLivros()) mostrarLivro(livro);
    }

    private void exigirUsuario() {
        if (usuarioAtual == null) throw new IllegalArgumentException("Selecione um usuário na opção 3.");
    }

    private TipoOferta lerTipoOferta() {
        int opcao = numero("Tipo de oferta (1 - Troca / 2 - Empréstimo): ");
        if (opcao == 1) return TipoOferta.TROCA;
        if (opcao == 2) return TipoOferta.EMPRESTIMO;
        throw new IllegalArgumentException("Tipo inválido: escolha 1 ou 2.");
    }

    private String texto(String mensagem) {
        saida.print(mensagem);
        return entrada.nextLine().strip();
    }

    private int numero(String mensagem) {
        try { return Integer.parseInt(texto(mensagem)); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Informe um número inteiro válido."); }
    }
}
