package br.edu.sebo.model;

import java.time.LocalDateTime;

// Fotografia da negociação: título, tipo e participantes não mudam com o livro.
public record Solicitacao(int id, int livroId, int solicitanteId, int ofertanteOriginalId,
                          String tituloLivro, TipoOferta tipoOferta, StatusSolicitacao status,
                          LocalDateTime dataSolicitacao, LocalDateTime dataDecisao,
                          LocalDateTime dataFinalizacao) { }
