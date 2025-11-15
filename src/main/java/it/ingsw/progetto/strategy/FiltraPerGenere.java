package it.ingsw.progetto.strategy;

import it.ingsw.progetto.media.Documentario;
import it.ingsw.progetto.media.Film;
import it.ingsw.progetto.media.Media;
import it.ingsw.progetto.media.SerieTv;

import java.util.List;
import java.util.stream.Collectors;


public class FiltraPerGenere implements CriterioFiltro {

    private final String genereDaFiltrare;

    public FiltraPerGenere(String genereDaFiltrare) {
        this.genereDaFiltrare = genereDaFiltrare.toUpperCase();
    }

    @Override
    public List<Media> applicaFiltro(List<Media> mediaList) {
        return mediaList.stream()
                .filter(this::controllaGenere)
                .collect(Collectors.toList());
    }

    private boolean controllaGenere(Media media) {
        try {
            if (media instanceof Film) {
                Film.Genere genere = ((Film) media).getGenere();
                return genere != null && genere.name().equals(genereDaFiltrare);
            }
            if (media instanceof Documentario) {
                Documentario.GenereDocumentario genere = ((Documentario) media).getGenere();
                return genere != null && genere.name().equals(genereDaFiltrare);
            }
            if (media instanceof SerieTv) {
                SerieTv.GenereSerie genere = ((SerieTv) media).getGenere();
                return genere != null && genere.name().equals(genereDaFiltrare);
            }
        } catch (Exception e) {
            System.err.println("Errore nel matching genere: " + e.getMessage());
            return false;
        }
        return false;
    }
}