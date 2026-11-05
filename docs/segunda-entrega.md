# Preparação da segunda entrega

## Exigências

Segundo as diretrizes, a entrega deve conter código-fonte completo, aplicação funcional, pilares de POO, CRUD completo, conexão ativa com banco e contribuição de todos no repositório.

| Critério | Evidência a preparar | Peso |
| --- | --- | --- |
| Aderência | Fluxos correspondentes a RF01–RF06. | 0,2 |
| POO | Classe abstrata ou interface, herança, polimorfismo com @Override; código tipado e convenções de nomes. Demonstrar também a composição prevista. | 0,3 |
| CRUD | Cadastro, consulta, edição e exclusão de livros, com tratamento de vínculos. | 0,3 |
| Banco | Dados persistidos em MySQL e script de criação das tabelas em /database compatível com as classes. | 0,2 |

As diretrizes preveem nota zero automática quando o software não executa, ignora os pilares de POO ou diverge totalmente do planejamento. Listas em memória não substituem a persistência exigida.

## Ordem de trabalho para retomar depois

1. Fechar as [decisões de negócio](decisoes-pendentes.md).
2. Definir versões e configuração do ambiente Java/MySQL.
3. Criar as classes de domínio e o script SQL.
4. Implementar cadastro de usuários e CRUD de livros.
5. Implementar solicitações, aceite/recusa e bloqueio de negociação simultânea.
6. Implementar finalização de empréstimo/troca e histórico.
7. Testar os fluxos e a persistência.
8. Documentar instalação e execução e validar a partir de uma cópia limpa do repositório.

Os responsáveis e períodos do planejamento original estão no [README](../README.md). Um novo cronograma deve refletir datas reais combinadas pelo grupo, sem marcar as etapas anteriores como executadas.

## Checklist de aceite

- [ ] RF01: cadastrar usuário com os campos previstos.
- [ ] RF02: cadastrar livro de troca e livro de empréstimo.
- [ ] CRUD: consultar, editar e excluir um livro sem vínculos impeditivos.
- [ ] RF03: solicitar livro disponível e impedir outra solicitação ativa.
- [ ] RF04: aceitar ou recusar apenas como proprietário responsável.
- [ ] RF04: tornar o livro indisponível após o aceite.
- [ ] RF05: registrar devolução de empréstimo.
- [ ] RF05: confirmar troca conforme a regra final acordada.
- [ ] RF06: consultar histórico de livros ofertados e solicitados.
- [ ] Demonstrar classe abstrata/interface, herança, composição e @Override em uso.
- [ ] Demonstrar que fechar e reabrir o programa preserva os dados.
- [ ] Testar entradas inválidas e transições de estado indevidas.
- [ ] Testar que solicitações simultâneas não geram duas negociações ativas.
- [ ] Verificar compatibilidade entre SQL, DER e classes Java.
- [ ] Executar seguindo apenas as instruções do README.
- [ ] Conferir contribuições reais de Cauã e João Pedro.
- [ ] Conferir prazo e forma de envio na disciplina.

## Roteiro sugerido de demonstração

Cadastrar dois usuários; cadastrar um livro; consultar e editar seus dados; solicitar com o segundo usuário; tentar outro pedido; aceitar como proprietário; concluir a negociação; consultar o histórico; reiniciar e verificar os dados. Demonstrar a recusa e a exclusão com registros separados, além dos dois tipos de negociação.

## Instruções de execução a completar junto com o código

Documentar versões, dependências, criação do banco, configuração local de credenciais, comandos de compilação, testes e início da aplicação. Não publicar senhas. Não declarar testes como aprovados antes de executá-los.
