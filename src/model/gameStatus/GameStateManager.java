package model.gameStatus;

import model.characters.Character;
import java.io.*;
import java.util.List;

public class GameStateManager {
    private static final String DIRECTORY_NAME = "Salvataggi";
    private static final String FILE_NAME = "game_state.dat";

    // Crea la cartella Salvataggi se non esiste
    private File getSaveFile() {
        File directory = new File(DIRECTORY_NAME);
        if (!directory.exists()) {
            directory.mkdir(); // Crea la cartella se non esiste
        }

        return new File(directory, FILE_NAME); // Restituisce il file nella cartella Salvataggi
    }

    public void saveStatus(List<Character> allies, List<Character> enemies, int level) {
        GameState gameState = new GameState(level, allies, enemies);
        File saveFile = getSaveFile();

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(saveFile))) {
            out.writeObject(gameState);
            System.out.println("Stato di gioco salvato con successo.");
        } catch (IOException e) {
            System.err.println("Errore durante il salvataggio dello stato di gioco: " + e.getMessage());
        }
    }

    public GameState loadStatus() {
        File saveFile = getSaveFile();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(saveFile))) {
            return (GameState) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Errore durante il caricamento dello stato di gioco: " + e.getMessage());
            return null;
        }
    }
}
