package it.ingsw.progetto.strategy;

import it.ingsw.progetto.media.Media;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Strategia concreta per filtrare (cercare) per titolo.
 * Cerca se il titolo CONTIENE la stringa (case-insensitive).
 */
public class FiltraPerTitolo implements CriterioFiltro {

    private final String queryDiRicerca;

    public FiltraPerTitolo(String queryDiRicerca) {
        this.queryDiRicerca = queryDiRicerca.toLowerCase();
    }

    @Override
    public List<Media> applicaFiltro(List<Media> mediaList) {
        if (queryDiRicerca == null || queryDiRicerca.trim().isEmpty()) {
            return mediaList; // Ritorna tutto se la ricerca è vuota
        }

        return mediaList.stream()
                .filter(media ->
                        media.getTitolo().toLowerCase().contains(queryDiRicerca)
                )
                .collect(Collectors.toList());
    }
}