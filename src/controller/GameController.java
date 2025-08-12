package controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import model.characters.Archer;
import model.characters.Barbarian;
import model.characters.Character;
import model.characters.Juggernaut;
import model.characters.Knight;
import model.characters.Wizard;
import model.gameStatus.Game;
import model.gameStatus.Level;
import model.gameStatus.MusicManager;
import model.gameStatus.saveSystem.GameSave;
import model.gameStatus.saveSystem.GameSaveManager;
import model.point.Point;
import view.PauseMenu;
import view.map.AbstractMap;
import view.menu.EndGameMenu;
import view.menu.LoadGameMenu;
import view.menu.MainMenu;
import view.menu.TutorialMenu;
import view.selectionMenu.CharacterReplaceMenu;
import view.selectionMenu.CharacterSelectionMenu;

/**
 * Coordinates user actions with the game logic.
 * <p>
 * The controller manages the interaction between the game's model layer
 * and various UI menus, including saving/loading, character management,
 * and in-game operations such as movement and combat.
 */
public class GameController 
{

    /* ==============================
       Fields & Dependencies
       ============================== */
    private Game game;
    private final GameSaveManager gameSaveManager;
    private final MusicManager musicManager;

    // View references
    private MainMenu mainMenu;
    private LoadGameMenu loadGameMenu;
    private TutorialMenu tutorialMenu;
    private CharacterSelectionMenu characterSelectionMenu;
    private CharacterReplaceMenu characterReplaceMenu;
    private EndGameMenu endGameMenu;
    private PauseMenu pauseMenu;

    /* ==============================
       Constructor
       ============================== */

    /**
     * Creates a GameController instance.
     *
     * @param game initial game instance
     * @param gsm  save manager for game persistence
     */
    public GameController(Game game, GameSaveManager gsm) 
    {
        this.game            = game;
        this.gameSaveManager = gsm;
        this.musicManager    = new MusicManager();

        this.initUI();
        this.checkExistSave();
        this.setupMainMenuListeners();
        this.setupEndGameListeners();
        this.setupLoadMenuListeners();
    }

    /* ==============================
       Initialization
       ============================== */

    /** Initializes UI components. */
    private void initUI() 
    {
        this.mainMenu     = new MainMenu();
        this.loadGameMenu = new LoadGameMenu();
        this.tutorialMenu = new TutorialMenu();
        this.endGameMenu  = new EndGameMenu();
        this.characterSelectionMenu = new CharacterSelectionMenu();
        this.characterReplaceMenu   = new CharacterReplaceMenu();
    }

    /** Enables/disables Load button depending on save existence. */
    private void checkExistSave() 
    {
        this.mainMenu.setLoadButtonEnabled(this.gameSaveManager.hasSaved());
    }

    /* ==============================
       Menu Navigation
       ============================== */

    /** Displays the main menu. */
    public void mainMenuShow() 
    {
    	this.musicManager.play("background", true);
        this.mainMenu.show();
    }

    /**
     * Displays the end-game menu with the given result.
     *
     * @param result true if the player won, false otherwise
     */
    public void endGameMenuShow(boolean result) 
    {
    	this.musicManager.play((result ? "win" : "lose"), true);
        this.endGameMenu.setGameResult(result);
        this.endGameMenu.show();
    }

    /* ==============================
       Listener Setup
       ============================== */

    /** Configures listeners for the main menu. */
    private void setupMainMenuListeners() 
    {
    	this.mainMenu.addStartListener(e -> 
        {
        	this.log("Starting a new game...");
            this.mainMenu.close();
            try 
            {
            	this.game.startNewGame();
            } 
            catch (IOException ex) 
            {
                ex.printStackTrace();
            }
        });

    	this.mainMenu.addLoadListener(e -> 
        {
        	this.log("Loading game menu...");
        	this.mainMenu.close();
        	this.loadGameMenu.show();
        });

        this.mainMenu.addExitListener(e -> this.exitGame());
    }

    /** Configures listeners for the end-game menu. */
    private void setupEndGameListeners() 
    {
    	this.endGameMenu.addMainMenuListener(e -> 
    	{
			this.musicManager.stop();
			this.endGameMenu.close();
            this.restartGameFromMenu();
		}); 
    	
    	this.endGameMenu.addExitListener(e -> this.exitGame());
    }

    /** Configures listeners for the load-game menu. */
    private void setupLoadMenuListeners() 
    {
    	this.loadGameMenu.addChooseSaveListener(e -> 
    	{
            File[] saveFiles = this.gameSaveManager.getSaveFiles();
            this.loadGameMenu.showSaveFile(saveFiles);
        });

    	this.loadGameMenu.addMainMenuListener(e -> 
    	{
        	this.loadGameMenu.close();
            this.restartGameFromMenu();
		});    

    	this.loadGameMenu.addSaveFileClickListener(file -> 
    	{
    		this.log("Loading game from: " + file.getAbsolutePath());
            try 
            {
            	this.loadGameMenu.close();
                this.game.startLoadGame(file);
            } 
            catch (Exception ex) 
            {
                ex.printStackTrace();
            }
        });
    }

