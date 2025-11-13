package it.ingsw.progetto;

import it.ingsw.progetto.collection.CollezioneMedia;
import it.ingsw.progetto.dati.GestoreDatiInterface;
import it.ingsw.progetto.dati.GestoreDatiJSON;
import it.ingsw.progetto.media.Media;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory; // Collega colonne ai getter
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;


//Classe principale della GUI (JavaFX).
public class GuiApplication extends Application {

    // --- Collegamento Backend ---
    private static CollezioneMedia collezione;
    private static final String FILE_PATH = "collezione.json";

    // --- Componenti GUI ---
    private TableView<Media> tableView; // Tabella principale

    // Lista "osservabile" per la GUI
    private ObservableList<Media> observableMediaList;

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
        Label titleLabel = new Label("My Media Collection Manager");
        titleLabel.setFont(new Font("Arial", 24));
        rootLayout.setTop(titleLabel);
        BorderPane.setMargin(titleLabel, new Insets(0, 0, 10, 0));

        // 2b. Tabella
        tableView = new TableView<>();
        setupTableColumns();

        // 2c. Caricamento Dati
        // Converte la List del backend in ObservableList per la GUI
        observableMediaList = FXCollections.observableArrayList(collezione.getElencoCompleto());

        // Collega la lista alla tabella
        tableView.setItems(observableMediaList);

        rootLayout.setCenter(tableView);

        // TODO: 2d. Pulsanti (Basso/Destra)

        // --- 3. Setup Scena e Finestra ---
        Scene scene = new Scene(rootLayout, 1024, 768); // Dimensioni finestra

        primaryStage.setTitle("My Media Collection Manager");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setupTableColumns() {

        TableColumn<Media, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("tipoContenuto"));
        typeColumn.setMinWidth(100);

        TableColumn<Media, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("titolo"));
        titleColumn.setMinWidth(250);

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