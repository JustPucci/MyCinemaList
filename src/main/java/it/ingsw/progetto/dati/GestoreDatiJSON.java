package it.ingsw.progetto.dati;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter; // <-- IMPORTANTE
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import it.ingsw.progetto.media.Media;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class GestoreDatiJSON implements GestoreDatiInterface {

    private final ObjectMapper objectMapper;
    private final String filePath;
    private final File fileAssoluto;


    private final TypeReference<List<Media>> mediaListTypeRef;
    private final ObjectWriter mediaListWriter; // Writer specializzato

    public GestoreDatiJSON(String filePath) {
        this.filePath = filePath;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);


        this.mediaListTypeRef = new TypeReference<List<Media>>() {};


        this.mediaListWriter = objectMapper.writerFor(mediaListTypeRef);

        // DEBUG del percorso
        this.fileAssoluto = new File(filePath).getAbsoluteFile();
        System.out.println("************************************************************");
        System.out.println("DEBUGGING PERCORSO: GestoreDatiJSON (Writer Corretto) inizializzato.");
        System.out.println("===> Percorso Assoluto: " + this.fileAssoluto.getPath());
        System.out.println("************************************************************");
    }

    @Override
    public void salva(List<Media> elencoMedia) throws IOException {
        try {
            // RISOLTO CON writer specializzato
            mediaListWriter.writeValue(fileAssoluto, elencoMedia);

        } catch (IOException e) {
            System.err.println("ERRORE: Impossibile salvare i dati su " + fileAssoluto.getPath());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<Media> carica() throws IOException {
        if (!fileAssoluto.exists() || fileAssoluto.length() == 0) {
            System.out.println("DEBUGGING: File non trovato. Restituisco lista vuota.");
            return new ArrayList<>();
        }

        System.out.println("DEBUGGING: Trovato file. Tentativo di lettura da: " + fileAssoluto.getPath());

        try {
            return objectMapper.readValue(fileAssoluto, mediaListTypeRef);

        } catch (InvalidTypeIdException e) {
            System.err.println("--- ERRORE DI CARICAMENTO (JSON OBSOLETO) ---");
            System.err.println("MOTIVO: Manca il campo '@type'.");
            System.err.println("SOLUZIONE: Elimina 'collezione.json' e riavvia l'app.");
            System.err.println("----------------------------------------------");
            return new ArrayList<>();

        } catch (IOException e) {
            System.err.println("ERRORE GENERICO DI I/O: Impossibile caricare i dati da " + fileAssoluto.getPath());
            e.printStackTrace();
            throw e;
        }
    }
}