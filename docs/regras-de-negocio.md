# Regras de negócio consolidadas

## Base e alcance

Documento consolidado em 04/11/2026 e aplicado na implementação. Preserva RF01–RF06, Java, MySQL, JDBC e a arquitetura da primeira entrega. As definições que o PDF deixou abertas são decisões de detalhamento autorizadas pelo grupo, não exigências atribuídas ao professor. A verificação está registrada na [conferência final](conferencia-final.md).

Este documento define as regras; os relatórios de validação registram as evidências de implementação e execução.

## 1. Usuários — RF01

**RN01.** O cadastro exige nome completo, e-mail institucional, curso e telefone. Todos devem ser preenchidos, sem aceitar apenas espaços. E-mail deve ter formato válido e ser único, desconsiderando maiúsculas e espaços nas extremidades. Telefone é texto para preservar prefixos e zeros.

**RN02.** O e-mail será informado como institucional pelo estudante. Não haverá lista fixa de domínios nem verificação por envio de mensagem, pois isso não foi previsto. A aplicação validará formato e unicidade; não alegará ter comprovado o vínculo institucional.

**RN03.** Cada operação terá um usuário atuante identificado. O serviço deve conferir sua relação com o livro ou pedido. Login, senha e recuperação de conta não fazem parte do escopo original; uma seleção de usuário no terminal é suficiente para a demonstração acadêmica, mas não equivale a autenticação para uso público.

## 2. Livros e CRUD — RF02 e exigência da segunda entrega

**RN04.** Cada cadastro representa um exemplar, vinculado a exatamente um proprietário atual. São obrigatórios título, autor, disciplina relacionada, estado de conservação e tipo de oferta: TROCA ou EMPRESTIMO. Conservação será uma descrição não vazia. O exemplar começa DISPONIVEL.

**RN05.** Um usuário pode ofertar vários exemplares. Dois registros com o mesmo título são permitidos: podem representar exemplares físicos diferentes, com identificadores distintos.

**RN06.** O CRUD principal será de livros:
- Criar: vincular o exemplar ao usuário atuante.
- Consultar: listar livros e consultar detalhes, exibindo status e se há pedido ativo.
- Atualizar: somente o proprietário atual pode editar metadados, conservação e tipo de oferta, quando não houver solicitação ativa.
- Excluir: somente o proprietário atual, sem qualquer solicitação vinculada, inclusive encerrada. Havendo histórico, recusar a exclusão e explicar o motivo.

Propriedade e status não são campos de edição livre; mudam pelos fluxos de negócio. Não haverá exclusão de usuários nem exclusão de solicitações nesta entrega. Isso preserva os vínculos sem ampliar o CRUD exigido para entidades adicionais.

## 3. Solicitações e disponibilidade — RF03

**RN07.** Uma solicitação registra livro, solicitante, proprietário no momento do pedido e data/hora de criação. O solicitante deve existir e não pode solicitar livro próprio.

**RN08.** Pedido ativo é aquele com status PENDENTE ou ACEITA. Cada livro pode ter no máximo um pedido ativo. RECUSADA e FINALIZADA são estados encerrados e não bloqueiam pedidos futuros.

**RN09.** Um pedido só pode ser criado quando o livro estiver DISPONIVEL e não houver pedido ativo. O novo pedido começa PENDENTE. O livro continua com status DISPONIVEL durante a pendência, preservando RF04, que determina a mudança para INDISPONIVEL no aceite. A interface deve mostrar "solicitação pendente — novos pedidos bloqueados", evitando sugerir que ele pode ser solicitado.

Assim, "pode ser solicitado" exige simultaneamente status DISPONIVEL e ausência de solicitação ativa.

**RN10.** Pedidos simultâneos devem ser controlados atomicamente no banco: verificar e criar como uma única operação protegida, de modo que somente um seja aceito. Uma checagem isolada na interface não atende à regra.

## 4. Aceite e recusa — RF04

**RN11.** Somente o proprietário registrado na solicitação pode aceitar ou recusar um pedido PENDENTE. Durante a negociação, ele continua sendo o proprietário do livro.

**RN12.** Aceitar muda a solicitação para ACEITA e o livro para INDISPONIVEL, na mesma transação. Ambas as decisões registram data/hora de decisão. Recusar muda a solicitação para RECUSADA; o livro permanece DISPONIVEL e pode receber outro pedido.

Não é permitido aceitar ou recusar novamente um pedido já decidido. Recusar após o aceite não é uma operação prevista no escopo.

## 5. Finalização — RF05

**RN13. Empréstimo:** após receber o livro devolvido, o proprietário original registra a devolução. A solicitação passa de ACEITA a FINALIZADA, recebe data/hora de finalização e o livro volta a DISPONIVEL. A propriedade permanece com quem emprestou.

**RN14. Troca:** após a efetivação da troca física, o proprietário original confirma a conclusão. A solicitação passa de ACEITA a FINALIZADA, recebe data/hora de finalização, a propriedade do exemplar passa ao solicitante e o livro volta automaticamente a DISPONIVEL sob o novo proprietário. O tipo de oferta permanece TROCA; o novo proprietário poderá alterá-lo quando não houver pedido ativo.

A disponibilidade automática após a troca preserva expressamente o RF05. O retorno não acontece em nome do antigo dono.

