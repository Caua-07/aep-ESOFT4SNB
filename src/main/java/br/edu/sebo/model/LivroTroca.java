package br.edu.sebo.model;

public class LivroTroca extends Livro {
    public LivroTroca(int id, int usuarioId, String titulo, String autor,
                      String disciplina, String conservacao, StatusLivro status) {
        super(id, usuarioId, titulo, autor, disciplina, conservacao, status);
    }

    @Override
    public TipoOferta getTipoOferta() { return TipoOferta.TROCA; }
    @Override
    public void finalizarNegociacao(Usuario solicitante) {
        validarFinalizacao(solicitante);
        transferirPara(solicitante);
        disponibilizar();
    }
}
