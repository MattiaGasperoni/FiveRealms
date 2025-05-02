package controller;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JButton;

import model.characters.Character;
import model.gameStatus.Game;
import model.gameStatus.Level;
import model.gameStatus.saveSystem.GameState;
import model.gameStatus.saveSystem.GameStateManager;
import model.point.Point;
import view.CharacterSelectionMenu;
import view.LoadGameMenu;
import view.MainMenu;
import view.TutorialMenu;
import view.map.AbstractMap;

/**
 * Controller class that coordinates user actions with the game logic.
 * It interacts with the GameStateManager to handle game saving/loading,
 * and manages the movement of characters on the map.
 */
public class GameController 
{

	
	private MainMenu mainMenuView;
    private Game game;
    private TutorialMenu tutorialMenuView;
    private CharacterSelectionMenu characterSelectionMenuView;
    private GameStateManager gameStateManager;
    

 
    public GameController(Game game, GameStateManager gameStateManager, MainMenu mainMenu, TutorialMenu tutorialMenu, CharacterSelectionMenu characterSelectionMenu) 
    {
		this.game = game;
		this.gameStateManager = gameStateManager;
		this.mainMenuView = mainMenu;
		this.tutorialMenuView = tutorialMenu;
		this.characterSelectionMenuView = characterSelectionMenu;
		
        this.setupMainMenuListeners();
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
            //new LoadGameMenu();
            /*
             * * Qui dovrei implementare il caricamento del gioco
             */
        });

        mainMenuView.addExitListener(event -> 
        {
            System.out.println(" You chose to close the game.");
            this.mainMenuView.close();
            System.exit(0);
        });
    }
    
    public void startNewGame() 
    {
    	this.tutorialMenuView.show();

    	this.tutorialMenuView.addYesListener(event -> 
    	{
    		this.tutorialMenuView.close();
            System.out.println(" Yes, start play the tutorial");

            boolean tutorialSuccess = this.game.startTutorial(); 
            
            if (tutorialSuccess) 
            {
                this.game.startSelectionCharacter();
                
            } 
            else
            {
                System.out.println(" You failed the tutorial");
                /*
                 * Qui voglio un PopUp a schermo che dice "Tutorial fallito, e mi chiede se voglio riprovare o uscire"
                 */
            }
        });

    	this.tutorialMenuView.addNoListener(event -> 
    	{
    		this.tutorialMenuView.close();
            System.out.println(" No, Tutorial skipped");
            this.game.startSelectionCharacter();
        });

    	this.tutorialMenuView.addExitListener(event -> 
    	{
            System.out.println("Exited game from tutorial menu");
            this.tutorialMenuView.close();
            System.exit(0);
        });
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
            this.game.startLevel();
        });

    }
    
    private List<Character> transformList(List<Character> allAllies, List<String> selectedCharacters) {
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
    public void saveGame(List<Character> allies, List<Character> enemies, int level) throws IOException 
    {
        this.gameStateManager.saveStatus(allies, enemies, level);  // Delegate saving to GameStateManager
    }

    /**
     * Loads the most recent saved game state.
     * @return The loaded GameState object.
     * @throws IOException If an error occurs during loading.
     */
    public GameState loadGame() throws IOException {
        return gameStateManager.loadStatus();  // Delegate loading to GameStateManager
    }

    // Movement functionality
    
    /**
     * Moves the given character to a new point on the map.
     * @param map The game map where the character resides.
     * @param character The character to be moved.
     * @param point The destination point on the map.
     */
    public void move(AbstractMap map, Character character, Point point) {
        map.moveCharacter(character, point);  // Move the character on the map
        character.moveTo(point);  // Update the character's position
    }
    
    
    
     
}