**RN15.** A contrapartida da troca é combinada diretamente entre os estudantes. Não haverá segundo livro obrigatório, pareamento de ofertas nem registro de pagamento. O sistema acompanha apenas o exemplar associado à solicitação. Se a contrapartida também existir no catálogo, seu registro não será alterado automaticamente: isso exigiria um fluxo de troca entre dois registros que não foi previsto.

**RN16.** Finalizar exige solicitação ACEITA e atuação do proprietário original, que confirma ter recebido a devolução ou concluído a troca. Não será exigida uma segunda confirmação digital do solicitante. Repetir a finalização será rejeitado sem alterar novamente o proprietário, a data ou o histórico.

A mudança de propriedade, o status do livro e o encerramento do pedido devem ser confirmados juntos; em caso de erro, nenhuma dessas mudanças pode permanecer parcialmente gravada.

## 6. Histórico — RF06

**RN17.** O histórico de um usuário reúne todas as solicitações em que ele foi solicitante ou proprietário à época, incluindo pendentes, aceitas, recusadas e finalizadas. Ofertas cadastradas sem pedido aparecem na consulta de livros do proprietário; não se inventa uma negociação para elas.

**RN18.** Cada solicitação preservará os identificadores dos participantes da época, o título e o tipo de oferta no momento do pedido, os estados e as datas pertinentes. Atualizar o livro ou transferi-lo não deve reatribuir o histórico antigo ao novo proprietário. Dados de contato dos usuários continuam vindo do cadastro atual, sem necessidade de copiá-los.

## 7. POO, relações e persistência

**RN19.** Livro será abstrato. LivroTroca e LivroEmprestimo sobrescreverão finalizarNegociacao() com comportamentos distintos: transferência de propriedade na troca e manutenção no empréstimo. O serviço coordenará validações e persistência transacional.

**RN20.** A composição 1:N planejada será representada pela coleção de livros do proprietário atual: um livro pertence a uma única coleção por vez e não pode existir sem proprietário. Na troca, sai da coleção do proprietário antigo e entra na do novo, atomicamente. As relações com solicitações são associações históricas, não composição. Não haverá remoção de usuários nem exclusão em cascata de seus livros e históricos.

**RN21.** MySQL será a fonte persistente dos dados. Classes, DER e SQL deverão concordar em campos, tipos e relacionamentos. Listas usadas para carregar objetos não substituem o banco.

## 8. Transições permitidas

| Ação | Solicitação antes → depois | Livro antes → depois | Proprietário depois |
| --- | --- | --- | --- |
| Solicitar | inexistente → PENDENTE | DISPONIVEL → DISPONIVEL, bloqueado para novos pedidos | Original |
| Recusar | PENDENTE → RECUSADA | DISPONIVEL → DISPONIVEL, liberado | Original |
| Aceitar | PENDENTE → ACEITA | DISPONIVEL → INDISPONIVEL | Original |
| Registrar devolução | ACEITA → FINALIZADA | INDISPONIVEL → DISPONIVEL | Original |
| Confirmar troca | ACEITA → FINALIZADA | INDISPONIVEL → DISPONIVEL | Solicitante |

Nenhuma outra transição faz parte da entrega. Cancelamento, expiração automática, prazos de devolução, multas, chat, pagamentos e notificações ficam fora do escopo.

## 9. Critérios de aceite da implementação

| Cenário | Resultado esperado |
| --- | --- |
| Cadastro com campo obrigatório vazio ou e-mail duplicado | Recusado sem gravação parcial. |
| Cadastro de dois exemplares com mesmo título | Permitido, com identificadores diferentes. |
| Usuário solicita exemplar próprio | Recusado. |
| Dois usuários solicitam o mesmo exemplar simultaneamente | Apenas um pedido PENDENTE é criado. |
| Livro DISPONIVEL com pedido PENDENTE | Continua no catálogo, mas novos pedidos são bloqueados. |
| Proprietário recusa pedido PENDENTE | Pedido encerrado; outro estudante pode solicitar. |
| Outro usuário tenta aceitar, recusar ou finalizar | Operação recusada, sem alterar dados. |
| Proprietário aceita pedido | Pedido ACEITA e livro INDISPONIVEL gravados juntos. |
| Devolução confirmada | Pedido FINALIZADA, data gravada, livro DISPONIVEL e mesmo proprietário. |
| Troca confirmada | Pedido FINALIZADA, livro DISPONIVEL e propriedade transferida ao solicitante. |
| Finalização repetida | Recusada; dados anteriores preservados. |
| Livro com pedido ativo é editado | Edição recusada. |
| Livro sem pedidos é excluído pelo proprietário | Exclusão permitida. |
| Livro com qualquer solicitação é excluído | Exclusão recusada para preservar o histórico. |
| Título/tipo do livro é editado após negociação encerrada | Histórico preserva título/tipo da época. |
| Livro é transferido mais de uma vez | Cada pedido preserva seu ofertante e solicitante originais. |
| Falha ao salvar parte da negociação | Transação revertida; sem estado intermediário persistido. |
| Programa é reiniciado | Cadastros, estados, propriedade e histórico continuam disponíveis. |

Esta tabela especifica o comportamento esperado. Os resultados executados estão em [validação da etapa 2](validacao-etapa2.md) e [conferência final](conferencia-final.md).
