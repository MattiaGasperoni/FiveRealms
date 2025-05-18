package view;

import java.awt.Button;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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
    public void movementPhase(Character attacker, List<Character> alliesList, List<Character> enemiesList) 
    {
        // Ottieni tutte le posizioni dei bottoni
        List<Point> availableMoves = getAvailablePositions(attacker, alliesList, enemiesList);

        //Mette i bottoni dentro alla lista availableButton 
        //availableMoves.stream().forEach(a -> availableButton.add(this.levelMap.getButtonAt(a.getX(), a.getY())));
        
        // Mostra i movimenti disponibili sulla griglia
        showAvailableMovementGrid(availableMoves);
        
    }

    // Questo metodo genera la lista dei bottoni, in cui il personaggio si può spostare
    private List<Point> getAvailablePositions(Character character, List<Character> alliesList, List<Character> enemiesList) {
        List<Point> availableMoves = new ArrayList<>();
        int moveRange = character.getSpeed() / 10;  // Range di movimento fisso per tutti i personaggi!!
        
        // Otteniamo tutti i pulsanti della mappa 
        JButton[][] buttonGrid = this.levelMap.getGridButtons();
        
        // Ora dobbiamo scorrere tutti i pulsanti e prendiamo solo quelli abbastanza vicini al nostro personaggio
        for (int y = 0; y < buttonGrid.length; y++){
		    for (int x = 0; x < buttonGrid[y].length; x++){
		        Point point = new Point(x, y);
		        JButton button = buttonGrid[x][y];
		        // Calcoliamo la distanza tra dove si trova il pesonaggio e l'iesimo bottone,  
		        // se abbastanza vicini e non ce gia un personaggio lo inseriamo nella lista dei possibili movimenti. 
		        if(point.distanceFrom(character.getPosition()) <= moveRange && !this.levelMap.isPositionOccupied(point)){
		        	availableMoves.add(point);
		        	
		        	button.addActionListener(click ->
	        			this.controller.move(levelMap, character, point)
		            );
		        	
		        }
		    }
		}
        
        return availableMoves;
    }

    // Mostra la griglia con i movimenti disponibili
    private void showAvailableMovementGrid(List<Point> availableMoves){
        for (Point point : availableMoves) {
            JButton button = this.levelMap.getButtonAt(point.getX(), point.getY());
            button.setBackground(Color.GRAY);
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
    
    // Mostra la griglia con gli attacchi possibili, fare un altro metodo che ti da una lista di posizioni occupate per l'attacco
    private void showAvailableAttackGrid(List<Point> availableMoves){
        for (Point point : availableMoves) {
            JButton button = this.levelMap.getButtonAt(point.getX(), point.getY());
            button.setBackground(Color.RED);
        }
    }
    
    
    

    // Seleziona un bersaglio tra gli avversari, IL metodo non funziona non aspetta che lutente clicchi il bersaglio
    /*public Character chooseTarget(List<Character> enemiesList) 
    {
        for (Character enemy : enemiesList) 
        {
            Point position = enemy.getPosition();
            JButton button = this.levelMap.getButtonAt(position.getX(), position.getY());

            button.addActionListener(e -> {
                selectedTarget = enemy;  // Assegna il bersaglio selezionato
                System.out.println("Bersaglio selezionato: " + enemy.getClass().getSimpleName());
            });
        }
        return this.selectedTarget;
    }*/
    

    
    public void chooseTarget(List<Character> enemiesList, Character attacker) {
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
                        
                    	attacker.fight(attacker, levelMap.getAlliesList(), enemiesList);

                    }
                });

            }
        }
    }
    
    /*
     * public void chooseTarget(List<Character> enemiesList, Consumer<Character> onTargetSelected) {
    List<JButton> enemyButtons = new ArrayList<>();

    for (Character enemy : enemiesList) {
        Point position = enemy.getPosition();
        JButton button = this.levelMap.getButtonAt(position.getX(), position.getY());
        enemyButtons.add(button);

        ActionListener actionListener = new ActionListener() {
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
                onTargetSelected.accept(enemy); // Esegui la lambda
            }
        };

        button.addActionListener(actionListener);
        button.setEnabled(true);
    }
}

     */

    
}
