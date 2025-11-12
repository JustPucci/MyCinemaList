package strategy;

import media.Media; // Import the base Media interface
import java.util.List;


public interface CriterioFiltro {

    /**
     * Filtrare la collezione
     * @param mediaList la lista da filtrare
     * @return la lista filtrata
     */
    List<Media> applicaFiltro(List<Media> mediaList);
}