package it.ingsw.progetto.collection; // Updated package

import it.ingsw.progetto.dati.GestoreDatiInterface; // Updated import
import it.ingsw.progetto.dati.MockGestoreDati; // Updated import (Mock)
import it.ingsw.progetto.media.Film; // Updated import
import it.ingsw.progetto.media.Media; // Updated import


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*; // Import assertions

/**
 * Unit test per CollezioneMedia Singleton.
 * Usa un Mock GestoreDati for isolare
 */
public class CollezioneMediaTest {

    private CollezioneMedia collezione;

    private GestoreDatiInterface mockGestore;


    @BeforeEach
    public void setUp() {

        mockGestore = new MockGestoreDati();

        collezione = CollezioneMedia.getIstanza(mockGestore);
    }

    @Test
    public void testAggiungiMedia() {

        Film film = new Film("Inception", "Nolan", 2010, Film.Genere.AZIONE, 5, Media.StatoVisione.VISTO);

        collezione.aggiungiMedia(film);


        assertEquals(1, collezione.getElencoCompleto().size());
        assertEquals("Inception", collezione.trovaMedia("Inception").getTitolo());
    }

    @Test
    public void testRimuoviMedia() {

        Film film = new Film("Inception", "Nolan", 2010, Film.Genere.AZIONE, 5, Media.StatoVisione.VISTO);
        collezione.aggiungiMedia(film);
        assertEquals(1, collezione.getElencoCompleto().size());


        boolean rimosso = collezione.rimuoviMedia("Inception");


        assertTrue(rimosso); // Check if removal was successful
        assertEquals(0, collezione.getElencoCompleto().size());
        assertNull(collezione.trovaMedia("Inception")); // Check
    }
}