package view.map;

import javax.swing.*;

import controller.GameController;

import java.awt.*;
import java.util.List;
import model.characters.Character;
import model.point.Point;

/**
 * Tutorial con struttura sequenziale e uso esclusivo delle costanti.
 */
public class TutorialMap extends LevelMap {

    public TutorialMap(List<Character> enemiesList, List<Character> alliesList, GameController controller) {
        super(enemiesList, alliesList, 0, controller);
    }

    public void startPopUpTutorial() {
        showWelcomeMessage();
    }

    private void showWelcomeMessage() {
        showTutorialPopup("Welcome, soldier! Listen carefully.", () -> 
            highlightRowsWithTimer(0, AbstractMap.GRID_SIZE_HEIGHT / 2, Color.RED, 1500, this::showEnemyWarning));
    }

    private void showEnemyWarning() {
        showTutorialPopup("The enemies are above you. Defeat them!", () -> 
            highlightRowsWithTimer(AbstractMap.GRID_SIZE_WIDTH / 2, AbstractMap.GRID_SIZE_WIDTH - 1, Color.BLUE, 1500, this::showAllyInfo));
    }

    private void showAllyInfo() {
        showTutorialPopup("Your allies are down here!", this::showMissionInfo); 
    }

    private void showMissionInfo() {
        showTutorialPopup("Your mission is to defeat all enemies!", this::showFinalMessage);
    }

    private void showFinalMessage() {
        showTutorialPopup("Good luck, soldier!", null);
    }

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

    private void showTutorialPopup(String message, Runnable afterAction) {
        JDialog dialog = new JDialog();
        dialog.setSize(350, 130);
        dialog.setUndecorated(true);
        dialog.setModal(true);

        // Centra il popup nel pannello principale
        dialog.setLocationRelativeTo(gridPanel);

        JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 14));
        messageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dialog.add(messageLabel, BorderLayout.CENTER);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            dialog.dispose();
            clearHighlightedRows();
            if (afterAction != null) {
                afterAction.run();
            }
        });
        dialog.add(okButton, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

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
