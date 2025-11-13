package it.ingsw.progetto.dati;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import it.ingsw.progetto.media.Media;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// ADAPTER CONCRETO JSON

public class GestoreDatiJSON implements GestoreDatiInterface {

    // Mappatore di oggetti di Jacson
    private final ObjectMapper objectMapper;

    // Path al file
    private final String filePath;

    /**
     * Costruttore per il JSON adapter.
     * @param filePath Il path al file JSON
     */
    public GestoreDatiJSON(String filePath) {
        this.filePath = filePath;
        this.objectMapper = new ObjectMapper();

        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public void salva(List<Media> elencoMedia) throws IOException {
        try {
            objectMapper.writeValue(new File(filePath), elencoMedia);
        } catch (IOException e) {
            System.err.println("ERROR: Failed to salvare dati in" + filePath);
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<Media> carica() throws IOException {
        File file = new File(filePath);

        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }

        try {
            return objectMapper.readValue(file, new TypeReference<List<Media>>() {});

        } catch (IOException e) {

            System.err.println("ERROR: Failed to caricare i dati da" + filePath);
            e.printStackTrace();
            throw e;
        }
    }
}