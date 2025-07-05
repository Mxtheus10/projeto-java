package com.historiaevents.view;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.historiaevents.controller.EventController;
import com.historiaevents.model.EventBase;

/**
 * Classe responsável pela interface gráfica do usuário (GUI) para o gerenciador de eventos históricos.
 */
public class EventGUI {
    private EventController eventController; // Controlador de eventos
    private JFrame frame; // Janela principal
    private JTable table; // Tabela para exibição dos eventos
    private DefaultTableModel tableModel; // Modelo de tabela
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // Formato da data

    /**
     * Construtor que inicializa a interface gráfica.
     */
    public EventGUI(EventController eventController) {
        this.eventController = eventController;
        initialize();
    }

    /**
     * Método que configura a interface gráfica.
     */
    private void initialize() {
        frame = new JFrame("Gerador de Eventos Históricos");
        frame.setSize(700, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Definição das colunas da tabela
        String[] columnNames = { "Nome", "Data", "Descrição" };
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Adicionar Evento");
        JButton removeButton = new JButton("Remover Eventos Selecionados");
        JButton generateButton = new JButton("Gerar Evento Aleatório");
        JButton shareButton = new JButton("Compartilhar no WhatsApp");

        // Adicionando os botões ao painel
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(generateButton);
        buttonPanel.add(shareButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Definição das ações dos botões
        addButton.addActionListener(e -> addEvent());
        removeButton.addActionListener(e -> removeEvents());
        generateButton.addActionListener(e -> generateEvent());
        shareButton.addActionListener(e -> shareEvent());

        frame.setVisible(true);
        loadEvents();
    }

    /**
     * Carrega os eventos do banco de dados para a tabela.
     */
    private void loadEvents() {
        tableModel.setRowCount(0); // Limpa a tabela antes de carregar os eventos
        for (EventBase event : eventController.getAllEvents()) {
            tableModel.addRow(new Object[] {
                    event.getName(),
                    event.getDate().format(DATE_FORMATTER),
                    event.getDescription()
            });
        }
    }

    /**
     * Coleta os dados de um novo evento a partir de inputs do usuário.
     */
    private EventBase collectEventData() {
        String nome;
        do {
            nome = JOptionPane.showInputDialog(frame, "Nome do Evento:");
            if (nome == null) return null; // Cancelar retorna null
        } while (nome.trim().isEmpty());

        String dataStr;
        LocalDate data = null;
        do {
            dataStr = JOptionPane.showInputDialog(frame, "Data (dd/MM/yyyy):");
            if (dataStr == null) return null;
            try {
                data = LocalDate.parse(dataStr, DATE_FORMATTER);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Formato de data inválido! Use dd/MM/yyyy.", "Erro", JOptionPane.ERROR_MESSAGE);
                dataStr = "";
            }
        } while (dataStr.trim().isEmpty());

        String descricao;
        do {
            descricao = JOptionPane.showInputDialog(frame, "Descrição do Evento:");
            if (descricao == null) return null;
        } while (descricao.trim().isEmpty());

        return new EventBase(0, nome, descricao, data);
    }

    /**
     * Adiciona um novo evento à lista e ao banco de dados.
     */
    private void addEvent() {
        EventBase newEvent = collectEventData();
        if (newEvent != null) {
            eventController.addEvent(newEvent);
            loadEvents(); // Atualiza a interface gráfica
            JOptionPane.showMessageDialog(frame, "Evento adicionado com sucesso!");
        }
    }

    /**
     * Remove os eventos selecionados na tabela.
     */
    private void removeEvents() {
        int[] selectedRows = table.getSelectedRows();
        if (selectedRows.length > 0) {
            List<String> nomes = new ArrayList<>();
            for (int row : selectedRows) {
                nomes.add(tableModel.getValueAt(row, 0).toString());
            }
            eventController.removeEvents(nomes);
            loadEvents();
            JOptionPane.showMessageDialog(frame, "Eventos removidos com sucesso!");
        } else {
            JOptionPane.showMessageDialog(frame, "Selecione eventos para remover!", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Gera um evento aleatório e adiciona à lista.
     */
    private void generateEvent() {
        EventBase randomEvent = eventController.generateRandomEvent();
        if (randomEvent != null) {
            eventController.addEvent(randomEvent);
            loadEvents();
            JOptionPane.showMessageDialog(frame, "Evento gerado com sucesso!");
        } else {
            JOptionPane.showMessageDialog(frame, "Não foi possível gerar um evento!");
        }
    }

    /**
     * Compartilha o evento selecionado pelo WhatsApp.
     */
    private void shareEvent() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            EventBase event = getEventFromRow(selectedRow);
            openWhatsApp(event);
        } else {
            JOptionPane.showMessageDialog(frame, "Selecione um evento para compartilhar!", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Obtém os detalhes do evento de uma linha selecionada na tabela.
     */
    private EventBase getEventFromRow(int row) {
        String nome = tableModel.getValueAt(row, 0).toString();
        LocalDate data = LocalDate.parse(tableModel.getValueAt(row, 1).toString(), DATE_FORMATTER);
        String descricao = tableModel.getValueAt(row, 2).toString();
        return new EventBase(0, nome, descricao, data);
    }

    /**
     * Abre o WhatsApp Web para compartilhar o evento selecionado.
     */
    private void openWhatsApp(EventBase event) {
        try {
            String message = "🎉 Novo Evento: " + event.getName() +
                    "\n📅 Data: " + event.getDate().format(DATE_FORMATTER) +
                    "\n📝 Descrição: " + event.getDescription();
            String url = "https://api.whatsapp.com/send?text=" + URLEncoder.encode(message, StandardCharsets.UTF_8);
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}