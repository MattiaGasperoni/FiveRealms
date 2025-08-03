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

public class EndGameMenu 
{

    private JFrame frame;
    private JLabel titleLabel;
    private JButton mainMenuButton;
    private JButton exitButton;

    /**
     * Constructor that initializes the end game menu frame and all its components
     * Sets up the window properties, creates buttons, and configures the layout
     */
    public EndGameMenu()
    {
        this.frame = new JFrame("FiveRealms - EndGameMenu");
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setSize(800, 800);
        this.frame.setLocationRelativeTo(null);
        this.frame.setResizable(false);

        // Aggiungere la dichiarazione del titolo
        this.titleLabel = new JLabel(
            "<html><div style='text-align: center; color: #8B4513; font-family: serif; font-size: 42px;'>" +
            "Level Complete!" +
            "</div></html>",
            SwingConstants.CENTER
        );

        this.mainMenuButton = createButton("Main Menu");
        this.exitButton = createButton("Exit");

        this.setupLayout();
    }
    /**
     * Makes the main menu frame visible to the user
     */
    public void show() {
        this.frame.setVisible(true);
    }

    /**
     * Closes and disposes of the main menu frame
     */
    public void close() {
        this.frame.dispose();
    }

    /**
     * Adds an ActionListener to the start game button
     * @param listener The ActionListener to handle start button click events
     */
    public void addMainMenuListener(ActionListener listener) {
        this.mainMenuButton.addActionListener(listener);
    }

    /**
     * Adds an ActionListener to the exit button
     * @param listener The ActionListener to handle exit button click events
     */
    public void addExitListener(ActionListener listener) {
        this.exitButton.addActionListener(listener);
    }

    /**
     * Creates a styled button with consistent appearance and properties
     * @param buttonText The text to display on the button
     * @return A JButton with standardized styling applied
     */
    private JButton createButton(String buttonText)
    {
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
     * Configures the layout of the main menu by setting up the background image
     * and positioning all buttons using GridBagLayout
     */
    private void setupLayout()
    {
        JLabel backgroundLabel = new JLabel(new ImageIcon("images/Background/background4.jpg"));
        backgroundLabel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // AGGIUNGERE IL TITOLO PRIMA DEI BOTTONI
        // Titolo con margine superiore maggiore
        gbc.insets = new Insets(50, 10, 30, 10); // Margine sopra più grande
        gbc.gridy = 0;
        backgroundLabel.add(this.titleLabel, gbc);

        // Reset margini per i bottoni
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Bottoni (iniziano dalla riga 1)
        gbc.gridy = 1;
        backgroundLabel.add(this.mainMenuButton, gbc);
        gbc.gridy = 2;
        backgroundLabel.add(this.exitButton, gbc);

        this.frame.add(backgroundLabel);
    }
}
