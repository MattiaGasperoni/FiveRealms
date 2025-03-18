package view;

import javax.swing.*;
import java.awt.*;

public class LoadGameMenu {

    public static void startLoadGame() {
        JFrame frame = new JFrame("Load Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);

        JLabel backgroundLabel = new JLabel(new ImageIcon("images/Background/background4.jpg"));
        backgroundLabel.setLayout(new GridBagLayout());

        JButton level1Button = createButton("Level 1", e -> {
            System.out.println("Inizia partita Level 1");
            frame.dispose();
        });
        
        JButton level2Button = createButton("Level 2", e -> {
            System.out.println("Inizia partita Level 2");
            frame.dispose();
        });
        
        JButton level3Button = createButton("Level 3", e -> {
            System.out.println("Inizia partita Level 3");
            frame.dispose();
        });
        
        JButton level4Button = createButton("Level 4", e -> {
            System.out.println("Inizia partita Level 4");
            frame.dispose();
        });
        
        JButton exitButton = createButton("Exit", e -> System.exit(0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridy = 0;
        backgroundLabel.add(level1Button, gbc);
        gbc.gridy = 1;
        backgroundLabel.add(level2Button, gbc);
        gbc.gridy = 2;
        backgroundLabel.add(level3Button, gbc);
        gbc.gridy = 3;
        backgroundLabel.add(level4Button, gbc);
        gbc.gridy = 4;
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoadGameMenu::startLoadGame);
    }
}