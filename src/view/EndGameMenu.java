package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import view.menu.AbstractMenu;

/**
 * EndGameMenu represents the screen displayed when a level or the entire game is completed.
 * It provides options to return to the main menu or exit the game.
 */
public class EndGameMenu extends AbstractMenu
{
    private JButton mainMenuButton;
    private JButton exitButton;

    /**
     * Constructs a new EndGameMenu with default settings
     * Initializes the window, buttons and layout
     */
    public EndGameMenu() 
    {
    	super("FiveRealms - End Game Menu");
    }
    
    @Override
    protected void initializeComponents()
    {
        this.mainMenuButton = super.createStyledButton("Back to Main Menu");
        this.exitButton     = super.createStyledButton("Exit");
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
        this.mainPanel.add(super.createTitleLabel("Game Completed!"), gbc);
        
        // Subtitle
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 20, 30, 20);
        this.mainPanel.add(super.createSubtitleLabel("Congratulations, you have completed all the levels!"), gbc);
        
        // Buttons panel
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 20, 20, 20);
        this.mainPanel.add(super.createButtonPanel(this.mainMenuButton, this.exitButton), gbc);
        
        // Bottom spacer
        gbc.gridy = 4;
        gbc.weighty = 0.4;
        gbc.fill = GridBagConstraints.BOTH;
        this.mainPanel.add(Box.createGlue(), gbc);
    }

    // === LISTENER METHODS ===
    
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
}