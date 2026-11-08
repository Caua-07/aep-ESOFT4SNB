# Conferência final da segunda entrega — 08/11/2026

## Resultado

O projeto atende aos requisitos técnicos da segunda entrega dos documentos fornecidos pelo grupo. RF01–RF06, CRUD de Livro, POO e persistência MySQL estão implementados e integrados na branch `main`. Não foi encontrado defeito funcional bloqueador na revisão e nos cenários executados. A avaliação e o recebimento da entrega cabem à disciplina.

Fontes conferidas: `Sebo_Colaborativo_AEP_1.pdf` (4 páginas) e `Diretrizes Gerais da AEP (4º Semestre de 2026) (5).pdf` (8 páginas). A segunda entrega exige software e código-fonte; o PDF formal é explicitamente exigido para a primeira entrega. As regras adicionais adotadas pelo grupo estão separadas das exigências do professor em [regras-de-negocio.md](regras-de-negocio.md).

## Correspondência com as diretrizes

| Exigência | Evidência no projeto |
| --- | --- |
| Código-fonte completo e aplicação funcional — p. 4 | `src/main/java`, `pom.xml`, execução pelo terminal com `mvn compile exec:java`. |
| Aderência ao planejamento — p. 7 | RF01–RF06 preservados no README e implementados conforme a tabela abaixo; Java, MySQL, JDBC e camadas mantidos. |
| Classe abstrata/interface, herança e polimorfismo — p. 7 | `Livro` abstrata; `LivroTroca` e `LivroEmprestimo`; `@Override` em `finalizarNegociacao(Usuario)` com comportamento distinto, chamado pelo serviço. |
| Composição — p. 4 e planejamento original | `Usuario` mantém coleção de livros; proprietário obrigatório e transferência entre coleções na troca; coleção externa não modificável. |
| Tipagem e convenções — p. 7 | Classes Java, enums de tipo/estado, campos privados e nomes de classes/métodos conforme as convenções Java. |
| CRUD completo da entidade principal — p. 7 | `LivroService`/`LivroDAO` e opções 4–8 do menu: criar, consultar, editar e excluir. Restrições de vínculo preservam o histórico. |
| Banco ativo e SQL compatível em `/database` — p. 7 | JDBC real; `database/schema.sql`; três tabelas, chaves estrangeiras e datas; 17 testes com MySQL. |
| Contribuição de todos — p. 4 | Commits e PRs dos dois integrantes preservados em main, conforme seção abaixo. |
| Repositório público, pastas e README — p. 6 | Link público; `/src`, `/docs`, `/database`; requisitos e cronograma original mantidos no README. |
| Instruções de execução e dependências — p. 8 | [execucao.md](execucao.md): JDK, Maven, MySQL, schema, variáveis, comandos e problemas comuns; validado em Windows e Linux. |
| ODS obrigatório — p. 1 | ODS 4 e 12 e problema atendido descritos no README, coerentes com o planejamento. |

Não se exige interface gráfica avançada (p. 8). A seleção de usuário é a simulação acadêmica documentada; não foi acrescentado escopo de autenticação, pagamentos ou mensagens.

## Requisitos da primeira entrega

| Requisito | Implementação e demonstração |
| --- | --- |
| RF01 | Cadastro com nome, e-mail, curso e telefone; opções 1–3. |
| RF02 | Livros com título, autor, disciplina, conservação e oferta; CRUD nas opções 4–8. |
| RF03 | Pedido com data; opção 9; uma solicitação ativa por livro, protegida por transação e bloqueio. |
| RF04 | Opções 10–13; só ofertante original aceita/recusa; aceite torna o livro indisponível. |
| RF05 | Opção 14; empréstimo mantém dono e troca transfere ao solicitante; ambos tornam o livro disponível. |
| RF06 | Opções 10, 11 e 15 preservam participantes, título e tipo da época; opção 16 mostra ofertas atuais sem exigir negociação. |

## Integração e contribuição

- Cauã, conta `Caua-07`: [commit 2085ee3](https://github.com/Caua-07/aep-ESOFT4SNB/commit/2085ee376cbf933edb79542ec8998430062bf751), cadastros, CRUD, JDBC e testes; [PR #1](https://github.com/Caua-07/aep-ESOFT4SNB/pull/1).
- João Pedro, conta `jpparralopes`: [commit 5a9e01b](https://github.com/Caua-07/aep-ESOFT4SNB/commit/5a9e01b388ea23f6c537c4db9261856477cfc4be), negociações e testes; [commit a8f6247](https://github.com/Caua-07/aep-ESOFT4SNB/commit/a8f624718ded00cfd7c053c439caef6a321a3602), documentação; [PR #2](https://github.com/Caua-07/aep-ESOFT4SNB/pull/2).
- PR #2 incorporado à branch da primeira etapa; em seguida PR #1 incorporado a main. Foram usados merges que preservam os commits e seus autores.
- Base integrada validada: `44ca468057bd3fbf8200fd641fe6edfd5b504ccc`. As alterações finais posteriores são de documentação, sem mudança do código Java, SQL ou dependências.

## Validação executada

Cópia limpa da branch main obtida após os merges. Ambiente: Windows, Temurin JDK 17.0.14, Maven 3.9.9, MySQL 8.4.13, Connector/J 8.4.0. Ferramentas e credenciais locais ficam fora do repositório.

1. `mvn clean verify`, com as três variáveis de integração: **30 testes, zero falhas, zero erros e zero ignorados; BUILD SUCCESS**. São 8 testes de modelo, 5 de menu e 17 com MySQL real.
2. Criação de banco vazio de demonstração e execução de `database/schema.sql`: tabelas `usuarios`, `livros` e `solicitacoes` criadas com sucesso.
3. Sete processos independentes de `Main`: cadastro de dois usuários e dois livros, solicitação, aceite, devolução, troca, consulta persistente após reiniciar, novo pedido e recusa. Propriedade transferida e histórico finalizado confirmados.
4. Comando documentado `mvn compile exec:java`: menu, leitura dos usuários persistidos e encerramento normal; BUILD SUCCESS.
5. Revisão independente de regras, POO, transações e histórico; nenhuma falha funcional bloqueadora encontrada. Testes cobrem concorrência de pedidos e rollback após alteração real do livro.

Uma tentativa anterior não alcançou o servidor local de teste. Após reiniciar a instância no ambiente de execução ativo, a suíte passou; esse erro de ambiente não motivou alteração no código. A evidência válida é a execução completa com zero testes ignorados.

## Antes de enviar na disciplina

- Usar o [repositório principal](https://github.com/Caua-07/aep-ESOFT4SNB), branch main, que contém a versão completa; incluir código, `pom.xml`, `/database` e documentação caso seja solicitado arquivo compactado.
- Confirmar prazo e canal na disciplina: os PDFs fornecidos não os especificam. Não há afirmação de envio acadêmico realizado.
- Ensaiar o [roteiro de demonstração](roteiro-demonstracao.md) no computador da apresentação e explicar as classes, composição, finalização polimórfica e transações.
- Há divergência no peso de POO nas diretrizes: 0,3 na tabela e 0,4 na orientação final. Todos os pilares listados foram contemplados, independentemente do peso.
- O PDF original da primeira etapa conserva um marcador de link em sua seção 5.1. O README contém o endereço real. Se a disciplina pedir que esse PDF seja reenviado, substituir o marcador pelo link público antes de anexá-lo; o original histórico não foi modificado nesta finalização.

Os registros `validacao-etapa1.md` e `validacao-etapa2.md` descrevem seus respectivos momentos do desenvolvimento. Este documento registra o estado integrado para a segunda entrega.
