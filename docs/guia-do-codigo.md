# Organização do código

## Exemplo: cadastrar livro

1. Menu lê os dados e o usuário selecionado.
2. LivroService cria o objeto e verifica se o proprietário existe.
3. O construtor de Livro valida os campos obrigatórios e os tamanhos.
4. LivroDAO faz o INSERT com parâmetros e devolve o objeto com o ID gerado.
5. Menu apresenta o resultado; os dados continuam no MySQL ao fechar o programa.

## Responsabilidades

Os arquivos ficam em src/main/java/br/edu/sebo.

| Classe | Responsabilidade |
| --- | --- |
| controller/Main | Lê configuração, testa conexão e inicia o menu. |
| controller/Menu | Teclado e mensagens; não contém SQL. |
| config/Conexao | Abre conexões JDBC com variáveis de ambiente. |
| model/Usuario | Dados, validação e coleção protegida de livros do estudante. |
| model/Livro | Classe abstrata, vínculo com proprietário e contrato de finalização. |
| model/LivroTroca e LivroEmprestimo | Herdam Livro; finalizam com @Override: troca transfere, empréstimo mantém o dono. |
| model/TipoOferta e StatusLivro | Enumeram valores permitidos. |
| model/Validacao | Texto obrigatório e limite de tamanho. |
| dao/UsuarioDAO e LivroDAO | Comandos SQL e conversão de linhas em objetos. |
| service/UsuarioService | Coordena cadastro/consulta e trata e-mail duplicado. |
| service/LivroService | Confere proprietário, pedido ativo e histórico antes de alterar. |

## Conceitos utilizados

- Encapsulamento: atributos privados e getters; status e dono não são editáveis pelo menu.
- Abstração/herança: Livro não é instanciado diretamente; subclasses representam ofertas.
- Polimorfismo: DAO e menu usam Livro; getTipoOferta() executa a versão da subclasse.
- Persistência: listas apenas apresentam resultados; quem guarda os dados é o banco.
- PreparedStatement: separa valores do usuário dos comandos SQL.
- try-with-resources: fecha conexão, comando e resultado, mesmo com erro.
- Transação: regras e gravação de edição/exclusão acontecem juntas; rollback desfaz a operação em caso de falha.
- FOR UPDATE: bloqueia o livro durante a verificação e alteração. SolicitacaoService segue a mesma ordem: livro primeiro, pedido depois.

## Solicitações e negociações

- `Solicitacao`: record Java 17, uma fotografia imutável da linha do pedido; seus métodos de leitura são `id()`, `status()`, `tituloLivro()` etc. O record é uma classe de dados, não contém SQL.
- `StatusSolicitacao`: enum dos quatro estados permitidos.
- `SolicitacaoDAO`: INSERT, consultas e UPDATE explícitos; copia título/tipo/participantes no pedido e nunca os atualiza.
- `SolicitacaoService`: verifica existência, autorização e transições; coordena bloqueios, commit e rollback.

Ao solicitar, o serviço bloqueia o livro, confere disponibilidade e ausência de pedido ativo e grava PENDENTE. Ao decidir/finalizar, a primeira leitura só localiza o ID do livro; dentro da transação o livro e o pedido são relidos sob bloqueio antes de validar. Isso evita usar um estado antigo após esperar outra transação.

Na finalização, o serviço chama `livro.finalizarNegociacao(solicitante)` usando a referência abstrata Livro. A JVM executa o @Override correto: LivroTroca transfere a mesma instância entre as coleções e disponibiliza; LivroEmprestimo apenas disponibiliza. O serviço grava o livro e FINALIZADA/data na mesma transação. Os objetos são locais à operação e não são publicados se houver rollback.

A composição usa `Usuario.getLivros()` e `Livro.getProprietario()`. O DAO carrega o vínculo do exemplar ao ler o banco; `UsuarioService.buscarComLivros()` carrega a coleção completa para a opção 16. A lista retornada é uma cópia não modificável. Os construtores com ID são preservados para compatibilidade; objetos montados diretamente precisam de `vincularProprietario()` antes de finalizar. Não existe cache global: cada consulta reflete novamente o banco.

O bloqueio do livro serializa operações da aplicação sobre o mesmo exemplar. Escritas SQL externas que ignorem esse protocolo não são cobertas pela regra de uma solicitação ativa. Não há alteração do schema nem índice UNIQUE adicional nesta etapa.

## Escolhas de simplicidade

Java e JDBC diretos deixam as regras e o SQL visíveis. Maven apenas organiza dependências e testes. Mantivemos transações explícitas, sem estrutura genérica, framework web ou ORM.
