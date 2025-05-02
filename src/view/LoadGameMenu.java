package view;

import javax.swing.*;

import model.gameStatus.saveSystem.GameStateManager;

import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;

public class LoadGameMenu {

    public LoadGameMenu() {
        startLoadGame();
    }

    public void startLoadGame() {
        GameStateManager manager = new GameStateManager();

        JFrame frame = new JFrame("Load Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);

        JLabel backgroundLabel = new JLabel(new ImageIcon("images/Background/background4.jpg"));
        backgroundLabel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridy = 0;

        // Aggiunge il pulsante solo se esiste un salvataggio
        if (manager.hasSaved()) {
            String lastSave = manager.getSaveFile(1);
            if (lastSave != null) {
                JButton loadGameButton = createButton(manager.getSaveFile(1), e -> {
                    System.out.println("Loading saved game: " + manager.getSaveFile(1));
                    frame.dispose();
                    // Caricamento del salvataggio può avvenire qui
                });
                backgroundLabel.add(loadGameButton, gbc);
                gbc.gridy++;
            }
        }

        // Il pulsante "Exit" è sempre presente
        JButton exitButton = createButton("Exit", e -> System.exit(0));
        backgroundLabel.add(exitButton, gbc);

        frame.add(backgroundLabel);
        frame.setVisible(true);
    }

    private JButton createButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(139, 69, 19));
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(280, 50));
        button.addActionListener(action);
        return button;
    }

}
