package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.*;

public class LevelMap {
    private static final int GRID_SIZE = 6;
    private static final int BUTTON_SIZE = 80;
    private static final int MAX_LEVEL = 5;
    
    private JFrame frame;
    private JPanel gridPanel;
    private JLabel levelLabel;
    private JButton nextLevelButton;
    private int currentLevel = 1;
    private String[] enemyImages = {"images/logo.jpg"};
    private String[] allyImages = {"images/barbaroAlleato.png"};
    private Random random = new Random();

    public LevelMap() {
        // Constructor: Initializes the game frame and its components
        initializeFrame();
        initializeLevelLabel();
        initializeNextLevelButton();
        initializeGridPanel();
        showTutorial();
    }

    // Creates and configures the main game frame
    private void initializeFrame() {
        frame = new JFrame("Saga dei 5 Regni");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 1000);
        frame.setLocationRelativeTo(null);

        JPanel contentPanel = createContentPanel();
        frame.setContentPane(contentPanel);

        frame.setVisible(true);
    }

    // Creates a panel with a custom background image
    private JPanel createContentPanel() {
        return new JPanel(null) {
            private Image background = new ImageIcon("images/background2.jpg").getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
            }
        };
    }

    // Sets up and displays the level label
    private void initializeLevelLabel() {
        levelLabel = new JLabel("Livello: " + currentLevel, SwingConstants.CENTER);
        levelLabel.setBounds(400, 10, 200, 30);
        levelLabel.setFont(new Font("Arial", Font.BOLD, 20));
        levelLabel.setForeground(Color.WHITE);
        frame.getContentPane().add(levelLabel);
    }

    // Creates and configures the "Next Level" button
    private void initializeNextLevelButton() {
        nextLevelButton = new JButton("Prossimo Livello");
        nextLevelButton.setBounds(400, 50, 200, 30);
        nextLevelButton.addActionListener(e -> nextLevel());
        frame.getContentPane().add(nextLevelButton);
    }

    // Initializes the grid panel used for the game map
    private void initializeGridPanel() {
        gridPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE));
        gridPanel.setOpaque(false);
        gridPanel.setBounds(0, 100, frame.getWidth(), frame.getHeight() - 150);
        frame.getContentPane().add(gridPanel);
    }

    // Displays the tutorial level
    private void showTutorial() {
        levelLabel.setText("Tutorial");
        gridPanel.removeAll();
        addCellsToGrid(true);
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    // Handles the logic for progressing to the next level or displaying the end game screen
    private void nextLevel() {
        if (currentLevel < MAX_LEVEL) {
            currentLevel++;
            levelLabel.setText("Livello: " + currentLevel);
            initializeGrid();
        } else {
            nextLevelButton.setEnabled(false);

            JDialog dialog = new JDialog(frame, "Fine", true);
            dialog.setLayout(new BorderLayout());
            dialog.setSize(300, 150);
            dialog.setLocationRelativeTo(frame);

            JLabel messageLabel = new JLabel("Gioco finito! Hai completato tutti i livelli!", SwingConstants.CENTER);
            dialog.add(messageLabel, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new FlowLayout());

            JButton replayButton = new JButton("Rigioca");
            replayButton.setPreferredSize(new Dimension(100, 30));
            replayButton.addActionListener(e -> {
                resetGame();
                dialog.dispose();
            });

            JButton exitButton = new JButton("Esci");
            exitButton.setPreferredSize(new Dimension(100, 30));
            exitButton.addActionListener(e -> System.exit(0));

            buttonPanel.add(replayButton);
            buttonPanel.add(exitButton);

            dialog.add(buttonPanel, BorderLayout.SOUTH);
            dialog.setVisible(true);
        }
    }

    // Resets the game back to the first level
    private void resetGame() {
        currentLevel = 1;
        levelLabel.setText("Livello: " + currentLevel);
        nextLevelButton.setEnabled(true);
        initializeGrid();
    }

    // Initializes or refreshes the game grid
    private void initializeGrid() {
        gridPanel.removeAll();
        addCellsToGrid(false);
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    // Populates the grid with buttons representing cells
    private void addCellsToGrid(boolean isTutorial) {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                JButton button = new JButton();
                button.setContentAreaFilled(false);
                button.setBorderPainted(false);
                button.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));

                if (isTutorial) {
                    setTutorialImage(row, button);
                } else {
                    setCellImage(row, button);
                }

                button.addActionListener(new CellClickListener(row + 1, col + 1));
                gridPanel.add(button);
            }
        }
    }

    // Configures the tutorial images on the grid cells
    private void setTutorialImage(int row, JButton button) {
        setCellImage(row, button);
    }

    // Randomly assigns images to the grid cells for gameplay
    private void setCellImage(int row, JButton button) {
        int chance = random.nextInt(3);

        if (chance == 1 && row < GRID_SIZE / 2) {
            String enemyImage = enemyImages[random.nextInt(enemyImages.length)];
            button.setIcon(resizeImage(enemyImage, BUTTON_SIZE, BUTTON_SIZE));
        } else if (chance == 2 && row > GRID_SIZE / 2) {
            String allyImage = allyImages[random.nextInt(allyImages.length)];
            button.setIcon(resizeImage(allyImage, BUTTON_SIZE, BUTTON_SIZE));
        } else {
            button.setIcon(null);
        }
    }

    // Resizes an image to fit the button dimensions
    private ImageIcon resizeImage(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage();
        Image resizedImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImg);
    }

    // Handles cell click events and displays cell coordinates
    private class CellClickListener implements ActionListener {
        private int row, col;

        public CellClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(frame, "Personaggio con coordinate: (" + row + ", " + col + ")", "Informazioni", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Main method to launch the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(LevelMap::new);
    }
}
