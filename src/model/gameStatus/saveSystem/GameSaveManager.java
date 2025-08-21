package model.gameStatus.saveSystem;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import model.characters.Character;

/**
 * Manages game save and load operations including file management and state caching.
 * Handles serialization/deserialization of game states to/from disk files.
 */
public class GameSaveManager 
{
    /** Directory name where save files are stored */
    public static final String DIRECTORY_NAME = "saves";
    
    /** File extension for save files */
    public static final String FILE_EXTENSION = ".sav";

    /** Currently loaded game state kept in cache */
    private GameSave currentLoadedGameState = null;

    /**
     * Saves the current game state to a file.
     * Creates the save directory if it doesn't exist.
     * 
     * @param gameSave the game state to save
     * @param fileName name of the file (optional, if null generates automatically)
     * @throws IOException if an error occurs during saving
     */
    public void saveGameState(GameSave gameSave, String fileName) throws IOException 
    {
        // Create directory if it doesn't exist
        File directory = new File(DIRECTORY_NAME);
        
        if (!directory.exists()) 
        {
            directory.mkdirs();
        }

        // Generate filename if not provided
        if (fileName == null) 
        {
            fileName = this.generateSaveFileName(gameSave.getLevel());
        }

        // Assign correct extension to filename
        if (!fileName.endsWith(FILE_EXTENSION))
        {
            fileName += FILE_EXTENSION;
        }

        File saveFile = new File(directory, fileName);
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFile)))
        {
            oos.writeObject(gameSave);
            System.out.println("=== Game state saved successfully to " + fileName + " ===");
        } 
        catch (IOException e) 
        {
        	e.printStackTrace();
            throw new IOException("Error while saving the game state: " + e.getMessage(), e);
        }
    }

    /**
     * Loads a game state from a specific file, or the latest save if fileName is null.
     * 
     * @param fileName name of the file to load, or null to load the latest save
     * @return GameSave object representing the loaded game state
     * @throws IOException if an error occurs during loading
     * @throws ClassNotFoundException if the GameState class cannot be found during deserialization
     */
    public GameSave loadGameState(String fileName) throws IOException, ClassNotFoundException 
    {
        // If fileName is null, load the latest save
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
            GameSave gameState = (GameSave) ois.readObject();
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
     * Checks if save files exist in the 'saves' folder.
     * 
     * @return true if saved files exist, false otherwise
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
     * Returns an array of Files representing the save files present,
     * sorted by modification date (most recent first).
     * 
     * @return array of save files, or empty array if none exist
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
        
        // Sort by modification date (most recent first)
        Arrays.sort(files, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
        
        return files;
    }
    
    /**
     * Deletes a specific save file.
     * 
     * @param fileName name of the file to delete
     * @return true if the file was successfully deleted, false otherwise
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
     * Loads the latest saved game state from the 'saves' folder.
     * 
     * @return GameSave object representing the loaded game state
     * @throws IOException if an error occurs during loading
     * @throws ClassNotFoundException if the GameState class cannot be found
     */
    public GameSave loadLatestGameState() throws IOException, ClassNotFoundException 
    {
        File latestSaveFile = getLatestSaveFile();
        if (latestSaveFile == null) 
        {
            throw new FileNotFoundException("No save file found in directory: " + DIRECTORY_NAME);
        }
        
        return loadGameState(latestSaveFile.getName());
    }
    
    /**
     * Returns the most recent save file present in the 'saves' folder.
     * 
     * @return the most recent file, or null if no files exist
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
     * Generates a unique filename for saving based on the current level and timestamp.
     * 
     * @param level the current game level
     * @return the generated save filename
     */
    private String generateSaveFileName(int level) 
    {
        // Create timestamp with desired format
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH-mm_dd-MM-yyyy");
        String timestamp = now.format(formatter);
        
        return "Level" + (level+1) + "_" + timestamp + FILE_EXTENSION;
    }
      
    /**
     * Loads information from a specific file and keeps it cached.
     * This method must be called before using the getter methods below.
     * 
     * @param saveFile the file from which to load data
     * @throws IOException if an error occurs during loading
     * @throws ClassNotFoundException if the GameState class cannot be found
     */
    public void loadFileInfo(File saveFile) throws IOException, ClassNotFoundException 
    {
        this.currentLoadedGameState = loadGameState(saveFile.getName());
    }

    /**
     * Returns the list of allies from the currently loaded GameState.
     * NOTE: Call loadFileInfo() first.
     * 
     * @return list of allies
     * @throws IllegalStateException if no file has been loaded
     */
    public List<Character> getLoadedAllies() 
    {
        if (currentLoadedGameState == null) 
        {
            throw new IllegalStateException("No game state loaded. Call loadFileInfo() first.");
        }
        return currentLoadedGameState.getAllies();
    }

    /**
     * Returns the list of enemies from the currently loaded GameState.
     * NOTE: Call loadFileInfo() first.
     * 
     * @return list of enemies
     * @throws IllegalStateException if no file has been loaded
     */
    public List<Character> getLoadedEnemies() 
    {
        if (currentLoadedGameState == null) 
        {
            throw new IllegalStateException("No game state loaded. Call loadFileInfo() first.");
        }
        return currentLoadedGameState.getEnemies();
    }

    /**
     * Returns the level number from the currently loaded GameState.
     * NOTE: Call loadFileInfo() first.
     * 
     * @return the level number
     * @throws IllegalStateException if no file has been loaded
     */
    public int getLoadedLevel() 
    {
        if (currentLoadedGameState == null) 
        {
            throw new IllegalStateException("No game state loaded. Call loadFileInfo() first.");
        }
        return currentLoadedGameState.getLevel();
    }
   
    /**
     * Clears the cache of the loaded GameState.
     * Useful for freeing memory after using the data.
     */
    public void clearLoadedGameState() 
    {
        this.currentLoadedGameState = null;
    }
}