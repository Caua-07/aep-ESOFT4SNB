package br.edu.sebo.model;

public abstract class Livro {
    private final int id;
    private int usuarioId;
    private Usuario proprietario;
    private final String titulo;
    private final String autor;
    private final String disciplina;
    private final String estadoConservacao;
    private StatusLivro status;

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

    public abstract void finalizarNegociacao(Usuario solicitante);

    public Usuario getProprietario() { return proprietario; }

    public void vincularProprietario(Usuario usuario) {
        if (usuario == null || usuario.getId() != usuarioId) {
            throw new IllegalArgumentException("Proprietário incompatível com o livro.");
        }
        if (proprietario != null) proprietario.removerLivro(this);
        proprietario = usuario;
        usuario.adicionarLivro(this);
    }

    protected void validarFinalizacao(Usuario solicitante) {
        if (status != StatusLivro.INDISPONIVEL || proprietario == null
                || solicitante == null || solicitante.getId() <= 0
                || solicitante.getId() == usuarioId) {
            throw new IllegalArgumentException("Livro ou solicitante inválido para finalização.");
        }
    }

    protected void transferirPara(Usuario novoProprietario) {
        proprietario.removerLivro(this);
        usuarioId = novoProprietario.getId();
        proprietario = novoProprietario;
        proprietario.adicionarLivro(this);
    }

    protected void disponibilizar() { status = StatusLivro.DISPONIVEL; }

    public int getId() { return id; }
    public int getUsuarioId() { return usuarioId; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public String getDisciplina() { return disciplina; }
    public String getEstadoConservacao() { return estadoConservacao; }
    public StatusLivro getStatus() { return status; }
}
