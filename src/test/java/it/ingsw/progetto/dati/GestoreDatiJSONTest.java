package it.ingsw.progetto.dati;

import it.ingsw.progetto.media.Film;
import it.ingsw.progetto.media.Media;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration Tests for GestoreDatiJSON.
 * This test interacts with the real file system.
 */
public class GestoreDatiJSONTest {

    // Il percorso del file di test
    private static final String TEST_FILE_PATH = "test_collezione.json";

    // Il gestore che stiamo testando
    private GestoreDatiInterface gestore;
    private File testFile;

    /**
     * Setup: Eseguito PRIMA di ogni test.
     * Crea il gestore e pulisce eventuali file vecchi.
     */
    @BeforeEach
    public void setUp() {
        gestore = new GestoreDatiJSON(TEST_FILE_PATH);
        testFile = new File(TEST_FILE_PATH).getAbsoluteFile();

        // Assicurati che il file di test sia pulito prima di iniziare
        if (testFile.exists()) {
            testFile.delete();
        }
    }

    /**
     * Teardown: Eseguito DOPO ogni test.
     * Pulisce il file di test.
     */
    @AfterEach
    public void tearDown() {
        // Pulisci il file dopo il test
        if (testFile.exists()) {
            testFile.delete();
        }
    }

    /**
     * Test per 'salva' e 'carica' (Test di Integrazione)
     * Verifica il ciclo completo di persistenza.
     */
    @Test
    public void testSalvaECaricaCicloCompleto() {

        // --- 1. ARRANGE (Prepara i dati) ---
        List<Media> elencoOriginale = new ArrayList<>();
        Film film1 = new Film("Inception", "Nolan", 2010, Film.Genere.FANTASCIENZA, 5, Media.StatoVisione.VISTO);
        elencoOriginale.add(film1);

        try {
            // --- 2. ACT (Esegui 'salva') ---
            gestore.salva(elencoOriginale);

            // --- 3. ASSERT (Verifica la scrittura) ---
            assertTrue(testFile.exists(), "Il file JSON non è stato creato.");
            assertTrue(testFile.length() > 0, "Il file JSON è vuoto.");

            // --- 4. ACT (Esegui 'carica') ---
            // Creiamo un *nuovo* gestore per simulare il riavvio dell'app
            GestoreDatiInterface gestoreCaricamento = new GestoreDatiJSON(TEST_FILE_PATH);
            List<Media> elencoCaricato = gestoreCaricamento.carica();

            // --- 5. ASSERT (Verifica la lettura) ---
            assertNotNull(elencoCaricato, "La lista caricata non deve essere null.");
            assertEquals(1, elencoCaricato.size(), "La lista caricata dovrebbe contenere 1 elemento.");

            // Verifica che i dati siano identici
            Media mediaCaricato = elencoCaricato.get(0);
            assertEquals("Inception", mediaCaricato.getTitolo());
            assertEquals(5, mediaCaricato.getValutazionePersonale());
            // Controlliamo che sia stato deserializzato come Film
            assertTrue(mediaCaricato instanceof Film, "L'oggetto caricato non è un'istanza di Film.");

        } catch (IOException e) {
            // Se il test fallisce a causa di un I/O, fallisce qui
            fail("Il test è fallito a causa di una IOException: " + e.getMessage());
        }
    }
}