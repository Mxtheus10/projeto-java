CREATE DATABASE IF NOT EXISTS cesarprojeto;
USE cesarprojeto;

CREATE TABLE eventos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL UNIQUE,
    data DATE NOT NULL,
    descricao TEXT
);

CREATE TABLE pessoas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    idade INT NOT NULL
);

CREATE TABLE evento_pessoa (
    id INT AUTO_INCREMENT PRIMARY KEY,
    evento_id INT,
    pessoa_id INT,
    papel VARCHAR(100), -- Ex: "Palestrante", "Participante"
    FOREIGN KEY (evento_id) REFERENCES eventos(id) ON DELETE CASCADE,
    FOREIGN KEY (pessoa_id) REFERENCES pessoas(id) ON DELETE CASCADE
);
