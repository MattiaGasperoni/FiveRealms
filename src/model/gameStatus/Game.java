package model.gameStatus;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import model.characters.*;
import model.characters.Character;
import model.characters.bosses.*;
import model.gameStatus.saveSystem.GameSaveManager;
import view.map.*;
import controller.*;

public class Game 
{
	/** Total number of levels in the game */
	public static final int TOTAL_LEVEL = 5;
	/** Maximum number of allied characters allowed per round */
	public static final int MAX_ALLIES_PER_ROUND = 3;

	private List<GameLevel> gameLevels;
	private List<Character> availableAllies;
	private List<Character> selectedAllies;
	/** Flag indicating if the game is waiting for character replacement between levels */
	private boolean waitingForCharacterReplacement;
	/** Index of the current level being played */
	private int currentLevelIndex = 0;

	/** Manager for game save and load operations */
	private GameSaveManager gameSaveManager;
	/** Main game controller for UI and game logic coordination */
	private GameController controller;
	/** Executor service for managing the game loop thread */
	private ScheduledExecutorService gameExecutor;

	/**
	 * Constructs a new Game instance and initializes all necessary components.
	 * Sets up empty lists for levels and characters, initializes the save manager
	 * and controller, and sets initial game state values.
	 */
	public Game() 
	{
		this.gameLevels        = new ArrayList<>();
		this.availableAllies   = new ArrayList<>();
		this.selectedAllies    = new ArrayList<>();

		this.waitingForCharacterReplacement = false;
		this.currentLevelIndex = 0;

		this.gameSaveManager   = new GameSaveManager();

		this.controller   = new GameController(this, this.gameSaveManager);
	}

	/**
	 * Starts the game by displaying the main menu.
	 * This is the entry point for the game application.
	 */
	public void start() 
	{
		this.controller.mainMenuShow();	
	}

