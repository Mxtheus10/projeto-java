package com.historiaevents.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.historiaevents.model.Event;

/**
 * Classe responsável por gerenciar a conexão com o banco de dados MySQL e
 * fornecer métodos para buscar eventos.
 */
public class DatabaseConnection {
    private Connection connection; // Objeto de conexão com o banco de dados

    /**
     * Construtor que estabelece a conexão com o banco de dados ao inicializar a
     * classe.
     */
    public DatabaseConnection() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cesarprojeto?useSSL=false",
                    "root", "JOAOcoca33");
            System.out.println("✅ Conexão com o banco de dados estabelecida.");
        } catch (SQLException e) {
            System.err.println("❌ Erro ao conectar ao banco de dados!");
            e.printStackTrace();
        }
    }

    /**
     * Obtém todos os eventos armazenados no banco de dados.
     * 
     * @return Lista de eventos
     */
    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Define o formato da data no banco

        // Corrigido o nome da tabela para "eventos"
        String sql = "SELECT nome, data, descricao FROM eventos"; 

        try (PreparedStatement pstmt = connection.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // Cria um novo objeto Event com os dados retornados do banco
                Event event = new Event(
                        rs.getString("nome"),
                        LocalDate.parse(rs.getString("data"), formatter), // Converte a data do banco para LocalDate
                        rs.getString("descricao"));
                events.add(event);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao obter eventos!");
            e.printStackTrace();
        }
        return events;
    }

    /**
     * Fecha a conexão com o banco de dados para liberar recursos.
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔻 Conexão com o banco de dados fechada.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao fechar conexão!");
            e.printStackTrace();
        }
    }
}

