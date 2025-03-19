package view;

import javax.swing.*;
import java.awt.*;
import model.gameStatus.Level;

public class TutorialMenu {
	
	public static boolean tutorialSelected = false;

    public static void startTutorialMenu() {
        JFrame frame = new JFrame("Tutorial Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);

        JLabel backgroundLabel = new JLabel(new ImageIcon("images/Background/background4.jpg"));
        backgroundLabel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel infoLabel = new JLabel("Do you want to play Tutorial?", SwingConstants.CENTER);
        infoLabel.setFont(new Font("Arial", Font.BOLD, 20));
        infoLabel.setForeground(Color.WHITE);
        gbc.gridy = 0;
        backgroundLabel.add(infoLabel, gbc);

        JButton yesButton = createButton("Yes", e -> {
            System.out.println("Yes");
            tutorialSelected = true; 
            
        });
        
        JButton noButton = createButton("No", e -> {
            System.out.println("No");
            tutorialSelected = false;
            
            
        });
        
        System.out.println(tutorialSelected);
        
        
        JButton exitButton = createButton("Exit", e -> System.exit(0));

        gbc.gridy = 1;
        backgroundLabel.add(yesButton, gbc);
        gbc.gridy = 2;
        backgroundLabel.add(noButton, gbc);
        gbc.gridy = 3;
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
        SwingUtilities.invokeLater(TutorialMenu::startTutorialMenu);
    }
    
}