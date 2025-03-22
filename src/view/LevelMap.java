package view;

import javax.swing.*;
import model.characters.Character;
import java.awt.*;
import java.util.*;
import java.util.List;

public class LevelMap {
    private static final int GRID_SIZE = 6;
    private static final int BUTTON_SIZE = 80;
    private static final int MAX_LEVEL = 5;
    
    private JFrame frame;
    private JPanel gridPanel;
    private JLabel levelLabel;
    private JButton nextLevelButton;
    private int currentLevel = 1;
    private final Random random = new Random();
    private JButton[][] gridButtons = new JButton[GRID_SIZE][GRID_SIZE];

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
        frame.setLayout(null);
        updateBackground();

        levelLabel = new JLabel("Livello: " + currentLevel, SwingConstants.CENTER);
        levelLabel.setFont(new Font("Arial", Font.BOLD, 20));
        levelLabel.setForeground(Color.WHITE);
        levelLabel.setBounds(400, 10, 200, 30);
        frame.getLayeredPane().add(levelLabel, JLayeredPane.PALETTE_LAYER);

        nextLevelButton = new JButton("Prossimo Livello");
        nextLevelButton.setFont(new Font("Arial", Font.BOLD, 16));
        nextLevelButton.setBounds(400, 50, 200, 30);
        nextLevelButton.addActionListener(e -> nextLevel());
        frame.getLayeredPane().add(nextLevelButton, JLayeredPane.PALETTE_LAYER);

        gridPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE, 2, 2));
        gridPanel.setBounds(80, 120, 840, 760);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        gridPanel.setOpaque(false);
        frame.getLayeredPane().add(gridPanel, JLayeredPane.PALETTE_LAYER);
    }

    private void nextLevel() {
        if (currentLevel < MAX_LEVEL) {
            currentLevel++;
            levelLabel.setText("Livello: " + currentLevel);
            updateBackground();
            initializeGrid();
        } else {
            showEndGameDialog();
        }
    }

    private void updateBackground() {
        ImageIcon backgroundIcon = new ImageIcon("images/background/background" + (int)(Math.random() * 6) + ".jpg");
        Image backgroundImage = backgroundIcon.getImage();
        Image scaledImage = backgroundImage.getScaledInstance(frame.getWidth(), frame.getHeight(), Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel bgLabel = new JLabel(scaledIcon);
        bgLabel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
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
        gridPanel.removeAll();
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                gridButtons[i][j] = new JButton();
                gridButtons[i][j].setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
                gridPanel.add(gridButtons[i][j]);
            }
        }
        gridPanel.repaint();
        gridPanel.revalidate();
    }

    public void spawnCharacters(List<Character> enemiesList,List<Character> alliesList) {
        Set<Point> occupiedPositions = new HashSet<>();
        Random random = new Random();

        // Spawna i nemici nella metà superiore della griglia
        for (Character enemy : enemiesList) {
            Point position;
            do {
                position = new Point(random.nextInt(GRID_SIZE), random.nextInt(GRID_SIZE / 2));
            } while (occupiedPositions.contains(position));
            occupiedPositions.add(position);
            gridButtons[position.x][position.y].setText("E"); // Indica un nemico
        }

        // Spawna gli alleati (da selectedAllies) nella metà inferiore
        for (Character ally : alliesList) {
            Point position;
            do {
                position = new Point(random.nextInt(GRID_SIZE), random.nextInt(GRID_SIZE / 2) + GRID_SIZE / 2);
            } while (occupiedPositions.contains(position));
            occupiedPositions.add(position);
            gridButtons[position.x][position.y].setText("A"); // Indica un alleato
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(LevelMap::new);
    }
}
