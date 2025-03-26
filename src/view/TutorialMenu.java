package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TutorialMenu
{
    private boolean tutorialSelecion;
    
    public TutorialMenu()
    {
    	this.tutorialSelecion = false;
        System.out.println("Open Tuturial Menu Frame");

    }

    public void start(ActionListener callback) 
    {
        JFrame frame = createFrame();

        JLabel backgroundLabel = createBackgroundLabel();
        GridBagConstraints gbc = createGridBagConstraints();

        JLabel infoLabel = createInfoLabel();
        backgroundLabel.add(infoLabel, gbc);

        JButton yesButton = createButton("Yes", e -> handleSelection(frame, true, callback, e));
        JButton noButton = createButton("No", e -> handleSelection(frame, false, callback, e));
        JButton exitButton = createButton("Exit", e -> System.exit(0));

        addButtonsToBackground(backgroundLabel, yesButton, noButton, exitButton, gbc);
        
        frame.add(backgroundLabel);
        frame.setVisible(true);
    }

    private JFrame createFrame() 
    {
        JFrame frame = new JFrame("Tutorial Menu");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        return frame;
    }

    private JLabel createBackgroundLabel() 
    {
        JLabel backgroundLabel = new JLabel(new ImageIcon("images/Background/background4.jpg"));
        backgroundLabel.setLayout(new GridBagLayout());
        return backgroundLabel;
    }

    private JLabel createInfoLabel() 
    {
        JLabel infoLabel = new JLabel("Do you want to play the Tutorial?", SwingConstants.CENTER);
        System.out.println("Do you want to play the Tutorial? ");
        infoLabel.setFont(new Font("Arial", Font.BOLD, 20));
        infoLabel.setForeground(Color.WHITE);
        return infoLabel;
    }

    private GridBagConstraints createGridBagConstraints() 
    {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridy = 0;
        return gbc;
    }

    private JButton createButton(String text, ActionListener action) 
    {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(139, 69, 19));
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200, 50));
        button.addActionListener(action);
        return button;
    }

    private void handleSelection(JFrame frame, boolean selection, ActionListener callback, ActionEvent e)
    {
    	tutorialSelecion = selection;
        System.out.print(selection ? "Yes" : "No");
        frame.dispose();
        callback.actionPerformed(new ActionEvent(e.getSource(), ActionEvent.ACTION_PERFORMED, selection ? "Yes" : "No"));
    }

    private void addButtonsToBackground(JLabel backgroundLabel, JButton yesButton, JButton noButton, JButton exitButton, GridBagConstraints gbc) {
        gbc.gridy = 1;
        backgroundLabel.add(yesButton, gbc);
        gbc.gridy = 2;
        backgroundLabel.add(noButton, gbc);
        gbc.gridy = 3;
        backgroundLabel.add(exitButton, gbc);
    }

    public boolean isTutorialSelected() {
        return tutorialSelecion;
    }
}