package it.ingsw.progetto.media;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;


@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Film.class, name = "Film"),
        @JsonSubTypes.Type(value = Documentario.class, name = "Documentario"),
        @JsonSubTypes.Type(value = SerieTv.class, name = "SerieTv"),
})

public interface Media {

    public enum StatoVisione {
        VISTO, DA_VEDERE, IN_VISIONE
    }
    public enum Genere {
        AZIONE, COMMEDIA, DRAMMATICO, HORROR, FANTASCIENZA, THRILLER
    }

    String getTitolo();
    int getValutazionePersonale();
    StatoVisione getStatoVisione();
    @JsonIgnore
    String getTipoContenuto();
    void setValutazionePersonale(int valutazionePersonale);
    void setStatoVisione(StatoVisione statoVisione);


    @JsonIgnore
    String getDettagliVisualizzazione();
}
