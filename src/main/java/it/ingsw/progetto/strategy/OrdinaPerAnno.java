package it.ingsw.progetto.strategy;

import it.ingsw.progetto.media.Documentario;
import it.ingsw.progetto.media.Film;
import it.ingsw.progetto.media.Media;
import it.ingsw.progetto.media.SerieTv;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class OrdinaPerAnno implements CriterioOrdinamento {

    @Override
    public List<Media> ordina(List<Media> mediaList) {
        return mediaList.stream()
                .sorted(Comparator.comparingInt(this::getAnnoDaMedia).reversed()) // Usa un helper
                .collect(Collectors.toList());
    }

    private int getAnnoDaMedia(Media media) {
        if (media instanceof Film) {
            return ((Film) media).getAnnoUscita();
        } else if (media instanceof Documentario) {
            return ((Documentario) media).getAnnoUscita(); // Ora è unificato
        } else if (media instanceof SerieTv) {
            return ((SerieTv) media).getAnnoUscita(); // Ora è unificato
        }
        return 0; // Default se il tipo non ha anno
    }
}