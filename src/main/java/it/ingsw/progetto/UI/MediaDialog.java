package it.ingsw.progetto.UI;

import it.ingsw.progetto.media.Documentario;
import it.ingsw.progetto.media.Film;
import it.ingsw.progetto.media.Media;
import it.ingsw.progetto.media.SerieTv;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;


public class MediaDialog extends Dialog<Media> {

    // --- Variabile per salvare l'oggetto in modifica ---
    private Media mediaDaModificare;

    // Componenti del modulo comuni
    private ComboBox<String> tipoMediaComboBox;
    private TextField titoloField;
    private ComboBox<Media.StatoVisione> statoVisioneComboBox;
    private Spinner<Integer> valutazioneSpinner;
    private Spinner<Integer> annoSpinner;

    // Componenti specifici per TIPO (dinamici)
    private TextField registaField;
    private ComboBox<Film.Genere> genereFilmComboBox;

    private TextField soggettoDocField;
    private ComboBox<Documentario.GenereDocumentario> genereDocComboBox;

    private TextField registaSerieField;
    private Spinner<Integer> stagioniSpinner;
    private Spinner<Integer> episodiVistiSpinner;
    private ComboBox<SerieTv.GenereSerie> genereSerieComboBox;

    private GridPane grid;

    /**
     * Costruttore 1: Per Aggiungere (Nuovo Media)
     */
    public MediaDialog() {
        this.mediaDaModificare = null;
        setupDialog();
        costruisciModuloDinamico(null);
    }

    /**
     * Costruttore 2: Per Modificare (Media Esistente)
     */
    public MediaDialog(Media mediaToEdit) {
        this.mediaDaModificare = mediaToEdit;
        setupDialog();
        popolaModulo(mediaToEdit);
    }

    /**
     * Logica comune per impostare il Dialog (pulsanti, layout, ecc.)
     */
    private void setupDialog() {
        this.setTitle(mediaDaModificare == null ? "Aggiungi Nuovo Media" : "Modifica Media");
        this.setHeaderText(mediaDaModificare == null ? "Seleziona il tipo e compila i campi." : "Modifica i campi.");

        ButtonType saveButtonType = new ButtonType("Salva", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        tipoMediaComboBox = new ComboBox<>();
        tipoMediaComboBox.setItems(FXCollections.observableArrayList("Film", "Documentario", "SerieTv"));

        if (mediaDaModificare != null) {
            tipoMediaComboBox.setDisable(true);
        }

        grid.add(new Label("Tipo:"), 0, 0);
        grid.add(tipoMediaComboBox, 1, 0);

        // Listener Dinamico (solo per modalità "Aggiungi")
        if (mediaDaModificare == null) {
            tipoMediaComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
                costruisciModuloDinamico(newVal);
            });
        }

        this.getDialogPane().setContent(grid);

