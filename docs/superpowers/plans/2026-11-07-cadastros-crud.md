# Cadastros e CRUD Implementation Plan

> Execução nesta sessão com executing-plans e testes antes da implementação.

**Goal:** cadastro de usuários e CRUD de livros em terminal, com MySQL real.
**Architecture:** Model → DAO → Service → Controller/Main. JDBC direto, sem framework web ou ORM.
**Tech Stack:** Java 17, Maven 3.9, MySQL 8.4, Connector/J e JUnit 5.
**Spec:** ../../regras-de-negocio.md, RN01–RN06, RN19–RN21 e critérios de aceite aplicáveis.

## Restrições

- Código simples, nomes em português, métodos curtos e SQL explícito.
- Não implementar o fluxo de solicitações nesta etapa. Preparar sua tabela para os vínculos do CRUD.
- Não afirmar que a segunda entrega inteira está concluída.
- Usar a cópia local isolada e branch feat/cadastros-crud.

## 1. Domínio e build

Arquivos: pom.xml; src/main/java/br/edu/sebo/model/{Usuario,Livro,LivroTroca,LivroEmprestimo,TipoOferta,StatusLivro,Validacao}.java; src/test/java/br/edu/sebo/model/ModeloTest.java.

- [x] Escrever testes de normalização de e-mail, campos obrigatórios, limites e distinção entre tipos de livro.
- [x] Rodar `mvn test`: verificar falha por ausência do domínio.
- [x] Implementar construtores com validação, getters e herança. Usar `getTipoOferta()` abstrato e sobrescrito nesta etapa; finalização virá no fluxo de negociação.
- [x] Rodar `mvn test`: todos os testes de domínio devem passar.

Exemplos de contrato:
```java
Usuario usuario = new Usuario(0, " Ana ", " ANA@ALUNOS.EXEMPLO.BR ", "Software", "044900000000");
assertEquals("ana@alunos.exemplo.br", usuario.getEmailInstitucional());
assertThrows(IllegalArgumentException.class, () -> new Usuario(0, " ", "a@b.br", "Curso", "123"));
```

## 2. Banco, DAO e serviços

Arquivos: database/schema.sql; config/Conexao.java; dao/{UsuarioDAO,LivroDAO}.java; service/{UsuarioService,LivroService}.java; teste IntegracaoMySqlTest.java.

Interfaces: Conexao.abrir() retorna Connection; serviços recebem Conexao; cadastrar retorna objeto persistido com ID; listar retorna List; buscar retorna objeto ou erro de negócio. LivroService.atualizar(usuarioId, livroId, titulo, autor, disciplina, conservacao, tipo) e excluir(usuarioId, livroId).

- [x] Testar cadastro persistido, e-mail duplicado e CRUD em MySQL separado.
- [x] Verificar falha antes de implementar os serviços.
- [x] Criar tabelas usuarios/livros/solicitacoes, FKs restritivas e script sem apagar dados.
- [x] Implementar PreparedStatement e try-with-resources; serviços abrem conexão por operação.
- [x] Edição/exclusão bloqueiam a linha do livro em transação; validar proprietário, pedido ativo e histórico antes de alterar.
- [x] Testar outro proprietário, livro inexistente, usuário inexistente, edição com pedido ativo, exclusão com histórico, títulos repetidos e troca de tipo de oferta.
- [x] Confirmar persistência reabrindo conexão e reiniciando um processo Java.

Exemplo de aceite independente:
```java
Livro salvo = livros.cadastrar(dono.getId(), "Java", "Autor", "POO", "Bom", TipoOferta.TROCA);
assertEquals("Java", new LivroService(conexao).buscar(salvo.getId()).getTitulo());
assertThrows(IllegalArgumentException.class, () -> livros.excluir(outro.getId(), salvo.getId()));
```

## 3. Menu e documentação

Arquivos: controller/Main.java; controller/Menu.java; teste MenuTest.java; README.md; docs/guia-do-codigo.md; database/README.md.

- [x] Testar saída com opção zero e recuperação de opção inválida antes do menu.
- [x] Implementar leitura por linha, mensagens de validação e confirmação antes da exclusão.
- [x] Demonstrar cadastro, seleção de usuário, CRUD e leitura após reiniciar.
- [x] Documentar comandos, credenciais via ambiente, limites da etapa e mapa de classes para apresentação.
- [x] Executar suíte completa, revisão independente e publicar a etapa com status honesto.

## Registro

- Base: 399c6f6244a50ff400fc7cbd1bcfa6a9616da159.
- Decisão: layout padrão Maven mantém camadas e facilita abrir o projeto em IDEs.
- Decisão: instância MySQL de teste portátil e isolada, sem alterar serviços existentes.
- Revisão deve focar autorização de edição/exclusão, histórico, falhas SQL, configuração e facilidade de explicar.

## Evidências de conclusão

18 testes passaram com MySQL real; menu executado por Maven e demonstração em sete processos. Revisão independente corrigida com regressão de e-mail. Detalhes em ../../validacao-etapa1.md.

Ruling: cópia clonada isolada com branch própria em vez de worktree vinculado; não havia checkout anterior. Ruling: composição e finalização adiadas com negociações, para manter o marco atual simples.
