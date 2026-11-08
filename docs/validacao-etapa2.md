# Validação da etapa 2 - 07/11/2026

## Base e ambiente

Base: `feat/cadastros-crud`, commit `2085ee376cbf933edb79542ec8998430062bf751`. O PR #1 estava aberto, sem merge. Esta etapa preserva os cadastros, CRUD, requisitos e cronograma da dupla.

Ambiente verificado: Linux, OpenJDK 17.0.20, Maven 3.9.9, MySQL Community 8.4.13, Connector/J 8.4.0 e JUnit 5.11.4. MySQL foi iniciado em instância isolada, com dados fictícios; integração em sebo_test e demonstração em banco separado. Ferramentas portáteis e credenciais não integram o repositório.

## Resultados

- Suíte original reproduzida com MySQL: 18 testes aprovados (9 de integração), zero falhas/erros/ignorados nos relatórios JUnit.
- Suíte final, `mvn clean verify`: **30 testes, zero falhas, zero erros, zero ignorados; BUILD SUCCESS**.
- Modelo: 8 testes (5 existentes + 3 novos).
- Menu: 5 testes (4 existentes + 1 novo).
- Integração MySQL: 17 testes (9 existentes + 8 novos).
- Sem variáveis de integração: a execução inicial confirmou que testes com MySQL são ignorados; esse resultado não foi usado como prova de persistência.

## Cobertura acrescentada

- Pedido válido; usuário/livro inexistentes; livro próprio; segundo pedido pendente ou aceito.
- Aceite e recusa pelo proprietário original; usuários sem autorização; transições inválidas e repetidas.
- Recusa libera nova solicitação.
- Devolução mantém dono; troca transfere; ambas disponibilizam; repetição preserva estado e datas.
- Histórico mantém título, tipo e participantes após edição e duas transferências.
- Dois pedidos concorrentes em conexões distintas: um sucesso e uma rejeição; apenas um pedido ativo persistido.
- Falha injetada na gravação do pedido após UPDATE real do livro: rollback confirmado no MySQL no aceite e na finalização, incluindo dono, status e datas.
- Composição: mesma instância sai da coleção antiga e entra na nova; lista externa não modificável. Polimorfismo executado tanto no modelo quanto no serviço real.
- Menu exige usuário nas oito opções novas.

## Demonstração em processos separados

Sete inicializações Java, usando Main e entrada pelo terminal, verificadas por código de saída e mensagens esperadas:

| Processo | Ação confirmada |
| --- | --- |
| 1 | Cadastro de Ana/Bruno e livros de empréstimo/troca. |
| 2 | Bruno solicita os dois livros e consulta pedidos pendentes. |
| 3 | Ana consulta recebidos e aceita os dois pedidos. |
| 4 | Ana registra devolução e confirma troca; consulta histórico e seus livros. |
| 5 | Novo processo: Bruno consulta o livro transferido e histórico FINALIZADA. |
| 6 | Ana cria novo pedido para o exemplar agora pertencente a Bruno. |
| 7 | Bruno recusa; consulta histórico RECUSADA. |

A demonstração foi repetida após as últimas alterações, em banco fictício separado. Passos utilizados: [roteiro-demonstracao.md](roteiro-demonstracao.md).

## Limites e pendências

- Testes realizados neste ambiente Linux; ainda é necessário reproduzir no computador da dupla com as credenciais locais.
- O controle de concorrência pressupõe gravações pelo serviço que bloqueia o livro. SQL externo fora do protocolo não é validado pela aplicação.
- Sem mudança estrutural de schema. Dados anteriores não são apagados nem suas datas convertidas; novas operações usam UTC.
- Identificação por seleção de usuário é simulação acadêmica, não autenticação.
