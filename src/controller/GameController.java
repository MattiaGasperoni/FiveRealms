package controller;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import model.characters.Character;
import model.gameStatus.Game;
import model.gameStatus.Level;
import model.gameStatus.saveSystem.GameState;
import model.gameStatus.saveSystem.GameStateManager;
import model.point.Point;
import view.CharacterSelectionMenu;
import view.LoadGameMenu;
import view.MainMenu;
import view.PauseMenu;
import view.TutorialMenu;
import view.map.AbstractMap;
import view.map.TutorialMap;

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
    private TutorialMap currentTutorialMap; // Aggiunto per tenere riferimento alla mappa tutorial
    private boolean isTutorialMode = false; // Flag per sapere se siamo in modalità tutorial

    public GameController(Game game, GameStateManager gameStateManager, MainMenu mainMenu, TutorialMenu tutorialMenu, CharacterSelectionMenu characterSelectionMenu) 
    {
        this.game = game;
        this.gameStateManager = gameStateManager;
        this.mainMenuView = mainMenu;
        this.tutorialMenuView = tutorialMenu;
        this.characterSelectionMenuView = characterSelectionMenu;

        // Controlla se esistono salvataggi e abilita/disabilita il pulsante
        boolean hasSave = this.gameStateManager.hasSaved();
        this.mainMenuView.setLoadButtonEnabled(hasSave);

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
            new LoadGameMenu();
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
            
            this.isTutorialMode = true;
            
            // Avvia il tutorial ma NON la selezione dei personaggi
            // La selezione avverrà solo quando i popup del tutorial saranno completati
            boolean tutorialSuccess = this.game.startTutorial(); 
            
            if (!tutorialSuccess) 
            {
                System.out.println(" You failed the tutorial");
                /*
                 * Qui voglio un PopUp a schermo che dice "Tutorial fallito, e mi chiede se voglio riprovare o uscire"
                 */
            }
            // NON chiamare startSelectionCharacter() qui - sarà chiamato da onTutorialPopupsCompleted()
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
                this.game.startLevel();
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
    public void move(AbstractMap map, Character character, Point point) 
    {
        if (map == null || character == null || point == null) 
        {
            throw new IllegalArgumentException("Map, character, and point must not be null");
        }        
        map.moveCharacter(character, point);  // Move the character on the map
        character.moveTo(point);  // Update the character's position
        map.updateMap();  // Refresh the map to reflect the new position
    }
    
    public void remove(AbstractMap map, Character deadCharacter, Point point, List<Character> listOfTheDead) 
    {
        if (map == null || deadCharacter == null || point == null || listOfTheDead == null) 
        {
            throw new IllegalArgumentException("Map, deadCharacter, point and listOfTheDead must not be null");
        }        
        map.removeCharacter(deadCharacter); 
        listOfTheDead.remove(deadCharacter);
        map.updateMap();                     
    }
    
    
    public void fight(Character attackingCharacter, Character attackedCharacter, List<Character> alliedList, List<Character> enemyList, AbstractMap levelMap) 
    {
        Character deadCharacter = attackingCharacter.fight(attackedCharacter);
        
        if(deadCharacter != null)
        {
            this.remove(levelMap, deadCharacter, deadCharacter.getPosition(), (deadCharacter.isAllied()? alliedList : enemyList));
        }
    }
    
    
    /*public void setupPauseMenuListeners(PauseMenu pauseMenu) {

        // Resume → nasconde il pannello di pausa
        pauseMenu.addYesListener(e -> {
            System.out.println("Game resumed");
            pauseMenu.getPanel().setVisible(false); // Devi creare getPanel() se non esiste
        });

        // Save → salva il gioco
        pauseMenu.addNoListener(e -> {
            try {
                this.saveGame(
                    pauseMenu.getAlliesList(), 
                    pauseMenu.getEnemiesList(), 
                    pauseMenu.getNumLevel()
                );
                System.out.println("Game saved successfully!");
                pauseMenu.getPanel().setVisible(false); 
            } catch (IOException ex) {
                System.err.println("Error saving game: " + ex.getMessage());
            }
        });

        // Exit → chiude il gioco
        pauseMenu.addExitListener(e -> {
            System.out.println("You chose to close the game");
            System.exit(0);
        });
    }*/
}