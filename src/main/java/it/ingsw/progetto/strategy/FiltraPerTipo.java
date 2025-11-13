package it.ingsw.progetto.strategy;

import it.ingsw.progetto.media.Media;
import java.util.List;
import java.util.stream.Collectors;

public class FiltraPerTipo implements CriterioFiltro {


    private final String tipoDaFiltrare;


    public FiltraPerTipo(String tipoDaFiltrare) {
        this.tipoDaFiltrare = tipoDaFiltrare;
    }

    @Override
    public List<Media> applicaFiltro(List<Media> mediaList) {
        return mediaList.stream()
                .filter(media -> media.getTipoContenuto().equalsIgnoreCase(tipoDaFiltrare))
                .collect(Collectors.toList());
    }
}