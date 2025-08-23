package view;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.swing.JButton;
import controller.GameController;
import model.characters.AbstractCharacter;
import model.characters.Character;
import model.point.Point;
import view.map.LevelMap;

/**
 * Handles the visual and interactive aspects of the battle phase in the game.
 * This class manages character movement and attack targeting by providing
 * interactive grid interfaces, highlighting valid positions, and managing
 * user input through button listeners.
 * 
 * The BattlePhaseView coordinates between the game controller and the level map
 */
public class BattlePhaseView 
{
	/** The level map containing the game grid and visual elements */
	private LevelMap levelMap;
	
	/** The game controller for handling game logic and state changes */
    private GameController controller;
    
    /** Map storing action listeners for movement actions at specific positions */
    private Map<Point, ActionListener> movementListeners = new HashMap<>();
    
    /** Map storing action listeners for attack actions at specific positions */
    private Map<Point, ActionListener> attackListeners   = new HashMap<>(); 
    
    /**
     * Constructs a new BattlePhaseView with the specified level map and controller.
     * Initializes the listener maps for managing user interactions during battle phases.
     * 
     * @param map the level map to display and interact with
     * @param controller the game controller for handling game logic
     */
    public BattlePhaseView(LevelMap map, GameController controller) 
    { 
		this.levelMap       = map;
		this.controller		= controller;
		
		this.movementListeners = new HashMap<>();
		this.attackListeners   = new HashMap<>();
	}

    /**
     * Starts the movement phase for the given character.
     * Highlights all valid tiles the character can move to, colors the grid accordingly,
     * and attaches listeners to allow movement. Once the movement is completed
     * or if no valid positions are available, the onMovementCompleted callback is executed.
     * 
     * The character can move to any position within their speed range that is not occupied.
     * Valid movement positions are highlighted in gray to guide the player's selection.
     *
     * @param character the character whose movement phase is being processed
     * @param onMovementCompleted callback to execute once the movement phase is finished
     */
    public void movementPhase(Character character, Runnable onMovementCompleted)
    {
        List<Point> availableMoves = new ArrayList<>();
        JButton[][] buttonGrid = this.levelMap.getGridButtons();

        // Prevent multiple clicks
        AtomicBoolean movementDone = new AtomicBoolean(false);
        
        // Get occupied positions by allies and enemies
        Set<Point> occupied = new HashSet<>();
        for (Character ally : this.levelMap.getAlliesList()) 
        {
            occupied.add(ally.getPosition());
        }
        for (Character enemy : this.levelMap.getEnemiesList()) 
        {
            occupied.add(enemy.getPosition());
        }
        
        // PHASE 1: identify positions we can move to and color them gray
        for (int row = 0; row < buttonGrid.length; row++) 
        {
            for (int col = 0; col < buttonGrid[row].length; col++)
            {
                Point point = new Point(row, col);
                boolean withinRange = character.getDistanceInSquares(point)
                    <= character.getSpeed() / AbstractCharacter.SPEED_TO_MOVEMENT;

                // A cell is free if it's the character's current position or not occupied by any other character
                boolean cellFree = point.equals(character.getPosition()) || !occupied.contains(point);

                if (withinRange && cellFree) 
                {
                    availableMoves.add(point);
                }
            }
        }
        
        // No positions found -> phase finished
        if (availableMoves.isEmpty()) 
        {
            if (onMovementCompleted != null) onMovementCompleted.run();
            return;
        }
        
        // Color the positions we can move to
        this.levelMap.colourPositionAvailable(availableMoves, new Color(80, 80, 80, 160)); // gray color
        
        this.levelMap.colourCharacterPosition(character);
            
        // PHASE 2: Associate ActionListeners to buttons at free positions
        for (Point validPoint : availableMoves)
        {
            JButton button = this.levelMap.getButtonAt(validPoint.getX(), validPoint.getY());
            
            // Create listener for the i-th free position
            Point targetPoint = validPoint;

            // Define what happens when we press a button with a valid position
            ActionListener moveListener = click ->
            {
                if (movementDone.compareAndSet(false, true))
                {	
                    this.controller.move(levelMap, character, targetPoint);
                    
                    this.clearActionListenersAtPositions(availableMoves, this.movementListeners);
                    
                    // Unblock the execution flow
                    if (onMovementCompleted != null) onMovementCompleted.run();
                }
            };
            // Remove any old listeners
            for (ActionListener al : button.getActionListeners()) {
                button.removeActionListener(al);
            }
            
            // Add the ActionListener and the map point where it's located to the ActionListener map
            this.movementListeners.put(validPoint, moveListener);
            
            // Add the ActionListener to the i-th button
            button.addActionListener(moveListener);
            button.setEnabled(true);
        }
    }
    
