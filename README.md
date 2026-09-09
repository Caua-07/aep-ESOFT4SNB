# Sebo Colaborativo

## Sistema de Troca e Empréstimo de Livros Acadêmicos entre Estudantes

Projeto desenvolvido como parte da *Atividade de Estudo Programada (AEP)* do 4º semestre de 2026.2 dos cursos de *Engenharia de Software* e *Análise e Desenvolvimento de Sistemas* da UniCesumar.

### Integrantes

* Cauã Olivastro Braga
* João Pedro Parra Lopes

*Turma:* ESOFT4S-NB

---

## Sobre o Projeto

O *Sebo Colaborativo* é uma aplicação desenvolvida em Java para facilitar a troca e o empréstimo de livros acadêmicos entre estudantes.

A proposta surgiu a partir da identificação de uma situação comum no ambiente acadêmico: estudantes podem adquirir livros e apostilas de alto custo para utilização durante um semestre e, posteriormente, deixar esses materiais sem uso, enquanto outros estudantes precisam dos mesmos conteúdos e podem ter dificuldade para adquiri-los.

O sistema busca criar um meio organizado para conectar estudantes que possuem livros disponíveis com estudantes que necessitam desses materiais, incentivando o reaproveitamento e reduzindo gastos com novas aquisições.

---

## Objetivos de Desenvolvimento Sustentável (ODS)

O projeto está alinhado aos seguintes Objetivos de Desenvolvimento Sustentável da ONU:

### ODS 4 — Educação de Qualidade

O sistema contribui para ampliar o acesso a materiais didáticos por meio do compartilhamento entre estudantes, reduzindo a barreira financeira relacionada à aquisição de livros e outros materiais acadêmicos.

### ODS 12 — Consumo e Produção Responsáveis

O sistema incentiva o reaproveitamento e a circulação de materiais acadêmicos já existentes, reduzindo o desperdício e a necessidade de novas compras e impressões.

---

## Requisitos do Sistema

Os requisitos abaixo representam o escopo funcional definido para o projeto.

1. O sistema deve permitir o cadastro de usuários, com nome completo, e-mail institucional, curso e telefone.

2. O sistema deve permitir o cadastro de livros, com título, autor, disciplina relacionada, estado de conservação e tipo de oferta, sendo troca ou empréstimo.

3. O sistema deve permitir que um usuário solicite um livro disponível, registrando a data da solicitação.

4. O sistema deve permitir que o proprietário do livro aceite ou recuse uma solicitação recebida.

5. O sistema deve permitir apenas uma solicitação ativa por livro disponível.

6. O sistema deve permitir o registro da devolução, no caso de empréstimos, ou da confirmação da troca, no caso de trocas, atualizando o status do livro.

7. O sistema deve permitir a consulta do histórico de livros ofertados e solicitados por um usuário.

---

## Regra de Negócio

Um livro só pode possuir uma solicitação ativa por vez.

Quando uma solicitação é aceita pelo ofertante, o status do livro deve ser alterado de *"disponível"* para *"indisponível"*.

O livro retorna ao status *"disponível"* após a confirmação da devolução, no caso de empréstimo, ou após a confirmação da efetivação da troca.

---

## Tecnologias

### Linguagem

* Java

### Banco de Dados

* MySQL

### Conexão com o Banco

* JDBC / MySQL Connector/J

### Arquitetura

O projeto utiliza uma arquitetura em camadas:

text
Model → DAO → Service → Controller/Main


As camadas possuem as seguintes responsabilidades:

* *Model:* representação das entidades e seus comportamentos.
* *DAO:* acesso e persistência dos dados no banco.
* *Service:* aplicação das regras de negócio.
* *Controller/Main:* controle do fluxo da aplicação e interação com as funcionalidades do sistema.

---

## Programação Orientada a Objetos

O projeto utilizará os principais conceitos de Programação Orientada a Objetos exigidos pela AEP, incluindo:

* Classe abstrata;
* Herança;
* Encapsulamento;
* Polimorfismo;
* Sobrescrita de métodos com @Override;
* Composição.

A classe abstrata Livro será utilizada como base para as classes específicas de negociação:

text
Livro
├── LivroTroca
└── LivroEmprestimo


O método finalizarNegociacao() será sobrescrito pelas subclasses para representar comportamentos específicos de cada tipo de negociação.

---

## Estrutura do Repositório

A estrutura inicial do projeto está organizada da seguinte forma:

text
/
├── src/
│   └── ...
│
├── docs/
│   └── ...
│
├── database/
│   └── ...
│
└── README.md


### Diretórios

* /src — código-fonte da aplicação.
* /docs — documentação e diagramas do projeto.
* /database — scripts relacionados ao banco de dados.
* README.md — documentação principal do projeto.

---

## Diagramas

A documentação do projeto contém:

* Diagrama de Classes;
* Diagrama Entidade-Relacionamento (DER).

O Diagrama de Classes contempla:

* Herança entre Livro, LivroTroca e LivroEmprestimo;
* Polimorfismo por meio da sobrescrita de finalizarNegociacao();
* Composição com multiplicidade 1:N.

O DER representa as entidades principais:

* USUARIO;
* LIVRO;
* SOLICITACAO.

---

## Cronograma de Execução

O cronograma abaixo corresponde ao planejamento apresentado na primeira entrega da AEP.

| Data                       | Atividade                                            | Responsável |
| -------------------------- | ---------------------------------------------------- | ----------- |
| 07/09/2026                 | Revisão dos requisitos e concepção do projeto        | Todos       |
| 07/09/2026 – 08/09/2026    | Revisão do Diagrama de Classes e DER                 | Todos       |
| 08/09/2026                 | Organização do repositório e estrutura de pastas     | Cauã        |
| 08/09/2026 – 09/09/2026    | Redação e revisão do PDF da 1ª entrega               | João Pedro  |
| 09/09/2026                 | Revisão final da documentação                        | Todos       |
| 10/09/2026                 | Entrega da 1ª etapa da AEP                           | Todos       |
| Após 10/09/2026            | Implementação das classes — camada Model             | Todos       |
| Após 10/09/2026            | Implementação do CRUD e conexão com o banco de dados | Todos       |
| Após 10/09/2026            | Testes e ajustes finais                              | Todos       |
| Data oficial da 2ª entrega | Entrega final — 2º Bimestre                          | Todos       |

---

## Segunda Entrega

Na segunda etapa da AEP será desenvolvida a versão funcional e integrada do sistema, contemplando:

* Implementação das classes;
* Aplicação dos conceitos de Programação Orientada a Objetos;
* CRUD completo da entidade principal;
* Conexão ativa com o banco de dados;
* Persistência dos dados;
* Testes e ajustes finais.

A implementação deverá permanecer de acordo com os requisitos e o planejamento definidos na primeira entrega.

---

## Status do Projeto

*1ª Entrega — Planejamento, Escopo e Estruturação*

* [x] Definição do problema
* [x] Definição dos stakeholders
* [x] Definição da regra de negócio
* [x] Definição dos requisitos
* [x] Alinhamento com os ODS
* [x] Escolha das tecnologias
* [x] Definição da arquitetura
* [x] Diagrama de Classes
* [x] DER
* [x] Estrutura inicial do repositório
* [ ] Implementação completa do sistema

---

## Instituição

*UniCesumar — Centro Universitário de Maringá*

*Atividade de Estudo Programada — AEP 4º Semestre (2026.2)*
