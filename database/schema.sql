-- Execute dentro do banco escolhido (sebo_colaborativo ou sebo_test).
-- Este script cria a estrutura; não remove registros existentes.
CREATE TABLE IF NOT EXISTS usuarios (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome_completo VARCHAR(150) NOT NULL,
    email_institucional VARCHAR(150) NOT NULL UNIQUE,
    curso VARCHAR(100) NOT NULL,
    telefone VARCHAR(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_ci;

CREATE TABLE IF NOT EXISTS livros (
    id INT PRIMARY KEY AUTO_INCREMENT,
    usuario_id INT NOT NULL,
    titulo VARCHAR(150) NOT NULL,
    autor VARCHAR(150) NOT NULL,
    disciplina VARCHAR(100) NOT NULL,
    estado_conservacao VARCHAR(50) NOT NULL,
    tipo_oferta ENUM('TROCA', 'EMPRESTIMO') NOT NULL,
    status ENUM('DISPONIVEL', 'INDISPONIVEL') NOT NULL DEFAULT 'DISPONIVEL',
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Histórico independente dos metadados atuais do livro. Datas gravadas em UTC pela aplicação.
CREATE TABLE IF NOT EXISTS solicitacoes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    livro_id INT NOT NULL,
    usuario_id INT NOT NULL,
    ofertante_original_id INT NOT NULL,
    titulo_livro_na_solicitacao VARCHAR(150) NOT NULL,
    tipo_oferta_na_solicitacao ENUM('TROCA', 'EMPRESTIMO') NOT NULL,
    status ENUM('PENDENTE', 'ACEITA', 'RECUSADA', 'FINALIZADA') NOT NULL DEFAULT 'PENDENTE',
    data_solicitacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_decisao DATETIME NULL,
    data_finalizacao DATETIME NULL,
    FOREIGN KEY (livro_id) REFERENCES livros(id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (ofertante_original_id) REFERENCES usuarios(id),
    INDEX idx_solicitacao_livro_status (livro_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
