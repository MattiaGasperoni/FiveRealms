package controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import model.characters.Character;
import model.gameStatus.Game;
import model.gameStatus.Level;
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
 * Controller class that coordinates user actions with the game logic.
 * It interacts with the GameStateManager to handle game saving/loading,
 * and manages the movement of characters on the map.
 */
public class GameController 
{
    private Game game;
    private GameSaveManager gameStateManager;
    
    private MainMenu mainMenuView;
    private LoadGameMenu loadGameMenu;
    private TutorialMenu tutorialMenu;
    private CharacterSelectionMenu characterSelectionMenu;
    private CharacterReplaceMenu characterReplaceMenu;
    private EndGameMenu endGameMenu;
    private PauseMenu pauseMenu;

    public GameController(Game game, GameSaveManager gameStateManager, MainMenu mainMenu, LoadGameMenu loadGameMenu,TutorialMenu tutorialMenu, CharacterSelectionMenu characterSelectionMenu,CharacterReplaceMenu characterReplaceMenu, EndGameMenu endGameMenu) 
    {
        this.game             = game;
        this.gameStateManager = gameStateManager;
        
        this.mainMenuView           = mainMenu;
        this.loadGameMenu           = loadGameMenu;
        this.tutorialMenu           = tutorialMenu;
        this.characterSelectionMenu = characterSelectionMenu;
        this.characterReplaceMenu   = characterReplaceMenu;
        this.endGameMenu            = endGameMenu;
        this.pauseMenu              = null;

        this.checkExistSave();
        
        this.setupMainMenuListeners();
        this.setupEndGameListeners();
        this.setupLoadMenuListeners();
    }
        
    private void checkExistSave()
    {
    	// Controlla se esistono salvataggi e abilita/disabilita il pulsante
        this.mainMenuView.setLoadButtonEnabled(this.gameStateManager.hasSaved());
    }
    
    private void setupMainMenuListeners() 
    {
        mainMenuView.addStartListener(event -> 
        {
            System.out.println(" You chose to start a new game.");
            this.mainMenuView.close();
            try
            {
                this.game.startNewGame();
            } 
            catch (IOException error) 
            {
                error.printStackTrace();
            }
        });

        mainMenuView.addLoadListener(event -> 
        {
            System.out.println(" You chose to load a game.");
            this.mainMenuView.close();
            this.loadGameMenu.show();
        });

        mainMenuView.addExitListener(event -> 
        {
            System.out.println(" You chose to close the game.");
            this.mainMenuView.close();
            System.exit(0);
        });
    }
    
    private void setupEndGameListeners()
    {
    	this.endGameMenu.addMainMenuListener(event -> 
    	{
    	    System.out.println(" You chose to return to Main Menu.");
    	    
    	    this.endGameMenu.close();
    	    
    	    this.game = null;
    	    
    	    Game newGame = new Game();
            
    	    newGame.start();
    	    
    	});

    	this.endGameMenu.addExitListener(event -> 
        {
            System.out.println(" You chose to close the game.");
            this.endGameMenu.close();
            System.exit(0);
        });
    }
    
    private void setupLoadMenuListeners() 
    {
    	this.loadGameMenu.addChooseSaveListener(event -> 
        {            
            // Carica i salvataggi disponibili
            File[] saveFiles = this.gameStateManager.getSaveFiles();
            
            //Mostra i salvataggi
            this.loadGameMenu.showSaveFile(saveFiles);
        });

    	this.loadGameMenu.addMainMenuListener(event -> 
        {
            System.out.println(" You chose to go back to main manu.");
            this.loadGameMenu.close();
            
            this.game = null;
    	    
    	    Game newGame = new Game();
            
    	    newGame.start();
        });
    	
    	this.loadGameMenu.addSaveFileClickListener(saveFile -> 
    	{
    		System.out.println("Loading game from: " + saveFile.getAbsolutePath());
    		try 
    		{
                // Chiudi il menu di caricamento
    			this.loadGameMenu.close();
                // Qui implementi la logica di caricamento del gioco
    			System.out.println("Mo si bestemmia");
    			this.game.startLoadGame(saveFile);
            } 
    		catch (Exception error)
    		{
    			 error.printStackTrace();            
		    }
    	});
    	

    }
    
    public void setPauseMenu(PauseMenu pauseMenu)
    {
    	this.pauseMenu = pauseMenu;
    	this.setupPauseMenuListeners();
    }
    
    private void setupPauseMenuListeners()
    {
    	this.pauseMenu.addPauseListener(event -> 
        {
        	// Mostriamo la finestra del menu di pausa
        	this.pauseMenu.show();
        	Level currentLevel = this.game.getGameLevels().get(this.game.getCurrentLevelIndex());
        	currentLevel.setLevelPaused(true);
        });
    	
    	
    	this.pauseMenu.addResumeListener(event -> 
        {
        	// Chiudiamo la finestra del menu di pausa
        	this.pauseMenu.hide();
        	Level currentLevel = this.game.getGameLevels().get(this.game.getCurrentLevelIndex());
        	currentLevel.setLevelPaused(false);
        });

    	
    	this.pauseMenu.addSaveListener(event -> 
    	{    	    
            try 
            {
                this.saveGame();
            } 
            catch (IOException ex)
            {
                System.err.println("Error saving game: " + ex.getMessage());
            }
            finally
            {
            	// Chiudiamo la finestra del menu di pausa
				this.pauseMenu.hide();
				Level currentLevel = this.game.getGameLevels().get(this.game.getCurrentLevelIndex());
	        	currentLevel.setLevelPaused(false);
			}
    	});

    	this.pauseMenu.addMainMenuListener(event -> 
        {
            System.out.println(" You chose to go back to main manu.");
            this.game.closeAll();           
    	    this.game = null;
    	    
    	    Game newGame = new Game();
            
    	    newGame.start();
        });
    }
    
