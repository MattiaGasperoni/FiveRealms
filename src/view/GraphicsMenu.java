package view;

import javax.swing.*;
import java.awt.*;

public class GraphicsMenu {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GraphicsMenu::startMenu);
    }

    public static void startMenu() {
        JFrame frame = new JFrame("Graphics Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);

        JLabel backgroundLabel = new JLabel(new ImageIcon("images/Background/background4.jpg"));
        backgroundLabel.setLayout(new GridBagLayout());

        JButton startButton = createButton("New Game", e -> {
            frame.dispose(); // Chiude il menu attuale
            TutorialMenu tutorialMenu = new TutorialMenu(); // Apre il menu tutorial
            tutorialMenu.setVisible(true); // Rende visibile il menu tutorial
        });

        JButton loadButton = createButton("Load Game", e -> {
            frame.dispose(); // Chiude il menu attuale
            LoadGameMenu loadGameMenu = new LoadGameMenu(); // Crea una nuova istanza di LoadGameMenu
            loadGameMenu.setVisible(true); // Rende visibile il menu di caricamento
        });

        JButton exitButton = createButton("Exit", e -> System.exit(0));

        // Disabilita il pulsante "Load Game" se non ci sono salvataggi (logica da implementare)
        boolean hasSaves = checkSavedGames();
        loadButton.setEnabled(hasSaves);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridy = 0;
        backgroundLabel.add(startButton, gbc);
        gbc.gridy = 1;
        backgroundLabel.add(loadButton, gbc);
        gbc.gridy = 2;
        backgroundLabel.add(exitButton, gbc);

        frame.add(backgroundLabel);
        frame.setVisible(true);
    }

    private static JButton createButton(String text, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(139, 69, 19));
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200, 50));
        button.addActionListener(action);
        return button;
    }

    private static boolean checkSavedGames() {
        // Qui puoi aggiungere la logica per verificare la presenza di salvataggi
        return true; // Modifica questo valore in base ai tuoi dati
    }
}
