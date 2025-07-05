package com.historiaevents.view;

import com.historiaevents.controller.EventController;
import com.historiaevents.model.EventBase;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.awt.Desktop;
import java.net.URI;
import java.io.File;


public class EventView extends Application {

    private final EventController eventController = new EventController();
    private final ObservableList<EventBase> eventList = FXCollections.observableArrayList();
    private final TableView<EventBase> tableView = new TableView<>();
    private final TextField searchField = new TextField();
    private final ComboBox<String> orderBox = new ComboBox<>();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final BorderPane root = new BorderPane();

    @Override
    public void start(Stage stage) {
    stage.setTitle("Eventos Históricos - JavaFX Edition");

    VBox topBox = new VBox(10);
    topBox.setAlignment(Pos.CENTER);
    HBox searchBox = new HBox(10);

    Label titulo = new Label("\uD83D\uDD70️ Eventos Históricos");
    titulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2a2a2a;");
    titulo.setPadding(new Insets(0, 0, 10, 0));
    titulo.setAlignment(Pos.CENTER);

    searchField.setPromptText("Buscar evento por nome...");
    searchField.setStyle("-fx-background-radius: 5; -fx-padding: 5;");
    searchField.textProperty().addListener((obs, oldVal, newVal) -> filtrarEventos());

    orderBox.getItems().addAll("Nome A-Z", "Nome Z-A", "Mais recentes", "Mais antigos");
    orderBox.setValue("Mais recentes");
    orderBox.setStyle("-fx-background-radius: 5; -fx-padding: 5;");
    orderBox.setOnAction(e -> ordenarEventos());

    Button btnAdd = new Button("➕ Novo Evento");
    btnAdd.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
    btnAdd.setOnAction(e -> abrirFormularioNovoEvento());

    Button btnEdit = new Button("✏️ Editar Evento");
    btnEdit.setStyle("-fx-background-color: #FFC107; -fx-text-fill: white; -fx-font-weight: bold;");
    btnEdit.setOnAction(e -> {
        EventBase selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            abrirFormularioEditarEvento(selected);
        } else {
            showAlerta("Selecione um evento para editar.");
        }
    });

