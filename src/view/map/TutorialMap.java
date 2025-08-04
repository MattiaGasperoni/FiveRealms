package view.map;

import javax.swing.*;
import controller.GameController;
import java.awt.*;
import java.util.List;
import model.characters.Character;

public class TutorialMap extends AbstractMap {
    
    private boolean popupsCompleted = false;
    private List<Character> alliesList;
    private List<Character> enemiesList;
    private GameController controller;
    
    /**
     * Constructs a new TutorialMap with specified characters and controller
     * @param enemiesList The list of enemy characters
     * @param alliesList The list of ally characters
     * @param controller The game controller for callback notifications
     */
    public TutorialMap(List<Character> enemiesList, List<Character> alliesList, GameController controller) {
        super(enemiesList, alliesList, 0, controller);
        this.alliesList = alliesList;
        this.enemiesList = enemiesList;
        this.controller = controller;
    }
    
    /**
     * Checks if all tutorial popups have been completed
     * @return true if all tutorial messages have been shown, false otherwise
     */
    public boolean arePopupsCompleted() {
        return popupsCompleted;
    }
    
    /**
     * Starts the tutorial sequence by showing the first welcome message
     */
    public void startPopUpTutorial() {
        showWelcomeMessage();
    }
    
    /**
     * Shows the initial welcome message and highlights ally positions
     */
    private void showWelcomeMessage() {
        showTutorialPopup("Welcome, soldier! Listen carefully.", () -> 
            highlightRowsWithTimer(0, AbstractMap.GRID_SIZE_HEIGHT / 2, Color.RED, 1500, this::showEnemyWarning));
    }
    
    /**
     * Shows enemy warning message and highlights enemy positions
     */
    private void showEnemyWarning() {
        showTutorialPopup("The enemies are above you. Defeat them!", () -> 
            highlightRowsWithTimer(AbstractMap.GRID_SIZE_WIDTH / 2, AbstractMap.GRID_SIZE_WIDTH - 1, Color.BLUE, 1500, this::showAllyInfo));
    }
    
    /**
     * Shows ally information message
     */
    private void showAllyInfo() {
        showTutorialPopup("Your allies are down here!", this::showMissionInfo); 
    }
    
    /**
     * Shows the mission objective message
     */
    private void showMissionInfo() {
        showTutorialPopup("Your mission is to defeat all enemies!", this::showFinalMessage);
    }
    
    /**
     * Shows the final encouragement message and notifies controller
     */
    private void showFinalMessage() {
        showTutorialPopup("Good luck, soldier!", () -> {
            popupsCompleted = true;
            if (frame != null) {
                frame.setVisible(false);
            }
            if (controller != null) {
                controller.onTutorialPopupsCompleted();
            }
        });
    }
    
    /**
     * Restarts the tutorial with newly selected ally characters
     * @param selectedAllies The list of ally characters selected by player
     */
    public void restartWithSelectedCharacters(List<Character> selectedAllies) {
        this.alliesList.clear();
        this.alliesList.addAll(selectedAllies);
        
        if (frame != null) {
            frame.setVisible(true);
            frame.toFront();
        }
        
        spawnCharacter(this.alliesList);
        spawnCharacter(this.enemiesList);
                
        System.out.println("Tutorial riavviato con personaggi selezionati!");
    }
    
    /**
     * Highlights specified grid rows for a set duration
     * @param startRow The starting row index to highlight
     * @param endRow The ending row index to highlight
     * @param color The highlight color to apply
     * @param duration The highlight duration in milliseconds
     * @param afterAction The action to perform after highlighting completes
     */
    private void highlightRowsWithTimer(int startRow, int endRow, Color color, int duration, Runnable afterAction) {
        for (int i = startRow; i <= endRow; i++) {
            for (int j = 0; j < AbstractMap.GRID_SIZE_HEIGHT; j++) {
                JButton btn = gridPanel.getGridButtons()[i][j];
                btn.setBackground(color);
                btn.setOpaque(false);
                btn.setContentAreaFilled(true);
                btn.setBorderPainted(false);
            }
        }
        
        Timer timer = new Timer(duration, e -> {
            clearHighlightedRows();
            if (afterAction != null) {
                afterAction.run();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    /**
     * Displays a tutorial popup message with OK button
     * @param message The instructional message to display
     * @param afterAction The action to perform after popup is dismissed
     */
    private void showTutorialPopup(String message, Runnable afterAction) {
        JDialog dialog = new JDialog();
        dialog.setSize(360, 120);
        dialog.setUndecorated(true);
        dialog.setModal(true);
        dialog.setLocationRelativeTo(gridPanel);
        
        // Pannello principale con stile pergamena
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 230, 200));
        panel.setBorder(BorderFactory.createLineBorder(new Color(150, 100, 50), 2));
        
        // Testo con font più grande
        JLabel messageLabel = new JLabel("<html><div style='text-align: center;'>" + message + "</div></html>", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Times New Roman", Font.BOLD, 18)); 
        messageLabel.setForeground(new Color(70, 30, 10));
        messageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10)); 
        
        panel.add(messageLabel, BorderLayout.CENTER);
        
        // Pulsante con font più grande
        JButton okButton = new JButton("OK");
        okButton.setFont(new Font("Times New Roman", Font.BOLD, 14)); 
        okButton.setBackground(new Color(150, 100, 50));
        okButton.setForeground(Color.WHITE);
        okButton.setBorder(BorderFactory.createEmptyBorder(4, 15, 4, 15));
        okButton.setBorderPainted(false);
        okButton.setFocusPainted(false);
        okButton.addActionListener(e -> {
            dialog.dispose();
            clearHighlightedRows();
            if (afterAction != null) {
                afterAction.run();
            }
        });
        
        panel.add(okButton, BorderLayout.SOUTH);
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    /**
     * Clears all row highlighting from the grid
     */
    private void clearHighlightedRows() {
        for (int i = 0; i < AbstractMap.GRID_SIZE_WIDTH; i++) {
            for (int j = 0; j < AbstractMap.GRID_SIZE_HEIGHT; j++) {
                JButton btn = gridPanel.getGridButtons()[i][j];
                btn.setBackground(null);
                btn.setOpaque(false);
                btn.setContentAreaFilled(false);
                btn.setBorderPainted(false);
            }
        }
    }
}