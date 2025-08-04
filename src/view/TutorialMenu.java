package view;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TutorialMenu
{    
    private JFrame frame;
    private JLabel backgroundLabel;
    private GridBagConstraints gbc;
    private JLabel infoLabel;
    private JButton yesButton;
    private JButton noButton;
    private JButton exitButton;
    
    /**
     * Constructor that initializes the tutorial menu frame and all its components
     * Sets up the window properties, creates buttons, and configures the layout
     */
    public TutorialMenu()
    {
        // Setup the frame
        this.frame = new JFrame("FiveRealms - TutorialMenu");
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setSize(800, 800);
        this.frame.setLocationRelativeTo(null);
        this.frame.setResizable(false);
        this.backgroundLabel = new JLabel(new ImageIcon("images/Background/background4.jpg"));
        this.backgroundLabel.setLayout(new GridBagLayout());

        // GridBagConstraints per il titolo
        GridBagConstraints titleGbc = new GridBagConstraints();
        titleGbc.insets = new Insets(90, 10, 10, 10);
        titleGbc.gridy = 0;

        this.infoLabel = new JLabel(
    	    "<html>"
    	        + "<body style='text-align: center; background-color: rgba(0,0,0,0.8); "
    	        + "border: 2px solid #8B4513; padding: 6px 10px;'>"
    	        + "<span style='font-family: Serif; font-size: 16px; color: #D2B48C;'>"
    	        + "Welcome! Would you like to start the tutorial?"
    	        + "</span>"
    	        + "</body>"
    	        + "</html>",
    	    SwingConstants.CENTER
    	);

        this.backgroundLabel.add(this.infoLabel, titleGbc);

        // GridBagConstraints per i bottoni 
        this.gbc = new GridBagConstraints();
        this.gbc.insets = new Insets(10, 10, 10, 10);
        this.gbc.gridy = 1; 

        this.yesButton = createButton("Yes");
        this.noButton = createButton("No");
        this.exitButton = createButton("Exit");

        this.addButtonsToBackground(this.backgroundLabel, this.yesButton, this.noButton, this.exitButton, this.gbc);

        this.frame.add(this.backgroundLabel);
    }
    
    /**
     * Makes the tutorial menu frame visible and prints debug information to console
     */
    public void show() 
    {        
    	System.out.print("Open Tuturial Menu Frame ->");
        System.out.print(" Do you want to play the Tutorial? ->");
        this.frame.setVisible(true);
    }
    
    /**
     * Adds an ActionListener to the yes button
     * @param listener The ActionListener to handle yes button click events
     */
    public void addYesListener(ActionListener listener) {
        yesButton.addActionListener(listener);
    }
    
    /**
     * Adds an ActionListener to the no button
     * @param listener The ActionListener to handle no button click events
     */
    public void addNoListener(ActionListener listener) {
        noButton.addActionListener(listener);
    }
    
    /**
     * Adds an ActionListener to the exit button
     * @param listener The ActionListener to handle exit button click events
     */
    public void addExitListener(ActionListener listener) {
        exitButton.addActionListener(listener);
    }
    
    /**
     * Closes and disposes of the tutorial menu frame
     */
    public void close() {
        frame.dispose();
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
     * Adds the tutorial menu buttons to the background label using GridBagLayout
     * @param backgroundLabel The background label container to add buttons to
     * @param yesButton The yes button to add
     * @param noButton The no button to add
     * @param exitButton The exit button to add
     * @param gbc The GridBagConstraints object for layout positioning
     */
    private void addButtonsToBackground(JLabel backgroundLabel, JButton yesButton, JButton noButton, JButton exitButton, GridBagConstraints gbc) {
        gbc.gridy = 1;
        backgroundLabel.add(yesButton, gbc);
        gbc.gridy = 2;
        backgroundLabel.add(noButton, gbc);
        gbc.gridy = 3;
        backgroundLabel.add(exitButton, gbc);
    }
}