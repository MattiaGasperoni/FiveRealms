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
        this.loadButton  = createButton("Load Game");
        this.exitButton  = createButton("Exit");
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
     * Creates the main title label with medieval fantasy styling
     * @return A JLabel containing the game title with appropriate formatting
     */
    private JLabel createTitleLabel()
    {
        JLabel titleLabel = new JLabel("FIVE REALMS", SwingConstants.CENTER);
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
     * Creates the subtitle label with thematic description
     * @return A JLabel containing the game subtitle
     */
    private JLabel createSubtitleLabel()
    {
        JLabel subtitleLabel = new JLabel("Medieval Combat Adventure", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Serif", Font.ITALIC, 16));
        subtitleLabel.setForeground(Color.WHITE);
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        subtitleLabel.setOpaque(true);
        subtitleLabel.setBackground(new Color(0, 0, 0, 150));
        return subtitleLabel;
    }
    
    /**
     * Configures the layout of the main menu by setting up the background image,
     * adding the title at the top, subtitle below, and centering all buttons close together in the middle
     */
    private void setupLayout()
    {
        JLabel backgroundLabel = new JLabel(new ImageIcon("images/Background/background4.jpg"));
        backgroundLabel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Add title at the top
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        //gbc.weighty = 0.3; // Weight to push title up
        gbc.insets = new Insets(40, 10, 10, 10);
        backgroundLabel.add(this.createTitleLabel(), gbc);
        
        // Add subtitle slightly below title
        gbc.gridy = 1;
        //gbc.weighty = 0.1;
        gbc.insets = new Insets(5, 10, 40, 10); // Small gap after title, then bigger gap before buttons
        backgroundLabel.add(this.createSubtitleLabel(), gbc);
        
        // Center all buttons close together in the middle
        gbc.anchor = GridBagConstraints.CENTER;
        //gbc.weighty = 5.0; // No weight so buttons stay close together
        
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 10, 5, 10); // Small spacing between buttons
        backgroundLabel.add(this.startButton, gbc);
        
        gbc.gridy = 3;
        gbc.insets = new Insets(5, 10, 5, 10); // Small spacing between buttons
        backgroundLabel.add(this.loadButton, gbc);
        
        gbc.gridy = 4;
        //gbc.weighty = 0.6; // Large weight at bottom to center the button group
        gbc.insets = new Insets(5, 10, 10, 10); // Small spacing, then bottom margin
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