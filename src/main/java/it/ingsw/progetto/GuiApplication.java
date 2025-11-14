package it.ingsw.progetto;

import it.ingsw.progetto.collection.CollezioneMedia;
import it.ingsw.progetto.dati.GestoreDatiInterface;
import it.ingsw.progetto.dati.GestoreDatiJSON;
import it.ingsw.progetto.media.Media;
import it.ingsw.progetto.strategy.CriterioFiltro;
import it.ingsw.progetto.strategy.FiltraPerTipo;
import it.ingsw.progetto.UI.MediaDialog; // Importa il tuo MediaDialog

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
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

public class GuiApplication extends Application {

    // --- Backend Connection ---
    private static CollezioneMedia collezione;
    private static final String FILE_PATH = "collezione.json";

    // --- GUI Components ---
    private TableView<Media> tableView;
    private ObservableList<Media> observableMediaList;
    private ComboBox<String> filtroTipoComboBox;

    @Override
    public void start(Stage primaryStage) {

        // --- 1. Initialization (Backend) ---
        GestoreDatiInterface gestoreDati = new GestoreDatiJSON(FILE_PATH);
        collezione = CollezioneMedia.getIstanza(gestoreDati);

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

        // --- !!! MODIFICATION: EDIT BUTTON !!! ---
        Button editButton = new Button("Modifica Selezionato...");
        editButton.setMaxWidth(Double.MAX_VALUE);
        editButton.setOnAction(e -> gestisciModifica());

        // --- Remove Button ---
        Button removeButton = new Button("Rimuovi Selezionato");
        removeButton.setMaxWidth(Double.MAX_VALUE);
        removeButton.setOnAction(e -> gestisciRimuovi());

        // --- Filter Controls ---
        Label filtroLabel = new Label("Filtra per Tipo:");
        filtroTipoComboBox = new ComboBox<>();
        filtroTipoComboBox.setItems(FXCollections.observableArrayList(
                "Film", "Documentario", "SerieTv" // Match your class name
        ));
        filtroTipoComboBox.setMaxWidth(Double.MAX_VALUE);

        Button filterButton = new Button("Applica Filtro");
        filterButton.setMaxWidth(Double.MAX_VALUE);
        filterButton.setOnAction(e -> gestisciFiltro());

        Button clearFilterButton = new Button("Pulisci Filtro");
        clearFilterButton.setMaxWidth(Double.MAX_VALUE);
        clearFilterButton.setOnAction(e -> refreshTableData());

        // --- !!! MODIFICATION: ADD ALL BUTTONS !!! ---
        // Add all controls to the panel
        panel.getChildren().addAll(
                addButton, editButton, removeButton, // Ensure 'editButton' is here
                new Separator(),
                filtroLabel, filtroTipoComboBox, filterButton, clearFilterButton
        );

        return panel;
    }

    /**
     * Handles "Aggiungi Nuovo Media" button click.
     * (Fix: Added update to observableMediaList)
     */
    private void gestisciAggiungi() {
        // 1. Create the dialog (Add mode)
        MediaDialog dialog = new MediaDialog();

        // 2. Show dialog and wait for result
        Optional<Media> result = dialog.showAndWait();

        // 3. If user clicked "Salva"
        result.ifPresent(nuovoMedia -> {
            // 4. Call backend
            collezione.aggiungiMedia(nuovoMedia);

            // 5. Update GUI (This line is crucial)
            observableMediaList.add(nuovoMedia);

            System.out.println("Added: " + nuovoMedia.getTitolo());
        });
    }

    /**
     * Handles "Modifica Selezionato" button click.
     */
    private void gestisciModifica() {
        // 1. Get selected item
        Media selectedMedia = tableView.getSelectionModel().getSelectedItem();

        if (selectedMedia == null) {
            mostraErrore("Nessun elemento selezionato", "Seleziona un media da modificare.");
            return;
        }

        // 2. Create dialog (Edit mode)
        MediaDialog dialog = new MediaDialog(selectedMedia);

        // 3. Show dialog and wait for result
        Optional<Media> result = dialog.showAndWait();

        // 4. If user clicked "Salva"
        result.ifPresent(mediaModificato -> {
            // 5. Call backend (just to save)
            collezione.modificaMedia();

            // 6. Update GUI (force table refresh)
            tableView.refresh();
            System.out.println("Modified: " + mediaModificato.getTitolo());
        });
    }


    /**
     * Handles "Rimuovi Selezionato" button click.
     */
    private void gestisciRimuovi() {
        // 1. Get selected item
        Media selectedMedia = tableView.getSelectionModel().getSelectedItem();

        if (selectedMedia == null) {
            mostraErrore("Nessun elemento selezionato", "Seleziona un media da rimuovere.");
            return;
        }

        // 2. Ask for confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma Rimozione");
        alert.setHeaderText("Rimuovere '" + selectedMedia.getTitolo() + "'?");
        alert.setContentText("Questa azione è permanente.");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            // 3. Call backend
            boolean rimosso = collezione.rimuoviMedia(selectedMedia.getTitolo());

            if (rimosso) {
                // 4. Update GUI
                observableMediaList.remove(selectedMedia);
                System.out.println("Removed: " + selectedMedia.getTitolo());
            } else {
                mostraErrore("Errore di Rimozione", "Impossibile rimuovere " + selectedMedia.getTitolo() + ".");
            }
        }
    }

    /**
     * Handles "Applica Filtro" button click.
     * Uses the Strategy Pattern.
     */
    private void gestisciFiltro() {
        String tipoSelezionato = filtroTipoComboBox.getValue();

        if (tipoSelezionato == null || tipoSelezionato.isEmpty()) {
            mostraErrore("Filtro non valido", "Seleziona un tipo dal menu.");
            return;
        }

        // 1. Create the Strategy
        CriterioFiltro criterio = new FiltraPerTipo(tipoSelezionato);

        // 2. Call backend
        List<Media> risultatiFiltrati = collezione.applicaFiltro(criterio);

        // 3. Update GUI
        observableMediaList.setAll(risultatiFiltrati);
        System.out.println("Filter applied: " + tipoSelezionato);
    }

    /**
     * Reloads all data from backend into the table.
     */
    private void refreshTableData() {
        if (observableMediaList == null) {
            observableMediaList = FXCollections.observableArrayList();
            tableView.setItems(observableMediaList);
        }

        // Load full list and update table
        observableMediaList.setAll(collezione.getElencoCompleto());
        System.out.println("Table refreshed. " + observableMediaList.size() + " items.");
    }

    /**
     * Helper to show error popups.
     * @param titolo Title of the error.
     * @param messaggio The error message.
     */
    private void mostraErrore(String titolo, String messaggio) {
        System.err.println(titolo + ": " + messaggio); // Debugging print
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