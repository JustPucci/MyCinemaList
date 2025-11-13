package it.ingsw.progetto;

import it.ingsw.progetto.UI.MediaDialog;
import it.ingsw.progetto.collection.CollezioneMedia;
import it.ingsw.progetto.dati.GestoreDatiInterface;
import it.ingsw.progetto.dati.GestoreDatiJSON;
import it.ingsw.progetto.media.Media;
import it.ingsw.progetto.strategy.CriterioFiltro;
import it.ingsw.progetto.strategy.FiltraPerTipo;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory; // Collega colonne ai getter
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Box;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Optional;
import java.util.List;


//Classe principale della GUI (JavaFX).
public class GuiApplication extends Application {

    // --- Collegamento Backend ---
    private static CollezioneMedia collezione;
    private static final String FILE_PATH = "collezione.json";

    // --- Componenti GUI ---
    private TableView<Media> tableView; // Tabella principale

    // Lista "osservabile" per la GUI
    private ObservableList<Media> observableMediaList;

    // Componenti per i filtri
    private ComboBox<String> filtroTipoComboBox;

    // Punto di ingresso dell'applicazione JavaFX.
    @Override
    public void start(Stage primaryStage) {

        // --- 1. Inizializzazione Backend ---
        GestoreDatiInterface gestoreDati = new GestoreDatiJSON(FILE_PATH);
        collezione = CollezioneMedia.getIstanza(gestoreDati);

        // --- 2. Setup GUI ---
        BorderPane rootLayout = new BorderPane();
        rootLayout.setPadding(new Insets(10));

        // 2a. Titolo
        Label titleLabel = new Label("My Cinema List");
        titleLabel.setFont(new Font("Arial", 24));
        rootLayout.setTop(titleLabel);
        BorderPane.setMargin(titleLabel, new Insets(0, 0, 10, 0));

        // 2b. Tabella
        tableView = new TableView<>();
        setupTableColumns();
        rootLayout.setCenter(tableView);

        // 2c. Caricamento Dati
        // Converte la List del backend in ObservableList per la GUI
        observableMediaList = FXCollections.observableArrayList(collezione.getElencoCompleto());
        VBox actionPanel = createActionPanel(); // Creiamo il pannello
        rootLayout.setRight(actionPanel); // !!! Aggiungiamo il pannello a destra !!!
        BorderPane.setMargin(actionPanel, new Insets(0, 0, 0, 10));

        // Collega la lista alla tabella
        tableView.setItems(observableMediaList);

        rootLayout.setCenter(tableView);

        // --- 3. Setup Scena e Finestra ---
        Scene scene = new Scene(rootLayout, 1024, 768); // Dimensioni finestra

        try {
            String cssPath = getClass().getResource("/styles.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
        } catch (Exception e) {
            System.err.println("Errore: Impossibile caricare style.css.");
            e.printStackTrace();
        }

        primaryStage.setTitle("My Media Collection Manager");
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setTitle("My Cinema List");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createActionPanel() {
        VBox panel = new VBox(10); // Spaziatura di 10px
        panel.setPadding(new Insets(10));
        panel.setMinWidth(200);

        // --- Pulsante Aggiungi (Logica da implementare) ---
        Button addButton = new Button("Aggiungi Nuovo Media...");
        addButton.setMaxWidth(Double.MAX_VALUE);
        addButton.setOnAction(e -> gestisciAggiungi()); // Prossimo passo

        // --- Pulsante Rimuovi ---
        Button removeButton = new Button("Rimuovi Selezionato");
        removeButton.setMaxWidth(Double.MAX_VALUE);
        removeButton.setOnAction(e -> gestisciRimuovi());

        // --- Controlli Filtro ---
        Label filtroLabel = new Label("Filtra per Tipo:");
        filtroTipoComboBox = new ComboBox<>();
        // Popoliamo il menu a tendina
        filtroTipoComboBox.setItems(FXCollections.observableArrayList(
                "Film", "Documentario", "SerieTV"
        ));
        filtroTipoComboBox.setMaxWidth(Double.MAX_VALUE);

        Button filterButton = new Button("Applica Filtro");
        filterButton.setMaxWidth(Double.MAX_VALUE);
        filterButton.setOnAction(e -> gestisciFiltro());

        Button clearFilterButton = new Button("Pulisci Filtro");
        clearFilterButton.setMaxWidth(Double.MAX_VALUE);
        clearFilterButton.setOnAction(e -> refreshTableData()); // Ricarica tutto

        // Aggiunge tutti i controlli al pannello
        panel.getChildren().addAll(
                addButton, removeButton,
                new Separator(), // Linea di separazione
                filtroLabel, filtroTipoComboBox, filterButton, clearFilterButton
        );

        return panel;
    }

    private void gestisciAggiungi() {
        MediaDialog dialog = new MediaDialog();

        // 2. Mostra il dialogo e attendi il risultato
        Optional<Media> result = dialog.showAndWait();

        // 3. Controlla se l'utente ha premuto "Salva" e il risultato è valido
        result.ifPresent(nuovoMedia -> {
                    // 4. Chiama il backend (Singleton)
                    collezione.aggiungiMedia(nuovoMedia);

                    // 5. Aggiorna la GUI (la tabella)
                    observableMediaList.add(nuovoMedia);
                    System.out.println("Aggiunto: " + nuovoMedia.getTitolo());
                });
    }

    private void gestisciRimuovi() {
        // 1. Prendi l'oggetto selezionato dalla tabella
        Media selectedMedia = tableView.getSelectionModel().getSelectedItem();

        if (selectedMedia == null) {
            mostraErrore("Nessun elemento selezionato", "Per favore, seleziona un media dalla tabella prima di rimuoverlo.");
            return; // Esci se non c'è nulla da fare
        }

        // 2. Chiedi conferma
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma Rimozione");
        alert.setHeaderText("Rimuovere '" + selectedMedia.getTitolo() + "'?");
        alert.setContentText("Questa azione è permanente.");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            // 3. Chiama il backend (Singleton)
            boolean rimosso = collezione.rimuoviMedia(selectedMedia.getTitolo());

            if (rimosso) {
                // 4. Aggiorna la GUI
                observableMediaList.remove(selectedMedia);
                System.out.println("Rimosso: " + selectedMedia.getTitolo());
            } else {
                mostraErrore("Errore di Rimozione", "Impossibile rimuovere " + selectedMedia.getTitolo() + ".");
            }
        }
    }

    /**
     * Logica per il pulsante "Applica Filtro".
     * Usa il Pattern Strategy.
     */
    private void gestisciFiltro() {
        String tipoSelezionato = filtroTipoComboBox.getValue();

        if (tipoSelezionato == null || tipoSelezionato.isEmpty()) {
            mostraErrore("Filtro non valido", "Per favore, seleziona un tipo dal menu a tendina.");
            return;
        }

        // 1. Crea la Strategia concreta
        CriterioFiltro criterio = new FiltraPerTipo(tipoSelezionato);

        // 2. Chiama il backend
        List<Media> risultatiFiltrati = collezione.applicaFiltro(criterio);

        // 3. Aggiorna la GUI (la tabella)
        observableMediaList.setAll(risultatiFiltrati);
        System.out.println("Filtro applicato per: " + tipoSelezionato);
    }

    /**
     * Ricarica TUTTI i dati dal backend e aggiorna la tabella.
     * Usato all'avvio e per "Pulisci Filtro".
     */
    private void refreshTableData() {
        if (observableMediaList == null) {
            observableMediaList = FXCollections.observableArrayList();
            tableView.setItems(observableMediaList);
        }

        // Carica la lista completa e aggiorna la tabella
        observableMediaList.setAll(collezione.getElencoCompleto());
        System.out.println("Tabella aggiornata. Mostrati " + observableMediaList.size() + " elementi.");
    }

    /**
     * Metodo helper per mostrare un pop-up di errore.
     * @param titolo Il titolo della finestra di errore.
     * @param messaggio Il messaggio di errore.
     */
    private void mostraErrore(String titolo, String messaggio) {
        System.err.println(titolo + ": " + messaggio); // Debugging print
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }

    private void setupTableColumns() {

        TableColumn<Media, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("tipoContenuto"));
        typeColumn.setMinWidth(100);

        TableColumn<Media, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("titolo"));
        titleColumn.setMinWidth(250);
        titleColumn.getStyleClass().add("title-column-cell");

        TableColumn<Media, Integer> ratingColumn = new TableColumn<>("Rating (1-5)");
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("valutazionePersonale"));
        ratingColumn.setMinWidth(100);

        TableColumn<Media, Media.StatoVisione> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("statoVisione"));
        statusColumn.setMinWidth(120);

        tableView.getColumns().addAll(typeColumn, titleColumn, ratingColumn, statusColumn);

        tableView.getSortOrder().add(titleColumn);
    }


    public static void main(String[] args) {
        launch(args);
    }
}