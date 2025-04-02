package view.map;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import model.characters.Character;
import model.point.Point;

public class TutorialMap extends LevelMap {
    
    public TutorialMap(List<Character> enemiesList, List<Character> alliesList) {
    	super(enemiesList, alliesList);
    	initializeMap();
    }

    public void initializeMap() {
    	
        Point centerPoint = new Point(GRID_SIZE / 2, GRID_SIZE / 2);
        
        // Sequenza del tutorial con illuminazione temporizzata
        showTutorialPopup("Benvenuto, soldato! Ascolta bene.dfvvfderdervfrvfdfdrv", centerPoint, true);
        
        // 1° Illuminazione: prime due righe in alto (nemici)
        highlightRowsWithTimer(0, 1, Color.YELLOW, 1500, () -> {
        	showTutorialPopup("I nemici sono sopra di te. Sconfiggili!", new Point(0, 0), false);
            
            // 2° Illuminazione: ultime due righe in basso (alleati)
            highlightRowsWithTimer(4, 5, Color.YELLOW, 1500, () -> {
                showTutorialPopup("Qui in basso troverai i tuoi alleati!", new Point(5, 0), false);
                
                // Messaggi successivi senza illuminazione
                showTutorialPopup("Usa le abilità dei tuoi alleati per vincere!", new Point(4, 2), false);
                showTutorialPopup("La missione è sconfiggere tutti i nemici!", new Point(3, 2), false);
                showTutorialPopup("Buona fortuna soldato!", centerPoint, true);
            });
        });
    }

    private void highlightRowsWithTimer(int startRow, int endRow, Color color, int duration, Runnable afterAction) {
        // Applica l'illuminazione alle righe specificate
        for (int i = startRow; i <= endRow; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                gridButtons[i][j].setBackground(color);
                gridButtons[i][j].setOpaque(true);
                gridButtons[i][j].setBorderPainted(false);
            }
        }
        
        // Timer per rimuovere l'illuminazione dopo la durata specificata
        Timer timer = new Timer(duration, e -> {
            resetGridColors();
            afterAction.run(); // Esegue l'azione successiva
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void showTutorialPopup(String message, Point gridPoint, boolean isCenter) {
        JDialog dialog = new JDialog();
        dialog.setSize(350, 130);
        dialog.setLayout(new BorderLayout());
        dialog.setUndecorated(true);
        dialog.setModal(true);

        // Messaggio centrato con stile migliorato
        JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 14));
        messageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dialog.add(messageLabel, BorderLayout.CENTER);

        // Pulsanti con stile
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JButton okButton = new JButton("OK");
        JButton exitButton = new JButton("Esci");

        okButton.addActionListener(e -> dialog.dispose());
        exitButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(okButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(exitButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Posizionamento preciso
        if (isCenter) {
            dialog.setLocationRelativeTo(null);
        } else {
            JButton targetButton = gridButtons[gridPoint.getX()][gridPoint.getY()];
            if (targetButton != null) {
                java.awt.Point buttonLocation = targetButton.getLocationOnScreen();
                dialog.setLocation(
                    buttonLocation.x + targetButton.getWidth()/2 - dialog.getWidth()/2,
                    buttonLocation.y - dialog.getHeight() - 10
                );
            }
        }
        dialog.pack();
        dialog.setVisible(true);
    }

    private void resetGridColors() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                gridButtons[i][j].setBackground(null);
                gridButtons[i][j].setOpaque(false);
                gridButtons[i][j].setBorderPainted(true);
            }
        }
    }

	/*public void spawnCharacters(List<Character> allies, List<Character> enemies) {
		// TODO Auto-generated method stub
		
	}*/
	
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            List<Character> emptyEnemies = List.of();
            List<Character> emptyAllies = List.of();
            new TutorialMap(emptyEnemies, emptyAllies);
        });
    }

}