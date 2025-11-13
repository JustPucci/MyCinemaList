package it.ingsw.progetto.media;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Documentario implements Media {

    // Specific enum for Documentary genres
    public enum GenereDocumentario {
        NATURA, STORIA, BIOGRAFICO, SCIENZA, VIAGGI, ALTRO
    }

    // Specific attributes
    private String titolo;
    private String soggettoPrincipale;
    private int dataPubblicazione;
    private GenereDocumentario genere;

    // Common attributes (from Media)
    private int valutazionePersonale;
    private StatoVisione statoVisione;

    // Default constructor needed by Jackson for deserialization
    public Documentario() {
        // Jackson needs this constructor
    }

    @JsonCreator
    public Documentario(
            @JsonProperty("titolo") String titolo,
            @JsonProperty("soggettoPrincipale") String soggettoPrincipale,
            @JsonProperty("dataPubblicazione") int dataPubblicazione,
            @JsonProperty("genere") GenereDocumentario genere,
            @JsonProperty("valutazionePersonale") int valutazionePersonale,
            @JsonProperty("statoVisione") StatoVisione statoVisione) {

        this.titolo = titolo;
        this.soggettoPrincipale = soggettoPrincipale;
        this.dataPubblicazione = dataPubblicazione;
        this.genere = genere;
        setValutazionePersonale(valutazionePersonale);
        this.statoVisione = statoVisione;
    }

    // --- Getters ---
    @Override
    public String getTitolo() { return titolo; }
    public String getSoggettoPrincipale() { return soggettoPrincipale; }
    public int getDataPubblicazione() { return dataPubblicazione; }
    public GenereDocumentario getGenere() { return genere; }

    @Override
    public int getValutazionePersonale() { return valutazionePersonale; }

    @Override
    public StatoVisione getStatoVisione() { return statoVisione; }

    @Override
    @JsonIgnore
    public String getTipoContenuto() { return "Documentario"; }

    // --- Setters ---
    public void setTitolo(String titolo) { this.titolo = titolo; }
    public void setSoggettoPrincipale(String soggettoPrincipale) { this.soggettoPrincipale = soggettoPrincipale; }
    public void setDataPubblicazione(int dataPubblicazione) { this.dataPubblicazione = dataPubblicazione; }
    public void setGenere(GenereDocumentario genere) { this.genere = genere; }

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
                "| Type: %-12s | Title: %-30s | Subject: %-15s | Year: %4d | Genre: %-12s | Rating: %d/5 | Status: %-10s |",
                getTipoContenuto(), titolo, soggettoPrincipale, dataPubblicazione, genere.name(), valutazionePersonale, statoVisione.name().replace("_", " ")
        );
    }
}