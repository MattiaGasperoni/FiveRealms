package controller;

import java.io.IOException;
import java.util.List;

import model.characters.Character;
import model.gameStatus.Game;
import model.gameStatus.saveSystem.GameSaveManager;
import model.point.Point;
import view.map.AbstractMap;
import view.menu.PauseMenu;

/**
 * Main controller that coordinates between specialized controllers.
 * <p>
 * This refactored controller delegates specific responsibilities to 
 * specialized controllers while maintaining the main coordination logic.
 */
public class GameController 
{

    /* ==============================
       Fields & Dependencies
       ============================== */
    private Game game;
    
    // Specialized Controllers
    private final MusicController musicController;
    private final MapController     mapController;
    private final SaveController   saveController;
    private final MenuController   menuController;

    /* ==============================
       Constructor
       ============================== */

    /**
     * Creates a GameController instance with specialized sub-controllers.
     *
     * @param game initial game instance
     * @param gameSaveManager save manager for game persistence
     */
    public GameController(Game game, GameSaveManager gameSaveManager) 
    {
        this.game = game;
        
        // Initialize specialized controllers
        this.musicController = new MusicController();
        this.mapController   = new MapController();
        this.saveController  = new SaveController(gameSaveManager);
        this.menuController  = new MenuController(this.musicController, this.saveController);
        
        // Setup menu controller with game reference
        this.menuController.setGame(this.game);
        this.menuController.updateLoadButtonState();
    }

    /* ==============================
       Public Methods
       ============================== */

    /**
     * Displays the main menu.
     */
    public void mainMenuShow() 
    {
        this.menuController.showMainMenu();
    }

    /**
     * Displays the end-game menu with the given result.
     *
     * @param result true if the player won, false otherwise
     */
    public void endGameMenuShow(boolean result) 
    {
        this.menuController.showEndGameMenu(result);
    }

    /**
     * Starts a new game with optional tutorial.
     */
    public void startNewGame() 
    {
        this.menuController.startNewGameFlow();
    }

    /**
     * Called when tutorial popups are completed.
     */
    public void onTutorialPopupsCompleted() 
    {
        this.musicController.stopMusic();
        this.game.startSelectionCharacter();
    }

    /**
     * Starts the character selection phase.
     */
    public void startSelectionCharacter() 
    {
        this.menuController.startCharacterSelection();
    }

    /**
     * Starts the replacement of dead allies.
     *
     * @param alliesToChange number of allies to replace
     */
    public void startReplaceDeadAllies(int alliesToChange) 
    {
        this.menuController.startCharacterReplacement(alliesToChange);
    }

    /**
     * Sets the pause menu and configures its listeners.
     *
     * @param pauseMenu the pause menu instance
     */
    public void setPauseMenu(PauseMenu pauseMenu) 
    {
        this.menuController.setupPauseMenu(pauseMenu);
    }

    /**
     * Saves the current game state.
     *
     * @throws IOException if saving fails
     */
    public void saveGame() throws IOException 
    {
        this.saveController.saveGame(this.game);
    }

    /**
     * Starts level-specific music.
     *
     * @param levelIndex the level number
     */
    public void startLevelMusic(int levelIndex) 
    {
        this.musicController.playLevelMusic(levelIndex);
    }

    /**
     * Stops the current level music.
     */
    public void stopLevelMusic() 
    {
        this.musicController.stopMusic();
    }

    /**
     * Moves a character to a new point on the map.
     *
     * @param map the game map
     * @param character the character to move
     * @param point the destination point
     */
    public void move(AbstractMap map, Character character, Point point) 
    {
        this.mapController.moveCharacter(map, character, point);
    }

    /**
     * Removes a character from the map and the given list.
     *
     * @param map the game map
     * @param deadCharacter the character to remove
     * @param point the character's position
     * @param listOfTheDead the list containing the character
     */
    public void remove(AbstractMap map, Character deadCharacter, Point point, List<Character> listOfTheDead) 
    {
        this.mapController.removeCharacter(map, deadCharacter, point, listOfTheDead);
    }

    /**
     * Executes a fight action between two characters.
     *
     * @param attacker the attacking character
     * @param defender the defending character
     * @param allies the list of allied characters
     * @param enemies the list of enemy characters
     * @param map the game map
     */
    public void fight(Character attacker, Character defender, List<Character> allies, List<Character> enemies, AbstractMap map) 
    {
        this.mapController.executeCombat(attacker, defender, allies, enemies, map);
    }

    /**
     * Returns the current level index.
     *
     * @return the current level index
     */
    public int getLevelIndex() 
    {
        return this.game.getCurrentLevelIndex();
    }

    /* ==============================
       Controller Access Methods
       ============================== */

    /**
     * Gets the music controller for direct access if needed.
     *
     * @return the music controller instance
     */
    public MusicController getMusicController() 
    {
        return this.musicController;
    }

    /**
     * Gets the map controller for direct access if needed.
     *
     * @return the map controller instance
     */
    public MapController getMapController() 
    {
        return this.mapController;
    }

    /**
     * Gets the save controller for direct access if needed.
     *
     * @return the save controller instance
     */
    public SaveController getSaveController() 
    {
        return this.saveController;
    }

    /**
     * Gets the menu controller for direct access if needed.
     *
     * @return the menu controller instance
     */
    public MenuController getMenuController() 
    {
        return this.menuController;
    }

    /**
     * Updates the game reference in case of game restart.
     *
     * @param newGame the new game instance
     */
    public void setGame(Game newGame) 
    {
        this.game = newGame;
        this.menuController.setGame(newGame);
    }
}