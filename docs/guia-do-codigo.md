# Guia para explicar o código

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
| model/Usuario | Dados e validação do estudante. |
| model/Livro | Classe abstrata com dados comuns dos livros. |
| model/LivroTroca e LivroEmprestimo | Herdam Livro e informam seu tipo com @Override. |
| model/TipoOferta e StatusLivro | Enumeram valores permitidos. |
| model/Validacao | Texto obrigatório e limite de tamanho. |
| dao/UsuarioDAO e LivroDAO | Comandos SQL e conversão de linhas em objetos. |
| service/UsuarioService | Coordena cadastro/consulta e trata e-mail duplicado. |
| service/LivroService | Confere proprietário, pedido ativo e histórico antes de alterar. |

## Conceitos para apresentar

- Encapsulamento: atributos privados e getters; status e dono não são editáveis pelo menu.
- Abstração/herança: Livro não é instanciado diretamente; subclasses representam ofertas.
- Polimorfismo inicial: DAO e menu usam Livro; getTipoOferta() executa a versão da subclasse.
- Persistência: listas apenas apresentam resultados; quem guarda os dados é o banco.
- PreparedStatement: separa valores do usuário dos comandos SQL.
- try-with-resources: fecha conexão, comando e resultado, mesmo com erro.
- Transação: regras e gravação de edição/exclusão acontecem juntas; rollback desfaz a operação em caso de falha.
- FOR UPDATE: bloqueia o livro durante a verificação e alteração. O futuro serviço de solicitações também deverá bloquear a mesma linha antes de criar/alterar pedidos.

## O que ainda falta

A classe Solicitacao, a composição 1:N em Java, finalizarNegociacao() polimórfico e o fluxo de negociações/histórico pertencem à próxima etapa. As FKs já existem, mas não substituem composição em objetos. Não apresentar esses itens como concluídos.

## Escolhas de simplicidade

Java e JDBC diretos deixam as regras e o SQL visíveis. Maven apenas organiza dependências e testes. Mantivemos transações explícitas, sem estrutura genérica, framework web ou ORM.

Cada integrante deve conseguir explicar um cadastro do menu ao banco, a diferença entre DAO e Service e um teste de regra de negócio. As próximas contribuições devem ser próprias e reais.
