package media;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;


@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@type" // Field name used to store the concrete type (e.g., "Film")
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Film.class, name = "Film"),
        @JsonSubTypes.Type(value = Documentario.class, name = "Documentario"),
        // SerieTV will be added here when implemented
})

public interface Media {
    // Common enum for the viewing status
    public enum StatoVisione {
        VISTO, DA_VEDERE, IN_VISIONE
    }
    public enum Genere {
        AZIONE, COMMEDIA, DRAMMATICO, HORROR, FANTASCIENZA, THRILLER, ALTRO
    }

    // Common access methods
    String getTitolo();
    int getValutazionePersonale();
    StatoVisione getStatoVisione();
    String getTipoContenuto(); // Used for filtering and identification

    // Common modification methods
    void setValutazionePersonale(int valutazionePersonale);
    void setStatoVisione(StatoVisione statoVisione);

    // Visualization method
    String getDettagliVisualizzazione();
}
