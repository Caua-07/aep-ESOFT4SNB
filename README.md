# Sebo Colaborativo

Sistema de troca e empréstimo de livros acadêmicos entre estudantes.

Projeto da Atividade de Estudo Programada (AEP), 4º semestre de 2026.2, UniCesumar — Maringá/PR.

**Integrantes:** Cauã Olivastro Braga e João Pedro Parra Lopes.  
**Turma:** ESOFT4S-NB.  
**Repositório:** https://github.com/Caua-07/aep-ESOFT4SNB

## Problema e proposta

Estudantes adquirem livros e apostilas de alto custo para um semestre e depois deixam esses materiais sem uso, enquanto colegas precisam dos mesmos conteúdos. A proposta é organizar o contato entre quem oferece e quem procura livros, com controle de disponibilidade, solicitações e histórico.

A descoberta descrita na primeira entrega destaca a dispersão das ofertas em grupos de mensagens e a necessidade de impedir negociações simultâneas para o mesmo livro.

## Alinhamento aos ODS

- **ODS 4 — Educação de Qualidade:** ampliar o acesso a materiais didáticos e reduzir a barreira financeira.
- **ODS 12 — Consumo e Produção Responsáveis:** incentivar a circulação e o reaproveitamento dos materiais existentes.

## Requisitos da primeira entrega

A numeração e o conteúdo abaixo espelham o PDF da primeira entrega.

| ID | Requisito |
| --- | --- |
| RF01 | O sistema deve permitir o cadastro de usuários, com nome completo, e-mail institucional, curso e telefone. |
| RF02 | O sistema deve permitir o cadastro de livros, com título, autor, disciplina relacionada, estado de conservação e tipo de oferta, sendo troca ou empréstimo. |
| RF03 | O sistema deve permitir que um usuário solicite um livro disponível, registrando a data da solicitação, sendo permitida apenas uma solicitação ativa por livro disponível. |
| RF04 | O sistema deve permitir que o proprietário do livro aceite ou recuse uma solicitação recebida, atualizando o status do livro de “disponível” para “indisponível” quando aceita. |
| RF05 | O sistema deve permitir o registro da devolução, no caso de empréstimos, ou da confirmação da troca, no caso de trocas, retornando o status do livro para “disponível”. |
| RF06 | O sistema deve permitir a consulta do histórico de livros ofertados e solicitados por um usuário. |

## Regras de negociação

- Pedidos com estado `PENDENTE` ou `ACEITA` bloqueiam novas solicitações.
- No empréstimo, a devolução mantém o proprietário e libera o exemplar.
- Na troca, a confirmação transfere o exemplar ao solicitante e o torna disponível, como prevê RF05.
- O histórico preserva os participantes e os dados da negociação da época.
- A exclusão de livros com solicitações vinculadas é impedida.

## Tecnologias e arquitetura

- **Java 17:** tipagem e recursos de orientação a objetos para representar o domínio.
- **MySQL 8.4:** persistência relacional e integridade entre usuários, livros e solicitações.
- **JDBC / MySQL Connector/J:** comunicação entre Java e MySQL.
- **Camadas:** Model, DAO, Service e Controller/Main.

| Camada | Responsabilidade |
| --- | --- |
| Model | Entidades, encapsulamento e comportamentos do domínio. |
| DAO | Consultas e persistência no banco. |
| Service | Regras de negócio e coordenação das operações. |
| Controller/Main | Entrada do usuário e fluxo da aplicação. |

O código utiliza a classe abstrata `Livro`, as subclasses `LivroTroca` e `LivroEmprestimo`, sobrescrita de `finalizarNegociacao(Usuario solicitante)` com `@Override` e composição 1:N entre usuário e livros.

Consulte os [diagramas de classes e banco](docs/diagramas.md).

## Cronograma da primeira entrega

| Sprint / período de 2026 | Épico | User story | Responsável |
| --- | --- | --- | --- |
| Sprint 1 — 11/09 a 25/09 | Cadastros e Perfis | COMO UM estudante EU QUERO cadastrar meus dados e os livros que possuo PARA QUE outros estudantes possam encontrá-los para troca ou empréstimo. | Cauã |
| Sprint 2 — 26/09 a 10/10 | Motor de Solicitações | COMO UM estudante EU QUERO solicitar um livro disponível PARA QUE eu possa obtê-lo por troca ou empréstimo. | João Pedro |
| Sprint 3 — 11/10 a 25/10 | Aceite e Regras de Negócio | COMO UM proprietário de um livro EU QUERO aceitar ou recusar solicitações recebidas PARA QUE apenas uma negociação fique ativa por vez. | Cauã e João Pedro |
| Sprint 4 — 26/10 até a 2ª entrega | Finalização e Histórico | COMO UM estudante EU QUERO confirmar a devolução ou a troca e consultar meu histórico de negociações PARA QUE eu acompanhe os livros que ofertei e solicitei. | Todos |

## Execução

Prepare o MySQL e as variáveis de ambiente conforme o [guia de execução](docs/execucao.md). Na pasta do `pom.xml`, execute `mvn compile exec:java`.
