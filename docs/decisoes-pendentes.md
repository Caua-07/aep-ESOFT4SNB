# Decisões do projeto

Decisões usadas na implementação. As regras completas estão em [regras de negócio](regras-de-negocio.md).

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
