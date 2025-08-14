package view;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.swing.JButton;
import controller.GameController;
import model.characters.AbstractCharacter;
import model.characters.Character;
import model.point.Point;
import view.map.LevelMap;

public class BattlePhaseView 
{

	private LevelMap levelMap;
    private GameController controller;
    private Map<Point, ActionListener> movementListeners = new HashMap<>();
    private Map<Point, ActionListener> attackListeners   = new HashMap<>(); 
    
    public BattlePhaseView(LevelMap map, GameController controll) 
    { 
		this.levelMap       = map;
		this.controller		= controll;
		
		this.movementListeners = new HashMap<>();
		this.attackListeners   = new HashMap<>();
	}

    /**
     * Starts the movement phase for the given character.
     * <p>
     * Highlights all valid tiles the character can move to, colors the grid accordingly,
     * and attaches listeners to allow movement. Once the movement is completed
     * (or if no valid positions are available), the {@code onMovementCompleted} callback is executed.
     *
     * @param character           the character whose movement phase is being processed
     * @param onMovementCompleted callback to execute once the movement phase is finished
     */
    public void movementPhase(Character character, Runnable onMovementCompleted)
    {
        List<Point> availableMoves = new ArrayList<>();
        JButton[][] buttonGrid = this.levelMap.getGridButtons();

        // Per prevenire click multipli
        AtomicBoolean movementDone = new AtomicBoolean(false);
        
        // FASE 1: identifica le posizioni in cui possiamo spostarci e le colora di grigio
        for (int row = 0; row < buttonGrid.length; row++) 
        {
            for (int col = 0; col < buttonGrid[row].length; col++)
            {
                Point point = new Point(row, col);
                
                if (character.getDistanceInSquares(point) <= character.getSpeed() / AbstractCharacter.SPEED_TO_MOVEMENT)
                {
                    availableMoves.add(point);
                }
            }
        }
        
        // Nessuna posizione trovata -> fase finita
        if (availableMoves.isEmpty()) 
        {
            System.out.print(" Nessuna posizione disponibile per " + character.getClass().getSimpleName());
            if (onMovementCompleted != null) onMovementCompleted.run();
            return;
        }
        
        // Coloriamo le posizioni in cui ci possiamo spostare
        this.levelMap.colourPositionAvailable(availableMoves, new Color(80, 80, 80, 160)); //color gray
        
            
        // FASE 2: Associamo ai bottoni delle posizioni libere un ActionLister
        for (Point validPoint : availableMoves)
        {
            JButton button = this.levelMap.getButtonAt(validPoint.getX(), validPoint.getY());
            
            // Crea il listener per i-esima posizione libera
            Point targetPoint = validPoint;

            // Definiamo cosa accade quando premiamo un bottone con una posizione valida
            ActionListener moveListener = click ->
            {
                if (movementDone.compareAndSet(false, true))
                {	
                    this.controller.move(levelMap, character, targetPoint);
                    
                    this.clearActionListenersAtPositions(availableMoves, this.movementListeners);
                    
                    // Sblocchiamo il flusso d'esecuzione
                    if (onMovementCompleted != null) onMovementCompleted.run();
                }
            };
            // Rimuovi eventuali vecchi listener
            for (ActionListener al : button.getActionListeners()) {
                button.removeActionListener(al);
            }
            
            // Aggiungiamo l'ActionLister e il punto della mappa in cui si trova nella mappa di ActionLister
            this.movementListeners.put(validPoint, moveListener);
            
            // Aggiungiamo l'ActionLister al bottone i-esimo
            button.addActionListener(moveListener);
            button.setEnabled(true);
        }
        
    }
    
