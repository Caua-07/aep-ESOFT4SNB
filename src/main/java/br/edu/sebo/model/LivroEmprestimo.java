package br.edu.sebo.model;

public class LivroEmprestimo extends Livro {
    public LivroEmprestimo(int id, int usuarioId, String titulo, String autor,
                          String disciplina, String conservacao, StatusLivro status) {
        super(id, usuarioId, titulo, autor, disciplina, conservacao, status);
    }

    @Override
    public TipoOferta getTipoOferta() { return TipoOferta.EMPRESTIMO; }
    @Override
    public void finalizarNegociacao(Usuario solicitante) {
        validarFinalizacao(solicitante);
        disponibilizar();
    }
}
