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

    public MainMenu() 
    {
        this.frame = new JFrame("Main Menu");
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setSize(800, 800);
        this.frame.setLocationRelativeTo(null);
        this.frame.setResizable(true);

        this.startButton = createButton("New Game");
        this.loadButton  = createButton("Load Game");
        this.exitButton  = createButton("Exit");

        this.setupLayout();
    }

    public void show() {
    	this.frame.setVisible(true);
    }

    public void close() {
    	this.frame.dispose();
    }

    public void addStartListener(ActionListener listener) {
    	this.startButton.addActionListener(listener);
    }

    public void addLoadListener(ActionListener listener) {
    	this.loadButton.addActionListener(listener);
    }

    public void addExitListener(ActionListener listener) {
    	this.exitButton.addActionListener(listener);
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
    
    public void setLoadButtonEnabled(boolean enabled) {
        this.loadButton.setEnabled(enabled);
    }

    public void setLoadButtonVisible(boolean visible) {
        this.loadButton.setVisible(visible);
    }
}

