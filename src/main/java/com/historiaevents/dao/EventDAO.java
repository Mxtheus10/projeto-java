package com.historiaevents.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.historiaevents.database.Database;
import com.historiaevents.model.EventBase;

/**
 * Classe DAO (Data Access Object) responsável por interagir com o banco de
 * dados.
 * Implementa a interface IEventDAO, garantindo a padronização das operações de
 * CRUD.
 */
public class EventDAO implements IEventDAO {

    /**
     * Obtém todos os eventos armazenados no banco de dados.
     * 
     * @return Uma lista de eventos do tipo EventBase.
     */
    @Override
    public List<EventBase> getAllEvents() {
        List<EventBase> events = new ArrayList<>();
        String sql = "SELECT id, name, description, date, image_path, external_link FROM eventos";

        try (Connection conn = Database.getInstance().getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                events.add(new EventBase(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDate("date").toLocalDate(),
                        rs.getString("image_path"),
                        rs.getString("external_link")));
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao buscar eventos", e);
        }
        return events;
    }

    /**
     * Adiciona um novo evento ao banco de dados.
     * 
     * @param event O evento a ser adicionado.
     */
    @Override
    public void addEvent(EventBase event) {
        String sql = "INSERT INTO eventos(name, description, date, image_path, external_link) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Database.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, event.getName());
            pstmt.setString(2, event.getDescription());
            pstmt.setDate(3, Date.valueOf(event.getDate()));
            pstmt.setString(4, event.getImagePath());
            pstmt.setString(5, event.getExternalLink());

            pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                event.setId(generatedKeys.getInt(1));
            }

        } catch (SQLException e) {
            throw new DaoException("Erro ao adicionar evento", e);
        }
    }

    /**
     * Remove um evento do banco de dados pelo ID.
     * 
     * @param id O ID do evento a ser removido.
     */
    @Override
    public void removeEvent(int id) {
        String sql = "DELETE FROM eventos WHERE id = ?";

        try (Connection conn = Database.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new DaoException("Erro ao remover evento", e);
        }
    }

    /**
     * Atualiza um evento existente no banco de dados.
     * 
     * @param event O evento atualizado contendo o ID e os novos valores.
     */
    @Override
    public void updateEvent(EventBase event) {
        String sql = "UPDATE eventos SET nome = ?, descricao = ?, data = ?, image_path = ?, external_link = ? WHERE id = ?";

        try (Connection conn = Database.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, event.getName());
            pstmt.setString(2, event.getDescription());
            pstmt.setDate(3, Date.valueOf(event.getDate()));
            pstmt.setString(4, event.getImagePath());
            pstmt.setString(5, event.getExternalLink());
            pstmt.setInt(6, event.getId());

            int rows = pstmt.executeUpdate();
            System.out.println("🔄 Linhas atualizadas: " + rows);

        } catch (SQLException e) {
            throw new DaoException("Erro ao atualizar evento", e);
        }
    }
}
