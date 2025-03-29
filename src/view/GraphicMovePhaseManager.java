package view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

import model.characters.Character;
import model.point.Point;
import view.map.LevelMap;
import view.map.TutorialMap;

public class GraphicMovePhaseManager {

    private LevelMap levelMap;
    private Character selectedTarget = null;  // Variabile per il bersaglio selezionato

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
        int moveRange = attacker.getSpeed() / 10;  // Determina la distanza di movimento
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
        return x >= 0 && x < LevelMap.GRID_SIZE && y >= 0 && y < LevelMap.GRID_SIZE;
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
            JButton button = levelMap.getButtonAt(point.getX(), point.getY());
            button.setBackground(Color.GRAY);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    attacker.moveTo(point);  // Muovi il personaggio alla nuova posizione
                }
            });
        }
    }

    // Seleziona un bersaglio tra gli avversari
    public Character chooseTarget(List<Character> enemiesList) {
        for (Character enemy : enemiesList) {
            Point position = enemy.getPosition();
            JButton button = levelMap.getButtonAt(position.getX(), position.getY());

            button.addActionListener(e -> {
                selectedTarget = enemy;  // Assegna il bersaglio selezionato
                System.out.println("Bersaglio selezionato: " + enemy.getClass().getSimpleName());
            });
        }

        return selectedTarget;
    }
    
}
