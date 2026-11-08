# Preparação da segunda entrega

## Exigências

Segundo as diretrizes, a entrega deve conter código-fonte completo, aplicação funcional, pilares de POO, CRUD completo, conexão ativa com banco e contribuição de todos no repositório.

| Critério | Evidência a preparar | Peso |
| --- | --- | --- |
| Aderência | Fluxos correspondentes a RF01-RF06. | 0,2 |
| POO | Classe abstrata ou interface, herança, polimorfismo com @Override; código tipado e convenções de nomes. Demonstrar também a composição prevista. | 0,3 |
| CRUD | Cadastro, consulta, edição e exclusão de livros, com tratamento de vínculos. | 0,3 |
| Banco | Dados persistidos em MySQL e script de criação das tabelas em /database compatível com as classes. | 0,2 |

As diretrizes preveem nota zero automática quando o software não executa, ignora os pilares de POO ou diverge totalmente do planejamento. Listas em memória não substituem a persistência exigida.

## Checklist de aceite

- [x] RF01: cadastrar usuário com os campos previstos.
- [x] RF02: cadastrar livro de troca e livro de empréstimo.
- [x] CRUD: consultar, editar e excluir um livro sem vínculos impeditivos.
- [x] RF03: solicitar livro disponível e impedir outra solicitação ativa.
- [x] RF04: aceitar ou recusar apenas como proprietário responsável.
- [x] RF04: tornar o livro indisponível após o aceite.
- [x] RF05: registrar devolução de empréstimo.
- [x] RF05: confirmar troca transferindo o exemplar ao solicitante e retornando a DISPONIVEL.
- [x] RF06: consultar histórico de livros ofertados e solicitados.
- [x] Demonstrar classe abstrata/interface, herança, composição e @Override em uso.
- [x] Demonstrar que fechar e reabrir o programa preserva os dados.
- [x] Testar entradas inválidas e transições de estado indevidas.
- [x] Testar que solicitações simultâneas não geram duas negociações ativas.
- [x] Verificar compatibilidade entre SQL, DER e classes Java.
- [x] Executar seguindo o README e o guia de execução, em cópia limpa de main.
- [x] Conferir contribuições reais de Cauã e João Pedro e integrar ambas em main.

## Instruções de execução disponíveis

O [guia de execução](execucao.md) documenta versões, dependências, criação do banco, configuração local de credenciais, compilação, testes e início da aplicação. A [conferência final](conferencia-final.md) registra a validação executada. Credenciais locais não fazem parte do repositório.
