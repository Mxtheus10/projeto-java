package com.historiaevents.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.historiaevents.database.Database;
import com.historiaevents.model.EventBase;

/**
 * Classe DAO (Data Access Object) responsável por interagir com o banco de dados.
 * Implementa a interface IEventDAO, garantindo a padronização das operações de CRUD.
 */
public class EventDAO implements IEventDAO {

    /**
     * Obtém todos os eventos armazenados no banco de dados.
     * @return Uma lista de eventos do tipo EventBase.
     */
    @Override
    public List<EventBase> getAllEvents() {
        List<EventBase> events = new ArrayList<>();
        String sql = "SELECT id, name, description, date FROM eventos"; // Consulta os campos necessários

        try (Connection conn = Database.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Percorre os resultados e adiciona à lista de eventos
            while (rs.next()) {
                events.add(new EventBase(
                        rs.getInt("id"),            // ID do evento
                        rs.getString("name"),       // Nome do evento
                        rs.getString("description"),// Descrição do evento
                        rs.getString("date")        // Data do evento
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Exibe erros de SQL no console
        }
        return events;
    }

    /**
     * Adiciona um novo evento ao banco de dados.
     * @param event O evento a ser adicionado.
     */
    @Override
    public void addEvent(EventBase event) {
        String sql = "INSERT INTO eventos(name, description, date) VALUES (?, ?, ?)"; // Query de inserção

        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Define os valores dos placeholders na query SQL
            pstmt.setString(1, event.getName());
            pstmt.setString(2, event.getDescription());
            pstmt.setString(3, event.getDate());

            pstmt.executeUpdate(); // Executa a inserção no banco

            // Obtém o ID gerado automaticamente e define no objeto EventBase
            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                event.setId(generatedKeys.getInt(1));
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Exibe erros de SQL no console
        }
    }

    /**
     * Remove um evento do banco de dados pelo ID.
     * @param id O ID do evento a ser removido.
     */
    @Override
    public void removeEvent(int id) {
        String sql = "DELETE FROM eventos WHERE id = ?"; // Query para remover um evento pelo ID

        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id); // Define o ID do evento a ser removido
            pstmt.executeUpdate(); // Executa a remoção

        } catch (SQLException e) {
            e.printStackTrace(); // Exibe erros de SQL no console
        }
    }

    /**
     * Atualiza um evento existente no banco de dados.
     * @param event O evento atualizado contendo o ID e os novos valores.
     */
    @Override
    public void updateEvent(EventBase event) {
        String sql = "UPDATE eventos SET name = ?, description = ?, date = ? WHERE id = ?"; // Query de atualização

        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Define os novos valores dos campos no banco de dados
            pstmt.setString(1, event.getName());
            pstmt.setString(2, event.getDescription());
            pstmt.setString(3, event.getDate());
            pstmt.setInt(4, event.getId()); // Define o ID do evento que será atualizado

            pstmt.executeUpdate(); // Executa a atualização

        } catch (SQLException e) {
            e.printStackTrace(); // Exibe erros de SQL no console
        }
    }
}