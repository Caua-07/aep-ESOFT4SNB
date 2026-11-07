package br.edu.sebo.controller;

import br.edu.sebo.config.Conexao;
import br.edu.sebo.service.*;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.*;

class MenuTest {
    private String executar(String entrada) {
        ByteArrayOutputStream saida = new ByteArrayOutputStream();
        Conexao conexao = new Conexao("jdbc:mysql://127.0.0.1/nao_usado", "teste", "teste");
        new Menu(new ByteArrayInputStream(entrada.getBytes(StandardCharsets.UTF_8)),
                new PrintStream(saida, true, StandardCharsets.UTF_8),
                new UsuarioService(conexao), new LivroService(conexao)).executar();
        return saida.toString(StandardCharsets.UTF_8);
    }

    @Test
    void encerraComOpcaoZero() { assertTrue(executar("0\n").contains("Até logo")); }

    @Test
    void recuperaEntradaInvalidaSemEncerrarMenu() {
        String saida = executar("abc\n99\n0\n");
        assertTrue(saida.contains("número inteiro"));
        assertTrue(saida.contains("Opção inválida"));
        assertTrue(saida.contains("Até logo"));
    }

    @Test
    void exigeUsuarioAntesDeCadastrarLivro() {
        assertTrue(executar("4\n0\n").contains("Selecione um usuário"));
    }

    @Test
    void fimDaEntradaNaoCausaLoopOuCadastroParcial() {
        assertTrue(executar("1\nAna\n").contains("Entrada encerrada"));
    }
}
