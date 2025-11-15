package it.ingsw.progetto;

import it.ingsw.progetto.collection.CollezioneMedia;
import it.ingsw.progetto.collection.LibreriaFacade;
import it.ingsw.progetto.dati.GestoreDatiInterface;
import it.ingsw.progetto.dati.GestoreDatiJSON;
import it.ingsw.progetto.media.Documentario;
import it.ingsw.progetto.media.Film;
import it.ingsw.progetto.media.Media;
import it.ingsw.progetto.media.SerieTv;
import it.ingsw.progetto.strategy.CriterioFiltro;
import it.ingsw.progetto.strategy.FiltraPerTipo;
import it.ingsw.progetto.UI.MediaDialog;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class GuiApplication extends Application {

    // --- Backend Connection ---
    private static LibreriaFacade libreria;
    private static final String FILE_PATH = "collezione.json";


    // --- GUI Components ---
    private TableView<Media> tableView;
    private TextField searchField;
    private ObservableList<Media> observableMediaList;
    private ComboBox<String> filtroTipoComboBox;
    private ComboBox<String> filtroGenereComboBox;
    private ComboBox<Media.StatoVisione> filtroStatoComboBox;
    private ComboBox<String> ordinamentoComboBox;

    @Override
    public void start(Stage primaryStage) {

        // --- 1. Initialization (Backend) ---
        GestoreDatiInterface gestoreDati = new GestoreDatiJSON(FILE_PATH);
        CollezioneMedia collezione = CollezioneMedia.getIstanza(gestoreDati);
        libreria = new LibreriaFacade(collezione);

        // --- 2. Setup GUI ---
        BorderPane rootLayout = new BorderPane();
        rootLayout.setPadding(new Insets(10));

        // 2a. Top Title
        Label titleLabel = new Label("My Media Collection Manager");
        titleLabel.setFont(new Font("Arial", 24));
        rootLayout.setTop(titleLabel);
        BorderPane.setMargin(titleLabel, new Insets(0, 0, 10, 0));

        // 2b. Center Table
        tableView = new TableView<>();
        setupTableColumns();
        rootLayout.setCenter(tableView);

        // 2c. Right Action Panel
        VBox actionPanel = createActionPanel();
        rootLayout.setRight(actionPanel); // Add panel to layout
        BorderPane.setMargin(actionPanel, new Insets(0, 0, 0, 10));

        // 2d. Load Data
        refreshTableData();

        // --- 3. Scene and Stage Setup ---
        Scene scene = new Scene(rootLayout, 1024, 768);

        // Load CSS
        try {
            String cssPath = getClass().getResource("/styles.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
        } catch (Exception e) {
            System.err.println("ERROR: Cannot load styles.css.");
            e.printStackTrace();
        }

        primaryStage.setTitle("My Media Collection Manager");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Helper method to create the side panel.
     * @return The VBox with all controls.
     */
    private VBox createActionPanel() {
        VBox panel = new VBox(10); // 10px spacing
        panel.setPadding(new Insets(10));
        panel.setMinWidth(200);

        // --- Add Button ---
        Button addButton = new Button("Aggiungi Nuovo Media...");
        addButton.setMaxWidth(Double.MAX_VALUE);
        addButton.setOnAction(e -> gestisciAggiungi());

        // --- Edit Button ---
        Button editButton = new Button("Modifica Selezionato...");
        editButton.setMaxWidth(Double.MAX_VALUE);
        editButton.setOnAction(e -> gestisciModifica());

        // --- Remove Button ---
        Button removeButton = new Button("Rimuovi Selezionato");
        removeButton.setMaxWidth(Double.MAX_VALUE);
        removeButton.setOnAction(e -> gestisciRimuovi());

        // --- Search Field (Non c'è nel tuo codice, lo aggiungo) ---
        Label searchLabel = new Label("Ricerca per Titolo:");
        searchField = new TextField();
        searchField.setPromptText("Contiene...");
        searchField.setMaxWidth(Double.MAX_VALUE);
        searchField.setOnKeyReleased(e -> gestisciRicerca());

        // --- Filter Controls ---
        Label filtroLabel = new Label("Filtra per Tipo:");
        filtroTipoComboBox = new ComboBox<>();
        filtroTipoComboBox.setItems(FXCollections.observableArrayList(
                "Film", "Documentario", "SerieTv"
        ));
        filtroTipoComboBox.setMaxWidth(Double.MAX_VALUE);

        // --- Filtro Genere ---
        Label filtroGenereLabel = new Label("Filtra per Genere:");
        filtroGenereComboBox = new ComboBox<>();
        List<String> tuttiGeneri = Stream.concat(
                Arrays.stream(Film.Genere.values()).map(Enum::name),
                Stream.concat(
                        Arrays.stream(Documentario.GenereDocumentario.values()).map(Enum::name),
                        Arrays.stream(SerieTv.GenereSerie.values()).map(Enum::name)
                )
        ).distinct().sorted().collect(java.util.stream.Collectors.toList());
        filtroGenereComboBox.setItems(FXCollections.observableArrayList(tuttiGeneri));
        filtroGenereComboBox.setMaxWidth(Double.MAX_VALUE);

        // --- Filtro Stato Visione ---
        Label filtroStatoLabel = new Label("Filtra per Stato:");
        filtroStatoComboBox = new ComboBox<>();
        filtroStatoComboBox.setItems(FXCollections.observableArrayList(Media.StatoVisione.values()));
        filtroStatoComboBox.setMaxWidth(Double.MAX_VALUE);

        // Pulsante unico per i filtri
        Button filterButton = new Button("Applica Filtri");
        filterButton.setMaxWidth(Double.MAX_VALUE);
        filterButton.setOnAction(e -> gestisciFiltro());

        Button clearFilterButton = new Button("Pulisci Filtri/Ricerca");
        clearFilterButton.setMaxWidth(Double.MAX_VALUE);
        clearFilterButton.setOnAction(e -> {
            searchField.clear();
            filtroTipoComboBox.setValue(null);
            filtroGenereComboBox.setValue(null);
            filtroStatoComboBox.setValue(null);
            refreshTableData();
        });

        // --- Ordinamento ---
        Label ordinamentoLabel = new Label("Ordina per:");
        ordinamentoComboBox = new ComboBox<>();
        ordinamentoComboBox.setItems(FXCollections.observableArrayList(
                "Titolo", "Anno (Recente)", "Valutazione (Migliore)"
        ));
        ordinamentoComboBox.setMaxWidth(Double.MAX_VALUE);
        ordinamentoComboBox.setOnAction(e -> gestisciOrdinamento());

        panel.getChildren().addAll(
                addButton, editButton, removeButton,
                new Separator(),
                searchLabel, searchField,
                new Separator(),
                filtroLabel, filtroTipoComboBox,
                filtroGenereLabel, filtroGenereComboBox,
                filtroStatoLabel, filtroStatoComboBox,
                filterButton, clearFilterButton,
                new Separator(),
                ordinamentoLabel, ordinamentoComboBox
        );

        return panel;
    }

    /**
     * Handles "Aggiungi Nuovo Media" button click.
     * (Fix: Added update to observableMediaList)
     */
    private void gestisciAggiungi() {
        MediaDialog dialog = new MediaDialog();
        Optional<Media> result = dialog.showAndWait();

        result.ifPresent(nuovoMedia -> {
            libreria.aggiungiMedia(nuovoMedia); // <-- Chiama FACADE
            observableMediaList.add(nuovoMedia);
            System.out.println("Aggiunto: " + nuovoMedia.getTitolo());
        });
    }

    private void gestisciModifica() {
        Media selectedMedia = tableView.getSelectionModel().getSelectedItem();
        if (selectedMedia == null) {
            mostraErrore("Nessun elemento selezionato", "Seleziona un media da modificare.");
            return;
        }

        MediaDialog dialog = new MediaDialog(selectedMedia);
        Optional<Media> result = dialog.showAndWait();

        result.ifPresent(mediaModificato -> {
            libreria.modificaMedia(); // <-- Chiama FACADE
            tableView.refresh();
            System.out.println("Modificato: " + mediaModificato.getTitolo());
        });
    }


    private void gestisciRimuovi() {
        Media selectedMedia = tableView.getSelectionModel().getSelectedItem();
        if (selectedMedia == null) {
            mostraErrore("Nessun elemento selezionato", "Seleziona un media da rimuovere.");
            return;
        }

        // (Logica Alert invariata)
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma Rimozione");
        alert.setHeaderText("Rimuovere '" + selectedMedia.getTitolo() + "'?");
        alert.setContentText("Questa azione è permanente.");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean rimosso = libreria.rimuoviMedia(selectedMedia.getTitolo()); // <-- Chiama FACADE

            if (rimosso) {
                observableMediaList.remove(selectedMedia);
                System.out.println("Rimosso: " + selectedMedia.getTitolo());
            } else {
                mostraErrore("Errore di Rimozione", "Impossibile rimuovere " + selectedMedia.getTitolo() + ".");
            }
        }
    }

    private void gestisciFiltro() {
        searchField.clear();

        List<Media> risultati = libreria.getElencoCompleto();

        String tipo = filtroTipoComboBox.getValue();
        if (tipo != null && !tipo.isEmpty()) {
            risultati = libreria.filtraPerTipo(tipo, risultati);
        }
        String genere = filtroGenereComboBox.getValue();
        if (genere != null && !genere.isEmpty()) {
            risultati = libreria.filtraPerGenere(genere, risultati);
        }
        Media.StatoVisione stato = filtroStatoComboBox.getValue();
        if (stato != null) {
            risultati = libreria.filtraPerStatoVisione(stato, risultati);
        }
        observableMediaList.setAll(risultati);
        System.out.println("Filtri applicati.");
    }
    private void gestisciOrdinamento() {
        String scelta = ordinamentoComboBox.getValue();
        if (scelta == null) return;

        List<Media> risultatiOrdinati;

        switch (scelta) {
            case "Titolo":
                risultatiOrdinati = libreria.ordinaPerTitolo();
                break;
            case "Anno (Recente)":
                risultatiOrdinati = libreria.ordinaPerAnno();
                break;
            case "Valutazione (Migliore)":
                risultatiOrdinati = libreria.ordinaPerValutazione();
                break;
            default:
                risultatiOrdinati = libreria.getElencoCompleto();
        }

        observableMediaList.setAll(risultatiOrdinati);
    }

    private void gestisciRicerca() {
        String query = searchField.getText();
        List<Media> risultati = libreria.filtraPerTitolo(query, libreria.getElencoCompleto());
        observableMediaList.setAll(risultati);
        // Resetta gli altri filtri per evitare confusione
        filtroTipoComboBox.setValue(null);
        filtroGenereComboBox.setValue(null);
        filtroStatoComboBox.setValue(null);
    }

    private void refreshTableData() {
        if (observableMediaList == null) {
            observableMediaList = FXCollections.observableArrayList();
            tableView.setItems(observableMediaList);
        }

        List<Media> elenco = libreria.getElencoCompleto(); // <-- Chiama FACADE
        observableMediaList.setAll(elenco);
        System.out.println("Tabella aggiornata. Mostrati " + elenco.size() + " elementi.");
    }

    private void mostraErrore(String titolo, String messaggio) {
        System.err.println(titolo + ": " + messaggio);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }

    /**
     * Helper method to set up table columns.
     */
    private void setupTableColumns() {
        TableColumn<Media, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("tipoContenuto"));
        typeColumn.setMinWidth(100);

        TableColumn<Media, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("titolo"));
        titleColumn.setMinWidth(250);
        titleColumn.getStyleClass().add("title-column-cell"); // CSS class

        TableColumn<Media, Integer> ratingColumn = new TableColumn<>("Rating (1-5)");
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("valutazionePersonale"));
        ratingColumn.setMinWidth(100);

        TableColumn<Media, Integer> yearColumn = new TableColumn<>("Anno");
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("annoUscita"));
        yearColumn.setMinWidth(100);


        TableColumn<Media, Media.StatoVisione> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("statoVisione"));
        statusColumn.setMinWidth(120);

        tableView.getColumns().addAll(typeColumn, titleColumn, yearColumn, statusColumn,ratingColumn);
        tableView.getSortOrder().add(titleColumn);
    }


    /**
     * Main method (Java entry point).
     */
    public static void main(String[] args) {
        launch(args);
    }
}