package model.gameStatus.saveSystem;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;

public class GameStateManager 
{
    // Nome della cartella dove vengono salvati i file di salvataggio
    public static final String DIRECTORY_NAME = "saves";
    public static final String FILE_EXTENSION = ".sav";

    /**
     * Salva lo stato del gioco usando la serializzazione Java.
     * @param gameState Lo stato del gioco da salvare.
     * @param fileName Nome del file (opzionale, se null viene generato automaticamente).
     * @throws IOException Se si verifica un errore durante il salvataggio.
     */
    public void saveGameState(GameState gameState, String fileName) throws IOException 
    {
        // Crea la directory se non esiste
        File directory = new File(DIRECTORY_NAME);
        
        if (!directory.exists()) 
        {
            directory.mkdirs();
        }

        // Genera il nome del file se non fornito
        if (fileName == null) 
        {
            fileName = this.generateSaveFileName(gameState.getLevel());
        }

        // Assegnamo al nome del file l'estensione corretta
        if (!fileName.endsWith(FILE_EXTENSION))
        {
            fileName += FILE_EXTENSION;
        }

        File saveFile = new File(directory, fileName);
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFile)))
        {
            oos.writeObject(gameState);
            System.out.println("=== Game state saved successfully to " + fileName + " ===");
        } 
        catch (IOException e) 
        {
        	e.printStackTrace();
            throw new IOException("Error while saving the game state: " + e.getMessage(), e);
            
        }
    }

    /**
     * Carica uno stato di gioco da un file specifico, o l'ultimo salvataggio se fileName è null.
     * @param fileName Nome del file da caricare, o null per caricare l'ultimo salvataggio.
     * @return Oggetto GameState che rappresenta lo stato di gioco caricato.
     * @throws IOException Se si verifica un errore durante il caricamento.
     * @throws ClassNotFoundException Se la classe GameState non può essere trovata durante la deserializzazione.
     */
    public GameState loadGameState(String fileName) throws IOException, ClassNotFoundException 
    {
        // Se fileName è null, carica l'ultimo salvataggio
        if (fileName == null) 
        {
            File latestSaveFile = getLatestSaveFile();
            if (latestSaveFile == null) 
            {
                throw new FileNotFoundException("No save file found in directory: " + DIRECTORY_NAME);
            }
            fileName = latestSaveFile.getName();
        }
        
        if (!fileName.endsWith(FILE_EXTENSION)) 
        {
            fileName += FILE_EXTENSION;
        }
        
        File saveFile = new File(DIRECTORY_NAME, fileName);
        
        if (!saveFile.exists()) 
        {
            throw new FileNotFoundException("Save file not found: " + fileName);
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saveFile))) 
        {
            GameState gameState = (GameState) ois.readObject();
            System.out.println("=== Game state loaded successfully from " + fileName + " ===");
            return gameState;
        } 
        catch (IOException e) 
        {
            throw new IOException("Error while loading the game state: " + e.getMessage(), e);
        } 
        catch (ClassNotFoundException e) 
        {
            throw new ClassNotFoundException("Could not deserialize GameState: " + e.getMessage());
        }
    }

    /**
     * Verifica se esistono file di salvataggio nella cartella 'saves'.
     * @return true se esistono file salvati, false altrimenti.
     */
    public boolean hasSaved() 
    {
        File directory = new File(DIRECTORY_NAME);
        
        if (!directory.exists())
        {
            return false;
        }
        
        File[] files = directory.listFiles((dir, name) -> name.endsWith(FILE_EXTENSION));
        return files != null && files.length > 0;
    }

    /**
     * Restituisce un array di File che rappresentano i file di salvataggio presenti,
     * ordinati per data di modifica (più recente prima).
     * @return Array di file di salvataggio, o array vuoto se non ce ne sono.
     */
    public File[] getSaveFiles() 
    {
        File directory = new File(DIRECTORY_NAME);
        
        if (!directory.exists()) 
        {
            return new File[0];
        }
        
        File[] files = directory.listFiles((dir, name) -> name.endsWith(FILE_EXTENSION));
        
        if (files == null || files.length == 0) 
        {
            return new File[0];
        }
        
        // Ordina per data di modifica (più recente prima)
        Arrays.sort(files, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
        
        return files;
    }
    
    /**
     * Elimina un file di salvataggio specifico.
     * @param fileName Nome del file da eliminare.
     * @return true se il file è stato eliminato con successo, false altrimenti.
     */
    public boolean deleteSaveFile(String fileName) 
    {
        if (!fileName.endsWith(FILE_EXTENSION)) 
        {
            fileName += FILE_EXTENSION;
        }
        
        File saveFile = new File(DIRECTORY_NAME, fileName);
        
        if (saveFile.exists() && saveFile.delete()) 
        {
            System.out.println("Save file deleted: " + fileName);
            return true;
        }
        
        return false;
    }

    /**
     * Carica l'ultimo stato di gioco salvato dalla cartella 'saves'.
     * @return Oggetto GameState che rappresenta lo stato di gioco caricato.
     * @throws IOException Se si verifica un errore durante il caricamento.
     * @throws ClassNotFoundException Se la classe GameState non può essere trovata.
     */
    public GameState loadLatestGameState() throws IOException, ClassNotFoundException 
    {
        File latestSaveFile = getLatestSaveFile();
        if (latestSaveFile == null) 
        {
            throw new FileNotFoundException("No save file found in directory: " + DIRECTORY_NAME);
        }
        
        return loadGameState(latestSaveFile.getName());
    }
    
    /**
     * Restituisce il file di salvataggio più recente presente nella cartella 'saves'.
     * @return Il file più recente, oppure null se non esistono file.
     */
    private File getLatestSaveFile() 
    {
        File directory = new File(DIRECTORY_NAME);
        if (!directory.exists()) 
        {
            return null;
        }
        
        File[] files = directory.listFiles((dir, name) -> name.endsWith(FILE_EXTENSION));
        if (files == null || files.length == 0)
        {
            return null;
        }
        
        return Arrays.stream(files)
                .filter(File::isFile)
                .max(Comparator.comparingLong(File::lastModified))
                .orElse(null);
    }
    
    /**
     * Genera un nome file univoco per il salvataggio in base al livello e al timestamp corrente.
     * @param level Il livello attuale del gioco.
     * @return Il nome del file di salvataggio generato.
     */
    private String generateSaveFileName(int level) 
    {
        // Crea timestamp con il formato desiderato
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH-mm_dd-MM-yyyy");
        String timestamp = now.format(formatter);
        
        return "Level" + level + "_" + timestamp + FILE_EXTENSION;
    }
    
    
    /**
     * Restituisce informazioni sui file di salvataggio disponibili.
     * @return Stringa con informazioni sui salvataggi.
     */
    public String getSaveInfo()
    {
    	System.out.print("infooooooo");
        File[] saveFiles = getSaveFiles();
        if (saveFiles.length == 0) {
            return "No save files found.";
        }
        
        StringBuilder info = new StringBuilder("Available save files:\n");
        for (int i = 0; i < saveFiles.length; i++) {
            File file = saveFiles[i];
            LocalDateTime modified = LocalDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(file.lastModified()),
                java.time.ZoneId.systemDefault()
            );
            info.append(String.format("%d. %s (Modified: %s)\n", 
                i + 1, 
                file.getName(), 
                modified.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            ));
        }
        return info.toString();
    }
}
