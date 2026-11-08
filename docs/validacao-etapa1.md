# Validação da etapa 1 - 07/11/2026

## Ambiente utilizado

- Windows, JDK 17.0.14, Maven 3.9.9.
- MySQL Community Server 8.4.13, instância isolada na porta 3307.
- Connector/J 8.4.0 e JUnit 5.11.4.
- Bancos separados sebo_colaborativo e sebo_test, sem dados reais.

## Verificações realizadas

- `mvn clean verify`: **18 testes, zero falhas, zero erros, zero ignorados**.
- Modelo: 5 testes para campos, e-mail, tamanhos e tipos de oferta.
- MySQL: 9 testes para persistência, CRUD, e-mail duplicado, autorização, pedido ativo e preservação de histórico.
- Menu: 4 testes para saída, entrada inválida, usuário não selecionado e fim da entrada.
- `mvn compile exec:java`: conexão e encerramento normal, sem avisos de thread do driver.
- Demonstração em 7 processos Java separados: cadastro de usuário, cadastro de livro, consulta após reiniciar, edição e mudança de tipo, cancelamento de exclusão, exclusão confirmada e consulta do registro inexistente.
- Revisão independente identificou validação insuficiente de domínio de e-mail; o caso foi reproduzido em teste que falhou e corrigido antes da suíte final.

## Limites

Não foram implementados ou validados os fluxos de solicitar/aceitar/recusar/finalizar, a composição 1:N em Java ou a consulta de histórico. Registros de solicitações nos testes são dados preparados para comprovar as proteções do CRUD; não representam um fluxo de negociação pronto.

A concorrência de pedidos será testada na próxima etapa, junto com seu serviço. O SQL desta etapa tem índice de consulta e FKs; a regra de apenas um pedido ativo deverá ser garantida pela transação do futuro serviço.

Instruções de execução em [execucao.md](execucao.md).
