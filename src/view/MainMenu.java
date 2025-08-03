package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class MainMenu
{

    private JFrame frame;
    private JButton startButton;
    private JButton loadButton;
    private JButton exitButton;

    /**
     * Constructor that initializes the main menu frame and all its components
     * Sets up the window properties, creates buttons, and configures the layout
     */
    public MainMenu()
    {
    	this.frame = new JFrame("FiveRealms - MainMenu");
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setSize(800, 800);
        this.frame.setLocationRelativeTo(null);
        this.frame.setResizable(false);

        this.startButton = createButton("New Game");
        this.loadButton = createButton("Load Game");
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
    public void addStartListener(ActionListener listener) {
        this.startButton.addActionListener(listener);
    }

    /**
     * Adds an ActionListener to the load game button
     * @param listener The ActionListener to handle load button click events
     */
    public void addLoadListener(ActionListener listener) {
        this.loadButton.addActionListener(listener);
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

        gbc.gridy = 0;
        backgroundLabel.add(this.startButton, gbc);
        gbc.gridy = 1;
        backgroundLabel.add(this.loadButton, gbc);
        gbc.gridy = 2;
        backgroundLabel.add(this.exitButton, gbc);

        this.frame.add(backgroundLabel);
    }

    /**
     * Enables or disables the load game button
     * @param enabled true to enable the button, false to disable it
     */
    public void setLoadButtonEnabled(boolean enabled) {
        this.loadButton.setEnabled(enabled);
    }

    /**
     * Sets the visibility of the load game button
     * @param visible true to make the button visible, false to hide it
     */
    public void setLoadButtonVisible(boolean visible) {
        this.loadButton.setVisible(visible);
    }
}