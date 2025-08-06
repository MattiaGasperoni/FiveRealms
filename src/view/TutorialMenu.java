package view;
import javax.swing.*;

import view.menu.AbstractMenu;

import java.awt.*;
import java.awt.event.*;

public class TutorialMenu extends AbstractMenu
{    
    private JButton yesButton;
    private JButton noButton;
    private JButton mainMenuButton;
    
    public TutorialMenu()
    {
        super("FiveRealms - Tutorial Menu");
    }
    
    @Override
    protected void initializeComponents()
    {
        this.yesButton      = super.createStyledButton("Yes");
        this.noButton       = super.createStyledButton("No");
        this.mainMenuButton = super.createStyledButton("Back to Main Menu");
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
        this.mainPanel.add(super.createTitleLabel("Would you like to start the tutorial?"), gbc);
                
        // Buttons panel
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 20, 20, 20);
        this.mainPanel.add(super.createButtonPanel(this.yesButton, this.noButton, this.mainMenuButton), gbc);
        
        // Bottom spacer
        gbc.gridy = 4;
        gbc.weighty = 0.4;
        gbc.fill = GridBagConstraints.BOTH;
        this.mainPanel.add(Box.createGlue(), gbc);
    }
    
	// === LISTENER METHODS ===
    
    /**
     * Adds an ActionListener to the yes button
     * @param listener The ActionListener to handle yes button click events
     */
    public void addYesListener(ActionListener listener) {
        this.yesButton.addActionListener(listener);
    }
    
    /**
     * Adds an ActionListener to the no button
     * @param listener The ActionListener to handle no button click events
     */
    public void addNoListener(ActionListener listener) {
    	this.noButton.addActionListener(listener);
    }
    
    /**
     * Adds an ActionListener to the Main Menu button
     * @param listener The ActionListener to handle exit button click events
     */
    public void addMainMenuListener(ActionListener listener) {
    	this.mainMenuButton.addActionListener(listener);
    }
}