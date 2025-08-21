package model.gameStatus.manager;

import java.util.List;
import model.characters.*;
import model.characters.Character;
import model.gameStatus.level.GameTutorial;
import view.map.TutorialMap;
import controller.GameController;

/**
 * Manages tutorial-related operations including setup,
 * character creation, and tutorial execution.
 */
public class TutorialManager 
{
    /** Reference to the game controller */
    private GameController controller;
    
    /** Reference to the character manager for creating tutorial characters */
    private CharacterManager characterManager;
    
    /**
     * Constructs a new TutorialManager.
     * 
     * @param controller the game controller
     * @param characterManager the character manager for creating characters
     */
    public TutorialManager(GameController controller, CharacterManager characterManager) 
    {
        this.controller = controller;
        this.characterManager = characterManager;
    }
    
    /**
     * Starts the tutorial mode with predefined characters and enemies.
     * The tutorial provides a controlled environment for players to learn game mechanics.
     * 
     * @return true if the tutorial was completed successfully, false otherwise
     */
    public boolean startTutorial() 
    {
        List<Character>[] tutorialCharacters = this.characterManager.createTutorialCharacters();
        List<Character> tutorialAllies = tutorialCharacters[0];
        List<Character> tutorialEnemies = tutorialCharacters[1];
        
        GameTutorial tutorial = new GameTutorial(
            new TutorialMap(tutorialEnemies, tutorialAllies, this.controller), 
            this.controller);
        
        return tutorial.play();
    }
    
    /**
     * Creates a custom tutorial with specific character types.
     * This allows for more flexible tutorial scenarios.
     * 
     * @param allyTypes the types of allies to create for the tutorial
     * @param enemyTypes the types of enemies to create for the tutorial
     * @return true if the tutorial was completed successfully, false otherwise
     */
    public boolean startCustomTutorial(Class<? extends Character>[] allyTypes, 
                                      Class<? extends Character>[] enemyTypes) 
    {
        try 
        {
            List<Character> tutorialAllies = createCharactersFromTypes(allyTypes, true);
            List<Character> tutorialEnemies = createCharactersFromTypes(enemyTypes, false);
            
            GameTutorial tutorial = new GameTutorial(
                new TutorialMap(tutorialEnemies, tutorialAllies, this.controller), 
                this.controller);
            
            return tutorial.play();
        } 
        catch (Exception e) 
        {
            System.err.println("Error creating custom tutorial: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Creates a basic combat tutorial with simple characters.
     * 
     * @return true if the tutorial was completed successfully, false otherwise
     */
    public boolean startBasicCombatTutorial() 
    {
        @SuppressWarnings("unchecked")
        Class<? extends Character>[] allyTypes = new Class[] { Barbarian.class };
        
        @SuppressWarnings("unchecked")
        Class<? extends Character>[] enemyTypes = new Class[] { Knight.class };
        
        return startCustomTutorial(allyTypes, enemyTypes);
    }
    
    /**
     * Creates an advanced tutorial with multiple character types.
     * 
     * @return true if the tutorial was completed successfully, false otherwise
     */
    public boolean startAdvancedTutorial() 
    {
        @SuppressWarnings("unchecked")
        Class<? extends Character>[] allyTypes = new Class[] { 
            Barbarian.class, Archer.class, Wizard.class 
        };
        
        @SuppressWarnings("unchecked")
        Class<? extends Character>[] enemyTypes = new Class[] { 
            Knight.class, Barbarian.class, Archer.class 
        };
        
        return startCustomTutorial(allyTypes, enemyTypes);
    }
    
    /**
     * Creates characters from an array of character class types.
     * 
     * @param types the character class types to instantiate
     * @param makeHeroes whether to make the characters heroes (allies)
     * @return list of created characters
     * @throws Exception if character creation fails
     */
    private List<Character> createCharactersFromTypes(Class<? extends Character>[] types, 
                                                     boolean makeHeroes) throws Exception 
    {
        java.util.List<Character> characters = new java.util.ArrayList<>();
        
        for (Class<? extends Character> type : types) 
        {
            Character character = type.getDeclaredConstructor().newInstance();
            if (makeHeroes) 
            {
                character.becomeHero();
            }
            characters.add(character);
        }
        
        return characters;
    }
    
    /**
     * Gets tutorial completion statistics (if implemented in the future).
     * This is a placeholder for potential tutorial analytics.
     * 
     * @return tutorial stats as a formatted string
     */
    public String getTutorialStats() 
    {
        // Placeholder for future implementation
        return "Tutorial stats not implemented yet";
    }
    
    /**
     * Checks if tutorial should be shown based on user preferences or first-time play.
     * This is a placeholder for future implementation.
     * 
     * @return true if tutorial should be shown
     */
    public boolean shouldShowTutorial() 
    {
        // Placeholder - could check user settings, first-time play, etc.
        return true;
    }
    
    /**
     * Validates tutorial prerequisites (e.g., game resources loaded).
     * 
     * @return true if tutorial can be started
     */
    public boolean validateTutorialPrerequisites() 
    {
        if (this.controller == null) 
        {
            System.err.println("Tutorial cannot start: controller is null");
            return false;
        }
        
        if (this.characterManager == null) 
        {
            System.err.println("Tutorial cannot start: character manager is null");
            return false;
        }
        
        return true;
    }
}