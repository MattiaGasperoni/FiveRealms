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
	}

	@Override
	public void start() {
	    super.start();        
	    initializeMap();      
	}

    /**
     * Initializes the tutorial map and guides the player with instructions.
     */
    public void initializeMap() {
        Point centerPoint = new Point(GRID_SIZE / 2, GRID_SIZE / 2);
        
        showTutorialPopup("Welcome, soldier! Listen carefully.", centerPoint, true);
        
        highlightRowsWithTimer(0, 1, Color.RED, 1500, () -> {
            showTutorialPopup("Enemies are above you. Defeat them!", new Point(1, 2), false);
            
            highlightRowsWithTimer(4, 5, Color.BLUE, 1500, () -> {
                showTutorialPopup("Your allies are positioned below!", new Point(5, 2), false);
                showTutorialPopup("Use your allies' abilities to win!", new Point(4, 2), false);
                showTutorialPopup("Your mission is to defeat all enemies!", new Point(3, 2), false);
                showTutorialPopup("Good luck, soldier!", centerPoint, true);
            });
        });
    }

    /**
     * Highlights specified rows in the grid for a limited duration.
     *
     * @param startRow The starting row index.
     * @param endRow The ending row index.
     * @param color The highlight color.
     * @param duration The duration of the highlight in milliseconds.
     * @param afterAction The action to perform after the highlight.
     */
    private void highlightRowsWithTimer(int startRow, int endRow, Color color, int duration, Runnable afterAction) {
        for (int i = startRow; i <= endRow; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                gridPanel.getGridButtons()[i][j].setBackground(color);
                gridPanel.getGridButtons()[i][j].setOpaque(true);
                gridPanel.getGridButtons()[i][j].setBorderPainted(false);
            }
        }
        
        Timer timer = new Timer(duration, e -> {
            resetGridColors();
            afterAction.run(); // Executes the next action
        });
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * Displays a tutorial popup message 
     *
     * @param message The message to display.
     * @param gridPoint The point in the grid where the message is associated.
     * @param isCenter If true, the popup is centered on the screen.
     */
    private void showTutorialPopup(String message, Point gridPoint, boolean isCenter) {
        JDialog dialog = new JDialog();
        dialog.setSize(350, 130);
        dialog.setLayout(new BorderLayout());
        dialog.setUndecorated(true);
        dialog.setModal(true);

        JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 14));
        messageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dialog.add(messageLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JButton okButton = new JButton("OK");
        JButton exitButton = new JButton("Exit");

        okButton.addActionListener(e -> dialog.dispose());
        exitButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(okButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(exitButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        if (isCenter) {
            dialog.setLocationRelativeTo(null);
        } else {
        	
        	JButton targetButton = gridPanel.getGridButtons()[gridPoint.getX()][gridPoint.getY()];
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

    /**
     * Resets all grid button colors to their default state.
     */
    private void resetGridColors() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
            	gridPanel.getGridButtons()[i][j].setBackground(null);
                gridPanel.getGridButtons()[i][j].setOpaque(true);
                gridPanel.getGridButtons()[i][j].setBorderPainted(true);
            }
        }
    }
    
    /**
     * Entry point for testing the tutorial map independently.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            List<Character> emptyEnemies = List.of();
            List<Character> emptyAllies = List.of();
            TutorialMap tutorialMap = new TutorialMap(emptyEnemies, emptyAllies);
            tutorialMap.start(); // <-- fondamentale!
        });
    }

}
