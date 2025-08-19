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
import view.menu.EndGameMenu;
import view.menu.LoadGameMenu;
import view.menu.MainMenu;
import view.menu.PauseMenu;
import view.menu.TutorialMenu;
import view.menu.selectionMenu.CharacterReplaceMenu;
import view.menu.selectionMenu.CharacterSelectionMenu;

/**
 * Manages all menu-related operations and UI interactions.
 * <p>
 * This controller handles menu display, user interactions,
 * and navigation between different game menus.
 */
public class MenuController 
{
    
    // Menu references
    private MainMenu mainMenu;
    private LoadGameMenu loadGameMenu;
    private TutorialMenu tutorialMenu;
    private CharacterSelectionMenu characterSelectionMenu;
    private CharacterReplaceMenu characterReplaceMenu;
    private EndGameMenu endGameMenu;
    private PauseMenu pauseMenu;
    
    // Controllers
    private final MusicController musicController;
    private final SaveController saveController;
    
    // Game reference
    private Game game;
    
    /**
     * Creates a new MenuController instance.
     *
     * @param musicController the music controller
     * @param saveController the save controller
     */
    public MenuController(MusicController musicController, SaveController saveController) 
    {
        this.musicController = musicController;
        this.saveController = saveController;
        this.initializeMenus();
    }
    
    /**
     * Sets the game reference.
     *
     * @param game the game instance
     */
    public void setGame(Game game) 
    {
        this.game = game;
        this.setupMainMenuListeners();
        this.setupEndGameListeners();
        this.setupLoadMenuListeners();
    }
    
    /**
     * Initializes all menu instances.
     */
    private void initializeMenus() 
    {
        this.mainMenu = new MainMenu();
        this.loadGameMenu = new LoadGameMenu();
        this.tutorialMenu = new TutorialMenu();
        this.endGameMenu = new EndGameMenu();
        this.characterSelectionMenu = new CharacterSelectionMenu();
        this.characterReplaceMenu = new CharacterReplaceMenu();
    }
    
    /**
     * Updates the load button state based on save availability.
     */
    public void updateLoadButtonState() 
    {
        this.mainMenu.setLoadButtonEnabled(this.saveController.hasSavedGames());
    }
    
    /**
     * Displays the main menu.
     */
    public void showMainMenu() 
    {
        this.musicController.playBackgroundMusic();
        this.mainMenu.show();
    }
    
    /**
     * Displays the end game menu with result.
     *
     * @param playerWon true if player won, false if lost
     */
    public void showEndGameMenu(boolean playerWon) 
    {
        if (playerWon) 
        {
            this.musicController.playWinMusic();
        } 
        else 
        {
            this.musicController.playLoseMusic();
        }
        this.endGameMenu.setGameResult(playerWon);
        this.endGameMenu.show();
    }
    
    /**
     * Starts the new game flow with tutorial choice.
     */
    public void startNewGameFlow() 
    {
        this.tutorialMenu.show();
        this.setupTutorialListeners();
    }
    
    /**
     * Starts the character selection process.
     */
    public void startCharacterSelection() 
    {
        this.musicController.playBackgroundMusic();
        List<Character> availableCharacters = this.game.createAllies();
        
        this.characterSelectionMenu.show();
        this.setupCharacterSelectionListeners(availableCharacters);
    }
    
    /**
     * Starts the character replacement process.
     *
     * @param numberOfReplacements number of characters to replace
     */
    public void startCharacterReplacement(int numberOfReplacements) 
    {
        this.musicController.playBackgroundMusic();
        
        if (this.characterReplaceMenu == null) 
        {
            this.characterReplaceMenu = new CharacterReplaceMenu();
        }
        
        List<Character> availableCharacters = this.getAvailableAllies();
        this.characterReplaceMenu.show(numberOfReplacements);
        this.setupCharacterReplacementListeners(availableCharacters);
    }
    
    /**
     * Sets up and shows the pause menu.
     *
     * @param pauseMenu the pause menu instance
     */
    public void setupPauseMenu(PauseMenu pauseMenu) 
    {
        this.pauseMenu = pauseMenu;
        this.setupPauseMenuListeners();
    }
    
