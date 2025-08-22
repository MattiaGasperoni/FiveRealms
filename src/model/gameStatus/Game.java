package model.gameStatus;

import java.io.File;
import java.io.IOException;
import java.util.List;
import model.characters.Character;
import model.gameStatus.manager.CharacterManager;
import model.gameStatus.manager.GameLoopManager;
import model.gameStatus.manager.GameStateManager;
import model.gameStatus.manager.LevelManager;
import model.gameStatus.manager.TutorialManager;
import model.gameStatus.saveSystem.GameSaveManager;
import controller.GameController;

/**
 * Main Game class that coordinates all game operations through specialized managers.
 * This class now serves as a facade that delegates responsibilities to appropriate managers.
 */
public class Game 
{
	/** Total number of levels in the game */
	public static final int TOTAL_LEVEL = 5;

	/** Maximum number of allied characters allowed per round */
	public static final int MAX_ALLIES_PER_ROUND = 3;
	
	/** Interval for the game loop in milliseconds */
	private static final int GAME_LOOP_INTERVAL_MS = 100;
	
    /** Manager for character operations */
    private CharacterManager characterManager;
    
    /** Manager for level operations */
    private LevelManager levelManager;
    
    /** Manager for tutorial operations */
    private TutorialManager tutorialManager;
    
    /** Manager for game loop execution */
    private GameLoopManager gameLoopManager;
    
    /** Manager for game state and phases */
    private GameStateManager gameStateManager;
    
    /** Manager for game save and load operations */
    private GameSaveManager gameSaveManager;
    
    /** Main game controller for UI and game logic coordination */
    private GameController controller;

    /**
     * Constructs a new Game instance and initializes all managers.
     */
    public Game() 
    {
        this.initializeCore();
        this.initializeDependentManagers();
        this.setupCallbacks();
    }

    /**
     * Initialize core managers that don't depend on others
     */
    private void initializeCore() 
    {
        this.characterManager = new CharacterManager();
        this.gameStateManager = new GameStateManager();
        this.gameLoopManager  = new GameLoopManager();
        this.gameSaveManager  = new GameSaveManager();
    }

    /**
     * Initialize managers that depend on controller or other managers
     */
    private void initializeDependentManagers() 
    {
        // Initialize controller with dependencies
        this.controller = new GameController(this, this.gameSaveManager);
        
        // Initialize remaining managers that need controller
        this.levelManager    = new LevelManager(this.controller);
        this.tutorialManager = new TutorialManager(this.controller, this.characterManager);
    }

    /**
     * Setup callbacks and wire managers together
     */
    private void setupCallbacks() 
    {
        this.gameLoopManager.setUpdateCallback(this::updateGameSafe);
    }

    /**
     * Starts the game by displaying the main menu.
     * This is the entry point for the game application.
     */
    public void start() 
    {
        this.gameStateManager.returnToMainMenu();
        this.controller.mainMenuShow();	
    }

    /**
     * Starts a new game session by initializing a fresh game state.
     * 
     * @throws IOException if there are issues initializing game resources
     */
    public void startNewGame() throws IOException 
    {
        this.gameStateManager.startNewGame();
        this.controller.startNewGame();
    }

