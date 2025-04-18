package view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import controller.Controller;
import model.characters.Character;
import model.point.Point;
import view.map.AbstractMap;
import view.map.LevelMap;

public class GraphicMovePhaseManager 
{

    private LevelMap levelMap;
    private Character selectedTarget;  // Variabile per il bersaglio selezionato
    private Controller controller;
    
    public GraphicMovePhaseManager(LevelMap map, Controller controll) 
    {
		this.levelMap       = map;
		this.controller		= controll;
		this.selectedTarget = null;
	}

	// Gestisce la fase di movimento
    public void movementPhase(Character attacker, List<Character> alliesList, List<Character> enemiesList) {
        // Ottieni tutte le posizioni dei bottoni
        List<Point> availableMoves = getAvailableMoves(attacker, alliesList, enemiesList);

        // Mostra i movimenti disponibili sulla griglia
        showGrid(availableMoves, attacker);
    }

    // Ottiene le posizioni valide per i movimenti
    private List<Point> getAvailableMoves(Character attacker, List<Character> alliesList, List<Character> enemiesList) {
        List<Point> availableMoves = new ArrayList<>();
        int moveRange = attacker.getSpeed() / 10;  // Da cambiare utilizzando la costante in AbstractCharacter!!
        Point currentPosition = attacker.getPosition();

        // Aggiungi le posizioni libere entro il raggio di movimento
        for (int dx = -moveRange; dx <= moveRange; dx++) {
            for (int dy = -moveRange; dy <= moveRange; dy++) {
                int newX = currentPosition.getX() + dx;
                int newY = currentPosition.getY() + dy;

                if (isValidPosition(newX, newY) && !isOccupied(newX, newY, alliesList, enemiesList)) {
                    availableMoves.add(new Point(newX, newY));
                }
            }
        }
        return availableMoves;
    }

    // Verifica se la posizione è valida (entro i limiti della griglia)
    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < AbstractMap.GRID_SIZE_WIDTH && y >= 0 && y < AbstractMap.GRID_SIZE_HEIGHT;
    }

    // Verifica se una posizione è occupata da un alleato o nemico
    private boolean isOccupied(int x, int y, List<Character> alliesList, List<Character> enemiesList) {
        for (Character character : alliesList) {
            if (character.getPosition().getX() == x && character.getPosition().getY() == y) {
                return true;
            }
        }
        for (Character character : enemiesList) {
            if (character.getPosition().getX() == x && character.getPosition().getY() == y) {
                return true;
            }
        }
        return false;
    }
    
    // Mostra la griglia con i movimenti disponibili
    private void showGrid(List<Point> availableMoves, Character attacker) {
        for (Point point : availableMoves) {
            JButton button = this.levelMap.getButtonAt(point.getX(), point.getY());
            button.setBackground(Color.GRAY);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                	try {
                		controller.move(levelMap, attacker, point);
                	}catch (Exception ex) {
						// Aggiunta di jDialog con messaggio di e.getMessage()
                		
					}
                }
            });
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
    

    
    public void chooseTarget(List<Character> enemiesList, Consumer<Character> onTargetSelected) {
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

                    System.out.println("Bersaglio selezionato: " + enemy.getClass().getName());
                    onTargetSelected.accept(enemy); // Esegui la lambda
                }
            };

            button.addActionListener(actionListener);
            button.setEnabled(true);
        }
    }

    
}
