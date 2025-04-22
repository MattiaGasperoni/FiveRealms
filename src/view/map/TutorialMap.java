package view.map;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import model.characters.Character;
import model.point.Point;

/**
 * Represents the tutorial in the game.
 * Provides guided steps to introduce players to the game mechanics.
 */
public class TutorialMap extends LevelMap {

    /**
     * Constructor
     * @param enemiesList List of enemy characters.
     * @param alliesList List of allied characters.
     */
    public TutorialMap(List<Character> enemiesList, List<Character> alliesList) {
        super(enemiesList, alliesList, 0);

        SwingUtilities.invokeLater(() -> {
            showTutorialPopup("Benvenuto al tutorial!", new Point(0, 0), () -> {
                highlightRows(0, AbstractMap.GRID_SIZE_HEIGHT / 2 - 1, Color.RED);
                
                showTutorialPopup("Attenzione! Nemici in arrivo.", new Point(1, 0), () -> {
                    highlightRows(AbstractMap.GRID_SIZE_HEIGHT / 2, AbstractMap.GRID_SIZE_HEIGHT - 1, Color.BLUE);

                    showTutorialPopup("I tuoi alleati sono questi.", new Point(2, 0), () -> {
                    	
                        showTutorialPopup("Raccogli oggetti per migliorare le abilità.", new Point(3, 0), () -> {
                        	showTutorialPopup("Complimenti! Tutorial completato.", new Point(4, 0), () -> {
                        	});
                        });
                    });
                });
            });
        });
    }

    /**
     * Evidenzia un intervallo di righe con un colore specifico.
     * @param startRow La riga di partenza.
     * @param endRow La riga finale.
     * @param color Il colore da applicare.
     */
    private void highlightRows(int startRow, int endRow, Color color) {
        for (int i = startRow; i <= endRow; i++) {
            for (int j = 0; j < AbstractMap.GRID_SIZE_HEIGHT; j++) {
                JButton button = gridPanel.getGridButtons()[i][j];

                button.setBackground(color);
                button.setOpaque(true);
                button.setBorderPainted(false);

                // Permetti il click, ma evita hover strani
                button.setFocusPainted(false);
                button.setContentAreaFilled(true); 
            }
        }
    }



    /**
     * Mostra un popup tutorial e avvia l'azione successiva dopo la chiusura.
     * @param message Il messaggio da visualizzare.
     * @param gridPoint La posizione del popup nella griglia.
     * @param afterAction L'azione successiva dopo la chiusura del popup.
     */
    private void showTutorialPopup(String message, Point gridPoint, Runnable afterAction) {
        JDialog dialog = new JDialog();
        dialog.setSize(350, 130);
        dialog.setUndecorated(true);
        dialog.setModal(true);

        JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 14));
        messageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dialog.add(messageLabel, BorderLayout.CENTER);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            dialog.dispose();
            
            // Cancella la colorazione evidenziata prima di procedere
            clearHighlightedRows();

            if (afterAction != null) {
                afterAction.run(); // Avvia la prossima azione dopo la chiusura del popup
            }
        });

        dialog.add(okButton, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    /**
     * Cancella tutte le righe evidenziate
     */
    private void clearHighlightedRows() {
        for (int i = 0; i < AbstractMap.GRID_SIZE_WIDTH; i++) {
            for (int j = 0; j < AbstractMap.GRID_SIZE_HEIGHT; j++) {
            }
        }
    }


    /**
     * Metodo `main()` per avviare il tutorial indipendentemente.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            List<Character> emptyEnemies = List.of();
            List<Character> emptyAllies = List.of();
            TutorialMap tutorial = new TutorialMap(emptyEnemies, emptyAllies);
            tutorial.start();
        });
    }
}
