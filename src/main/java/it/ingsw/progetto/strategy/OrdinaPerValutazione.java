package it.ingsw.progetto.strategy;

import it.ingsw.progetto.media.Media;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class OrdinaPerValutazione implements CriterioOrdinamento {

    @Override
    public List<Media> ordina(List<Media> mediaList) {
        // La valutazione è un attributo comune dell'interfaccia Media
        return mediaList.stream()
                .sorted(Comparator.comparingInt(Media::getValutazionePersonale).reversed())
                .collect(Collectors.toList());
    }
}