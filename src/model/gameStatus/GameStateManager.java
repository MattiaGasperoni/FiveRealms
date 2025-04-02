package model.gameStatus;

import model.characters.Character;
import java.io.*;
import java.util.List;

/**
 * The GameStateManager class is responsible for saving and loading the game state.
 * It handles the serialization and deserialization of the GameState object to and from a file.
 */
public class GameStateManager 
{
    private static final String DIRECTORY_NAME = "Saves";
    public static final String FILE_NAME = "game_state_save.dat";

    // Creates the Saves folder if it does not exist
    private File getSaveFile() 
    {
        File directory = new File(DIRECTORY_NAME);
        if (!directory.exists() && !directory.mkdirs()) 
        {
            System.err.println("Error: Unable to create the save directory.");
            return null;
        }
    
        File saveFile = new File(directory, FILE_NAME);
        if (!directory.canWrite()) 
        {
            System.err.println("Warning: The save directory is not writable.");
        }
    
        return saveFile;
    }
    

    /**
     * Saves the current game state to a file.
     *
     * @param allies  the list of ally characters to save
     * @param enemies the list of enemy characters to save
     * @param level   the current game level to save
     * @throws IOException 
     */
    public void saveStatus(List<Character> allies, List<Character> enemies, int level) throws IOException {
        GameState gameState = new GameState(level, allies, enemies);
        File saveFile = getSaveFile();
        
        if (saveFile == null) 
        {
            throw new IOException("Save file could not be determined.");
        }

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(saveFile)))
        {
            out.writeObject(gameState);
            System.out.println("Game state saved successfully.");
        } 
        catch (IOException e) 
        {
            throw new IOException("Error while saving the game state to the file '" + saveFile.getAbsolutePath() + "': " + e.getMessage());
        }
    }
    
    
        /**
         * Loads the game state from the save file.
         * 
         * @return the loaded GameState object, or null if the file does not exist,
         *         is not readable, or an error occurs during loading.
         *         If the file is corrupted or the class definition has changed, 
         *         an error message will be printed, and null will be returned.
         * @throws IOException 
         */
        public GameState loadStatus() throws IOException 
        {
            File saveFile = getSaveFile();
    
            if (!saveFile.exists() || !saveFile.canRead()) 
            {
                throw new FileNotFoundException("The save file does not exist or is not readable: " + saveFile.getAbsolutePath());
            }
    
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(saveFile))) 
            {
                return (GameState) in.readObject();
            } 
            catch (IOException | ClassNotFoundException e) 
            {
                throw new IOException("Error while loading the game state from the file: " + saveFile.getAbsolutePath() + " - " + e.getMessage());
            }
        }
    }