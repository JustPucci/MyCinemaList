package collection;

import dati.GestoreDatiInterface; // ADAPTER
import media.Media;
import strategy.CriterioFiltro;
import strategy.CriterioOrdinamento;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * GESTISCE la collezione di media
 * SINGLETON
 */
public class CollezioneMedia {

    private static CollezioneMedia istanza;

    private List<Media> elencoMedia;
    private final GestoreDatiInterface gestoreDati;

    /**
     * 2. Singleton: costruttore privato
     * Inizializza la collezione tramite l'adapter
     * @param gestoreDati Adapter.
     */
    private CollezioneMedia(GestoreDatiInterface gestoreDati) {
        this.gestoreDati = gestoreDati;
        this.elencoMedia = new ArrayList<>();
        caricaDati(); // Load data on initialization
    }

    /**
     * 3. Singleton: metodo get publico
     * Initializza il singleton
     * @param gestoreDati Adapter
     * @return La SINGOLA istanza della collezione
     */
    public static CollezioneMedia getIstanza(GestoreDatiInterface gestoreDati) {
        if (istanza == null) {
            istanza = new CollezioneMedia(gestoreDati);
        }
        return istanza;
    }

    // Metodi dell'ADAPTER

    private void caricaDati() {
        try {
            this.elencoMedia = gestoreDati.carica();
        } catch (IOException e) {
            System.err.println("ERROR: Failed to caricare i dati");
            e.printStackTrace();
            this.elencoMedia = new ArrayList<>();
        }
    }
    private void salvaDati() {
        try {
            gestoreDati.salva(this.elencoMedia);
        } catch (IOException e) {
            // Debugging print for save failure
            System.err.println("ERROR: Failed to save collection data.");
            e.printStackTrace();
        }
    }
    /**
     * La add di un media
     * @param media Il media da aggiungere
     */
    public void aggiungiMedia(Media media) {
        if (media != null) {
            this.elencoMedia.add(media);
            salvaDati(); // Save after modification
        }
    }

    /**
     * la find di un media
     * @param titolo titolo media
     * @return il media
     */
    public Media trovaMedia(String titolo) {
        //Uso stream per una ricerca efficente
        return elencoMedia.stream()
                .filter(m -> m.getTitolo().equalsIgnoreCase(titolo))
                .findFirst()
                .orElse(null);
    }

    /**
     * La remove di un media
     * @param titolo titolo media
     * @return true se rimosso correttamente
     */
    public boolean rimuoviMedia(String titolo) {
        Media mediaDaRimuovere = trovaMedia(titolo);
        if (mediaDaRimuovere != null) {
            boolean rimosso = elencoMedia.remove(mediaDaRimuovere);
            if (rimosso) {
                salvaDati();
            }
            return rimosso;
        }
        return false;
    }

    // Salva i cambiamenti (persistenza dei cambiamenti)
    public void modificaMedia() {
        salvaDati();
    }

    //Restituisce la collezione
    public List<Media> getElencoCompleto() {
        return new ArrayList<>(this.elencoMedia); // Return a copy
    }

    //Metodi dello STRATEGY
    
    public List<Media> applicaFiltro (CriterioFiltro criterio){
        return criterio.applicaFiltro(this.elencoMedia);
    }
    public List<Media> ordina(CriterioOrdinamento criterio){
        return criterio.ordina(this.elencoMedia);
    }

}