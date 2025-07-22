package view;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;
import javax.swing.*;

import controller.GameController;
import model.characters.Character;

public class PauseMenu {

    private JFrame frame;
    private JLayeredPane layeredPanel;
    private List<Character> enemiesList;
    private List<Character> alliesList;
    private final int numLevel;
    private GameController controller;

    public PauseMenu(JFrame frame, JLayeredPane layeredPanel, List<Character> enemiesList,
                     List<Character> alliesList, int numLevel, GameController controller) {
        this.frame = frame;
        this.layeredPanel = layeredPanel;
        this.enemiesList = enemiesList;
        this.alliesList = alliesList;
        this.numLevel = numLevel;
        this.controller = controller;
    }

    public void initializePauseMenu() {
        JPanel pauseMenuPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon backgroundImage = new ImageIcon("images/background/backgroundMenu.jpg");
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };

        pauseMenuPanel.setSize(frame.getSize());
        pauseMenuPanel.setOpaque(false);
        pauseMenuPanel.setLayout(new GridBagLayout());
        pauseMenuPanel.setVisible(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 20, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("PAUSE MENU", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Stencil", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);

        JButton resumeButton = createButton("Resume");
        JButton saveButton = createButton("Save Game");
        JButton exitButton = createButton("Exit");

        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        buttonPanel.add(resumeButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        buttonPanel.add(saveButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        buttonPanel.add(exitButton);

        resumeButton.addActionListener(e -> pauseMenuPanel.setVisible(false));

        saveButton.addActionListener(e -> {
            try {
                controller.saveGame(alliesList, enemiesList, numLevel);
                System.out.println("Game saved successfully!");
                pauseMenuPanel.setVisible(false);
            } catch (IOException ex) {
                System.err.println("Error saving game: " + ex.getMessage());
            }
        });

        exitButton.addActionListener(e -> System.exit(0));

        gbc.gridy++;
        pauseMenuPanel.add(titleLabel, gbc);
        gbc.gridy++;
        pauseMenuPanel.add(buttonPanel, gbc);

        JButton menuButton = new JButton(new ImageIcon(new ImageIcon("images/pauseGame.png")
                .getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH)));

        menuButton.setBounds(10, 10, 60, 60);
        menuButton.setBorderPainted(false);
        menuButton.setContentAreaFilled(false);
        menuButton.setFocusPainted(false);
        menuButton.addActionListener(e -> pauseMenuPanel.setVisible(true));

        layeredPanel.add(pauseMenuPanel, Integer.valueOf(3));
        layeredPanel.add(menuButton, Integer.valueOf(2));
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Stencil", Font.BOLD, 22));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(34, 34, 34));
        button.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(250, 60));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(new Color(44, 44, 44));
                button.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(new Color(34, 34, 34));
                button.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
            }
        });

        return button;
    }
}
