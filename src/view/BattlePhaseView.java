package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JButton;
import controller.GameController;
import model.characters.Character;
import model.point.Point;
import view.map.LevelMap;

public class BattlePhaseView 
{

	private LevelMap levelMap;
    private GameController controller;
    
    public BattlePhaseView(LevelMap map, GameController controll) 
    {
		this.levelMap       = map;
		this.controller		= controll;
	}

	// Gestisce la fase di movimento, mostra la griglia delle possibili posizioni in cui il personaggio può spostarsi
    // e cliccando sulla posizione della griglia ti sposta in quel punto
    public void movementPhase(Character character, List<Character> alliesList, List<Character> enemiesList, Runnable onMovementCompleted)
    {
        // Ottieni le posizioni in cui possiamo spostarci
        List<Point> availableMoves = this.getAvailablePositions(character, onMovementCompleted);
        
        
        // Se non ci sono mosse disponibili, completa immediatamente la fase
        if (availableMoves.isEmpty()) 
        {
            System.out.println("Nessuna posizione disponibile per " + character.getClass().getSimpleName());
            if (onMovementCompleted != null) 
            {
                onMovementCompleted.run();
            }
        }
    }

    private List<Point> getAvailablePositions(Character character, Runnable onMovementCompleted)
    {
        List<Point> availableMoves = new ArrayList<>();
        int moveRange = character.getSpeed() / 10;
        JButton[][] buttonGrid = this.levelMap.getGridButtons();

        // Per prevenire click multipli
        AtomicBoolean movementDone = new AtomicBoolean(false);
        
        // Prima fase: identifica le posizioni valide
        for (int row = 0; row < buttonGrid.length; row++) 
        {
            for (int col = 0; col < buttonGrid[row].length; col++)
            {
                Point point = new Point(row, col);
                
                if (point.distanceFrom(character.getPosition()) <= moveRange && 
                    !this.levelMap.isPositionOccupied(point) &&
                    !point.equals(character.getPosition())) // Evita la posizione corrente
                {
                    availableMoves.add(point);
                }
            }
        }

        // Seconda fase: configura i bottoni solo per le posizioni valide
        for (Point validPoint : availableMoves)
        {
            JButton button = this.levelMap.getButtonAt(validPoint.getX(), validPoint.getY());
            
            // Evidenzia i movimenti validi
            //button.setBackground(Color.GRAY);
            this.levelMap.colourPositionAvailable(availableMoves,"gray");
            
            // Crea il listener per questa specifica posizione
            ActionListener moveListener = click -> 
            {
                // Assicura che il movimento venga eseguito solo una volta
                if (movementDone.compareAndSet(false, true)) 
                {                    
                    // Esegui il movimento
                    this.controller.move(levelMap, character, validPoint);
                    
                    // Cleanup: rimuove i listener e resetta i colori
                    cleanupMovementUI(availableMoves);
                    
                    // Chiama il callback
                    if (onMovementCompleted != null) 
                    {
                        onMovementCompleted.run();
                    }
                }
            };
            
            button.addActionListener(moveListener);
        }

        return availableMoves;
    }

    // Metodo helper per pulire l'interfaccia dopo il movimento
    private void cleanupMovementUI(List<Point> availableMoves) 
    {
        for (Point p : availableMoves) 
        {
            JButton button = this.levelMap.getButtonAt(p.getX(), p.getY());
            
            // Rimuove tutti gli ActionListener dal bottone
            ActionListener[] listeners = button.getActionListeners();
            
            for (ActionListener listener : listeners)
            {
                button.removeActionListener(listener);
            }
            
            // Reset del colore di sfondo
            this.levelMap.resetGridColors();
        }
    }
    
    public void chooseTarget(List<Character> enemiesList, Character attacker, Runnable onAttackCompleted) 
    {
    	System.out.println(attacker.getClass().getSimpleName() + " sta valutando i bersagli nel raggio...");

    	List<Character> reachableEnemies = new ArrayList<>();
    	List<Point> availablePosition = new ArrayList<>();
    	
    	for (Character enemy : enemiesList) {
    	    int distance = attacker.getPosition().distanceFrom(enemy.getPosition());
    	    int range = attacker.getWeapon().getRange();

    	    if (distance <= range) {
    	        reachableEnemies.add(enemy);
    	        availablePosition.add(enemy.getPosition());
    	        System.out.println(" - " + enemy.getClass().getSimpleName() + " (distanza: " + distance + ")");
    	    }
    	    
    	}

    	if (reachableEnemies.isEmpty()) {
    		
    	    System.out.println("Nessun nemico nel raggio d'attacco. Attacco annullato.");
    	    onAttackCompleted.run();
    	    return;
    	}
    	this.levelMap.colourPositionAvailable(availablePosition, "red");
    	
    	System.out.println("Seleziona il bersaglio da attaccare.");

        List<JButton> enemyButtons = new ArrayList<>();

        for (Character enemy : enemiesList) 
        {
            Point enemyPos = enemy.getPosition();
            JButton enemyButton = this.levelMap.getButtonAt(enemyPos.getX(), enemyPos.getY());
            enemyButtons.add(enemyButton);

            int distance = attacker.getPosition().distanceFrom(enemyPos);
            int range = attacker.getWeapon().getRange();

            if (distance <= range) 
            {
            	System.out.println("Entrato qui dentro!!");
                enemyButton.setEnabled(true);
                enemyButton.addActionListener(new ActionListener() 
                {
                    @Override
                    public void actionPerformed(ActionEvent e) 
                    {
                        

                        // Disabilita tutti i bottoni e rimuove i listener
                        for (JButton b : enemyButtons) 
                        {
                            for (ActionListener al : b.getActionListeners()) {
                                b.removeActionListener(al);
                            }
                            // b.setEnabled(false); // Scommenta se vuoi disabilitare anche visivamente
                        }
                    	System.out.println("Entrato qui dentro2!!");

                        // Esegui l'attacco
                        System.out.println(attacker.getClass().getSimpleName() + " (HP: " + attacker.getCurrentHealth() + ", DMG: " + attacker.getPower() + 
                        	    ") ha selezionato " + enemy.getClass().getSimpleName() + " (HP: " + enemy.getCurrentHealth() + ", DEF: " + enemy.getDefence() + ")");

	        	        
                        Character deadCharacter = attacker.fight(enemy, levelMap.getAlliesList(), enemiesList);
                        if(deadCharacter != null)
                    		levelMap.removeCharacter(deadCharacter, deadCharacter.getPosition());
                        
                        System.out.println("\nPost Attacco "+enemy.getClass().getSimpleName()+" ha "+ enemy.getCurrentHealth()+" di vita");


                        // Callback completamento
                        if (onAttackCompleted != null) 
                        {
                            onAttackCompleted.run();
                        }
                    }
                });
            }
        }
    }
    
    public void graphicMovementCharacterToPoint (Character character, Point validPoint) 
    {
    	this.controller.move(levelMap, character, validPoint);
    }
}
