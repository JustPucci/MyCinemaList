package it.ingsw.progetto.UI;

import it.ingsw.progetto.media.Documentario;
import it.ingsw.progetto.media.Film;
import it.ingsw.progetto.media.Media;
import it.ingsw.progetto.media.SerieTv;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane; // Usiamo una griglia per il modulo

import java.util.Optional;

/**
 * Un Dialog (pop-up) personalizzato per aggiungere o modificare un Media.
 * Incapsula tutta la logica del modulo di inserimento.
 */
public class MediaDialog extends Dialog<Media> {

    // Componenti del modulo comuni
    private ComboBox<String> tipoMediaComboBox;
    private TextField titoloField;
    private ComboBox<Media.StatoVisione> statoVisioneComboBox;
    private Spinner<Integer> valutazioneSpinner; // Spinner per numeri (1-5)

    // Componenti specifici per TIPO (dinamici)
    private TextField registaField;
    private ComboBox<Film.Genere> genereFilmComboBox;
    private TextField soggettoDocField;
    private ComboBox<Documentario.GenereDocumentario> genereDocComboBox;
    private TextField registaSerieField;
    private Spinner<Integer> stagioniSpinner;
    private ComboBox<SerieTv.GenereSerie> genereSerieComboBox;

    // Layout per i campi dinamici
    private GridPane grid;

    /**
     * Costruttore: crea il modulo di dialogo.
     */
    public MediaDialog() {
        this.setTitle("Aggiungi Nuovo Media");
        this.setHeaderText("Seleziona il tipo e compila i campi.");

        // --- Setup dei Pulsanti (OK/Annulla) ---
        ButtonType saveButtonType = new ButtonType("Salva", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // --- Creazione Layout (GridPane) ---
        grid = new GridPane();
        grid.setHgap(10); // Spaziatura
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // --- Campo 1: Tipo di Media (il selettore) ---
        tipoMediaComboBox = new ComboBox<>();
        tipoMediaComboBox.setItems(FXCollections.observableArrayList("Film", "Documentario", "SerieTV"));

        grid.add(new Label("Tipo:"), 0, 0);
        grid.add(tipoMediaComboBox, 1, 0);

        // --- Listener Dinamico ---
        // Quando il tipo cambia, ricostruisci il modulo
        tipoMediaComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            costruisciModuloDinamico(newVal);
        });

        // Imposta il contenuto del dialogo
        this.getDialogPane().setContent(grid);

        // --- Logica di Salvataggio ---
        // Converte l'input in un oggetto Media quando "Salva" è premuto
        this.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return creaMediaDalModulo();
            }
            return null; // Annulla o chiudi
        });
    }

    /**
     * Ricostruisce dinamicamente i campi del modulo in base al tipo selezionato.
     */
    private void costruisciModuloDinamico(String tipo) {
        // Pulisce la griglia (mantenendo il selettore del tipo)
        grid.getChildren().removeIf(node -> GridPane.getRowIndex(node) > 0);

        if (tipo == null) return;

        // --- Campi Comuni (aggiunti sempre) ---
        titoloField = new TextField();
        statoVisioneComboBox = new ComboBox<>(FXCollections.observableArrayList(Media.StatoVisione.values()));
        valutazioneSpinner = new Spinner<>(1, 5, 3); // Min, Max, Default

        grid.add(new Label("Titolo:"), 0, 1);
        grid.add(titoloField, 1, 1);
        grid.add(new Label("Stato Visione:"), 0, 2);
        grid.add(statoVisioneComboBox, 1, 2);
        grid.add(new Label("Valutazione:"), 0, 3);
        grid.add(valutazioneSpinner, 1, 3);

        // --- Campi Specifici (dinamici) ---
        switch (tipo) {
            case "Film":
                registaField = new TextField();
                genereFilmComboBox = new ComboBox<>(FXCollections.observableArrayList(Film.Genere.values()));
                grid.add(new Label("Regista:"), 0, 4);
                grid.add(registaField, 1, 4);
                grid.add(new Label("Genere:"), 0, 5);
                grid.add(genereFilmComboBox, 1, 5);
                break;
            case "Documentario":
                soggettoDocField = new TextField();
                genereDocComboBox = new ComboBox<>(FXCollections.observableArrayList(Documentario.GenereDocumentario.values()));
                grid.add(new Label("Soggetto:"), 0, 4);
                grid.add(soggettoDocField, 1, 4);
                grid.add(new Label("Genere:"), 0, 5);
                grid.add(genereDocComboBox, 1, 5);
                break;
            case "SerieTV":
                registaSerieField = new TextField();
                stagioniSpinner = new Spinner<>(1, 50, 1);
                genereSerieComboBox = new ComboBox<>(FXCollections.observableArrayList(SerieTv.GenereSerie.values()));
                grid.add(new Label("Regista/Creator:"), 0, 4);
                grid.add(registaSerieField, 1, 4);
                grid.add(new Label("Stagioni:"), 0, 5);
                grid.add(stagioniSpinner, 1, 5);
                grid.add(new Label("Genere:"), 0, 6);
                grid.add(genereSerieComboBox, 1, 6);
                break;
        }

        // Adatta la finestra alla nuova dimensione
        this.getDialogPane().getScene().getWindow().sizeToScene();
    }

    /**
     * Legge i dati dal modulo e crea l'oggetto Media appropriato.
     * Chiamato dal 'setResultConverter' quando si preme "Salva".
     */
    private Media creaMediaDalModulo() {
        try {
            // Dati comuni
            String tipo = tipoMediaComboBox.getValue();
            String titolo = titoloField.getText();
            Media.StatoVisione stato = statoVisioneComboBox.getValue();
            int valutazione = valutazioneSpinner.getValue();

            // Validazione base
            if (titolo == null || titolo.trim().isEmpty() || stato == null) {
                mostraErrore("Dati mancanti", "Titolo e Stato Visione sono obbligatori.");
                return null;
            }

            switch (tipo) {
                case "Film":
                    return new Film(
                            titolo,
                            registaField.getText(),
                            2024, // TODO: Aggiungere campo Anno!
                            genereFilmComboBox.getValue(),
                            valutazione,
                            stato
                    );
                case "Documentario":
                    return new Documentario(
                            titolo,
                            soggettoDocField.getText(),
                            2024, // TODO: Aggiungere campo Anno!
                            genereDocComboBox.getValue(),
                            valutazione,
                            stato
                    );
                case "SerieTV":
                    return new SerieTv(
                            titolo,
                            registaSerieField.getText(),
                            stagioniSpinner.getValue(),
                            0, // TODO: Aggiungere campo Episodi Visti!
                            genereSerieComboBox.getValue(),
                            valutazione,
                            stato
                    );
            }
        } catch (Exception e) {
            System.err.println("Errore durante la creazione del media: " + e.getMessage());
            mostraErrore("Errore di Creazione", "Controlla che tutti i campi siano corretti.");
        }
        return null; // Fallimento
    }

    /**
     * Helper per mostrare un pop-up di errore.
     */
    private void mostraErrore(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }
}