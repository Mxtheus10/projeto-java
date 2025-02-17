package com.historiaevents;

import com.historiaevents.controller.EventController;
import com.historiaevents.database.Database;
import com.historiaevents.view.EventGUI;

/**
 * Classe principal que inicializa a aplicação.
 */
public class MainApp {
    public static void main(String[] args) {
        // Inicializa o banco de dados e cria as tabelas se não existirem
        Database.getInstance().createTables();

        // Cria o controlador de eventos, responsável pela lógica de negócio
        EventController eventController = new EventController();

        // Inicia a interface gráfica da aplicação
        new EventGUI(eventController);

        // Adiciona um "Shutdown Hook" para garantir que a conexão com o banco seja fechada ao sair do programa
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Database.getInstance().closeConnection();
        }));
    }
}
