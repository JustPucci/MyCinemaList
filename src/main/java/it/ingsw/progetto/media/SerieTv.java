package it.ingsw.progetto.media;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Concrete class representing a TV series. Implements Media.
 */
public class SerieTv implements Media {

    // Specific enum for TV Series genres (can be shared with Film or specific)
    public enum GenereSerie {
        FANTASY, CRIME, SITCOM, DRAMEDY, SCI_FI, ALTRO
    }

    // Specific attributes
    private String titolo;
    private String regista; // Or showrunner/creator
    private int numeroStagioni;
    private int episodiVisti;
    private GenereSerie genere;

    // Common attributes (from Media)
    private int valutazionePersonale;
    private StatoVisione statoVisione;
    private int annoUscita;

    // Default constructor needed by Jackson for deserialization
    public SerieTv() {
        // Jackson needs this constructor
    }

    /**
     * Full constructor with Jackson annotations for creation/deserialization.
     */
    @JsonCreator
    public SerieTv(
            @JsonProperty("titolo") String titolo,
            @JsonProperty("regista") String regista,
            @JsonProperty("numeroStagioni") int numeroStagioni,
            @JsonProperty("episodiVisti") int episodiVisti,
            @JsonProperty("genere") GenereSerie genere,
            @JsonProperty("valutazionePersonale") int valutazionePersonale,
            @JsonProperty("annoUscita") int annoUscita,
            @JsonProperty("statoVisione") StatoVisione statoVisione) {

        this.titolo = titolo;
        this.regista = regista;
        this.numeroStagioni = numeroStagioni;
        this.episodiVisti = episodiVisti;
        this.genere = genere;
        setValutazionePersonale(valutazionePersonale);
        this.statoVisione = statoVisione;
        this.annoUscita = annoUscita;
    }

    // --- Getters ---
    @Override
    public String getTitolo() { return titolo; }
    public String getRegista() { return regista; }
    public int getNumeroStagioni() { return numeroStagioni; }
    public int getEpisodiVisti() { return episodiVisti; }

    public int getAnnoUscita() {
        return annoUscita;
    }

    public GenereSerie getGenere() { return genere; }

    @Override
    public int getValutazionePersonale() { return valutazionePersonale; }

    @Override
    public StatoVisione getStatoVisione() { return statoVisione; }

    @Override
    @JsonIgnore
    public String getTipoContenuto() { return "SerieTV"; }

    // --- Setters ---
    public void setTitolo(String titolo) { this.titolo = titolo; }
    public void setRegista(String regista) { this.regista = regista; }
    public void setNumeroStagioni(int numeroStagioni) { this.numeroStagioni = numeroStagioni; }
    public void setEpisodiVisti(int episodiVisti) { this.episodiVisti = episodiVisti; }
    public void setGenere(GenereSerie genere) { this.genere = genere; }

    public void setAnnoUscita(int annoUscita) {
        this.annoUscita = annoUscita;
    }

    @Override
    public void setValutazionePersonale(int valutazionePersonale) {
        if (valutazionePersonale < 1 || valutazionePersonale > 5) {
            System.err.println("ERROR: Rating validation failed.");
            throw new IllegalArgumentException("Rating out of bounds.");
        }
        this.valutazionePersonale = valutazionePersonale;
    }

    @Override
    public void setStatoVisione(StatoVisione statoVisione) {
        this.statoVisione = statoVisione;
    }

    // --- Utility Methods ---

    @Override
    @JsonIgnore
    public String getDettagliVisualizzazione() {
        return String.format(
                "| Type: %-12s | Title: %-30s | Seasons: %2d | Watched: %3d eps | Genre: %-12s | Rating: %d/5 | Status: %-10s |",
                getTipoContenuto(), titolo, numeroStagioni, episodiVisti, genere.name(), valutazionePersonale, statoVisione.name().replace("_", " ")
        );
    }
}