        // Logica di Salvataggio
        this.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return (mediaDaModificare != null) ? aggiornaMediaDalModulo() : creaMediaDalModulo();
            }
            return null;
        });
    }

    /**
     * Pre-compila il modulo con i dati di un media esistente.
     */
    private void popolaModulo(Media media) {
        String tipo = media.getTipoContenuto();
        tipoMediaComboBox.setValue(tipo.equals("SerieTV") ? "SerieTv" : tipo);

        costruisciModuloDinamico(tipo);

        // Imposta i valori comuni
        titoloField.setText(media.getTitolo());
        annoSpinner.getValueFactory().setValue(getAnnoDaMedia(media)); // Usa helper
        statoVisioneComboBox.setValue(media.getStatoVisione());
        valutazioneSpinner.getValueFactory().setValue(media.getValutazionePersonale());

        // Imposta i valori specifici (usando il cast)
        switch (tipo) {
            case "Film":
                Film film = (Film) media;
                registaField.setText(film.getRegista());
                genereFilmComboBox.setValue(film.getGenere());
                break;
            case "Documentario":
                Documentario doc = (Documentario) media;
                soggettoDocField.setText(doc.getSoggettoPrincipale());
                genereDocComboBox.setValue(doc.getGenere());
                break;
            case "SerieTV":
            case "SerieTv":
                SerieTv serie = (SerieTv) media;
                registaSerieField.setText(serie.getRegista());
                stagioniSpinner.getValueFactory().setValue(serie.getNumeroStagioni());
                episodiVistiSpinner.getValueFactory().setValue(serie.getEpisodiVisti());
                genereSerieComboBox.setValue(serie.getGenere());
                break;
        }
    }

    /**
     * Ricostruisce dinamicamente i campi del modulo (Layout Corretto).
     */
    private void costruisciModuloDinamico(String tipo) {
        grid.getChildren().removeIf(node -> GridPane.getRowIndex(node) > 0);
        if (tipo == null) return;

        // --- Campi Comuni  ---
        titoloField = new TextField();
        annoSpinner = new Spinner<>(1900, 2030, 2024);
        statoVisioneComboBox = new ComboBox<>(FXCollections.observableArrayList(Media.StatoVisione.values()));
        valutazioneSpinner = new Spinner<>(1, 5, 3); // Inizializzato

        grid.add(new Label("Titolo:"), 0, 1);
        grid.add(titoloField, 1, 1);

        grid.add(new Label("Anno:"), 0, 2);
        grid.add(annoSpinner, 1, 2);

        grid.add(new Label("Stato Visione:"), 0, 3);
        grid.add(statoVisioneComboBox, 1, 3);

        grid.add(new Label("Valutazione:"), 0, 4);
        grid.add(valutazioneSpinner, 1, 4); // <-- CORRETTO

        int rigaCorrente = 5; // Prossima riga libera

        // --- Campi Specifici (Senza Anno) ---
        switch (tipo) {
            case "Film":
                registaField = new TextField();
                genereFilmComboBox = new ComboBox<>(FXCollections.observableArrayList(Film.Genere.values()));
                grid.add(new Label("Regista:"), 0, rigaCorrente);
                grid.add(registaField, 1, rigaCorrente);
                grid.add(new Label("Genere:"), 0, rigaCorrente + 1);
                grid.add(genereFilmComboBox, 1, rigaCorrente + 1);
                break;
            case "Documentario":
                soggettoDocField = new TextField();
                genereDocComboBox = new ComboBox<>(FXCollections.observableArrayList(Documentario.GenereDocumentario.values()));
                grid.add(new Label("Soggetto:"), 0, rigaCorrente);
                grid.add(soggettoDocField, 1, rigaCorrente);
                grid.add(new Label("Genere:"), 0, rigaCorrente + 1);
                grid.add(genereDocComboBox, 1, rigaCorrente + 1);
                break;
            case "SerieTv":
            case "SerieTV":
                registaSerieField = new TextField();
                stagioniSpinner = new Spinner<>(1, 50, 1);
                episodiVistiSpinner = new Spinner<>(0, 999, 0);
                genereSerieComboBox = new ComboBox<>(FXCollections.observableArrayList(SerieTv.GenereSerie.values()));
                grid.add(new Label("Regista/Creator:"), 0, rigaCorrente);
                grid.add(registaSerieField, 1, rigaCorrente);
                grid.add(new Label("Stagioni:"), 0, rigaCorrente + 1);
                grid.add(stagioniSpinner, 1, rigaCorrente + 1);
                grid.add(new Label("Episodi Visti:"), 0, rigaCorrente + 2);
                grid.add(episodiVistiSpinner, 1, rigaCorrente + 2);
                grid.add(new Label("Genere:"), 0, rigaCorrente + 3);
                grid.add(genereSerieComboBox, 1, rigaCorrente + 3);
                break;
        }

        this.getDialogPane().getScene().getWindow().sizeToScene();
    }

    /**
     * Applica le modifiche all'oggetto mediaDaModificare.
     */
    private Media aggiornaMediaDalModulo() {
        try {
            // Dati comuni
            String titolo = titoloField.getText();
            Media.StatoVisione stato = statoVisioneComboBox.getValue();
            int valutazione = valutazioneSpinner.getValue();
            int anno = annoSpinner.getValue();

            if (titolo == null || titolo.trim().isEmpty() || stato == null) {
                mostraErrore("Dati mancanti", "Titolo e Stato Visione sono obbligatori.");
                return null;
            }

            switch (mediaDaModificare.getTipoContenuto()) {
                case "Film":
                    Film film = (Film) mediaDaModificare;
                    film.setTitolo(titolo);
                    film.setStatoVisione(stato);
                    film.setValutazionePersonale(valutazione);
                    film.setAnnoUscita(anno);
                    film.setRegista(registaField.getText());
                    film.setGenere(genereFilmComboBox.getValue());
                    break;
                case "Documentario":
                    Documentario doc = (Documentario) mediaDaModificare;
                    doc.setTitolo(titolo);
                    doc.setStatoVisione(stato);
                    doc.setValutazionePersonale(valutazione);
                    doc.setAnnoUscita(anno);
                    doc.setSoggettoPrincipale(soggettoDocField.getText());
                    doc.setGenere(genereDocComboBox.getValue());
                    break;
                case "SerieTV":
                case "SerieTv":
                    SerieTv serie = (SerieTv) mediaDaModificare;
                    serie.setTitolo(titolo);
                    serie.setStatoVisione(stato);
                    serie.setValutazionePersonale(valutazione);
                    serie.setAnnoUscita(anno);
                    serie.setRegista(registaSerieField.getText());
                    serie.setNumeroStagioni(stagioniSpinner.getValue());
                    serie.setEpisodiVisti(episodiVistiSpinner.getValue());
                    serie.setGenere(genereSerieComboBox.getValue());
                    break;
            }
            return mediaDaModificare;

        } catch (Exception e) {
            mostraErrore("Errore di Modifica", "Controlla che tutti i campi siano corretti.");
        }
        return null;
    }

    /**
     * CREA un nuovo oggetto Media dal modulo
     */
    private Media creaMediaDalModulo() {
        try {
            // Dati comuni
            String tipo = tipoMediaComboBox.getValue();
            String titolo = titoloField.getText();
            int anno = annoSpinner.getValue();
            Media.StatoVisione stato = statoVisioneComboBox.getValue();
            int valutazione = valutazioneSpinner.getValue();

            if (titolo == null || titolo.trim().isEmpty() || stato == null || tipo == null) {
                mostraErrore("Dati mancanti", "Tipo, Titolo e Stato Visione sono obbligatori.");
                return null;
            }

            switch (tipo) {
                case "Film":
                    return new Film(
                            titolo,
                            registaField.getText(),
                            anno,
                            genereFilmComboBox.getValue(),
                            valutazione,
                            stato
                    );
                case "Documentario":
                    return new Documentario(
                            titolo,
                            soggettoDocField.getText(),
                            anno,
                            genereDocComboBox.getValue(),
                            valutazione,
                            stato
                    );
                case "SerieTv":
                case "SerieTV":
                    return new SerieTv(
                            titolo,
                            registaSerieField.getText(),
                            stagioniSpinner.getValue(),
                            episodiVistiSpinner.getValue(),
                            genereSerieComboBox.getValue(),
                            valutazione,
                            anno,
                            stato
                    );
            }
        } catch (Exception e) {
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

    /**
     * Helper per ottenere l'anno in modo polimorfico.
     */
    private int getAnnoDaMedia(Media media) {
        if (media instanceof Film) {
            return ((Film) media).getAnnoUscita();
        } else if (media instanceof Documentario) {
            return ((Documentario) media).getAnnoUscita();
        } else if (media instanceof SerieTv) {
            return ((SerieTv) media).getAnnoUscita();
        }
        return 1900; // Default
    }
}