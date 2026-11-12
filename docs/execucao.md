# Execução e testes

## Ambiente

Instale JDK 17, Maven 3.9+ e MySQL 8.4. Confira `java -version`, `javac -version` e `mvn -version`. O Maven deve usar JDK 17; ajuste JAVA_HOME se houver Java 8 no PATH.

Fontes oficiais: [JDK](https://adoptium.net/temurin/releases/?version=17), [Maven](https://maven.apache.org/download.cgi), [MySQL](https://dev.mysql.com/downloads/mysql/8.4.html).

## Banco

Entre no cliente MySQL como administrador (`mysql -u root -p`) ou use Workbench. Substitua a senha de exemplo por uma senha local:

```sql
CREATE DATABASE sebo_colaborativo CHARACTER SET utf8mb4;
CREATE USER 'sebo'@'localhost' IDENTIFIED BY 'SUBSTITUA_POR_SUA_SENHA';
GRANT SELECT, INSERT, UPDATE, DELETE ON sebo_colaborativo.* TO 'sebo'@'localhost';
USE sebo_colaborativo;
```

Execute o conteúdo de `database/schema.sql` no banco selecionado. No cliente MySQL aberto na raiz do projeto, use `SOURCE database/schema.sql;`. Não publique senhas.

O script não apaga dados. IF NOT EXISTS cria uma instalação nova; não migra uma estrutura antiga diferente.

## Iniciar no PowerShell

Na raiz do projeto:

```powershell
$env:SEBO_DB_URL='jdbc:mysql://127.0.0.1:3306/sebo_colaborativo'
$env:SEBO_DB_USER='sebo'
$senha=Read-Host 'Senha do MySQL' -AsSecureString
$env:SEBO_DB_PASSWORD=[System.Net.NetworkCredential]::new('', $senha).Password
mvn compile exec:java
```

Linux/macOS: defina as mesmas três variáveis com `export` e execute `mvn compile exec:java`. Na IDE, configure as variáveis na execução de Main. Arquivos .env não são carregados automaticamente.

A porta padrão é 3306; altere se necessário.

## Menu

1. Cadastrar usuário.
2. Listar usuários e seus IDs.
3. Selecionar o usuário atuante.
4. Cadastrar livro: tipo 1 para troca, 2 para empréstimo.
5. Listar livros.
6. Consultar pelo ID.
7. Editar todos os campos de um livro do usuário, sem pedido ativo.
8. Excluir livro sem solicitações, confirmando com SIM.
9. Solicitar livro como outro usuário.
10. Listar solicitações recebidas (todos os estados).
11. Listar solicitações feitas (todos os estados).
12. Aceitar pedido pendente como ofertante original.
13. Recusar pedido pendente como ofertante original.
14. Registrar devolução ou confirmar troca física, digitando SIM.
15. Consultar histórico completo do usuário, inclusive pedidos encerrados.
16. Consultar livros do proprietário atual, mesmo sem solicitações.
0. Sair. Reinicie para conferir a persistência.

A seleção de usuário é uma simulação acadêmica, não autenticação segura. Solicitação PENDENTE bloqueia novos pedidos embora o livro permaneça DISPONIVEL. No aceite ele passa a INDISPONIVEL.

## Testes

`mvn test` executa os testes de modelo e menu. Sem variáveis de teste, a classe de integração é explicitamente ignorada; isso não comprova persistência.

Para testar MySQL, crie um banco **descartável chamado sebo_test**, execute o mesmo schema.sql nele e conceda SELECT, INSERT, UPDATE e DELETE a um usuário de teste. Os testes limpam as três tabelas desse banco antes de cada cenário; nunca coloque dados reais nele.

```powershell
$env:SEBO_TEST_URL='jdbc:mysql://127.0.0.1:3306/sebo_test'
$env:SEBO_TEST_USER='sebo_teste'
$senha=Read-Host 'Senha do banco de teste' -AsSecureString
$env:SEBO_TEST_PASSWORD=[System.Net.NetworkCredential]::new('', $senha).Password
mvn test
mvn package
```

O nome sebo_test é obrigatório para impedir limpeza acidental do banco da aplicação. Configure os privilégios desse usuário apenas no banco de teste. Relatórios ficam em target/surefire-reports.

O JAR de `mvn package` não inclui dependências; use `mvn exec:java` para executar com o driver no classpath.

## Problemas comuns

- Java antigo: ajuste JAVA_HOME e confira mvn -version.
- Conexão recusada: confira serviço, host e porta do MySQL.
- Access denied: confira usuário, senha e privilégios.
- Tabela inexistente: execute schema.sql no banco indicado na URL.
- Public Key Retrieval is not allowed: apenas em instância descartável estritamente local sem TLS, acrescente `?allowPublicKeyRetrieval=true&sslMode=DISABLED` à URL. Em conexões remotas, configure TLS.
- Acentos: configure terminal e IDE para UTF-8; no Windows, `chcp 65001` pode ser necessário.
