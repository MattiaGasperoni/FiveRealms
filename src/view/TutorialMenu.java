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
    
    public TutorialMenu()
    {
    	// Setup the frame
        this.frame = new JFrame("Tutorial Menu");
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setSize(800, 800);
        this.frame.setLocationRelativeTo(null);
        this.frame.setResizable(true);
        
        this.backgroundLabel = new JLabel(new ImageIcon("images/Background/background4.jpg"));
        this.backgroundLabel.setLayout(new GridBagLayout());
        
        this.gbc = new GridBagConstraints();
        this.gbc.insets = new Insets(10, 10, 10, 10);
        this.gbc.gridy = 0;
        
        this.infoLabel = new JLabel("Do you want to play the Tutorial?", SwingConstants.CENTER);
        this.infoLabel.setFont(new Font("Arial", Font.BOLD, 20));
        this.infoLabel.setForeground(Color.WHITE);
        
        this.backgroundLabel.add(this.infoLabel, this.gbc);
        
        this.yesButton  = createButton("Yes");
    	this.noButton   = createButton("No");
    	this.exitButton = createButton("Exit");
    	
		this.addButtonsToBackground(this.backgroundLabel, this.yesButton, this.noButton, this.exitButton, this.gbc);
		
		this.frame.add(this.backgroundLabel);
    }
    
    public void show() 
    {        
    	System.out.print("Open Tuturial Menu Frame ->");
        System.out.print(" Do you want to play the Tutorial? ->");

        this.frame.setVisible(true);
    }
    
    
    public void addYesListener(ActionListener listener) {
        yesButton.addActionListener(listener);
    }

    public void addNoListener(ActionListener listener) {
        noButton.addActionListener(listener);
    }

    public void addExitListener(ActionListener listener) {
        exitButton.addActionListener(listener);
    }
    
    public void close() {
        frame.dispose();
    }

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
    
    private void addButtonsToBackground(JLabel backgroundLabel, JButton yesButton, JButton noButton, JButton exitButton, GridBagConstraints gbc) {
        gbc.gridy = 1;
        backgroundLabel.add(yesButton, gbc);
        gbc.gridy = 2;
        backgroundLabel.add(noButton, gbc);
        gbc.gridy = 3;
        backgroundLabel.add(exitButton, gbc);
    }

}