	/**
	 * Starts a new game session by initializing a fresh game state.
	 * 
	 * @throws IOException if there are issues initializing game resources
	 */
	public void startNewGame() throws IOException 
	{
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
			if (this.gameExecutor == null) 
			{
				this.gameExecutor = Executors.newScheduledThreadPool(1);
			}

			this.initializeGameLevels();

			this.gameSaveManager.loadFileInfo(saveFile);

			List<Character> allies  = this.gameSaveManager.getLoadedAllies();
			List<Character> enemies = this.gameSaveManager.getLoadedEnemies();
			int numLevel            = this.gameSaveManager.getLoadedLevel();

			// Reinitialize characters to restore images            
			allies  = this.reinitializeCharacters(allies);
			enemies = this.reinitializeCharacters(enemies);	

			// Set the level settings to resume from the loaded state
			this.setGameLevelValue(numLevel, allies, enemies);

			this.startCurrentLevel();

			this.gameSaveManager.clearLoadedGameState();

			// Start the game loop
			this.gameExecutor.scheduleAtFixedRate(() -> 
			{
				try 
				{
					this.updateGameSafe();
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}, 0, 100, TimeUnit.MILLISECONDS);

		} 
		catch (IOException | ClassNotFoundException e) 
		{
			System.err.println("Error loading save file: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Starts the tutorial mode with predefined characters and enemies.
	 * The tutorial provides a controlled environment for players to learn game mechanics.
	 * 
	 * @return true if the tutorial was completed successfully, false otherwise
	 */    public boolean startTutorial() 
	 {
		 List<Character> tutorialEnemies = new ArrayList<>();
		 tutorialEnemies.add(new Barbarian());
		 tutorialEnemies.add(new Archer());
		 tutorialEnemies.add(new Knight());

		 List<Character> tutorialAllies = new ArrayList<>();
		 tutorialAllies.add(new Barbarian());
		 tutorialAllies.add(new Archer());
		 tutorialAllies.add(new Knight());

		 for(Character ally : tutorialAllies) 
		 {
			 ally.becomeHero();
		 }

		 GameTutorial tutorial = new GameTutorial(new TutorialMap(tutorialEnemies, tutorialAllies, this.controller),this.controller);

		 return tutorial.play();
	 }

	 /**
	  * Starts the character selection phase of the game.
	  * Players choose their team of allied characters for the upcoming levels.
	  */
	 public void startSelectionCharacter() 
	 {
		 this.controller.startSelectionCharacter();
	 }

	 /**
	  * Initializes all game levels and starts the first level with the game loop.
	  * This method sets up the level progression and begins the main game execution thread.
	  */
	 public void startNewLevel() 
	 {
		 this.initializeGameLevels(); 

		 this.startCurrentLevel();

		 // Start a separate thread for game logic
		 this.gameExecutor = Executors.newSingleThreadScheduledExecutor();

		 this.gameExecutor.scheduleAtFixedRate(() -> 
		 {
			 try 
			 {
				 this.updateGameSafe();
			 } 
			 catch (Exception e) 
			 {
				 e.printStackTrace();
			 }
		 }, 0, 100, TimeUnit.MILLISECONDS);
	 }

	 /**
	  * Starts the current level based on the current level index.
	  * This method initializes the level, starts appropriate music, and begins level play.
	  */
	 public void startCurrentLevel() 
	 {
		 try 
		 {
			 GameLevel livello = this.gameLevels.get(this.currentLevelIndex);
			 this.controller.startLevelMusic(this.currentLevelIndex+1);
			 livello.play();
		 } 
		 catch (IOException e) 
		 {
			 System.err.println("Error during level " + this.currentLevelIndex + ": " + e.getMessage());
			 e.printStackTrace();
			 this.stopGameLoop();
		 }
	 }

	 /**
	  * Safely updates the game state and handles level progression.
	  * This method is called repeatedly by the game loop to check level completion,
	  * handle character replacement, and manage transitions between levels.
	  */
	 private void updateGameSafe()
	 {
		 GameLevel livello = this.gameLevels.get(this.currentLevelIndex);

		 livello.update();

		 if (livello.isCompleted() && !this.isWaitingForCharacterReplacement())
		 {
			 System.out.println("Level " + (this.currentLevelIndex+1) + " completed.");
			 this.controller.stopLevelMusic();

			 if ((this.currentLevelIndex+1)  >=  Game.TOTAL_LEVEL)
			 {
				 System.out.println("All levels completed!");
				 this.stopGameLoop();
				 this.showEndGameMenu(true);
			 }
			 else
			 {
				 if(this.selectedAllies.size() < 3)
				 {
					 this.setWaitingForCharacterReplacement(true);

					 /* for debugging purposes
					 System.out.println("\n[GAME] Allies remaining from level " + (this.currentLevelIndex+1) + ":");
					 for (Character elemento : this.selectedAllies) 
					 {
						 System.out.println(elemento.getClass().getSimpleName()+", "+ (elemento.isAlive() ? "alive": "dead") + ", "+(elemento.isAllied() ? "ally": "enemy"));
					 }*/

					 this.controller.startReplaceDeadAllies(Game.MAX_ALLIES_PER_ROUND - this.selectedAllies.size());
				 }
				 else
				 {
					 // Set positions to null for spawning in the next level
					 for (Character character : this.selectedAllies)
					 {
						 character.setPosition(null);
					 }

					 this.currentLevelIndex++;
					 this.startCurrentLevel();
				 }
			 }
		 }
		 else if (livello.isFailed())
		 {
			 System.out.println("Level " + this.currentLevelIndex + " failed. Exiting.");
			 this.stopGameLoop();
			 this.showEndGameMenu(false);
		 }
	 }

	 /**
	  * Checks if the game is currently waiting for character replacement.
	  * 
	  * @return true if waiting for character replacement, false otherwise
	  */
	 public boolean isWaitingForCharacterReplacement() 
	 {
		 return this.waitingForCharacterReplacement;
	 }

	 /**
	  * Sets the waiting for character replacement flag.
	  * 
	  * @param waiting true to set the game as waiting for replacement, false otherwise
	  */
	 public void setWaitingForCharacterReplacement(boolean waiting) {
		 this.waitingForCharacterReplacement = waiting;
	 }

	 /**
	  * Marks the character replacement process as completed and advances to the next level.
	  * This method resets character positions and increments the level index.
	  */
	 public void markCharacterReplacementCompleted()
	 {
		 this.waitingForCharacterReplacement = false;

		 for (Character character : this.selectedAllies)
		 {
			 character.setPosition(null);
		 }

		 this.currentLevelIndex++;
		 System.out.print("Incrementing the level");
		 this.startCurrentLevel();
	 }

	 /**
	  * Displays the end game menu with the specified result.
	  * 
	  * @param result true if the player won, false if they lost
	  */
	 private void showEndGameMenu(boolean result) 
	 {
		 this.controller.endGameMenuShow(result);	
	 }

	 /**
	  * Stops the game loop by shutting down the executor service.
	  */
	 private void stopGameLoop() 
	 {
		 if (this.gameExecutor != null && !this.gameExecutor.isShutdown()) 
		 {
			 this.gameExecutor.shutdownNow();
		 }
	 }

	 /**
	  * Creates and returns the list of all available allied characters.
	  * This method populates the available allies list with all character types.
	  * 
	  * @return the list of available allied characters
	  */
	 public List<Character> createAllies() 
	 {
		 this.availableAllies.add(new Barbarian());
		 this.availableAllies.add(new Archer());
		 this.availableAllies.add(new Knight());
		 this.availableAllies.add(new Wizard());
		 this.availableAllies.add(new Juggernaut());

		 return this.availableAllies;
	 }

	 /**
	  * Sets the selected characters for the current game session.
	  * All selected characters are converted to hero versions with enhanced stats.
	  * 
	  * @param selectedAllies the list of characters selected by the player
	  */
	 public void setSelectedCharacters(List<Character> selectedAllies) 
	 {
		 this.selectedAllies = selectedAllies;

		 for(Character ally : this.selectedAllies) 
		 {
			 ally.becomeHero();
		 }
	 }

	 /**
	  * Adds new characters to the selected allies list.
	  * Only valid and alive characters are added and converted to heroes.
	  * 
	  * @param selectedAllies the list of characters to add to the current selection
	  */
	 public void addSelectedCharacters(List<Character> selectedAllies) 
	 {
		 for (Character character : selectedAllies) 
		 {
			 if (character != null && character.isAlive()) 
			 {
				 this.selectedAllies.add(character);
				 character.becomeHero();
			 } 
			 else 
			 {
				 System.out.println("Skipping invalid or dead character: " + (character == null ? "null" : character.getClass().getSimpleName()));
			 }
		 }
	 }

	 /**
	  * Initializes all five game levels with their respective enemy compositions.
	  * Each level features different enemy types and bosses with increasing difficulty.
	  */
	 private void initializeGameLevels()
	 {    	
		 List<Character> level1Enemies = new ArrayList<>();
		 level1Enemies.add(new KnightBoss());
		 level1Enemies.add(new Knight());
		 level1Enemies.add(new Barbarian());

		 List<Character> level2Enemies = new ArrayList<>();
		 level2Enemies.add(new BarbarianBoss());
		 level2Enemies.add(new Barbarian());
		 level2Enemies.add(new Barbarian());
		 level2Enemies.add(new Archer());

		 List<Character> level3Enemies = new ArrayList<>();
		 level3Enemies.add(new ArcherBoss());
		 level3Enemies.add(new Archer());
		 level3Enemies.add(new Archer());
		 level3Enemies.add(new Juggernaut());

		 List<Character> level4Enemies = new ArrayList<>();
		 level4Enemies.add(new JuggernautBoss());
		 level4Enemies.add(new Barbarian());
		 level4Enemies.add(new Wizard());
		 level4Enemies.add(new Knight());
		 level4Enemies.add(new Juggernaut());

		 List<Character> level5Enemies = new ArrayList<>();
		 level5Enemies.add(new WizardBoss());
		 level5Enemies.add(new JuggernautBoss());
		 level5Enemies.add(new KnightBoss());
		 level5Enemies.add(new BarbarianBoss());
		 level5Enemies.add(new ArcherBoss());

		 this.gameLevels.add(new GameLevel(new LevelMap(level1Enemies, this.selectedAllies, 1, this.controller), this.controller));
		 this.gameLevels.add(new GameLevel(new LevelMap(level2Enemies, this.selectedAllies, 2, this.controller), this.controller));
		 this.gameLevels.add(new GameLevel(new LevelMap(level3Enemies, this.selectedAllies, 3, this.controller), this.controller));
		 this.gameLevels.add(new GameLevel(new LevelMap(level4Enemies, this.selectedAllies, 4, this.controller), this.controller));
		 this.gameLevels.add(new GameLevel(new LevelMap(level5Enemies, this.selectedAllies, 5, this.controller), this.controller));
	 }

	 /**
	  * Gets the current level index.
	  * 
	  * @return the index of the current level (0-based)
	  */
	 public int getCurrentLevelIndex() {
		 return this.currentLevelIndex;
	 }

	 /**
	  * Sets the current level index.
	  * 
	  * @param currentLevelIndex the new level index to set
	  */
	 public void setCurrentLevelIndex(int currentLevelIndex) {
		 this.currentLevelIndex = currentLevelIndex;
	 }

	 /**
	  * Gets the list of all game levels.
	  * 
	  * @return the list of game levels
	  */
	 public List<GameLevel> getGameLevels() {
		 return this.gameLevels;
	 }   

	 /**
	  * Gets the list of currently selected allied characters.
	  * 
	  * @return the list of selected allies
	  */
	 public List<Character> getSelectedAllies() {
		 return this.selectedAllies;
	 }

	 /**
	  * Sets the game level values for loading a saved game state.
	  * This method configures the specified level with the loaded allies and enemies.
	  * 
	  * @param index the level index to set
	  * @param allies the list of allied characters to set
	  * @param enemies the list of enemy characters to set
	  */
	 private void setGameLevelValue(int index, List<Character> allies, List<Character> enemy)
	 {
		 this.currentLevelIndex = index;

		 this.gameLevels.get(index).setEnemiesList(enemy);

		 for (GameLevel level : this.gameLevels)
		 {
			 level.setAlliesList(allies);
		 }

		 this.selectedAllies = allies;
	 }

	 /**
	  * Re-initializes a list of characters after loading from a save file.
	  * This method handles restoration of non-serializable data such as images
	  * and validates that characters are in a valid state.
	  * 
	  * @param characters the list of characters to reinitialize
	  * @return the list of successfully reinitialized characters
	  */
	 private List<Character> reinitializeCharacters(List<Character> characters) 
	 {
		 List<Character> reinitializedCharacters = new ArrayList<>();

		 for (Character character : characters) 
		 {
			 try 
			 {
				 if (!this.isCharacterValid(character)) 
				 {
					 continue; 
				 }

				 // for image and icon reapplication reasons
				 character.reinitializeAfterLoad();

				 reinitializedCharacters.add(character);
			 } 
			 catch (Exception e) 
			 {
				 System.err.println("Error reinitializing character: " + e.getMessage());
			 }
		 }

		 return reinitializedCharacters;
	 }

	 /**
	  * Validates whether a character is in a valid state for game play.
	  * Checks for null references, valid position, and positive health.
	  * 
	  * @param character the character to validate
	  * @return true if the character is valid, false otherwise
	  */
	 private boolean isCharacterValid(Character character) 
	 {
		 if (character == null) {
			 return false;
		 }

		 if (character.getPosition() == null) 
		 {
			 System.out.println("Character has null position: " + character.getClass().getSimpleName());
			 return false;
		 }

		 if (character.getCurrentHealth() <= 0) 
		 {
			 System.out.println("Character is dead: " + character.getClass().getSimpleName());
			 return false;
		 }
		 System.out.println("Validooo");
		 return true;
	 }

	 /**
	  * Closes all game resources and shuts down the current level map.
	  */
	 public void closeAll()
	 {
		 this.gameLevels.get(this.currentLevelIndex).getLevelMap().close();
	 }

	 /**
	  * Gets the list of available allied characters.
	  * If the list is empty, it creates the allies first.
	  * 
	  * @return the list of available allied characters
	  */
	 public List<Character> getAvailableAllies() 
	 {
		 if (this.availableAllies.isEmpty()) 
		 {
			 this.createAllies();
		 }
		 return this.availableAllies;
	 }
}