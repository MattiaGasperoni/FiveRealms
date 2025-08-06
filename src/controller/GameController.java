package controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import model.characters.Character;
import model.gameStatus.Game;
import model.gameStatus.Level;
import model.gameStatus.saveSystem.GameState;
import model.gameStatus.saveSystem.GameStateManager;
import model.point.Point;
import view.CharacterSelectionMenu;
import view.EndGameMenu;
import view.PauseMenu;
import view.TutorialMenu;
import view.map.AbstractMap;
import view.map.TutorialMap;
import view.menu.LoadGameMenu;
import view.menu.MainMenu;

/**
 * Controller class that coordinates user actions with the game logic.
 * It interacts with the GameStateManager to handle game saving/loading,
 * and manages the movement of characters on the map.
 */
public class GameController 
{
    private Game game;
    private GameStateManager gameStateManager;
    
    private MainMenu mainMenuView;
    private LoadGameMenu loadGameMenu;
    private TutorialMenu tutorialMenuView;
    private CharacterSelectionMenu characterSelectionMenuView;
    private EndGameMenu endGameMenu;
    private PauseMenu pauseMenu;

    private TutorialMap currentTutorialMap; // Aggiunto per tenere riferimento alla mappa tutorial
    private boolean isTutorialMode = false; // Flag per sapere se siamo in modalità tutorial

    public GameController(Game game, GameStateManager gameStateManager, MainMenu mainMenu, LoadGameMenu loadGameMenu,TutorialMenu tutorialMenu, CharacterSelectionMenu characterSelectionMenu, EndGameMenu endGameMenu) 
    {
        this.game             = game;
        this.gameStateManager = gameStateManager;
        
        this.mainMenuView               = mainMenu;
        this.loadGameMenu               = loadGameMenu;
        this.tutorialMenuView           = tutorialMenu;
        this.characterSelectionMenuView = characterSelectionMenu;
        this.endGameMenu                = endGameMenu;
        this.pauseMenu                  = null;

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
            this.mainMenuView.show();
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
    			 error.printStackTrace();            }
    	});
    	

    }
    
    public void setPauseMenu(PauseMenu pauseMenu)
    {
    	this.pauseMenu = pauseMenu;
    	this.setupPauseListeners();
    }
    
    private void setupPauseListeners()
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
			}
    	});

    	this.pauseMenu.addExitListener(event -> 
        {
            System.out.println(" You chose to close the game.");
            System.exit(0);
        });
    }
    
    public void startNewGame() 
    {
        this.tutorialMenuView.show();

	     // E modifica il listener del pulsante "Yes" così:
	     this.tutorialMenuView.addYesListener(event -> 
	     {
	         // Controllo se il tutorial è già attivo
	         if (isTutorialMode) {
	             System.out.println("Tutorial già in esecuzione, ignoro la richiesta.");
	             return;
	         }
	         
	         this.tutorialMenuView.close();
	         System.out.println(" Yes, start play the tutorial");
	         
	         this.isTutorialMode = true;
	         
	         // Avvia il tutorial ma NON la selezione dei personaggi
	         boolean tutorialSuccess = this.game.startTutorial(); 
	         
	         if (!tutorialSuccess) 
	         {
	             System.out.println(" You failed the tutorial");
	             this.isTutorialMode = false; // Reset del flag se fallisce
	         }
	     });

        this.tutorialMenuView.addNoListener(event -> 
        {
            this.tutorialMenuView.close();
            System.out.println(" No, Tutorial skipped");
            this.isTutorialMode = false;
            this.game.startSelectionCharacter();
        });

        this.tutorialMenuView.addExitListener(event -> 
        {
            System.out.println("Exited game from tutorial menu");
            this.tutorialMenuView.close();
            System.exit(0);
        });
    }
        
    /**
     * Metodo chiamato dalla TutorialMap quando tutti i popup sono completati
     * Ora può mostrare il menu di selezione dei personaggi
     */
    public void onTutorialPopupsCompleted() {
        System.out.println("Popup del tutorial completati, mostro il menu di selezione...");
        
        // Ora avvia la selezione dei personaggi
        this.game.startSelectionCharacter();
    }
    
    /**
     * Metodo per impostare il riferimento alla mappa tutorial
     * Deve essere chiamato dal Game quando crea la TutorialMap
     */
    public void setTutorialMap(TutorialMap tutorialMap) {
        this.currentTutorialMap = tutorialMap;
    }
    
    public void startSelectionCharacter()
    {
        List<Character> availableCharacter = this.game.createAllies();
        this.characterSelectionMenuView.start(availableCharacter);
        
        this.characterSelectionMenuView.addNextButtonListener(event -> 
        {
            List<String> characterNames = this.characterSelectionMenuView.getSelectedCharacterNames();

            System.out.print(" You chose: " + String.join(", ", characterNames) + " -> ");
            System.out.println(" End of character selection");
            
            List<Character> characterSelected = transformList(availableCharacter, characterNames);
            
            this.game.setSelectedCharacters(characterSelected);
            
            this.characterSelectionMenuView.close();
            
            // Se siamo in modalità tutorial, riavvia la mappa tutorial con i personaggi selezionati
            if (this.isTutorialMode && this.currentTutorialMap != null) {
                this.currentTutorialMap.restartWithSelectedCharacters(characterSelected);
                System.out.println("Tutorial riavviato con personaggi selezionati!");
            } else {
                // Altrimenti avvia il livello normale
                this.game.startNewLevel();
            }
        });
    }
    
    private List<Character> transformList(List<Character> allAllies, List<String> selectedCharacters)
    {
        return allAllies.stream()
            .filter(ally -> selectedCharacters.contains(ally.getClass().getSimpleName()))
            .collect(Collectors.toList());
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
    	
    	GameState currentGameState = new GameState(this.game.getCurrentLevelIndex(), currentLevel.getAllies(), currentLevel.getEnemies());
    	
        this.gameStateManager.saveGameState(currentGameState,null);
        
    }

    /**
     * Loads the most recent saved game state.
     * @return The loaded GameState object.
     * @throws IOException If an error occurs during loading.
     * @throws ClassNotFoundException 
     */
    public GameState loadGame() throws IOException, ClassNotFoundException 
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
    }
}