    /** Sets the pause menu and configures its listeners. */
    public void setPauseMenu(PauseMenu pauseMenu) 
    {
        this.pauseMenu = pauseMenu;
        this.setupPauseMenuListeners();
    }

    /** Configures listeners for the pause menu. */
    private void setupPauseMenuListeners() 
    {
    	this.pauseMenu.addPauseListener(e -> 
        {
        	this.musicManager.stop();
        	this.pauseMenu.show();
        	this.getCurrentLevel().setLevelPaused(true);
        });

    	this.pauseMenu.addResumeListener(e -> 
        {
        	this.pauseMenu.hide();
        	this.musicManager.play("level" + (this.getLevelIndex()+1), true);
        	this.getCurrentLevel().setLevelPaused(false);
        });

    	this.pauseMenu.addSaveListener(e -> 
        {
            try 
            {
            	this.saveGame();
            } 
            catch (IOException ex) 
            {
            	this.log("Error saving game: " + ex.getMessage());
            } 
            finally 
            {
            	this.pauseMenu.hide();
            	this.musicManager.play("level" + (this.getLevelIndex()+1), true);
            	this.getCurrentLevel().setLevelPaused(false);
            }
        });

    	this.pauseMenu.addMainMenuListener(e -> 
    	{
    		this.pauseMenu.hide();
    		this.game.closeAll();
	    	this.restartGameFromMenu();
    	});
    }

    /* ==============================
       Game Flow
       ============================== */

    /** Starts a new game with optional tutorial. */
    public void startNewGame() 
    {
    	this.tutorialMenu.show();

    	this.tutorialMenu.addYesListener(e -> 
    	{
    		this.tutorialMenu.close();
    		this.musicManager.play("tutorial", true);
    		this.game.startTutorial();
        });

    	this.tutorialMenu.addNoListener(e -> 
    	{
    		this.tutorialMenu.close();
    		this.game.startSelectionCharacter();
        });

    	this.tutorialMenu.addMainMenuListener(e -> 
    	{
    		this.tutorialMenu.close();
    		this.restartGameFromMenu();
    	});
    }

    /** Called when tutorial popups are completed. */
    public void onTutorialPopupsCompleted() 
    {
    	this.musicManager.stop();
    	this.game.startSelectionCharacter();
    }

    /** Starts the character selection phase. */
    public void startSelectionCharacter() 
    {
    	this.musicManager.play("background", true);
        List<Character> available = this.game.createAllies();
        
        this.characterSelectionMenu.start();

        this.characterSelectionMenu.addNextButtonListener(e -> 
        {
            List<Character> chosen = transformList(available, this.characterSelectionMenu.getSelectedCharacterNames());
            this.game.setSelectedCharacters(chosen);
            this.characterSelectionMenu.close();
            this.characterSelectionMenu = null;
            this.musicManager.stop();
            this.game.startNewLevel();
        });
    }

    /** Starts the replacement of dead allies. */
    public void startReplaceDeadAllies(int alliesToChange) 
    {
    	this.musicManager.play("background", true);

        if (this.characterReplaceMenu == null) 
        {
        	this.characterReplaceMenu = new CharacterReplaceMenu();
        }
        List<Character> available = this.getAvailableAllies();
       
        // Debug: stampa tutti available
    	System.out.println("\n[CONTROLLER] Lista Available: ");
    	for (Character elemento : available) 
    	{
    	    System.out.println(elemento.getClass().getSimpleName() + ", " 
    	        + (elemento.isAlive()  ? "vivo" : "morto") + ", " 
    	        + (elemento.isAllied() ? "alleato" : "nemico"));
    	}
        
    	this.characterReplaceMenu.start(alliesToChange);

        this.characterReplaceMenu.addNextButtonListener(e ->
        {
        	List<Character> chosen = transformList(available, this.characterReplaceMenu.getSelectedCharacterNames());

        	// Debug: stampa tutti quelli scelti dal menu (anche sbagliati)
        	System.out.println("\n[CONTROLLER] Alleati in transformList:");
        	for (Character elemento : chosen) 
        	{
        	    System.out.println(elemento.getClass().getSimpleName() + ", " 
        	        + (elemento.isAlive() ? "vivo" : "morto") + ", " 
        	        + (elemento.isAllied() ? "alleato" : "nemico"));
        	}

        	System.out.println("\nAggiunti personaggi in selectedCharacter");
        	// Aggiunta e controlli come prima
        	this.game.addSelectedCharacters(chosen);
        	
        	System.out.println("\n[CONTROLLER] Personaggi in selectedCharacter:");
        	for (Character c : this.game.getSelectedAllies()) 
        	{
        	    System.out.println(c.getClass().getSimpleName() + ", " 
        	        + (c.isAlive() ? "vivo" : "morto") + ", " 
        	        + (c.isAllied() ? "alleato" : "nemico"));
        	}
        	
        	System.out.println("\nFiltraggio selectedCharacter per sicurezza...");
        	// Filtro per debug: scarta quelli non validi
        	this.game.getSelectedAllies().stream()
        	    .filter(c -> c != null && c.isAlive() && c.isAllied())
        	    .toList();

        	// Debug: mostra chi rimane dopo il filtro
        	this.game.getSelectedAllies().forEach(c -> System.out.println("✅ Personaggio valido: " + c.getClass().getSimpleName()));
        	
        	
        	this.game.getSelectedAllies().forEach(c -> c.setPosition(null));

        	
        	if (this.game.getSelectedAllies().size() > 3) 
        	{
        	    System.err.println("\nBUG: Removing last inserted character due to overflow.");
        	    for (int i = this.game.getSelectedAllies().size(); i > 3; i--) 
			    {
			        this.game.getSelectedAllies().removeLast();
			    }
        	    
        	}

        	this.musicManager.stop();
        	this.game.markCharacterReplacementCompleted();
        	this.characterReplaceMenu.close();
        	this.characterReplaceMenu = null;


        });
    }

