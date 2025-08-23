package controller;

import java.util.List;

import model.characters.Character;
import model.point.Point;
import view.map.AbstractMap;

/**
 * Manages all map and gameplay-related operations.
 * <p>
 * This controller handles character movement, combat actions,
 * and map state updates during gameplay.
 */
public class MapController 
{
    
    /**
     * Moves a character to a new point on the map.
     *
     * @param map the game map
     * @param character the character to move
     * @param point the destination point
     * @throws IllegalArgumentException if any parameter is null
     */
    public void moveCharacter(AbstractMap map, Character character, Point point) 
    {
        this.validateNotNull(map, character, point);
        map.moveCharacter(character, point);
        character.moveTo(point);
    }

    /**
     * Removes a dead character from the map and the appropriate list.
     *
     * @param map the game map
     * @param deadCharacter the character to remove
     * @param point the character's position
     * @param characterList the list containing the character (allies or enemies)
     * @throws IllegalArgumentException if any parameter is null
     */
    public void removeCharacter(AbstractMap map, Character deadCharacter, Point point, List<Character> characterList) 
    {
        this.validateNotNull(map, deadCharacter, point, characterList);
        characterList.remove(deadCharacter);
        map.removeCharacter(deadCharacter);
    }

    /**
     * Executes a combat action between two characters.
     *
     * @param attacker the attacking character
     * @param defender the defending character
     * @param allies the list of allied characters
     * @param enemies the list of enemy characters
     * @param map the game map
     * @throws IllegalArgumentException if any parameter is null
     */
    public void executeCombat(Character attacker, Character defender, List<Character> allies, List<Character> enemies, AbstractMap map) 
    {
        this.validateNotNull(attacker, defender, allies, enemies, map);
        
        Character deadCharacter = attacker.fight(defender);
        if (deadCharacter != null) 
        {
            List<Character> targetList = deadCharacter.isAllied() ? allies : enemies;
            this.removeCharacter(map, deadCharacter, deadCharacter.getPosition(), targetList);
        } 
        else 
        {
            map.updateToolTip();
        }
    }
    
    /**
     * Updates the map's tooltip display.
     *
     * @param map the game map to update
     */
    public void updateMapTooltip(AbstractMap map) 
    {
        if (map != null) 
        {
            map.updateToolTip();
        }
    }

    /**
     * Validates that all provided objects are not null.
     *
     * @param objects the objects to validate
     * @throws IllegalArgumentException if any object is null
     */
    private void validateNotNull(Object... objects) 
    {
        for (Object obj : objects) 
        {
            if (obj == null) 
            {
                throw new IllegalArgumentException("Argument must not be null");
            }
        }
    }
}