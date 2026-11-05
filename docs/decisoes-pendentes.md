# Decisões pendentes antes da implementação

O escopo original permanece no README. As propostas abaixo precisam ser discutidas pelo grupo; alterações no comportamento declarado devem ser alinhadas com o professor.

| Ponto | O que falta definir | Proposta para discussão |
| --- | --- | --- |
| RF03: solicitação ativa | Se o estado pendente já bloqueia outro pedido. | Tratar pendente e aceita como ativas; recusa ou finalização libera novo pedido. |
| RF05: conclusão da troca | Quem será o proprietário após a troca e a que oferta se refere o retorno a disponível. | Transferir a propriedade ao destinatário e explicitar como a oferta volta a ficar disponível. Confirmar a interpretação com o professor antes de implementar. |
| Contrapartida da troca | Se outro livro precisa ser registrado como parte da troca. | Manter a negociação da contrapartida fora do sistema, se aceito pelo professor; registrar a confirmação da troca do livro ofertado. |
| Histórico | Como diferenciar negociação aceita em andamento de negociação concluída. | Acrescentar estado finalizada e data de finalização, mantendo o histórico dos participantes da época. |
| CRUD e exclusão | O que ocorre com livros que possuem solicitações ou histórico. | Impedir exclusão física quando houver vínculos; explicar a restrição ao usuário. |
| Composição 1:N | Compatibilizar a relação usuário-livros com transferência de propriedade e preservação do histórico. | Formalizar o ciclo de vida da oferta e os efeitos de eventual exclusão de usuário; não adotar exclusões em cascata sem definir essa regra. |
| E-mail institucional | Quais domínios serão aceitos. | Confirmar os domínios utilizados pelos estudantes antes de restringir o cadastro. |

## Pendências de entrega

- Confirmar prazo oficial e canal de envio da segunda entrega.
- Confirmar com o professor o peso de POO: a tabela indica 0,3, enquanto as orientações finais mencionam 0,4.
- Definir a versão do JDK e a ferramenta de build na implementação.
- Registrar as decisões finais aqui e atualizar os diagramas e requisitos afetados.

Nenhuma proposta desta página deve ser apresentada como funcionalidade já implementada.
