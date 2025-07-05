-- Criação do banco de dados
CREATE DATABASE IF NOT EXISTS cesarprojeto;
USE cesarprojeto;

-- Tabela de eventos
CREATE TABLE IF NOT EXISTS eventos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    date DATE,
    image_path TEXT,
    external_link TEXT
);

-- Tabela de pessoas (caso queira relacionar pessoas a eventos)(nao utilizado)
CREATE TABLE IF NOT EXISTS pessoas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    idade INT
);

-- Tabela de associação evento-pessoa (para relacionamentos muitos-para-muitos)(nao utilizado)
CREATE TABLE IF NOT EXISTS evento_pessoa (
    evento_id INT,
    pessoa_id INT,
    FOREIGN KEY (evento_id) REFERENCES eventos(id) ON DELETE CASCADE,
    FOREIGN KEY (pessoa_id) REFERENCES pessoas(id) ON DELETE CASCADE
);

-- Tabela de usuários do sistema (opcional, para controle de login e logs)(nao utilizado)
CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    login VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(100) NOT NULL,
    tipo ENUM('admin', 'comum') DEFAULT 'comum'
);

-- Tabela de logs para auditoria de ações (opcional)
CREATE TABLE IF NOT EXISTS logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT,
    acao TEXT,
    data_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);
