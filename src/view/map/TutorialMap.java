package view.map;

import javax.swing.*;
import java.awt.*;
import model.point.Point; // Import della tua classe Point

public class TutorialMap extends LevelMap {

    public TutorialMap() {
        super();
        startTutorial();
    }

    private void startTutorial() {
        // Calcola il centro della griglia
        Point centerPoint = new Point(GRID_SIZE / 2, GRID_SIZE / 2); // Posizione centrale
        // Posizioni dei popup
        showTutorialPopup("Benvenuto, soldato! Prepara le tue forze e ascolta bene.", centerPoint, true); // Primo popup al centro
        showTutorialPopup("Attenzione! I nemici sono in agguato sopra di te. Sconfiggili!", new Point(0, 0), false); // Nemici in alto
        showTutorialPopup("In basso troverai i tuoi alleati pronti a combattere!", new Point(5, 0), false); // Alleati in basso
        showTutorialPopup("Usa le abilità speciali dei tuoi alleati per vincere!", new Point(2, 3), false); // Abilità speciali
        showTutorialPopup("La missione è chiara: sconfiggi tutti i nemici e porta la vittoria a casa!", new Point(4, 1), false); // Obiettivo
        showTutorialPopup("Buona fortuna soldato!", centerPoint, true); // Conclusione

    }

    private void showTutorialPopup(String message, Point gridPoint, boolean isCenter) {
        JDialog dialog = new JDialog();
        dialog.setSize(350, 130); // Dimensione del popup
        dialog.setLayout(new BorderLayout());
        dialog.setUndecorated(true); // Rimuove il bordo
        dialog.setModal(true);

        dialog.add(new JLabel(message, SwingConstants.CENTER), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("OK");
        JButton exitButton = new JButton("Esci");

        okButton.addActionListener(e -> dialog.dispose()); // Chiude solo il popup
        exitButton.addActionListener(e -> System.exit(0)); // Chiude l'intera applicazione

        buttonPanel.add(okButton);
        buttonPanel.add(exitButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Posiziona il popup
        if (isCenter) {
            // Posiziona il primo popup al centro della finestra
            dialog.setLocationRelativeTo(null); // Centro della finestra principale
        } else {
            // Posiziona gli altri popup in base alle coordinate fornite
            JButton targetButton = getButtonAt(gridPoint.getX(), gridPoint.getY());
            if (targetButton != null) {
                java.awt.Point buttonLocation = targetButton.getLocationOnScreen();
                dialog.setLocation(buttonLocation.x, buttonLocation.y); // Usa direttamente la posizione del bottone
            } else {
                System.err.println("Coordinate della griglia non valide: " + gridPoint);
            }
        }

        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TutorialMap::new);
    }
}
