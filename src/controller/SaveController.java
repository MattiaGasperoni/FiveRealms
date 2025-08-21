package controller;

import java.io.File;
import java.io.IOException;

import model.gameStatus.Game;
import model.gameStatus.level.GameLevel;
import model.gameStatus.saveSystem.GameSave;
import model.gameStatus.saveSystem.GameSaveManager;

/**
 * Manages all save and load operations for the game.
 * <p>
 * This controller handles game state persistence, including
 * saving current progress and loading saved games.
 */
public class SaveController 
{
    
    private final GameSaveManager gameSaveManager;
    
    /**
     * Creates a new SaveController instance.
     *
     * @param gameSaveManager the save manager instance
     */
    public SaveController(GameSaveManager gameSaveManager) 
    {
        this.gameSaveManager = gameSaveManager;
    }
    
    /**
     * Checks if any saved games exist.
     *
     * @return true if saved games exist, false otherwise
     */
    public boolean hasSavedGames() 
    {
        return this.gameSaveManager.hasSaved();
    }
    
    /**
     * Gets all available save files.
     *
     * @return array of save files
     */
    public File[] getSaveFiles() 
    {
        return this.gameSaveManager.getSaveFiles();
    }
    
    /**
     * Saves the current game state.
     *
     * @param game the current game instance
     * @throws IOException if saving fails
     */
    public void saveGame(Game game) throws IOException 
    {
        GameLevel currentLevel = this.getCurrentLevel(game);
        
        GameSave gameState = new GameSave(
            game.getCurrentLevelIndex(),
            currentLevel.getAllies(),
            currentLevel.getEnemies()
        );
        
        this.gameSaveManager.saveGameState(gameState, null);
    }
    
    /**
     * Saves a custom game state.
     *
     * @param gameState the game state to save
     * @param fileName optional file name (null for default)
     * @throws IOException if saving fails
     */
    public void saveGameState(GameSave gameState, String fileName) throws IOException 
    {
        this.gameSaveManager.saveGameState(gameState, fileName);
    }
    
    /**
     * Loads the most recent saved game state.
     *
     * @return the loaded game state
     * @throws IOException if loading fails
     * @throws ClassNotFoundException if the save file format is incompatible
     */
    public GameSave loadGame() throws IOException, ClassNotFoundException 
    {
        return this.gameSaveManager.loadGameState(null);
    }
    
    /**
     * Loads a specific saved game state.
     *
     * @param fileName the name of the save file to load
     * @return the loaded game state
     * @throws IOException if loading fails
     * @throws ClassNotFoundException if the save file format is incompatible
     */
    public GameSave loadGameState(String fileName) throws IOException, ClassNotFoundException 
    {
        return this.gameSaveManager.loadGameState(fileName);
    }
    
    /**
     * Deletes a specific save file.
     *
     * @param saveFile the save file to delete
     * @return true if the file was successfully deleted, false otherwise
     */
    public boolean deleteSave(File saveFile) 
    {
        if (saveFile != null && saveFile.exists()) 
        {
            return saveFile.delete();
        }
        return false;
    }
    
    /**
     * Gets the current level from the game instance.
     *
     * @param game the game instance
     * @return the current level
     */
    private GameLevel getCurrentLevel(Game game) 
    {
        return game.getGameLevels().get(game.getCurrentLevelIndex());
    }
}