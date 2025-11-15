package it.ingsw.progetto.strategy;

import it.ingsw.progetto.media.Media;
import java.util.List;
import java.util.stream.Collectors;

public class FiltraPerStatoVisione implements CriterioFiltro {

    private final Media.StatoVisione stato;

    public FiltraPerStatoVisione(Media.StatoVisione stato) {
        this.stato = stato;
    }

    @Override
    public List<Media> applicaFiltro(List<Media> mediaList) {
        if (stato == null) {
            return mediaList;
        }

        return mediaList.stream()
                .filter(media -> media.getStatoVisione() == stato)
                .collect(Collectors.toList());
    }
}