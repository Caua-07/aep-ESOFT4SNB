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
    private Usuario usuarioAtual;

    public Menu(InputStream entrada, PrintStream saida, UsuarioService usuarios, LivroService livros) {
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
            saida.println("Solicitação ativa: novos pedidos e edição bloqueados.");
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
