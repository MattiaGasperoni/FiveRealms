package view.menu;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class MainMenu extends AbstractMenu
{
    private JButton startButton;
    private JButton loadButton;
    private JButton exitButton;
    
    /**
     * Constructor that initializes the main menu frame and all its components
     * Sets up the window properties, creates buttons, and configures the layout
     */
    public MainMenu()
    {
        super("FiveRealms - Main Menu");
    }
    
    @Override
    protected void initializeComponents()
    {
        this.startButton = super.createStyledButton("New Game");
        this.loadButton  = super.createStyledButton("Load Game");
        this.exitButton  = super.createStyledButton("Exit");
    }
    
    @Override
    protected void setupLayout()
    {
        this.mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Top spacer
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.3;
        gbc.fill = GridBagConstraints.BOTH;
        this.mainPanel.add(Box.createGlue(), gbc);
        
        // Title
        gbc.gridy = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 20, 10, 20);
        this.mainPanel.add(super.createTitleLabel("FIVE REALMS"), gbc);
        
        // Subtitle
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 20, 30, 20);
        this.mainPanel.add(super.createSubtitleLabel("Medieval Combat Adventure"), gbc);
        
        // Buttons panel
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 20, 20, 20);
        this.mainPanel.add(super.createButtonPanel(this.startButton, this.loadButton, this.exitButton), gbc);
        
        // Bottom spacer
        gbc.gridy = 4;
        gbc.weighty = 0.4;
        gbc.fill = GridBagConstraints.BOTH;
        this.mainPanel.add(Box.createGlue(), gbc);
    }
    
    // === LISTENER METHODS ===
    
    /**
     * Adds an ActionListener to the start game button.
     * @param listener The ActionListener to handle start button click events
     */
    public void addStartListener(ActionListener listener) {
        this.startButton.addActionListener(listener);
    }
    
    /**
     * Adds an ActionListener to the load game button.
     * @param listener The ActionListener to handle load button click events
     */
    public void addLoadListener(ActionListener listener) {
        this.loadButton.addActionListener(listener);
    }
    
    /**
     * Adds an ActionListener to the exit button.
     * @param listener The ActionListener to handle exit button click events
     */
    public void addExitListener(ActionListener listener) {
        this.exitButton.addActionListener(listener);
    }
    
    // === UTILITY METHODS ===
    
    /**
     * Enables or disables the load game button.
     * @param enabled true to enable the button, false to disable it
     */
    public void setLoadButtonEnabled(boolean enabled) {
        this.loadButton.setEnabled(enabled);
    }
    
    /**
     * Sets the visibility of the load game button.
     * @param visible true to make the button visible, false to hide it
     */
    public void setLoadButtonVisible(boolean visible) {
        this.loadButton.setVisible(visible);
    }
}