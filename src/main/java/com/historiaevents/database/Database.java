package com.historiaevents.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static Database instance;
    private Connection connection;
    private static final String URL = "jdbc:mysql://localhost:3306/cesarprojeto?useSSL=false";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // Construtor privado para evitar múltiplas instâncias
    private Database() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Carrega o driver do MySQL
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Conexão com o banco de dados MySQL estabelecida.");
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("❌ Erro ao conectar ao banco de dados!", e);
        }
    }

    // Método estático para obter a instância única do Singleton
    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    // Retorna a conexão ativa, verificando se está fechada
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("🔄 Conexão reaberta.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("❌ Erro ao reabrir conexão!", e);
        }
        return connection;
    }

    // Método para criar as tabelas no banco de dados
    public void createTables() {
        String createEventosTable = "CREATE TABLE IF NOT EXISTS eventos (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL, " +
                "description TEXT, " +
                "date DATE, " +
                "image_path TEXT, " +
                "external_link TEXT" +
                ");";

        String createPessoasTable = "CREATE TABLE IF NOT EXISTS pessoas (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "nome VARCHAR(255) NOT NULL, " +
                "idade INT" +
                ");";

        String createEventoPessoaTable = "CREATE TABLE IF NOT EXISTS evento_pessoa (" +
                "evento_id INT, " +
                "pessoa_id INT, " +
                "FOREIGN KEY (evento_id) REFERENCES eventos(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (pessoa_id) REFERENCES pessoas(id) ON DELETE CASCADE" +
                ");";

        String createUsuariosTable = "CREATE TABLE IF NOT EXISTS usuarios (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "login VARCHAR(100) NOT NULL UNIQUE, " +
                "senha VARCHAR(100) NOT NULL, " +
                "tipo ENUM('admin', 'comum') DEFAULT 'comum'" +
                ");";

        String createLogsTable = "CREATE TABLE IF NOT EXISTS logs (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "usuario_id INT, " +
                "acao TEXT, " +
                "data_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (usuario_id) REFERENCES usuarios(id)" +
                ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createEventosTable);
            stmt.execute(createPessoasTable);
            stmt.execute(createEventoPessoaTable);
            stmt.execute(createUsuariosTable);
            stmt.execute(createLogsTable);

            System.out.println("✅ Tabelas criadas ou já existem.");
        } catch (SQLException e) {
            System.err.println("❌ Erro ao criar tabelas: " + e.getMessage());
        }
    }

    // Método para fechar a conexão quando a aplicação for finalizada
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔻 Conexão com o banco de dados fechada.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao fechar conexão: " + e.getMessage());
        }
    }
}
