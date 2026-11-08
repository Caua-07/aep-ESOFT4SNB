# Roteiro de demonstração - Sebo Colaborativo

Prepare o banco da aplicação e execute `mvn compile exec:java`, conforme execucao.md. Use dados fictícios e anote os IDs retornados: eles não precisam começar em 1.

1. Opção 1: cadastre Ana e Bruno. Opção 3: selecione Ana.
2. Opção 4: cadastre dois exemplares, um EMPRESTIMO e outro TROCA. Opções 5/6: consulte. Edite um exemplar na opção 7 antes de solicitar. Para demonstrar exclusão, cadastre um terceiro exemplar sem pedidos e exclua na opção 8, confirmando SIM.
3. Selecione Bruno (3). Solicite os dois livros (9). Consulte feitas (11). Tente solicitar novamente: o segundo pedido deve ser bloqueado, embora o status do livro permaneça DISPONIVEL durante a pendência.
4. Selecione Ana (3). Consulte recebidas (10). Aceite cada pedido (12). Consulte os livros: INDISPONIVEL. Tente editar: bloqueado.
5. Após simular a devolução física, finalize o empréstimo (14, ID do pedido, SIM). O exemplar fica DISPONIVEL e continua com Ana.
6. Após simular a troca física combinada fora do sistema, finalize a troca (14, ID do pedido, SIM). O exemplar fica DISPONIVEL e passa a Bruno. Não há segundo livro obrigatório.
7. Consulte os livros de Ana (16); selecione Bruno e consulte os dele (16). O livro trocado aparece apenas com Bruno. A opção 15 mostra o histórico de cada participante, com o ofertante original preservado.
8. Saia (0), execute novamente e confira livros e histórico. As informações devem continuar no MySQL.
9. Como Ana, solicite o livro agora pertencente a Bruno. Como Bruno, recuse (13). O pedido fica RECUSADA e novos pedidos são liberados.
10. Edite o título/tipo depois do encerramento e confira que o histórico anterior não mudou. Tente finalizar novamente: recusado sem mudar datas ou dono. Tente excluir um exemplar com histórico: bloqueado.