    /**
     * Sets up main menu event listeners.
     */
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
            this.musicController.stopMusic();
            this.loadGameMenu.show();
            this.musicController.playBackgroundMusic();
        });

        this.mainMenu.addExitListener(e -> this.exitGame());
    }
    
    /**
     * Sets up end game menu event listeners.
     */
    private void setupEndGameListeners() 
    {
        this.endGameMenu.addMainMenuListener(e -> 
        {
            this.musicController.stopMusic();
            this.endGameMenu.close();
            this.restartGameFromMenu();
        });
        
        this.endGameMenu.addExitListener(e -> this.exitGame());
    }
    
    /**
     * Sets up load game menu event listeners.
     */
    private void setupLoadMenuListeners() 
    {
        this.loadGameMenu.addChooseSaveListener(e -> 
        {
            File[] saveFiles = this.saveController.getSaveFiles();
            this.loadGameMenu.showSaveFile(saveFiles);
        });

        this.loadGameMenu.addMainMenuListener(e -> 
        {
            this.loadGameMenu.close();
            this.musicController.stopMusic();
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
    
    /**
     * Sets up tutorial menu listeners.
     */
    private void setupTutorialListeners() 
    {
        this.tutorialMenu.addYesListener(e -> 
        {
            this.tutorialMenu.close();
            this.musicController.playTutorialMusic();
            this.game.startTutorial();
        });

        this.tutorialMenu.addNoListener(e -> 
        {
            this.musicController.stopMusic();
            this.tutorialMenu.close();
            this.game.startSelectionCharacter();
        });

        this.tutorialMenu.addMainMenuListener(e -> 
        {
            this.musicController.stopMusic();
            this.tutorialMenu.close();
            this.restartGameFromMenu();
        });
    }
    
    /**
     * Sets up character selection menu listeners.
     */
    private void setupCharacterSelectionListeners(List<Character> availableCharacters) 
    {
        this.characterSelectionMenu.addNextButtonListener(e -> 
        {
            List<Character> chosenCharacters = this.transformCharacterList(
                availableCharacters, 
                this.characterSelectionMenu.getSelectedCharacterNames()
            );
            
            this.game.setSelectedCharacters(chosenCharacters);
            this.characterSelectionMenu.close();
            this.characterSelectionMenu = null;
            this.musicController.stopMusic();
            this.game.startNewLevel();
        });
    }
    
    /**
     * Sets up character replacement menu listeners.
     */
    private void setupCharacterReplacementListeners(List<Character> availableCharacters) 
    {
        this.characterReplaceMenu.addNextButtonListener(e -> 
        {
            List<Character> chosenCharacters = this.transformCharacterList(
                availableCharacters, 
                this.characterReplaceMenu.getSelectedCharacterNames()
            );

            this.game.addSelectedCharacters(chosenCharacters);
            
            // Reset positions for new characters
            this.game.getSelectedAllies().forEach(c -> c.setPosition(null));
            
            // Ensure maximum of 3 characters
            while (this.game.getSelectedAllies().size() > 3) 
            {
                this.game.getSelectedAllies().removeLast();
            }

            this.musicController.stopMusic();
            this.game.markCharacterReplacementCompleted();
            this.characterReplaceMenu.close();
            this.characterReplaceMenu = null;
        });
    }
    
    /**
     * Sets up pause menu event listeners.
     */
    private void setupPauseMenuListeners() 
    {
        this.pauseMenu.addPauseListener(e -> 
        {
            this.musicController.stopMusic();
            this.pauseMenu.show();
            this.game.getGameLevels().get(this.game.getCurrentLevelIndex()).setLevelPaused(true);
        });

        this.pauseMenu.addResumeListener(e -> 
        {
            this.pauseMenu.close();
            this.musicController.resumeLevelMusic(this.game.getCurrentLevelIndex());
            this.game.getGameLevels().get(this.game.getCurrentLevelIndex()).setLevelPaused(false);
        });

        this.pauseMenu.addSaveListener(e -> 
        {
            try 
            {
                this.saveController.saveGame(this.game);
            } 
            catch (IOException ex) 
            {
                this.log("Error saving game: " + ex.getMessage());
            } 
            finally 
            {
                this.pauseMenu.close();
                this.musicController.resumeLevelMusic(this.game.getCurrentLevelIndex());
                this.game.getGameLevels().get(this.game.getCurrentLevelIndex()).setLevelPaused(false);
            }
        });

        this.pauseMenu.addMainMenuListener(e -> 
        {
            this.pauseMenu.close();
            this.game.closeAll();
            this.restartGameFromMenu();
        });
    }
    
    /**
     * Gets all available ally character types.
     */
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
    
    /**
     * Transforms character names to character instances.
     */
    private List<Character> transformCharacterList(List<Character> allCharacters, List<String> selectedNames) 
    {
        return allCharacters.stream()
                .filter(character -> selectedNames.contains(character.getClass().getSimpleName()))
                .collect(Collectors.toList());
    }
    
    /**
     * Restarts the game by returning to main menu.
     */
    private void restartGameFromMenu() 
    {
        this.log("Returning to main menu...");
        this.game = new Game();
        this.game.start();
    }
    
    /**
     * Exits the application.
     */
    private void exitGame() 
    {
        this.log("Exiting game...");
        System.exit(0);
    }
    
    /**
     * Logs a message to the console.
     */
    private void log(String message) 
    {
        System.out.println(message);
    }
}