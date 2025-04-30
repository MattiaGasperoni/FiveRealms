package view;

import model.gameStatus.Game;
import model.gameStatus.GameStateManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class MainMenu {

    private boolean NewGameSelected;
    private boolean LoadGameSelected;

    public MainMenu() {
        this.NewGameSelected = false;
        this.LoadGameSelected = false;
    }

    public void startMainMenu(Game g) {
        System.out.print("Open Main Menu Frame ->");

        JFrame frame = new JFrame("Main Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);

        JLabel backgroundLabel = new JLabel(new ImageIcon("images/Background/background4.jpg"));
        backgroundLabel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Pulsante "New Game"
        JButton startButton = createButton("New Game", e -> {
            NewGameSelected = true;
            frame.dispose();
            System.out.println(" You chose to start a new game");
            try {
                g.startNewGame();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        // Pulsante "Load Game"
        JButton loadButton = createButton("Load Game", e -> {
            LoadGameSelected = true;
            new LoadGameMenu(); // Avvia il menu di caricamento
            System.out.println(" You chose to load a game");
            frame.dispose();
        });

        // Controlla se esistono salvataggi
        GameStateManager gsm = new GameStateManager();
        if (!gsm.hasSaved()) {
            loadButton.setEnabled(false);
            loadButton.setToolTipText("Nessun salvataggio disponibile");
        }

        // Pulsante "Exit"
        JButton exitButton = createButton("Exit", e -> {
            System.out.println(" You chose to close the game");
            System.exit(0);
        });

        // Aggiunta dei pulsanti al layout
        gbc.gridy = 0;
        backgroundLabel.add(startButton, gbc);
        gbc.gridy = 1;
        backgroundLabel.add(loadButton, gbc);
        gbc.gridy = 2;
        backgroundLabel.add(exitButton, gbc);

        frame.add(backgroundLabel);
        frame.setVisible(true);
    }

    private JButton createButton(String text, java.awt.event.ActionListener action) {
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

    public boolean isNewGameSelected() {
        return this.NewGameSelected;
    }

    public boolean isLoadGameSelected() {
        return this.LoadGameSelected;
    }
}