    /* ==============================
       Persistence
       ============================== */

	/** Saves the current game state. */
    public void saveGame() throws IOException 
    {
        Level currentLevel = this.getCurrentLevel();
        GameSave state = new GameSave(this.game.getCurrentLevelIndex(), currentLevel.getAllies(), currentLevel.getEnemies());
        this.gameSaveManager.saveGameState(state, null);
    }

    /** Loads the most recent saved game state. */
    public GameSave loadGame() throws IOException, ClassNotFoundException 
    {
        return this.gameSaveManager.loadGameState(null);
    }
    
    /** Start the specific level music */
    public void startLevelMusic(int levelIndex) 
	{
		this.musicManager.play("level" + levelIndex, true);
	}
    
    /** Stops the current level music. */
    public void stopLevelMusic()
    {
		this.musicManager.stop();
	}

    /* ==============================
       Gameplay Actions
       ============================== */

    /** Moves a character to a new point on the map. */
    public void move(AbstractMap map, Character character, Point point) 
    {
    	this.validateNotNull(map, character, point);
        map.moveCharacter(character, point);
        character.moveTo(point);
    }

    /** Removes a character from the map and the given list. */
    public void remove(AbstractMap map, Character deadCharacter, Point point, List<Character> listOfTheDead) 
    {
    	this.validateNotNull(map, deadCharacter, point, listOfTheDead);
        listOfTheDead.remove(deadCharacter);
        map.removeCharacter(deadCharacter);
    }

    /** Executes a fight action between two characters. */
    public void fight(Character attacker, Character defender, List<Character> allies, List<Character> enemies, AbstractMap map) 
    {
        Character dead = attacker.fight(defender);
        if (dead != null) 
        {
        	this.remove(map, dead, dead.getPosition(), dead.isAllied() ? allies : enemies);
        } 
        else 
        {
            map.updateToolTip();
        }
    }

    /* ==============================
       Utility & Helpers
       ============================== */

    /** Restarts the game by returning to the main menu. */
    private void restartGameFromMenu() 
    {
    	this.log("Returning to main menu...");
        
        this.game = new Game();
        this.game.start();
    }

    /** Exits the application. */
    private void exitGame() 
    {
    	this.log("Exiting game...");
        System.exit(0);
    }

    /** Logs messages to stdout (placeholder for real logger). */
    private void log(String msg) 
    {
        System.out.println(msg);
    }

    /** Validates that arguments are not null. */
    private void validateNotNull(Object... objs) 
    {
        for (Object o : objs) {
            if (o == null) throw new IllegalArgumentException("Argument must not be null");
        }
    }

    /** Retrieves the current level from the game. */
    private Level getCurrentLevel() 
    {
        return this.game.getGameLevels().get(this.getLevelIndex());
    }

    /** Filters the given list of allies by selected names. */
    private List<Character> transformList(List<Character> allAllies, List<String> selectedNames) 
    {
        return allAllies.stream()
                .filter(ally -> selectedNames.contains(ally.getClass().getSimpleName()))
                .collect(Collectors.toList());
    }

    /** Returns the current level index. */
    public int getLevelIndex() 
    {
        return this.game.getCurrentLevelIndex();
    }
    
    
    private List<Character> getAvailableAllies() 
    {
		List<Character> allies = new ArrayList<>();
		allies.add(new Barbarian());
		allies.add(new Archer());
		allies.add(new Knight());
		allies.add(new Wizard());
		allies.add(new Juggernaut());
		return allies;
	}
}
