package it.ingsw.progetto.strategy;

import it.ingsw.progetto.media.Media; // Import the base Media interface
import java.util.List;


public interface CriterioOrdinamento {

    /**
     * @param mediaList la lista da ordinare
     * @return la lista ordinata
     */
    List<Media> ordina(List<Media> mediaList);
}