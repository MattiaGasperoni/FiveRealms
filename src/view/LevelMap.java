package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class LevelMap {
    private static final int GRID_SIZE = 6; // Dimensione della griglia
    private static final int BUTTON_SIZE = 80; // Dimensione dei bottoni della griglia
    private static final int MAX_LEVEL = 5; // Numero massimo di livelli

    private JFrame frame;
    private JPanel gridPanel;
    private JLabel levelLabel;
    private JButton nextLevelButton;
    private int currentLevel = 1;
    private final Random random = new Random();

    public LevelMap() {
        initializeFrame();
        initializeComponents();
        initializeGrid();
    }

    private void initializeFrame() {
        frame = new JFrame("Saga dei 5 Regni");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 1000);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);
        frame.setVisible(true);

    }

    private void initializeComponents() {
        frame.setLayout(null); // Layout assoluto per il posizionamento preciso

        // Sfondo iniziale
        updateBackground(); // Modifica qui per caricare uno sfondo casuale

        // Label del livello
        levelLabel = new JLabel("Livello: " + currentLevel, SwingConstants.CENTER);
        levelLabel.setFont(new Font("Arial", Font.BOLD, 20));
        levelLabel.setForeground(Color.WHITE);
        levelLabel.setBounds(400, 10, 200, 30); // Posizione fissa
        frame.getLayeredPane().add(levelLabel, JLayeredPane.PALETTE_LAYER); // Sovrapposto sopra lo sfondo

        // Pulsante per passare al livello successivo
        nextLevelButton = new JButton("Prossimo Livello");
        nextLevelButton.setFont(new Font("Arial", Font.BOLD, 16));
        nextLevelButton.setBounds(400, 50, 200, 30);
        nextLevelButton.addActionListener(e -> nextLevel());
        frame.getLayeredPane().add(nextLevelButton, JLayeredPane.PALETTE_LAYER); // Anche questo sopra lo sfondo

        // Pannello della griglia con meno margine dai bordi
        gridPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE, 2, 2)); // Meno spazio tra i bottoni
        gridPanel.setBounds(80, 120, 840, 760); // Ridotto spazio dai bordi
        gridPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Minimizza i bordi
        gridPanel.setOpaque(false); // Permette di vedere lo sfondo dietro
        frame.getLayeredPane().add(gridPanel, JLayeredPane.PALETTE_LAYER); // Sovrapposto sopra lo sfondo
    }


    private void nextLevel() {
        if (currentLevel < MAX_LEVEL) {
            currentLevel++;
            levelLabel.setText("Livello: " + currentLevel);
            updateBackground(); // Cambia lo sfondo ad ogni livello
            initializeGrid();
        } else {
            showEndGameDialog();
        }
    }

    // Metodo per cambiare lo sfondo
    private void updateBackground() {
        // Carica l'immagine di sfondo
        ImageIcon backgroundIcon = new ImageIcon("images/background/background" + (int)(Math.random() * 6) + ".jpg");

        // Ottieni l'immagine dalla ImageIcon
        Image backgroundImage = backgroundIcon.getImage();

        // Ridimensiona l'immagine per adattarsi alla dimensione della finestra
        Image scaledImage = backgroundImage.getScaledInstance(frame.getWidth(), frame.getHeight(), Image.SCALE_SMOOTH);

        // Crea una nuova ImageIcon con l'immagine ridimensionata
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        // Aggiungi l'immagine ridimensionata come sfondo
        JLabel bgLabel = new JLabel(scaledIcon);
        bgLabel.setBounds(0, 0, frame.getWidth(), frame.getHeight()); // Copre tutta la finestra

        // Rimuovi la vecchia immagine di sfondo e aggiungi la nuova
        frame.getContentPane().removeAll();
        frame.getContentPane().add(bgLabel);
        frame.getContentPane().revalidate();
        frame.getContentPane().repaint();
    }


    private void showEndGameDialog() {
        nextLevelButton.setEnabled(false);
        JDialog dialog = new JDialog(frame, "Fine", true);
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(frame);
        dialog.setLayout(new BorderLayout());
        
        JLabel messageLabel = new JLabel("Gioco finito! Hai completato tutti i livelli!", SwingConstants.CENTER);
        dialog.add(messageLabel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        JButton replayButton = new JButton("Rigioca");
        replayButton.addActionListener(e -> {
            resetGame();
            dialog.dispose();
        });
        
        JButton exitButton = new JButton("Esci");
        exitButton.addActionListener(e -> System.exit(0));
        
        buttonPanel.add(replayButton);
        buttonPanel.add(exitButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void resetGame() {
        currentLevel = 1;
        levelLabel.setText("Livello: " + currentLevel);
        nextLevelButton.setEnabled(true);
        initializeGrid();
    }

    private void initializeGrid() {
        gridPanel.removeAll(); // Pulisce la griglia precedente

        for (int i = 0; i < GRID_SIZE * GRID_SIZE; i++) {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
            gridPanel.add(button);
        }

        gridPanel.repaint();
        gridPanel.revalidate();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LevelMap::new);
    }
}