    public void startNewGame() 
    {
    	
        this.tutorialMenu.show();

	    this.tutorialMenu.addYesListener(event -> 
	     {
	         this.tutorialMenu.close();
	         	         
	         // Avvia il tutorial
	         this.game.startTutorial(); 
	         
	     });

        this.tutorialMenu.addNoListener(event -> 
        {
            this.tutorialMenu.close();
            
            // Inizia la selezione dei personaggi
            this.game.startSelectionCharacter();
        });

        this.tutorialMenu.addMainMenuListener(event -> 
        {
    	    this.tutorialMenu.close();
    	    
    	    this.game = null;
    	    
    	    Game newGame = new Game();
            
    	    newGame.start();
        });
    }
        
    /**
     * Metodo chiamato dalla TutorialMap quando tutti i popup sono completati
     * Ora può mostrare il menu di selezione dei personaggi
     */
    public void onTutorialPopupsCompleted() 
    {
        this.game.startSelectionCharacter();
    }
    

    
    public void startSelectionCharacter()
    {
        List<Character> availableCharacter = this.game.createAllies();
        this.characterSelectionMenu.start(availableCharacter);
        
        this.characterSelectionMenu.addNextButtonListener(event -> 
        {
            List<String> characterNames = this.characterSelectionMenu.getSelectedCharacterNames();
            
            List<Character> characterSelected = transformList(availableCharacter, characterNames);
            
            this.game.setSelectedCharacters(characterSelected);
            
            this.characterSelectionMenu.close();

            this.game.startNewLevel();
        });
    }
    
    public void startReplaceDeadAllies(int alliesToChange)
    {        
    	List<Character> availableAllies = this.getCharacterToChange(this.game.createAllies(), this.game.getSelectedAllies());
        
        this.characterReplaceMenu.start(availableAllies,alliesToChange);
        
        this.characterReplaceMenu.addNextButtonListener(event -> 
        {            
            List<Character> characterSelected = this.transformList(availableAllies, this.characterReplaceMenu.getSelectedCharacterNames());
            
            this.game.addSelectedCharacters(characterSelected);
            
            // Imposta le posizioni a null per lo spawn nel livello successivo
        	for (Character character : this.game.getSelectedAllies()) 
        	{
        	    character.setPosition(null);
        	}
        	      
            this.characterReplaceMenu.close();
            System.out.print("Fine sostituzione personaggio");

            // Segnala che la sostituzione è completata
            this.game.markCharacterReplacementCompleted();
        });
    }
    
    
    private List<Character> transformList(List<Character> allAllies, List<String> selectedCharacters)
    {
        return allAllies.stream()
            .filter(ally -> selectedCharacters.contains(ally.getClass().getSimpleName()))
            .collect(Collectors.toList());
    }
    

    private List<Character> getCharacterToChange(List<Character> allAllies, List<Character> currentAllies) 
    {
        List<Character> remainingAllies = new ArrayList<>(allAllies);
        remainingAllies.removeAll(currentAllies);
        return remainingAllies;
    }
    /**
     * Saves the current game state, including allies, enemies, and the level.
     * @param allies List of ally characters.
     * @param enemies List of enemy characters.
     * @param level The current game level.
     * @throws IOException If an error occurs during saving.
     */
    public void saveGame() throws IOException 
    {
    	Level currentLevel = this.game.getGameLevels().get(this.game.getCurrentLevelIndex());
    	
    	GameSave currentGameState = new GameSave(this.game.getCurrentLevelIndex(), currentLevel.getAllies(), currentLevel.getEnemies());
    	
        this.gameStateManager.saveGameState(currentGameState,null);
        
    }

    /**
     * Loads the most recent saved game state.
     * @return The loaded GameState object.
     * @throws IOException If an error occurs during loading.
     * @throws ClassNotFoundException 
     */
    public GameSave loadGame() throws IOException, ClassNotFoundException 
    {
        return gameStateManager.loadGameState(null); 
    }
    
    /**
     * Moves the given character to a new point on the map.
     * @param map The game map where the character resides.
     * @param character The character to be moved.
     * @param point The destination point on the map.
     */
    public void move(AbstractMap map, Character character, Point point) 
    {
        if (map == null || character == null || point == null) 
        {
            throw new IllegalArgumentException("Map, character, and point must not be null");
        }        
        map.moveCharacter(character, point);  // Move the character on the map
        character.moveTo(point);  // Update the character's position
    }
    
    public void remove(AbstractMap map, Character deadCharacter, Point point, List<Character> listOfTheDead) 
    {
        if (map == null || deadCharacter == null || point == null || listOfTheDead == null) 
        {
            throw new IllegalArgumentException("Map, deadCharacter, point and listOfTheDead must not be null");
        } 
        listOfTheDead.remove(deadCharacter);
        map.removeCharacter(deadCharacter); 
        
    }
    
    public void fight(Character attackingCharacter, Character attackedCharacter, List<Character> alliedList, List<Character> enemyList, AbstractMap levelMap) 
    {
        Character deadCharacter = attackingCharacter.fight(attackedCharacter);
        
        if(deadCharacter != null)
        {
            this.remove(levelMap, deadCharacter, deadCharacter.getPosition(), (deadCharacter.isAllied()? alliedList : enemyList));
        }
        else
        {
        	levelMap.updateToolTip();
        }
    }

	public int getLevelIndex() 
	{
		return this.game.getCurrentLevelIndex();
	}
    
    
}