    Button btnDelete = new Button("🗑️ Excluir Evento");
    btnDelete.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");
    btnDelete.setOnAction(e -> {
        EventBase selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            deletarEvento(selected);
        } else {
            showAlerta("Selecione um evento para excluir.");
        }
    });

    Button btnTimeline = new Button("🕰️ Linha do Tempo");
    btnTimeline.setStyle("-fx-background-color: #9C27B0; -fx-text-fill: white; -fx-font-weight: bold;");
    btnTimeline.setOnAction(e -> mostrarTimeline());

    searchBox.getChildren().addAll(searchField, orderBox, btnAdd, btnEdit, btnDelete, btnTimeline);
    topBox.getChildren().addAll(titulo, searchBox);

    configurarTabela();
    carregarEventos();

    root.setTop(topBox);
    root.setCenter(tableView);

    Scene scene = new Scene(root, 800, 600);
    stage.setScene(scene);
    stage.show();
}

    private void configurarTabela() {
    TableColumn<EventBase, String> nameCol = new TableColumn<>("Nome");
    nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
    nameCol.setStyle("-fx-alignment: CENTER;");

    TableColumn<EventBase, String> descCol = new TableColumn<>("Descrição");
    descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
    descCol.setStyle("-fx-alignment: CENTER;");

    TableColumn<EventBase, String> dateCol = new TableColumn<>("Data");
    dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
    dateCol.setStyle("-fx-alignment: CENTER;");

    // Coluna de ações (editar/excluir)
    TableColumn<EventBase, Void> actionCol = new TableColumn<>("Ações");
    actionCol.setCellFactory(col -> new TableCell<>() {
        private final Button btnEditar = new Button("✏️");
        private final Button btnExcluir = new Button("🗑️");
        private final HBox pane = new HBox(5, btnEditar, btnExcluir);

        {
            btnEditar.setStyle("-fx-background-color: #FFC107; -fx-text-fill: black; -fx-font-weight: bold;");
            btnEditar.setOnAction(e -> {
                EventBase evento = getTableView().getItems().get(getIndex());
                abrirFormularioEditarEvento(evento);
            });

            btnExcluir.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-weight: bold;");
            btnExcluir.setOnAction(e -> {
                EventBase evento = getTableView().getItems().get(getIndex());
                deletarEvento(evento);
            });

            pane.setAlignment(Pos.CENTER);
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                setGraphic(pane);
            }
        }
    });

    tableView.getColumns().clear();  
    tableView.getColumns().addAll(nameCol, descCol, dateCol, actionCol);
}

    private void mostrarTimeline() {
        VBox timelineBox = new VBox(15);
        timelineBox.setPadding(new Insets(20));
        timelineBox.setAlignment(Pos.TOP_CENTER);

        ScrollPane scrollPane = new ScrollPane(timelineBox);
        scrollPane.setFitToWidth(true);

        List<EventBase> eventosOrdenados = new ArrayList<>(eventController.getAllEvents());
        eventosOrdenados.sort(Comparator.comparing(EventBase::getDate));

        for (EventBase evento : eventosOrdenados) {
            VBox card = criarCardDoEvento(evento);
            timelineBox.getChildren().add(card);
        }

        root.setCenter(scrollPane);
    }

    private VBox criarCardDoEvento(EventBase evento) {
    Label titulo = new Label(evento.getName());
    titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

    Label data = new Label(evento.getDate().format(formatter));
    data.setStyle("-fx-text-fill: gray; -fx-font-size: 14px;");

    ImageView imagemView = null;
    if (evento.getImagePath() != null && !evento.getImagePath().isBlank()) {
        try {
            File imagemArquivo = new File(evento.getImagePath());
            if (imagemArquivo.exists()) {
                Image img = new Image(imagemArquivo.toURI().toString(), 300, 200, true, true);
                imagemView = new ImageView(img);
                imagemView.setPreserveRatio(true);
            } else {
                System.out.println("❌ Imagem não encontrada no caminho: " + imagemArquivo.getAbsolutePath());
            }
        } catch (Exception e) {
            System.out.println("❌ Erro ao carregar imagem: " + e.getMessage());
        }
    }

    VBox box = new VBox(10);
    box.setPadding(new Insets(15));
    box.setMaxWidth(400);
    box.setStyle("""
                -fx-background-color: white;
                -fx-border-color: #3f51b5;
                -fx-border-width: 2px;
                -fx-background-radius: 10;
                -fx-border-radius: 10;
                -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 4);
            """);

    if (imagemView != null) {
        box.getChildren().add(imagemView);
    }

    box.getChildren().addAll(titulo, data);
    box.setOnMouseClicked(e -> mostrarDetalhes(evento));

    HBox linha = new HBox();
    linha.setPadding(new Insets(10));
    linha.setSpacing(20);
    linha.setAlignment(Pos.CENTER);

    if (evento.getId() % 2 == 0) {
        linha.getChildren().addAll(box, criarLinhaVertical());
    } else {
        linha.getChildren().addAll(criarLinhaVertical(), box);
    }

    return new VBox(linha);
}
    private VBox criarLinhaVertical() {
        VBox linha = new VBox();
        linha.setPrefWidth(10);
        linha.setMinHeight(100);
        linha.setStyle("-fx-background-color: #3f51b5;");
        return linha;
    }

    private void mostrarDetalhes(EventBase evento) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("📘 Detalhes do Evento");
        dialog.setHeaderText(evento.getName());

        Label dataLabel = new Label("📅 Data: " + evento.getDate().format(formatter));
        Label descLabel = new Label("🖊️ Descrição:\n" + evento.getDescription());

        VBox content = new VBox(10, dataLabel, descLabel);

        if (evento.getExternalLink() != null && !evento.getExternalLink().isBlank()) {
            Hyperlink link = new Hyperlink(evento.getExternalLink());
            link.setOnAction(e -> {
                try {
                    Desktop.getDesktop().browse(new URI(evento.getExternalLink()));
                } catch (Exception ex) {
                    showAlerta("Erro ao abrir o link: " + ex.getMessage());
                }
            });
            content.getChildren().add(new Label("🔗 Link:"));
            content.getChildren().add(link);
        }

        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.showAndWait();
    }

    private void carregarEventos() {
        List<EventBase> eventos = eventController.getAllEvents();
        eventList.setAll(eventos);
        tableView.setItems(eventList);
    }

    private void filtrarEventos() {
        String filtro = searchField.getText().toLowerCase();
        List<EventBase> filtrados = eventController.getAllEvents().stream()
                .filter(e -> e.getName().toLowerCase().contains(filtro))
                .collect(Collectors.toList());
        eventList.setAll(filtrados);
    }

    private void ordenarEventos() {
        String criterio = orderBox.getValue();
        List<EventBase> eventos = new ArrayList<>(eventController.getAllEvents());

        switch (criterio) {
            case "Nome A-Z" -> eventos.sort(Comparator.comparing(EventBase::getName));
            case "Nome Z-A" -> eventos.sort(Comparator.comparing(EventBase::getName).reversed());
            case "Mais recentes" -> eventos.sort(Comparator.comparing(EventBase::getDate).reversed());
            case "Mais antigos" -> eventos.sort(Comparator.comparing(EventBase::getDate));
        }

        eventList.setAll(eventos);
    }

    private void abrirFormularioNovoEvento() {
        Dialog<EventBase> dialog = new Dialog<>();
        dialog.setTitle("Novo Evento");

        TextField nomeField = new TextField();
        nomeField.setPromptText("Nome");

        TextField descField = new TextField();
        descField.setPromptText("Descrição");

        DatePicker datePicker = new DatePicker();

        TextField imagemField = new TextField();
        imagemField.setPromptText("Caminho da Imagem (opcional)");

        TextField linkField = new TextField();
        linkField.setPromptText("Link Externo (opcional)");

        VBox vbox = new VBox(10,
                new Label("Nome:"), nomeField,
                new Label("Descrição:"), descField,
                new Label("Data:"), datePicker,
                new Label("Caminho da Imagem:"), imagemField,
                new Label("Link Externo:"), linkField);
        vbox.setPadding(new Insets(10));

        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                String nome = nomeField.getText().trim();
                String descricao = descField.getText().trim();
                LocalDate data = datePicker.getValue();
                String imagem = imagemField.getText().trim();
                String link = linkField.getText().trim();

                if (nome.isEmpty() || descricao.isEmpty()) {
                    showAlerta("Nome e descrição são obrigatórios.");
                    return null;
                }

                if (data == null) {
                    showAlerta("Por favor, selecione uma data.");
                    return null;
                }

                if (data.isAfter(LocalDate.now())) {
                    showAlerta("A data não pode estar no futuro.");
                    return null;
                }

                return new EventBase(0, nome, descricao, data, imagem, link);
            }
            return null;
        });

        dialog.showAndWait().ifPresent(event -> {
            eventController.addEvent(event);
            carregarEventos();
            showAlerta("Evento adicionado com sucesso!");
        });
    }

    private void deletarEvento(EventBase evento) {
    Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
    confirmDialog.setTitle("Excluir Evento");
    confirmDialog.setHeaderText("Deseja realmente excluir o evento: " + evento.getName() + "?");
    confirmDialog.setContentText("Essa ação não poderá ser desfeita.");

    Optional<ButtonType> result = confirmDialog.showAndWait();
    if (result.isPresent() && result.get() == ButtonType.OK) {
        try {
            eventController.deleteEvent(evento.getId());
            carregarEventos();
            showAlerta("Evento excluído com sucesso!");
        } catch (Exception e) {
            showAlerta("Erro ao excluir evento: " + e.getMessage());
        }
    }
}


    private void abrirFormularioEditarEvento(EventBase eventoOriginal) {
        Dialog<EventBase> dialog = new Dialog<>();
        dialog.setTitle("Editar Evento");

        TextField nomeField = new TextField(eventoOriginal.getName());
        TextField descField = new TextField(eventoOriginal.getDescription());
        DatePicker datePicker = new DatePicker(eventoOriginal.getDate());
        TextField imagemField = new TextField(
                eventoOriginal.getImagePath() != null ? eventoOriginal.getImagePath() : "");
        imagemField.setPromptText("Caminho da Imagem (opcional)");

        TextField linkField = new TextField(
                eventoOriginal.getExternalLink() != null ? eventoOriginal.getExternalLink() : "");
        linkField.setPromptText("Link Externo (opcional)");

        VBox vbox = new VBox(10,
                new Label("Nome:"), nomeField,
                new Label("Descrição:"), descField,
                new Label("Data:"), datePicker,
                new Label("Caminho da Imagem:"), imagemField,
                new Label("Link Externo:"), linkField);
        vbox.setPadding(new Insets(10));

        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                return new EventBase(
                        eventoOriginal.getId(),
                        nomeField.getText(),
                        descField.getText(),
                        datePicker.getValue(),
                        imagemField.getText(),
                        linkField.getText());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(eventoEditado -> {
            if (eventoEditado.getName().trim().isEmpty() || eventoEditado.getDescription().trim().isEmpty()) {
                showAlerta("Todos os campos devem ser preenchidos.");
                return;
            }

            if (eventoEditado.getDate() == null || eventoEditado.getDate().isAfter(java.time.LocalDate.now())) {
                showAlerta("Data inválida.");
                return;
            }

            eventController.updateEvent(eventoEditado);
            carregarEventos();
            showAlerta("Evento atualizado com sucesso!");
        });
    }

    private void showAlerta(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}
