package com.historiaevents.database;

import java.util.List;

import com.historiaevents.model.Event;

public class DatabaseManager {
    private DatabaseConnection databaseConnection;

    public DatabaseManager() {
        this.databaseConnection = new DatabaseConnection(); // Inicializa a conexão com o banco de dados
    }

    public List<Event> getAllEvents() {
        return databaseConnection.getAllEvents(); // Recupera todos os eventos do banco de dados
    }

    public void closeConnection() {
        databaseConnection.close(); // Fecha a conexão ao finalizar o uso
    }

    // Outros métodos para gerenciar eventos podem ser adicionados aqui
}
