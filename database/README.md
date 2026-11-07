# Banco de dados

MySQL 8.4 com JDBC / Connector/J 8.4.0. Execute schema.sql no banco escolhido para criar usuarios, livros e solicitacoes. O script não apaga registros nem migra tabelas antigas diferentes.

Cadastros e CRUD já usam banco real. solicitacoes está preparada para a próxima etapa; seus vínculos já impedem edição com pedido ativo e exclusão com histórico. Ainda não há criação de pedidos no programa.

As próximas operações de solicitação devem bloquear a linha do livro antes de verificar/criar pedidos, no mesmo protocolo transacional do CRUD.

Veja [execução](../docs/execucao.md), [DER](../docs/diagramas.md) e [regras](../docs/regras-de-negocio.md). Não versionar credenciais.
