package view;
import javax.swing.*;
import model.gameStatus.saveSystem.GameStateManager;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;

public class LoadGameMenu {
    
    /**
     * Constructor that initializes and displays the load game menu
     */
    public LoadGameMenu() {
        startLoadGame();
    }
    
    /**
     * Creates and displays the load game menu interface with available save files
     * Shows a list of saved game files that can be loaded or an exit option
     */
    public void startLoadGame() {
        GameStateManager manager = new GameStateManager();
        JFrame frame = new JFrame("FiveRealms - Load Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        frame.setSize(800, 800);  // mantieni grande
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        JLabel backgroundLabel = new JLabel(new ImageIcon("images/Background/background4.jpg"));
        backgroundLabel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        // Riga vuota sopra (spinge il contenuto in basso)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;  // peso verticale per spingere in basso
        backgroundLabel.add(Box.createGlue(), gbc);
        // Titolo
        gbc.gridy = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 10, 20, 10);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        JLabel info = new JLabel("Select the appropriate save File", SwingConstants.CENTER);
        info.setFont(new Font("Arial", Font.BOLD, 20));
        info.setForeground(Color.WHITE);
        backgroundLabel.add(info, gbc);
        // Pulsanti load game
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 10, 10);
        if (manager.hasSaved()) {
            File[] saveFiles = manager.getSaveFiles();
            int row = gbc.gridy;
            for (File saveFile : saveFiles) {
                String fileName = saveFile.getName();
                int dotIndex = fileName.lastIndexOf('.');
                if (dotIndex > 0) fileName = fileName.substring(0, dotIndex);
                JButton loadGameButton = createButton(fileName, e -> {
                    System.out.println("Loading saved game: " + saveFile.getAbsolutePath());
                    frame.dispose();
                    // caricamento del file saveFile.getAbsolutePath()
                });
                gbc.gridy = row++;
                gbc.gridx = 0;
                gbc.gridwidth = GridBagConstraints.REMAINDER;
                gbc.anchor = GridBagConstraints.CENTER;
                backgroundLabel.add(loadGameButton, gbc);
            }
            gbc.gridy = row;
        }
        // Bottone Exit
        gbc.gridy++;
        JButton exitButton = createButton("Exit", e -> System.exit(0));
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        backgroundLabel.add(exitButton, gbc);
        // Riga vuota sotto (spinge il contenuto in alto)
        gbc.gridy++;
        gbc.weighty = 1.0;
        backgroundLabel.add(Box.createGlue(), gbc);
        frame.add(backgroundLabel);
        frame.setVisible(true);
    }
    
    /**
     * Creates a styled button with specified text and action listener
     * @param text The text to display on the button
     * @param action The ActionListener to handle button click events
     * @return A JButton with consistent styling applied
     */
    private JButton createButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(139, 69, 19));
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(280, 50));
        button.addActionListener(action);
        return button;
    }
}