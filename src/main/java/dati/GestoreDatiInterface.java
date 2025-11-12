package dati;

import media.Media;

import java.io.IOException;
import java.util.List;

public interface GestoreDatiInterface {
    //ADAPTER

    /**
     * Salva la lista dei media
     * @param elencoMedia lista dei media
     * @throws IOException errore di scrittura
     */

    void salva(List<Media> elencoMedia) throws IOException;

    /**
     * Carica la lista dei media
     * @return la lista dei media
     * @throws IOException errore di lettura
     */
    List<Media> carica() throws IOException;
}
