package br.edu.sebo.model;

public abstract class Livro {
    private final int id;
    private final int usuarioId;
    private final String titulo;
    private final String autor;
    private final String disciplina;
    private final String estadoConservacao;
    private final StatusLivro status;

    protected Livro(int id, int usuarioId, String titulo, String autor,
                    String disciplina, String conservacao, StatusLivro status) {
        if (usuarioId <= 0) throw new IllegalArgumentException("Proprietário inválido.");
        if (status == null) throw new IllegalArgumentException("Status é obrigatório.");
        this.id = id;
        this.usuarioId = usuarioId;
        this.titulo = Validacao.texto(titulo, "Título", 150);
        this.autor = Validacao.texto(autor, "Autor", 150);
        this.disciplina = Validacao.texto(disciplina, "Disciplina", 100);
        this.estadoConservacao = Validacao.texto(conservacao, "Conservação", 50);
        this.status = status;
    }

    public abstract TipoOferta getTipoOferta();

    public int getId() { return id; }
    public int getUsuarioId() { return usuarioId; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public String getDisciplina() { return disciplina; }
    public String getEstadoConservacao() { return estadoConservacao; }
    public StatusLivro getStatus() { return status; }
}
