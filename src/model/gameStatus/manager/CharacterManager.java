package model.gameStatus.manager;

import java.util.ArrayList;
import java.util.List;
import model.characters.*;
import model.characters.Character;
import model.gameStatus.Game;

/**
 * Manages all character-related operations including creation, selection, 
 * validation, and reinitialization of characters in the game.
 */
public class CharacterManager 
{   
    /** List of all available allied characters for selection */
    private List<Character> availableAllies;
    
    /** List of currently selected allied characters */
    private List<Character> selectedAllies;
    
    /**
     * Constructs a new CharacterManager and initializes empty character lists.
     */
    public CharacterManager() 
    {
        this.availableAllies = new ArrayList<>();
        this.selectedAllies  = new ArrayList<>();
    }
    
    /**
     * Creates and returns the list of all available allied characters.
     * This method populates the available allies list with all character types.
     * 
     * @return the list of available allied characters
     */
    public List<Character> createAvailableAllies() 
    {
        if (this.availableAllies.isEmpty()) 
        {
            this.availableAllies.add(new Barbarian());
            this.availableAllies.add(new Archer());
            this.availableAllies.add(new Knight());
            this.availableAllies.add(new Wizard());
            this.availableAllies.add(new Juggernaut());
        }
        
        return this.availableAllies;
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
            this.createAvailableAllies();
        }
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
     * @param newAllies the list of characters to add to the current selection
     */
    public void addSelectedCharacters(List<Character> newAllies) 
    {
        for (Character character : newAllies) 
        {
            if (character != null && character.isAlive()) 
            {
                this.selectedAllies.add(character);
                character.becomeHero();
            } 
            else 
            {
                System.out.println("Skipping invalid or dead character: " + 
                    (character == null ? "null" : character.getClass().getSimpleName()));
            }
        }
    }
    
    /**
     * Gets the list of currently selected allied characters.
     * 
     * @return the list of selected allies
     */
    public List<Character> getSelectedAllies() 
    {
        return this.selectedAllies;
    }
    
    /**
     * Checks if there are enough selected allies for the current round.
     * 
     * @return true if the number of selected allies is less than the maximum allowed
     */
    public boolean needsCharacterReplacement() 
    {
        return this.selectedAllies.size() < Game.MAX_ALLIES_PER_ROUND;
    }
    
    /**
     * Calculates how many characters need to be replaced.
     * 
     * @return the number of characters that need to be replaced
     */
    public int getCharactersToReplace() 
    {
        return Game.MAX_ALLIES_PER_ROUND - this.selectedAllies.size();
    }
    
    /**
     * Resets positions of all selected characters to null.
     * This is typically used when transitioning between levels.
     */
    public void resetCharacterPositions() 
    {
        for (Character character : this.selectedAllies)
        {
            character.setPosition(null);
        }
    }
    
    /**
     * Re-initializes a list of characters after loading from a save file.
     * This method handles restoration of non-serializable data such as images
     * and validates that characters are in a valid state.
     * 
     * @param characters the list of characters to reinitialize
     * @return the list of successfully reinitialized characters
     */
    public List<Character> reinitializeCharacters(List<Character> characters) 
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
                
                // for image and icon reinitialize character
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
        
        System.out.println("Character is valid: " + character.getClass().getSimpleName());
        return true;
    }
    
    /**
     * Creates tutorial characters for both allies and enemies.
     * 
     * @return an array containing two lists: [allies, enemies]
     */
    public List<Character>[] createTutorialCharacters() 
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
        
        @SuppressWarnings("unchecked")
        List<Character>[] result = new List[2];
        result[0] = tutorialAllies;
        result[1] = tutorialEnemies;
        
        return result;
    }
    
    /**
     * Sets the selected allies list directly (used for loading saved games).
     * 
     * @param allies the allies list to set
     */
    public void setSelectedAllies(List<Character> allies) 
    {
        this.selectedAllies = allies;
    }
    
    /**
     * Clears all character lists.
     */
    public void clearAllCharacters() 
    {
        this.availableAllies.clear();
        this.selectedAllies.clear();
    }
}