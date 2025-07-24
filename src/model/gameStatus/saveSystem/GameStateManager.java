package model.gameStatus.saveSystem;

import model.characters.Character;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class GameStateManager {
    // Nome della cartella dove vengono salvati i file di salvataggio
    public static final String DIRECTORY_NAME = "saves";

    /**
     * Genera un nome file univoco per il salvataggio in base al livello e al timestamp corrente.
     * @param level Il livello attuale del gioco.
     * @return Il nome del file di salvataggio generato.
     */
    public String getSaveFile(int level) {
        File directory = new File(DIRECTORY_NAME);
        if (!directory.exists()) directory.mkdirs(); // crea la directory se non esiste

        // Crea timestamp con il formato desiderato
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH-mm_dd-MM-yyyy");
        String timestamp = now.format(formatter);

        return "Level" + level + "_" + timestamp + ".txt";
    }

    /**
     * Salva lo stato attuale del gioco su file, includendo alleati e nemici.
     * @param allies Lista di personaggi alleati.
     * @param enemies Lista di personaggi nemici.
     * @param level Il livello attuale del gioco.
     * @throws IOException Se si verifica un errore durante il salvataggio.
     */
    public void saveStatus(List<Character> allies, List<Character> enemies, int level) throws IOException {
        String saveFileName = getSaveFile(level);
        File saveFile = new File(DIRECTORY_NAME + "/" + saveFileName);
        if (saveFile == null) throw new IOException("Save file could not be determined.");

        // Scrive i dati su file usando BufferedWriter
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile))) {
            writer.write("Type,Class,X,Y,Health,Power,Defence,Speed,Weapon\n"); // intestazione
            writer.write("Level," + level + "\n"); // livello

            // Salva gli alleati
            for (Character ally : allies)
                writer.write("Ally," + serializeCharacter(ally) + "\n");

            // Salva i nemici
            for (Character enemy : enemies)
                writer.write("Enemy," + serializeCharacter(enemy) + "\n");
            
            System.out.println("=== Game state saved successfully to " + saveFile.getName()+" ===");
            
        } catch (IOException e) {
            throw new IOException("Error while saving the game state: " + e.getMessage());
        }
    }

    /**
     * Carica l'ultimo stato di gioco salvato dalla cartella 'saves'.
     * @return Oggetto GameState che rappresenta lo stato di gioco caricato.
     * @throws IOException Se si verifica un errore durante il caricamento.
     */
    public GameState loadStatus() throws IOException {
        File saveFile = getLatestSaveFile(); // ottiene l'ultimo file salvato
        if (saveFile == null) throw new FileNotFoundException("No save file found.");

        try (BufferedReader reader = new BufferedReader(new FileReader(saveFile))) {
            int level = Integer.parseInt(reader.readLine().replace("Level: ", "").trim());
            List<Character> allies = new java.util.ArrayList<>();
            List<Character> enemies = new java.util.ArrayList<>();
            String line;
            List<Character> currentList = null;

            // Legge il file riga per riga (implementazione da completare con il parsing dei personaggi)
            while ((line = reader.readLine()) != null) {
                if (line.equals("Allies:")) {
                    currentList = allies;
                } else if (line.equals("Enemies:")) {
                    currentList = enemies;
                }
                // parsing dei personaggi non implementato qui
            }

            return new GameState(level, allies, enemies);
        }
    }

    /**
     * Restituisce il file di salvataggio più recente presente nella cartella 'saves'.
     * @return Il file più recente, oppure null se non esistono file.
     */
    private File getLatestSaveFile() {
        File directory = new File(DIRECTORY_NAME);
        File[] files = directory.listFiles();
        if (files == null || files.length == 0) return null;
        return Arrays.stream(files)
                .filter(File::isFile)
                .max(Comparator.comparingLong(File::lastModified))
                .orElse(null);
    }

    /**
     * Serializza un oggetto Character in una stringa compatta leggibile, per salvataggio o stampa.
     * @param character Il personaggio da serializzare.
     * @return Stringa formattata contenente i dati essenziali del personaggio.
     */
    private String serializeCharacter(Character character) {
        return String.format(
            "%-10s Pos:(%2d,%2d) | HP: %3d | Pow: %3d | Def: %3d | Spd: %3d | Wpn: %-15s",
            character.getClass().getSimpleName(),
            character.getPosition().getX(),
            character.getPosition().getY(),
            character.getCurrentHealth(),
            character.getPower(),
            character.getDefence(),
            character.getSpeed(),
            character.getWeapon().getClass().getSimpleName()
        );
    }


    /**
     * Verifica se esistono file di salvataggio nella cartella 'saves'.
     * @return true se esistono file salvati, false altrimenti.
     */
    public boolean hasSaved() {
        File directory = new File(DIRECTORY_NAME);
        if (!directory.exists()) {
            System.out.println("Directory does not exist: " + directory.getAbsolutePath());
            return false;
        }
        File[] files = directory.listFiles();
        if (files == null || files.length == 0) {
            System.out.println("No files found in directory: " + directory.getAbsolutePath());
            return false;
        }
        System.out.println("Found " + files.length + " save files.");
        return true;
    }

    /**
     * Restituisce un array di File che rappresentano i file di salvataggio presenti,
     * o un array vuoto se non ce ne sono.
     */
    public File[] getSaveFiles() {
        File directory = new File(DIRECTORY_NAME);
        if (!directory.exists()) return new File[0];
        File[] files = directory.listFiles();
        if (files == null) return new File[0];
        return files;
    }


}