    /**
     * Allows the given character to choose a target from the list of enemies.
     * Highlights all valid targets within attack range, assigns click listeners to allow selection,
     * and invokes the onAttackCompleted callback after an attack is executed.
     * If no valid targets are available, the callback is invoked immediately.
     * 
     * Valid targets are highlighted in red, and the attack range area is highlighted in dark red
     * to provide visual feedback to the player about available attack options.
     *
     * @param enemiesList the list of enemy characters that could potentially be targeted
     * @param attacker the character performing the attack
     * @param onAttackCompleted callback invoked after the attack phase is completed
     */
    public void chooseTarget(List<Character> enemiesList, Character attacker, Runnable onAttackCompleted) 
    {
        // Get enemies we can attack
		List<Character> reachableEnemies = getEnemiesInRange(attacker, enemiesList);
		
		if (reachableEnemies.isEmpty()) 
		{
			 if (onAttackCompleted != null) onAttackCompleted.run();
			 return;
		}
		else
		{
        	this.levelMap.updateBannerMessage("Movement completed, "+attacker.getClass().getSimpleName()+" choose a target", false);
		}
		
		// Get positions of enemies we can attack
		List<Point> enemyPositions = reachableEnemies.stream()
		                                           .map(Character::getPosition)
		                                           .collect(Collectors.toList());

		// Get all positions within attack range
		List<Point> positionsInAttackRange = new ArrayList<>();
		JButton[][] buttonGrid = this.levelMap.getGridButtons();
		
        for (int row = 0; row < buttonGrid.length; row++) 
        {
            for (int col = 0; col < buttonGrid[row].length; col++)
            {
                Point point = new Point(row, col);
                
                if (attacker.isWithinAttackRange(point))
                {
                	positionsInAttackRange.add(point);
                }
            }
        }
		
		// Highlight attackable enemies in red
		levelMap.colourPositionAvailable(positionsInAttackRange, new Color(139, 0, 0, 80)); // dark red
		levelMap.colourPositionAvailable(enemyPositions, new Color(255, 0, 0, 80)); // red

        for (Character enemy : enemiesList) 
        {
        	synchronized (this.attackListeners) {
                Point enemyPosition = enemy.getPosition();
                
                JButton button = levelMap.getButtonAt(enemyPosition.getX(), enemyPosition.getY());

                ActionListener attackListener = click -> 
                {

                    this.controller.fight(attacker, enemy, levelMap.getAlliesList(), enemiesList, levelMap);

                    this.clearActionListenersAtPositions(enemyPositions, this.attackListeners);

                    // Unblock the execution flow
                    if (onAttackCompleted != null) onAttackCompleted.run();
                };

                // Remove any old listeners
                for (ActionListener al : button.getActionListeners()) {
                    button.removeActionListener(al);
                }
                
                this.attackListeners.put(enemyPosition, attackListener);

                button.addActionListener(attackListener);
                button.setEnabled(true);
			}
        }
    }
    
    /**
     * Performs a graphical movement of the character to the specified point.
     * This method delegates to the controller to handle the actual movement logic.
     * 
     * @param character the character to move
     * @param validPoint the destination point for the movement
     */
    public void graphicMovementCharacterToPoint(Character character, Point validPoint) 
    {
    	this.controller.move(levelMap, character, validPoint);
    }

    /**
     * Helper Methods Section
     */
    
    /**
     * Filters the list of enemies to return only those within the attacker's weapon range.
     * Uses the attacker's weapon range to determine which enemies can be targeted.
     * 
     * @param attacker the character performing the attack
     * @param enemies the complete list of enemy characters
     * @return a filtered list containing only enemies within attack range
     */
    private List<Character> getEnemiesInRange(Character attacker, List<Character> enemies) 
    {
        int range = attacker.getWeapon().getRange();
        
        Point attackerPos = attacker.getPosition();

        return enemies.stream()
                      .filter(enemy -> attackerPos.distanceFrom(enemy.getPosition()) <= range)
                      .collect(Collectors.toList());
    }

    /**
     * Clears action listeners from buttons at the specified positions and resets grid colors.
     * This method is used to clean up after movement or attack phases are completed,
     * ensuring that old listeners don't interfere with future interactions.
     * 
     * The method is synchronized to prevent concurrent modification of the listener maps.
     * 
     * @param positions the list of positions where listeners should be removed
     * @param listenerMap the map containing the listeners to be removed
     */
    private void clearActionListenersAtPositions(List<Point> positions, Map<Point, ActionListener> listenerMap) 
    {
    	synchronized (this.attackListeners) 
    	{
	        for (Point point : positions) 
	        {
	            ActionListener actionListener = listenerMap.get(point);
	            
	            if (actionListener != null) 
	            {
	                this.levelMap.getButtonAt(point.getX(), point.getY()).removeActionListener(actionListener);
	            }
	        }
	        listenerMap.clear();
	        this.levelMap.resetGridColors();
    	}
    }
}