    /**
     * Allows the given character to choose a target from the list of candidates.
     * 
     * Highlights all valid targets within range, assigns click listeners to allow selection,
     * and invokes the {@code onTargetChosen} callback with the selected target.
     * If no valid targets are available, the callback is invoked with {@code null}.
     *
     * @param character       the character selecting a target
     * @param targets         the list of potential targets (e.g., enemies)
     * @param onTargetChosen  callback invoked with the selected target or {@code null} if none was chosen
     */
    public void chooseTarget(List<Character> enemiesList, Character attacker, Runnable onAttackCompleted) 
    {
        System.out.println("\n" + attacker.getClass().getSimpleName() + " (HP: " + attacker.getCurrentHealth() + ", DMG: " + attacker.getPower() + 
                ") seleziona chi vuoi attaccare -> ");
		
        // Otteniamo i nemici che possiamo attaccare
		List<Character> reachableEnemies = getEnemiesInRange(attacker, enemiesList);
		
		if (reachableEnemies.isEmpty()) 
		{
			 System.out.println(" Nessun nemico nel raggio d'attacco. Fase di attacco annullata.");
			 if (onAttackCompleted != null) onAttackCompleted.run();
			 return;
		}
		else
		{
        	this.levelMap.updateBannerMessage("Movimento completato, "+attacker.getClass().getSimpleName()+" scegli un  bersaglio", false);
		}
		
		// Otteniamo le posizioni dei nemici che possiamo attaccare
		List<Point> enemyPositions = reachableEnemies.stream()
		                                           .map(Character::getPosition)
		                                           .collect(Collectors.toList());

		//Get 
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
		
		// Evidenziamo di rosso i nemici attaccabili
		levelMap.colourPositionAvailable(positionsInAttackRange, new Color(139, 0, 0, 80)); // dark red
		levelMap.colourPositionAvailable(enemyPositions, new Color(255, 0, 0, 80)); // red

        for (Character enemy : enemiesList) 
        {
        	synchronized (this.attackListeners) {
                Point enemyPosition = enemy.getPosition();
                
                JButton button = levelMap.getButtonAt(enemyPosition.getX(), enemyPosition.getY());

                ActionListener attackListener = click -> 
                {
                    System.out.println(" ha selezionato " + enemy.getClass().getSimpleName() +
                                       " (HP: " + enemy.getCurrentHealth() + ", DEF: " + enemy.getDefence() + ")");
                    
                    this.controller.fight(attacker, enemy, levelMap.getAlliesList(), enemiesList, levelMap);

                    System.out.println("\nPost Attacco " + enemy.getClass().getSimpleName() +
                                       " ha " + enemy.getCurrentHealth() + " di vita");
                    
                    this.clearActionListenersAtPositions(enemyPositions, this.attackListeners);

                    // Sblocchiamo il flusso d'esecuzione
                    if (onAttackCompleted != null) onAttackCompleted.run();
                };

                // Rimuovi eventuali vecchi listener
                for (ActionListener al : button.getActionListeners()) {
                    button.removeActionListener(al);
                }
                
                this.attackListeners.put(enemyPosition, attackListener);

                button.addActionListener(attackListener);
                button.setEnabled(true);
			}

        }
    }
    
    
    public void graphicMovementCharacterToPoint(Character character, Point validPoint) 
    {
    	this.controller.move(levelMap, character, validPoint);
    }

    
    /*===============*/
    /* Metodi Helper */
    /*===============*/
    
    /*private boolean isValidMovementPosition(Point point, Character character, int moveRange) 
    {
        return point.distanceFrom(character.getPosition()) <= moveRange &&
               !this.levelMap.isPositionOccupied(point) &&
               !point.equals(character.getPosition());
    }*/
    
    private List<Character> getEnemiesInRange(Character attacker, List<Character> enemies) 
    {
        int range = attacker.getWeapon().getRange();
        
        Point attackerPos = attacker.getPosition();

        return enemies.stream()
                      .filter(enemy -> attackerPos.distanceFrom(enemy.getPosition()) <= range)
                      .collect(Collectors.toList());
    }

    private void clearActionListenersAtPositions(List<Point> positions, Map<Point, ActionListener> listenerMap) 
    {
    	synchronized (this.attackListeners) 
    	{
	        for (Point point : positions) 
	        {
	            ActionListener aL = listenerMap.get(point);
	            
	            if (aL != null) 
	            {
	                this.levelMap.getButtonAt(point.getX(), point.getY()).removeActionListener(aL);
	            }
	        }
	        listenerMap.clear();
	        this.levelMap.resetGridColors();
    	}
    }

}
