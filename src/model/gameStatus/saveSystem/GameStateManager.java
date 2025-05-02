package model.gameStatus.saveSystem;

import model.characters.Character;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * The GameStateManager class is responsible for saving and loading the game state.
 * It handles the serialization and deserialization of the GameState object to and from a file.
 */
public class GameStateManager 
{
    // Directory for saving game states
    private static final String DIRECTORY_NAME = "saves";

    /**
     * Creates the save file with a unique name based on the level and timestamp.
     * @param level The current game level.
     * @return A String representing the save file name.
     */
    public String getSaveFile(int level) 
    {
        File directory = new File(DIRECTORY_NAME);
        if (!directory.exists()) directory.mkdirs();  // Create the directory if it doesn't exist

        // Format the timestamp as HH-mm_dd-MM-yyyy
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH-mm_dd-MM-yyyy");
        String timestamp = now.format(formatter);

        // Create the save file name in the desired format
        String fileName = "Level" + level + "_" + timestamp + ".txt";

        return fileName;  // Return the save file name
    }



    /**
     * Saves the current game state to a file in a readable format.
     * @param allies List of ally characters.
     * @param enemies List of enemy characters.
     * @param level The current game level.
     * @throws IOException If an error occurs during saving.
     */
    public void saveStatus(List<Character> allies, List<Character> enemies, int level) throws IOException 
    {
        String saveFileName = getSaveFile(level);  // Get the save file name
        File saveFile = new File(DIRECTORY_NAME + "/" + saveFileName);  // Full path for the file

        if (saveFile == null) throw new IOException("Save file could not be determined.");

        // Try to write the game state to the save file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile))) 
        {
            writer.write("Level: " + level + "\n");  // Write the level
            writer.write("Allies:\n");
            // Write each ally's data to the file
            for (Character ally : allies) writer.write(serializeCharacter(ally) + "\n");
            writer.write("Enemies:\n");
            // Write each enemy's data to the file
            for (Character enemy : enemies) writer.write(serializeCharacter(enemy) + "\n");

            System.out.println("Game state saved successfully to " + saveFile.getName());
        } 
        catch (IOException e) 
        {
            throw new IOException("Error while saving the game state: " + e.getMessage());
        }
    }

    /**
     * Loads the most recent game state from the 'saves' directory.
     * @return A GameState object representing the loaded game state.
     * @throws IOException If an error occurs during loading.
     */
    public GameState loadStatus() throws IOException {
        File saveFile = getLatestSaveFile();  // Get the latest save file
        if (saveFile == null) throw new FileNotFoundException("No save file found.");

        // Try to read the game state from the save file
        try (BufferedReader reader = new BufferedReader(new FileReader(saveFile))) {
            int level = Integer.parseInt(reader.readLine().replace("Level: ", "").trim());  // Read the level
            List<Character> allies = new java.util.ArrayList<>();
            List<Character> enemies = new java.util.ArrayList<>();

            String line;
            List<Character> currentList = null;

            // Read each line and populate the corresponding list (allies or enemies)
            while ((line = reader.readLine()) != null) {
                if (line.equals("Allies:")) {
                    currentList = allies;  // Switch to allies list
                } else if (line.equals("Enemies:")) {
                    currentList = enemies;  // Switch to enemies list
                }
                // Deserialize each character and add to the current list
            }

            // Return the loaded game state
            return new GameState(level, allies, enemies);
        }
    }

    /**
     * Returns the most recent save file found in the 'saves' directory.
     * @return A File object representing the most recent save file.
     */
    private File getLatestSaveFile() {
        File directory = new File(DIRECTORY_NAME);
        if (!directory.exists()) return null;
        File[] files = directory.listFiles();
        if (files == null || files.length == 0) return null;
        return files[files.length - 1];  // Return the last file in the directory
    }

    /**
     * Serializes a Character object into a savable string format.
     * @param character The character to serialize.
     * @return A string representing the serialized character.
     */
    private String serializeCharacter(Character character) 
    {
        return character.getClass().getSimpleName().toLowerCase() + "," +
               character.getPosition().getX() + "," +
               character.getPosition().getY() + "," +
               character.getDefence() + "," +
               character.getCurrentHealth() + "," +
               character.getExperience();
    }
    
    /***
     * This method checks if there are any saved game files in the 'saves' directory.
     * It returns true if the directory exists and contains files, otherwise false.
     * @return boolean - true if there are saved games, false otherwise.
     */
    public boolean hasSaved() {
        File directory = new File(DIRECTORY_NAME);
        if (!directory.exists() || directory.listFiles() == null || directory.listFiles().length == 0) {
            return false;  // If the directory doesn't exist or is empty, return false
        }
        return true;  // If the directory exists and contains files, return true
    }

}
