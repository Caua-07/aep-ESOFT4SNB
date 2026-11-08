# Banco de dados

MySQL 8.4 com JDBC / Connector/J 8.4.0. Execute schema.sql no banco escolhido para criar usuarios, livros e solicitacoes. O script não apaga registros nem migra tabelas antigas diferentes.

Cadastros, CRUD e negociações usam banco real. A tabela solicitacoes registra os pedidos, participantes e metadados históricos, estados e datas. Seus vínculos impedem edição com pedido ativo e exclusão com histórico.

As operações de solicitação bloqueiam a linha do livro antes de verificar/criar pedidos, no mesmo protocolo transacional do CRUD. Aceite e finalização atualizam livro e solicitação na mesma transação, com rollback em caso de falha. Na troca, a propriedade muda; no empréstimo, permanece com o ofertante.

Veja [execução](../docs/execucao.md), [DER](../docs/diagramas.md) e [regras](../docs/regras-de-negocio.md). Não versionar credenciais.
