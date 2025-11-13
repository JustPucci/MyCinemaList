package it.ingsw.progetto.dati; // Updated package

import it.ingsw.progetto.media.Media; // Updated import
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MockGestoreDati implements GestoreDatiInterface {


    private List<Media> databaseFinto = new ArrayList<>();

    @Override
    public void salva(List<Media> elencoMedia) throws IOException {

        this.databaseFinto = new ArrayList<>(elencoMedia);
        System.out.println("MOCK: Saved " + elencoMedia.size() + " items.");
    }

    @Override
    public List<Media> carica() throws IOException {

        System.out.println("MOCK: Loaded " + databaseFinto.size() + " items.");
        return new ArrayList<>(this.databaseFinto);
    }
}