package view;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import javax.swing.*;
import controller.GameController;
import model.characters.Character;
import view.map.GridPanel;

public class PauseMenu{
	
	private JFrame frame;				 // Finestra principale del gioco
	private GridPanel gridPanel;       // Griglia di bottoni
    private JLayeredPane layeredPanel;   // Gestisce i vari layer del frame

    private List<Character> enemiesList; // Lista dei nemici
    private List<Character> alliesList;  // Lista degli alleati
    private final int numLevel;          // Numero del livello
    
    private GameController controller;
    
    private JButton resumeButton;
    private JButton saveButton;
    private JButton exitButton;
    
	
    public PauseMenu(JFrame frame, JLayeredPane layeredPanel, List<Character> enemiesList, List<Character> alliesList, int numLevel, GameController controller) {
        this.frame = frame;
        this.layeredPanel = layeredPanel;
        this.enemiesList = enemiesList;
        this.alliesList = alliesList;
        this.numLevel = numLevel;
        this.controller = controller;
    }

	
	/**
     * Initializes the Pause menu with buttons for resuming, saving, and exiting the game.
     */
    public void initializePauseMenu() 
    {
        // **Pannello unico che copre tutta la finestra**
        JPanel pauseMenuPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon backgroundImage = new ImageIcon("images/background/backgroundMenu.jpg");
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };

        pauseMenuPanel.setSize(frame.getSize());
        pauseMenuPanel.setOpaque(false); // Mantiene la trasparenza del background
        pauseMenuPanel.setLayout(new GridBagLayout()); // Layout per centrare elementi
        pauseMenuPanel.setVisible(false);

        // **Impedisce i clic su elementi esterni**
        pauseMenuPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                e.consume(); // Impedisce qualsiasi clic fuori dal menu
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 20, 0); // Spaziatura
        gbc.anchor = GridBagConstraints.CENTER;

        // **Titolo centrato**
        JLabel titleLabel = new JLabel("PAUSE MENU", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);

        // **Pannello interno per i pulsanti**
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);

        // Creazione dei pulsanti
        JButton resumeButton = new JButton("Resume");
        JButton saveButton = new JButton("Save Game");
        JButton exitButton = new JButton("Exit");

        for (JButton button : new JButton[]{resumeButton, saveButton, exitButton}) {
            button.setFont(new Font("Arial", Font.PLAIN, 20));
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setMaximumSize(new Dimension(200, 50));
            buttonPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Spaziatura tra i bottoni
            buttonPanel.add(button);
        }

        // **Azioni dei pulsanti** ----> da togliere pk ci sono i metodi sotto
        resumeButton.addActionListener(e -> pauseMenuPanel.setVisible(false));

        saveButton.addActionListener(e -> {
            try {
                this.controller.saveGame(this.alliesList, this.enemiesList, this.numLevel);
                System.out.println("Game saved successfully!");
                pauseMenuPanel.setVisible(false);
                
            } catch (IOException ex) {
                System.err.println("Error saving game: " + ex.getMessage());
            }
        });

        exitButton.addActionListener(e -> {
            System.out.println("You chose to close the game");
            System.exit(0);
        });

        gbc.gridy++; // Posiziona sotto il titolo
        pauseMenuPanel.add(titleLabel, gbc);
        gbc.gridy++;
        pauseMenuPanel.add(buttonPanel, gbc); // Aggiunge i pulsanti centrati

        // **Mostra il menu quando si clicca sull'immagine**
        JButton menuButton = new JButton(new ImageIcon(new ImageIcon("images/pauseGame.png")
            .getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH)));

        menuButton.setBounds(10, 10, 60, 60);
        menuButton.setBorderPainted(false);
        menuButton.setContentAreaFilled(false);
        menuButton.setFocusPainted(false);
        menuButton.addActionListener(e -> pauseMenuPanel.setVisible(true));

        this.layeredPanel.add(pauseMenuPanel, Integer.valueOf(3));
        this.layeredPanel.add(menuButton, Integer.valueOf(2));
    }
    
    
    // Farli bene
    public void addYesListener(ActionListener listener) {
    	this.resumeButton.addActionListener(listener);
    }

    public void addNoListener(ActionListener listener) {
    	this.saveButton.addActionListener(listener);
    }

    public void addExitListener(ActionListener listener) {
    	this.exitButton.addActionListener(listener);
    }

}
