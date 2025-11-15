package it.ingsw.progetto.collection;

import it.ingsw.progetto.media.Media;
import it.ingsw.progetto.strategy.*;
import java.util.List;

public class LibreriaFacade {

    private final CollezioneMedia collezione; // Il Singleton

    public LibreriaFacade(CollezioneMedia collezione) {
        this.collezione = collezione;
    }

    public List<Media> getElencoCompleto() {
        return collezione.getElencoCompleto();
    }

    public void aggiungiMedia(Media media) {
        collezione.aggiungiMedia(media);
    }

    public boolean rimuoviMedia(String titolo) {
        return collezione.rimuoviMedia(titolo);
    }

    public void modificaMedia() {
        collezione.modificaMedia();
    }
    // --- Filtri ---

    // --- Filtri ---
    public List<Media> filtraPerTipo(String tipo, List<Media> listaAttuale) {
        CriterioFiltro criterio = new FiltraPerTipo(tipo);
        return collezione.applicaFiltro(criterio, listaAttuale);
    }

    public List<Media> filtraPerTitolo(String query, List<Media> listaAttuale) {
        CriterioFiltro criterio = new FiltraPerTitolo(query);
        return collezione.applicaFiltro(criterio, listaAttuale);
    }

    public List<Media> filtraPerGenere(String genere, List<Media> listaAttuale) {
        CriterioFiltro criterio = new FiltraPerGenere(genere);
        return collezione.applicaFiltro(criterio, listaAttuale);
    }

    public List<Media> filtraPerStatoVisione(Media.StatoVisione stato, List<Media> listaAttuale) {
        CriterioFiltro criterio = new FiltraPerStatoVisione(stato);
        return collezione.applicaFiltro(criterio, listaAttuale);
    }

    // --- Ordinamenti ---

    public List<Media> ordinaPerTitolo() {
        CriterioOrdinamento criterio = new OrdinaPerTitolo();
        return collezione.ordina(criterio);
    }

    public List<Media> ordinaPerAnno() {
        CriterioOrdinamento criterio = new OrdinaPerAnno();
        return collezione.ordina(criterio);
    }

    public List<Media> ordinaPerValutazione() {
        CriterioOrdinamento criterio = new OrdinaPerValutazione();
        return collezione.ordina(criterio);
    }

}