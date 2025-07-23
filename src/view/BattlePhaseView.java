package view;

import java.awt.Button;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import controller.GameController;
import model.characters.Character;
import model.point.Point;
import view.map.AbstractMap;
import view.map.LevelMap;

public class BattlePhaseView 
{

	private LevelMap levelMap;
    private Character selectedTarget;  // Variabile per il bersaglio selezionato
    private GameController controller;
    
    public BattlePhaseView(LevelMap map, GameController controll) 
    {
		this.levelMap       = map;
		this.controller		= controll;
		this.selectedTarget = null;
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
        else
        {
        	System.out.println("Posizioni disponibili per " + character.getClass().getSimpleName() + ": " + availableMoves);
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
            button.setBackground(Color.GRAY);
            
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
        for (Point p : availableMoves) {
            JButton button = this.levelMap.getButtonAt(p.getX(), p.getY());
            
            // Rimuove tutti gli ActionListener dal bottone
            ActionListener[] listeners = button.getActionListeners();
            for (ActionListener listener : listeners) {
                button.removeActionListener(listener);
            }
            
            // Reset del colore di sfondo
            button.setBackground(null);
        }
    }
    
 // Questo metodo genera la lista dei bottoni, in cui il personaggio si può spostare
    private List<Point> getAttackablePositions(Character character, List<Character> alliesList, List<Character> enemiesList) {
        List<Point> availableAttackMoves = new ArrayList<>();
        
        // Otteniamo tutti i pulsanti della mappa 
        JButton[][] buttonGrid = this.levelMap.getGridButtons();
        
        // Ora dobbiamo scorrere tutti i pulsanti e prendiamo solo quelli abbastanza vicini al nostro personaggio
        for (int y = 0; y < buttonGrid.length; y++){
		    for (int x = 0; x < buttonGrid[y].length; x++){
		        Point point = new Point(x, y);
		        
		        // Calcoliamo la distanza tra dove si trova il pesonaggio e l'iesimo bottone,  
		        // se abbastanza vicini e non ce gia un personaggio lo inseriamo nella lista dei possibili movimenti. 
		        if(point.distanceFrom(character.getPosition()) <= character.getRange() && this.levelMap.isPositionOccupied(point)){
		        	availableAttackMoves.add(point);
		        }
		    }
		}
        
        return availableAttackMoves;
    }
    

    public void chooseTarget(List<Character> enemiesList, Character attacker, Runnable onAttackCompleted) {
        System.out.print("sono nella Fase di attacco");

        List<JButton> enemyButtons = new ArrayList<>();

        for (Character enemy : enemiesList) {
            Point position = enemy.getPosition();
            JButton button = this.levelMap.getButtonAt(position.getX(), position.getY());
            enemyButtons.add(button);

            // Calcolo della distanza
            int distance = attacker.getPosition().distanceFrom(enemy.getPosition());

            if (distance <= attacker.getWeapon().getRange()) {
                button.setEnabled(true);

                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Disabilita tutti i bottoni e rimuove i listener
                        for (JButton b : enemyButtons) {
                            for (ActionListener al : b.getActionListeners()) {
                                b.removeActionListener(al);
                            }
                            b.setEnabled(false);
                        }

                        System.out.println("Bersaglio selezionato: " + enemy.getClass().getSimpleName());

                        // Esegui l'attacco
                        attacker.fight(attacker, levelMap.getAlliesList(), enemiesList);

                        // Chiama il callback per notificare che l'attacco è completato
                        if (onAttackCompleted != null) {
                            onAttackCompleted.run();
                        }
                    }
                });
            }
        }
    }
    
    // Mostra la griglia con gli attacchi possibili, fare un altro metodo che ti da una lista di posizioni occupate per l'attacco
    private void showAvailableAttackGrid(List<Point> availableMoves){
        for (Point point : availableMoves) {
            JButton button = this.levelMap.getButtonAt(point.getX(), point.getY());
            button.setBackground(Color.RED);
        }
    }
}
