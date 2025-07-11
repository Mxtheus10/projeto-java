package com.historiaevents.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.historiaevents.dao.EventDAO;
import com.historiaevents.database.Database;
import com.historiaevents.model.EventBase;

public class EventController {
    private final EventDAO eventDAO = new EventDAO(); // ✅ Adicione essa linha!
    private final List<EventBase> events = new ArrayList<>();

    private static final DateTimeFormatter SQL_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Adiciona um evento ao banco de dados
    public void addEvent(EventBase event) {
        String sql = "INSERT INTO eventos(nome, data, descricao, image_path, external_link) VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = Database.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, event.getName());
            pstmt.setString(2, event.getDate().toString());
            pstmt.setString(3, event.getDescription());
            pstmt.setString(4, event.getImagePath());
            pstmt.setString(5, event.getExternalLink());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erro ao adicionar evento: " + e.getMessage());
        }
    }

    public void updateEvent(EventBase updatedEvent) {
        eventDAO.updateEvent(updatedEvent);
    }

    public void deleteEvent(int id) {
        eventDAO.removeEvent(id);
    }

    // Remove múltiplos eventos pelo nome
    public void removeEvents(List<String> nomes) {
        if (nomes.isEmpty())
            return;

        String placeholders = String.join(",", nomes.stream().map(n -> "?").toArray(String[]::new));

        String deleteAssociationsSQL = "DELETE FROM evento_pessoa WHERE evento_id IN (SELECT id FROM eventos WHERE nome IN ("
                + placeholders + "))";
        String deleteEventSQL = "DELETE FROM eventos WHERE nome IN (" + placeholders + ")";

        try (Connection conn = Database.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            // Remove associações antes de excluir os eventos
            try (PreparedStatement pstmt = conn.prepareStatement(deleteAssociationsSQL)) {
                for (int i = 0; i < nomes.size(); i++) {
                    pstmt.setString(i + 1, nomes.get(i));
                }
                pstmt.executeUpdate();
            }

            // Remove os eventos
            try (PreparedStatement pstmt = conn.prepareStatement(deleteEventSQL)) {
                for (int i = 0; i < nomes.size(); i++) {
                    pstmt.setString(i + 1, nomes.get(i));
                }
                int affectedRows = pstmt.executeUpdate();

                if (affectedRows > 0) {
                    System.out.println("Eventos removidos com sucesso!");
                    conn.commit();
                } else {
                    System.out.println("Nenhum evento encontrado para remover.");
                    conn.rollback();
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao remover eventos: " + e.getMessage());
        }
    }

    // Gera um evento aleatório
    public EventBase generateRandomEvent() {
        String sql = "SELECT * FROM eventos ORDER BY RAND() LIMIT 1";

        try (Connection conn = Database.getInstance().getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return createEventFromResultSet(rs);
            } else {
                System.out.println("Nenhum evento encontrado no banco de dados.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao gerar evento aleatório: " + e.getMessage());
        }
        return null;
    }

    // Obtém todos os eventos
    public List<EventBase> getAllEvents() {
        List<EventBase> events = new ArrayList<>();
        String sql = "SELECT * FROM eventos";

        try (Connection conn = Database.getInstance().getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                events.add(createEventFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao obter eventos: " + e.getMessage());
        }
        return events;
    }

    // Cria um objeto EventBase a partir do resultado do banco de dados
    private EventBase createEventFromResultSet(ResultSet rs) throws SQLException {
        return new EventBase(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("descricao"),
                LocalDate.parse(rs.getString("data"), SQL_DATE_FORMATTER),
                rs.getString("image_path"),
                rs.getString("external_link"));
    }

}
