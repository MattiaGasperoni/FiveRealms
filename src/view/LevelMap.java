package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class LevelMap {
    private static final int GRID_SIZE = 6;
    private static final int BUTTON_SIZE = 80;
    private static final int MAX_LEVEL = 5;

    private JFrame frame;
    private JPanel gridPanel;
    private JLabel levelLabel;
    private JButton nextLevelButton;
    private int currentLevel = 1;

    private final String[] enemyImages = {
        "images/Character/Barbarian/barbarianBoss.png",
        "images/Character/Juggernaut/juggernautBoss.png",
        "images/Character/Knight/knightBoss.png",
        "images/Character/Wizard/wizardBoss.png"
    };
    
    private final String[] allyImages = {
        "images/Character/barbarianHero.png",
        "images/Character/juggernautHero.png",
        "images/Character/knightHero.png",
        "images/Character/mageHero.png",
        "images/Character/mageHeroHealer.png"
    };
    
    private final String[] backgrounds = {
        "images/Background/background1.jpg",
        "images/Background/background2.jpg",
        "images/Background/background3.jpg",
        "images/Background/background4.jpg",
        "images/Background/background5.jpg"
    };

    private final Random random = new Random();

    public LevelMap() {
        initializeFrame();
        initializeComponents();
        showTutorial();
    }

    private void initializeFrame() {
        frame = new JFrame("Saga dei 5 Regni");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 1000);
        frame.setLocationRelativeTo(null);

        frame.setContentPane(createContentPanel());
        frame.setLayout(null);
        frame.setVisible(true);
    }

    private JPanel createContentPanel() {
        return new JPanel(null) {
            private final Image background = new ImageIcon(backgrounds[random.nextInt(backgrounds.length)]).getImage();
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
            }
        };
    }

    private void initializeComponents() {
        levelLabel = new JLabel("Livello: " + currentLevel, SwingConstants.CENTER);
        levelLabel.setBounds(400, 10, 200, 30);
        levelLabel.setFont(new Font("Arial", Font.BOLD, 20));
        levelLabel.setForeground(Color.WHITE);
        frame.getContentPane().add(levelLabel);

        nextLevelButton = new JButton("Prossimo Livello");
        nextLevelButton.setBounds(400, 50, 200, 30);
        nextLevelButton.addActionListener(e -> nextLevel());
        frame.getContentPane().add(nextLevelButton);

        gridPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE));
        gridPanel.setOpaque(false);
        gridPanel.setBounds(50, 100, 900, 800);
        frame.getContentPane().add(gridPanel);
    }

    private void showTutorial() {
        levelLabel.setText("Tutorial");
        initializeGrid(true);
    }

    private void nextLevel() {
        if (currentLevel < MAX_LEVEL) {
            currentLevel++;
            levelLabel.setText("Livello: " + currentLevel);
            initializeGrid(false);
        } else {
            showEndGameDialog();
        }
    }

    private void showEndGameDialog() {
        nextLevelButton.setEnabled(false);
        JDialog dialog = new JDialog(frame, "Fine", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(frame);

        JLabel messageLabel = new JLabel("Gioco finito! Hai completato tutti i livelli!", SwingConstants.CENTER);
        dialog.add(messageLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton replayButton = new JButton("Rigioca");
        replayButton.addActionListener(e -> {
            resetGame();
            dialog.dispose();
        });
        buttonPanel.add(replayButton);

        JButton exitButton = new JButton("Esci");
        exitButton.addActionListener(e -> System.exit(0));
        buttonPanel.add(exitButton);

        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void resetGame() {
        currentLevel = 1;
        levelLabel.setText("Livello: " + currentLevel);
        nextLevelButton.setEnabled(true);
        initializeGrid(false);
    }

    private void initializeGrid(boolean isTutorial) {
        gridPanel.removeAll();
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                JButton button = new JButton();
                button.setContentAreaFilled(false);
                button.setBorderPainted(false);
                button.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
                
                if (isTutorial || random.nextBoolean()) {
                    setCellImage(button);
                }
                gridPanel.add(button);
            }
        }
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private void setCellImage(JButton button) {
        int type = random.nextInt(3);
        String imagePath = null;
        
        if (type == 1) {
            imagePath = enemyImages[random.nextInt(enemyImages.length)];
        } else if (type == 2) {
            imagePath = allyImages[random.nextInt(allyImages.length)];
        }
        
        if (imagePath != null) {
            button.setIcon(resizeImage(imagePath, BUTTON_SIZE, BUTTON_SIZE));
            button.setEnabled(true);
            button.addActionListener(new CellClickListener());
        }
    }

    private ImageIcon resizeImage(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    private class CellClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(frame, "Hai cliccato su un personaggio!", "Informazioni", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LevelMap::new);
    }
}