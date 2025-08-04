package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * EndGameMenu represents the screen displayed when a level or the entire game is completed.
 * It provides options to return to the main menu or exit the game.
 */
public class EndGameMenu {
    private JFrame frame;
    private JButton mainMenuButton;
    private JButton exitButton;

    /**
     * Constructs a new EndGameMenu with default settings
     * Initializes the window, buttons and layout
     */
    public EndGameMenu() {
        this.frame = new JFrame("FiveRealms - EndGameMenu");
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setSize(800, 800);
        this.frame.setLocationRelativeTo(null);
        this.frame.setResizable(false);

        this.mainMenuButton = createButton("Main Menu");
        this.exitButton = createButton("Exit");

        this.setupLayout();
    }

    /**
     * Makes the end game menu visible
     */
    public void show() {
        this.frame.setVisible(true);
    }

    /**
     * Closes and disposes of the end game menu
     */
    public void close() {
        this.frame.dispose();
    }

    /**
     * Adds an ActionListener to the main menu button
     * @param listener The ActionListener to handle main menu button clicks
     */
    public void addMainMenuListener(ActionListener listener) {
        this.mainMenuButton.addActionListener(listener);
    }

    /**
     * Adds an ActionListener to the exit button
     * @param listener The ActionListener to handle exit button clicks
     */
    public void addExitListener(ActionListener listener) {
        this.exitButton.addActionListener(listener);
    }

    /**
     * Creates a styled button with consistent appearance
     * @param buttonText The text to display on the button
     * @return A JButton with standardized styling
     */
    private JButton createButton(String buttonText) {
        JButton button = new JButton(buttonText);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(139, 69, 19));
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200, 50));
        return button;
    }

    /**
     * Creates the title label with game-completion styling
     * @return A JLabel with formatted "LEVEL COMPLETE!" text
     */
    private JLabel createTitleLabel() {
        JLabel titleLabel = new JLabel("LEVEL COMPLETE!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 42));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(139, 69, 19), 2),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(0, 0, 0, 200));
        
        return titleLabel;
    }

    /**
     * Configures the layout of the end game menu
     * Sets up the background image and positions title and buttons
     */
    private void setupLayout() {
        JLabel backgroundLabel = new JLabel(new ImageIcon("images/Background/background4.jpg"));
        backgroundLabel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Add title at the top
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(40, 10, 30, 10);
        backgroundLabel.add(this.createTitleLabel(), gbc);
        
        // Center all buttons close together in the middle
        gbc.anchor = GridBagConstraints.CENTER;
        
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 10, 5, 10);
        backgroundLabel.add(this.mainMenuButton, gbc);
        
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 10, 10, 10);
        backgroundLabel.add(this.exitButton, gbc);
        
        this.frame.add(backgroundLabel);
    }
}