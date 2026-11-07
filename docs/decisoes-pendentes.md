# Registro de decisões e pendências administrativas

As lacunas de negócio foram resolvidas em 04/11/2026, por autorização do grupo para interpretar as melhores escolhas mantendo a primeira entrega. A especificação vigente está em [regras de negócio](regras-de-negocio.md).

| Tema | Decisão |
| --- | --- |
| Solicitação ativa | PENDENTE e ACEITA bloqueiam novos pedidos. |
| Disponibilidade durante pendência | Livro continua DISPONIVEL, com indicação de pedido pendente e bloqueio de nova solicitação. |
| Finalização do empréstimo | Proprietário confirma devolução; mantém propriedade e libera o livro. |
| Finalização da troca | Proprietário original confirma; transfere ao solicitante e torna DISPONIVEL, preservando RF05. |
| Contrapartida da troca | Combinada fora do sistema; nenhum segundo registro de livro é alterado automaticamente. |
| Histórico | Preservar participantes, título e tipo da época; acrescentar FINALIZADA e datas de decisão/finalização. |
| Exclusão | Livro apenas sem pedidos vinculados; não excluir usuários ou solicitações nesta entrega. |
| Composição | Livro pertence à coleção de um proprietário por vez; transferência atômica e sem exclusão em cascata. |
| E-mail institucional | Formato e unicidade validados; vínculo institucional declarado, sem domínio fixo ou confirmação por e-mail. |
| Identificação do usuário | Usuário atuante selecionado para demonstração; não ampliar o escopo com autenticação. |

## Pendências administrativas e técnicas para a próxima etapa

- Confirmar prazo oficial e canal de envio com a disciplina.
- Confirmar o peso de POO: tabela de avaliação indica 0,3; orientação final menciona 0,4.
- Ambiente definido na etapa 1: Java 17, Maven 3.9+, MySQL 8.4, Connector/J 8.4.0; consultar o guia de execução.
- Implementar e executar os critérios de aceite; documentar os comandos efetivamente verificados.

Não há decisão de negócio bloqueando o início do código. As definições acima não devem ser apresentadas como funcionalidades já implementadas.