    /**
     * Loads and starts a previously saved game from the specified save file.
     * This method handles loading game state, reinitializing characters,
     * setting up the appropriate level, and starting the game loop.
     * 
     * @param saveFile the save file to load the game state from
     */
    public void startLoadGame(File saveFile) 
    {
        try 
        {
            // Load save data
            this.gameSaveManager.loadFileInfo(saveFile);

            List<Character> allies = this.gameSaveManager.getLoadedAllies();
            List<Character> enemies = this.gameSaveManager.getLoadedEnemies();
            int numLevel = this.gameSaveManager.getLoadedLevel();

            // Reinitialize characters to restore images            
            allies = this.characterManager.reinitializeCharacters(allies);
            enemies = this.characterManager.reinitializeCharacters(enemies);	

            // Set up the game state for the loaded level
            this.setupLoadedGame(numLevel, allies, enemies);

            // Start the current level and game loop
            this.levelManager.startCurrentLevel();
            this.gameLoopManager.startGameLoopForLoad(Game.GAME_LOOP_INTERVAL_MS);

            this.gameSaveManager.clearLoadedGameState();
            this.gameStateManager.startPlayingLevel();

        } 
        catch (IOException | ClassNotFoundException e) 
        {
            System.err.println("Error loading save file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Starts the tutorial mode.
     * 
     * @return true if the tutorial was completed successfully, false otherwise
     */    
    public boolean startTutorial() 
    {
        if (!this.tutorialManager.validateTutorialPrerequisites()) 
        {
            return false;
        }
        
        this.gameStateManager.startTutorial();
        return this.tutorialManager.startTutorial();
    }

    /**
     * Starts the character selection phase of the game.
     */
    public void startSelectionCharacter() 
    {
        this.gameStateManager.setCurrentPhase(GameStateManager.GamePhase.CHARACTER_SELECTION);
        this.controller.startSelectionCharacter();
    }

    /**
     * Initializes all game levels and starts the first level with the game loop.
     * This method sets up the level progression and begins the main game execution thread.
     */
    public void startNewLevel() 
    {
        this.levelManager.initializeGameLevels(this.characterManager.getSelectedAllies());
        
        try 
        {
            this.levelManager.startCurrentLevel();
            this.gameLoopManager.startGameLoop(Game.GAME_LOOP_INTERVAL_MS);
            this.gameStateManager.startPlayingLevel();
        } 
        catch (IOException e) 
        {
            System.err.println("Error starting new level: " + e.getMessage());
            e.printStackTrace();
            this.handleGameError();
        }
    }

    /**
     * Safely updates the game state and handles level progression.
     * This method is called repeatedly by the game loop to check level completion,
     * handle character replacement, and manage transitions between levels.
     */
    private void updateGameSafe()
    {
        // Only update if we're in an active playing state
        if (!this.gameStateManager.isActivelyPlaying()) 
        {
            return;
        }

        this.levelManager.updateCurrentLevel();

        if (this.levelManager.isCurrentLevelCompleted() && !this.gameStateManager.isWaitingForCharacterReplacement())
        {
            this.handleLevelCompleted();
        }
        else if (this.levelManager.isCurrentLevelFailed())
        {
            this.handleLevelFailed();
        }
    }

    /**
     * Handles the completion of a level.
     */
    private void handleLevelCompleted() 
    {
        int currentLevel = this.levelManager.getCurrentLevelIndex();
        System.out.println("Level " + (currentLevel + 1) + " completed.");
        this.controller.stopLevelMusic();

        if (this.levelManager.areAllLevelsCompleted())
        {
            System.out.println("All levels completed!");
            this.handleGameWin();
        }
        else
        {
            this.handleLevelTransition();
        }
    }

    /**
     * Handles the transition between levels.
     */
    private void handleLevelTransition() 
    {
        if (this.characterManager.needsCharacterReplacement())
        {
            this.gameStateManager.setWaitingForCharacterReplacement(true);
            int charactersNeeded = this.characterManager.getCharactersToReplace();
            this.controller.startReplaceDeadAllies(charactersNeeded);
        }
        else
        {
            this.advanceToNextLevel();
        }
    }

    /**
     * Advances to the next level.
     */
    private void advanceToNextLevel() 
    {
        this.characterManager.resetCharacterPositions();
        
        if (this.levelManager.advanceToNextLevel()) 
        {
            try 
            {
                this.levelManager.startCurrentLevel();
            } 
            catch (IOException e) 
            {
                System.err.println("Error starting next level: " + e.getMessage());
                this.handleGameError();
            }
        }
    }

    /**
     * Handles level failure.
     */
    private void handleLevelFailed() 
    {
        int currentLevel = this.levelManager.getCurrentLevelIndex();
        System.out.println("Level " + (currentLevel + 1) + " failed. Game over.");
        this.handleGameLoss();
    }

    /**
     * Handles game win condition.
     */
    private void handleGameWin() 
    {
        this.gameLoopManager.stopGameLoop();
        this.gameStateManager.endGame(true);
        this.controller.endGameMenuShow(true);
    }

    /**
     * Handles game loss condition.
     */
    private void handleGameLoss() 
    {
        this.gameLoopManager.stopGameLoop();
        this.gameStateManager.endGame(false);
        this.controller.endGameMenuShow(false);
    }

    /**
     * Handles game errors by stopping the game loop and showing error state.
     */
    private void handleGameError() 
    {
        this.gameLoopManager.stopGameLoop();
        this.gameStateManager.endGame(false);
        // Could show specific error menu in the future
        this.controller.endGameMenuShow(false);
    }

    /**
     * Marks the character replacement process as completed and advances to the next level.
     */
    public void markCharacterReplacementCompleted()
    {
        this.gameStateManager.markCharacterReplacementCompleted();
        this.advanceToNextLevel();
    }

    /**
     * Sets up the game for a loaded save file.
     * 
     * @param levelIndex the level to load
     * @param allies the allies from the save
     * @param enemies the enemies from the save
     */
    private void setupLoadedGame(int levelIndex, List<Character> allies, List<Character> enemies) 
    {
        this.levelManager.initializeGameLevels(allies);
        this.levelManager.setCurrentLevelIndex(levelIndex);
        this.levelManager.setEnemiesForLevel(levelIndex, enemies);
        this.characterManager.setSelectedAllies(allies);
    }

    // ==================== DELEGATE METHODS ====================

    /**
     * Sets the selected characters for the current game session.
     * 
     * @param selectedAllies the list of characters selected by the player
     */
    public void setSelectedCharacters(List<Character> selectedAllies) 
    {
        this.characterManager.setSelectedCharacters(selectedAllies);
    }

    /**
     * Adds new characters to the selected allies list.
     * 
     * @param selectedAllies the list of characters to add to the current selection
     */
    public void addSelectedCharacters(List<Character> selectedAllies) 
    {
        this.characterManager.addSelectedCharacters(selectedAllies);
    }

    /**
     * Gets the current level index.
     * 
     * @return the index of the current level (0-based)
     */
    public int getCurrentLevelIndex() 
    {
        return this.levelManager.getCurrentLevelIndex();
    }

    /**
     * Sets the current level index.
     * 
     * @param currentLevelIndex the new level index to set
     */
    public void setCurrentLevelIndex(int currentLevelIndex) 
    {
        this.levelManager.setCurrentLevelIndex(currentLevelIndex);
    }

    /**
     * Gets the list of all game levels.
     * 
     * @return the list of game levels
     */
    public List<model.gameStatus.level.GameLevel> getGameLevels() 
    {
        return this.levelManager.getGameLevels();
    }

    /**
     * Gets the list of currently selected allied characters.
     * 
     * @return the list of selected allies
     */
    public List<Character> getSelectedAllies() 
    {
        return this.characterManager.getSelectedAllies();
    }

    /**
     * Gets the list of available allied characters.
     * 
     * @return the list of available allied characters
     */
    public List<Character> getAvailableAllies() 
    {
        return this.characterManager.getAvailableAllies();
    }
    
	/** 
	 * Creates a list of allies for the game.
	 * This method is used to initialize the allies for the game.
	 * 
	 * @return a list of created allies
	 */
	public List<Character> createAllies() 
	{
		return this.characterManager.createAvailableAllies();
	}

    /**
     * Checks if the game is currently waiting for character replacement.
     * 
     * @return true if waiting for character replacement, false otherwise
     */
    public boolean isWaitingForCharacterReplacement() 
    {
        return this.gameStateManager.isWaitingForCharacterReplacement();
    }

    /**
     * Sets the waiting for character replacement flag.
     * 
     * @param waiting true to set the game as waiting for replacement, false otherwise
     */
    public void setWaitingForCharacterReplacement(boolean waiting) 
    {
        this.gameStateManager.setWaitingForCharacterReplacement(waiting);
    }

    /**
     * Closes all game resources and shuts down the current level map.
     */
    public void closeAll()
    {
        this.gameLoopManager.stopGameLoop();
        this.levelManager.closeCurrentLevelMap();
        this.gameStateManager.resetGameState();
    }

    // ==================== GETTER METHODS FOR MANAGERS ====================

    /**
     * Gets the character manager instance.
     * 
     * @return the character manager
     */
    public CharacterManager getCharacterManager() 
    {
        return this.characterManager;
    }

    /**
     * Gets the level manager instance.
     * 
     * @return the level manager
     */
    public LevelManager getLevelManager() 
    {
        return this.levelManager;
    }

    /**
     * Gets the game loop manager instance.
     * 
     * @return the game loop manager
     */
    public GameLoopManager getGameLoopManager() 
    {
        return this.gameLoopManager;
    }

    /**
     * Gets the game state manager instance.
     * 
     * @return the game state manager
     */
    public GameStateManager getGameStateManager() 
    {
        return this.gameStateManager;
    }

    /**
     * Gets the tutorial manager instance.
     * 
     * @return the tutorial manager
     */
    public TutorialManager getTutorialManager() 
    {
        return this.tutorialManager;
    }

    /**
     * Gets the game save manager instance.
     * 
     * @return the game save manager
     */
    public GameSaveManager getGameSaveManager() 
    {
        return this.gameSaveManager;
    }
}