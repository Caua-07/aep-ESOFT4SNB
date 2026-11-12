# Diagramas do Sebo Colaborativo

## Classes

```mermaid
classDiagram
    class Usuario {
        -int id
        -String nomeCompleto
        -String emailInstitucional
        -String curso
        -String telefone
        -List~Livro~ livros
    }
    class Livro {
        <<abstract>>
        -int id
        -String titulo
        -String autor
        -String disciplina
        -String estadoConservacao
        -StatusLivro status
        -Usuario proprietario
        +getProprietario() Usuario
        +finalizarNegociacao(Usuario solicitante) void
    }
    class LivroTroca {
        +finalizarNegociacao(Usuario solicitante) void
    }
    class LivroEmprestimo {
        +finalizarNegociacao(Usuario solicitante) void
    }
    class Solicitacao {
        -int id
        -LocalDateTime dataSolicitacao
        -LocalDateTime dataDecisao
        -LocalDateTime dataFinalizacao
        -int ofertanteOriginalId
        -int solicitanteId
        -int livroId
        -String tituloLivro
        -TipoOferta tipoOferta
        -StatusSolicitacao status
    }
    Livro <|-- LivroTroca
    Livro <|-- LivroEmprestimo
    Usuario "1" *-- "0..*" Livro : oferta
    Usuario "1" --> "0..*" Solicitacao : solicita
    Usuario "1" --> "0..*" Solicitacao : ofertante original
    Livro "1" --> "0..*" Solicitacao : recebe
```

Na devolução, o exemplar continua com o proprietário original. Na troca, passa à coleção do solicitante.

## Banco de dados

```mermaid
erDiagram
    usuarios ||--o{ livros : oferta
    usuarios ||--o{ solicitacoes : solicita
    usuarios ||--o{ solicitacoes : ofertante_original
    livros ||--o{ solicitacoes : recebe

    usuarios {
        INT id PK
        VARCHAR(150) nome_completo
        VARCHAR(150) email_institucional
        VARCHAR(100) curso
        VARCHAR(20) telefone
    }
    livros {
        INT id PK
        VARCHAR(150) titulo
        VARCHAR(150) autor
        VARCHAR(100) disciplina
        VARCHAR(50) estado_conservacao
        ENUM tipo_oferta
        ENUM status
        INT usuario_id FK
    }
    solicitacoes {
        INT id PK
        INT livro_id FK
        INT usuario_id FK
        INT ofertante_original_id FK
        VARCHAR(150) titulo_livro_na_solicitacao
        ENUM tipo_oferta_na_solicitacao
        DATETIME data_solicitacao
        DATETIME data_decisao
        DATETIME data_finalizacao
        ENUM status
    }
```

A tabela `livros` identifica o proprietário atual. A tabela `solicitacoes` guarda o ofertante, o solicitante, o título e o tipo de oferta da época do pedido.
