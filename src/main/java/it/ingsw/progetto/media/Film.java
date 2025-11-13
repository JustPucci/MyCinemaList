package it.ingsw.progetto.media;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Concrete class representing a feature film. Implements Media.
 */
public class Film implements Media {

    // Specific attributes
    private String titolo;
    private String regista;
    private int annoUscita;
    private Genere genere;

    // Common attributes (from Media)
    private int valutazionePersonale;
    private StatoVisione statoVisione;

    // Default constructor needed by Jackson for deserialization
    public Film() {
        // Jackson needs this constructor
    }

    /**
     * Full constructor with Jackson annotations for creation/deserialization.
     */
    @JsonCreator // Instructs Jackson to use this constructor for creation
    public Film(
            @JsonProperty("titolo") String titolo,
            @JsonProperty("regista") String regista,
            @JsonProperty("annoUscita") int annoUscita,
            @JsonProperty("genere") Genere genere,
            @JsonProperty("valutazionePersonale") int valutazionePersonale,
            @JsonProperty("statoVisione") StatoVisione statoVisione) {

        this.titolo = titolo;
        this.regista = regista;
        this.annoUscita = annoUscita;
        this.genere = genere;
        setValutazionePersonale(valutazionePersonale); // Use setter for validation
        this.statoVisione = statoVisione;
    }

    // --- Getters ---
    @Override
    public String getTitolo() { return titolo; }
    public String getRegista() { return regista; }
    public int getAnnoUscita() { return annoUscita; }
    public Genere getGenere() { return genere; }

    @Override
    public int getValutazionePersonale() { return valutazionePersonale; }

    @Override
    public StatoVisione getStatoVisione() { return statoVisione; }

    @Override
    public String getTipoContenuto() { return "Film"; }

    // --- Setters ---
    public void setTitolo(String titolo) { this.titolo = titolo; }
    public void setRegista(String regista) { this.regista = regista; }
    public void setAnnoUscita(int annoUscita) { this.annoUscita = annoUscita; }
    public void setGenere(Genere genere) { this.genere = genere; }

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
    public String getDettagliVisualizzazione() {
        return String.format(
                "| Type: %-12s | Title: %-30s | Director: %-15s | Year: %4d | Genre: %-12s | Rating: %d/5 | Status: %-10s |",
                getTipoContenuto(), titolo, regista, annoUscita, genere.name(), valutazionePersonale, statoVisione.name().replace("_", " ")
        );
    }
}