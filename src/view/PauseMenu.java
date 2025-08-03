package view;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;
import javax.swing.*;

import controller.GameController;
import model.characters.Character;

public class PauseMenu {

    private JFrame frame;
    private JLayeredPane layeredPanel;
    private List<Character> enemiesList;
    private List<Character> alliesList;
    private final int numLevel;
    private GameController controller;

    /**
     * Constructor that initializes the pause menu with game state information
     * @param frame The main game frame
     * @param layeredPanel The layered panel where the pause menu will be displayed
     * @param enemiesList List of enemy characters in the current game
     * @param alliesList List of ally characters in the current game
     * @param numLevel Current level number
     * @param controller Game controller for handling save operations
     */
    public PauseMenu(JFrame frame, JLayeredPane layeredPanel, List<Character> enemiesList,
                     List<Character> alliesList, int numLevel, GameController controller) {
        this.frame = frame;
        this.layeredPanel = layeredPanel;
        this.enemiesList = enemiesList;
        this.alliesList = alliesList;
        this.numLevel = numLevel;
        this.controller = controller;
    }

    /**
     * Initializes and sets up the pause menu interface with fantasy-themed styling
     * Creates the pause menu panel, buttons, and pause trigger button
     */
    public void initializePauseMenu() {
        JPanel pauseMenuPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                
                // Abilita antialiasing per una grafica più fluida
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Sfondo semi-trasparente con effetto blur
                g2d.setColor(new Color(0, 0, 0, 120)); // Nero semi-trasparente
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Pannello centrale con stile pergamena/legno
                int panelWidth = 400;
                int panelHeight = 350;
                int x = (getWidth() - panelWidth) / 2;
                int y = (getHeight() - panelHeight) / 2;
                
                // Ombra del pannello
                g2d.setColor(new Color(0, 0, 0, 80));
                g2d.fillRoundRect(x + 8, y + 8, panelWidth, panelHeight, 20, 20);
                
                // Gradiente per il pannello principale (effetto pergamena)
                GradientPaint gradient = new GradientPaint(
                    x, y, new Color(101, 67, 33), // Marrone scuro
                    x, y + panelHeight, new Color(139, 117, 82) // Marrone chiaro
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(x, y, panelWidth, panelHeight, 15, 15);
                
                // Bordo decorativo esterno
                g2d.setStroke(new BasicStroke(4));
                g2d.setColor(new Color(160, 130, 90)); // Oro antico
                g2d.drawRoundRect(x, y, panelWidth, panelHeight, 15, 15);
                
                // Bordo decorativo interno
                g2d.setStroke(new BasicStroke(2));
                g2d.setColor(new Color(80, 50, 20)); // Marrone molto scuro
                g2d.drawRoundRect(x + 6, y + 6, panelWidth - 12, panelHeight - 12, 10, 10);
                
                // Decorazioni angolari (piccoli dettagli fantasy)
                drawCornerDecorations(g2d, x, y, panelWidth, panelHeight);
                
                g2d.dispose();
            }
            
            /**
             * Draws decorative corner elements for the pause menu panel
             * @param g2d Graphics2D object for drawing
             * @param x X coordinate of the panel
             * @param y Y coordinate of the panel
             * @param width Width of the panel
             * @param height Height of the panel
             */
            private void drawCornerDecorations(Graphics2D g2d, int x, int y, int width, int height) {
                g2d.setColor(new Color(160, 130, 90));
                g2d.setStroke(new BasicStroke(2));
                
                int decorSize = 15;
                
                // Angolo superiore sinistro
                g2d.drawLine(x + 15, y + 15, x + 15 + decorSize, y + 15);
                g2d.drawLine(x + 15, y + 15, x + 15, y + 15 + decorSize);
                
                // Angolo superiore destro
                g2d.drawLine(x + width - 15, y + 15, x + width - 15 - decorSize, y + 15);
                g2d.drawLine(x + width - 15, y + 15, x + width - 15, y + 15 + decorSize);
                
                // Angolo inferiore sinistro
                g2d.drawLine(x + 15, y + height - 15, x + 15 + decorSize, y + height - 15);
                g2d.drawLine(x + 15, y + height - 15, x + 15, y + height - 15 - decorSize);
                
                // Angolo inferiore destro
                g2d.drawLine(x + width - 15, y + height - 15, x + width - 15 - decorSize, y + height - 15);
                g2d.drawLine(x + width - 15, y + height - 15, x + width - 15, y + height - 15 - decorSize);
            }
        };

        pauseMenuPanel.setSize(frame.getSize());
        pauseMenuPanel.setOpaque(false);
        pauseMenuPanel.setLayout(new GridBagLayout());
        pauseMenuPanel.setVisible(false);

        // Blocca i click fuori dai pulsanti
        pauseMenuPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                e.consume();
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 30, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        // Titolo con stile fantasy
        JLabel titleLabel = new JLabel("PAUSE MENU", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 36));
        titleLabel.setForeground(new Color(245, 222, 179)); // Beige/oro chiaro

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);

        JButton resumeButton = createFantasyButton("Resume", "⚔");
        JButton saveButton = createFantasyButton("Save Game", "📜");
        JButton exitButton = createFantasyButton("Exit", "🚪");

        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(resumeButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        buttonPanel.add(saveButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        buttonPanel.add(exitButton);

        resumeButton.addActionListener(e -> pauseMenuPanel.setVisible(false));

        saveButton.addActionListener(e -> {
            try {
                controller.saveGame(alliesList, enemiesList, numLevel);
                System.out.println("Game saved successfully!");
                // Feedback visivo per il salvataggio
                showSaveConfirmation(saveButton);
                pauseMenuPanel.setVisible(false);
            } catch (IOException ex) {
                System.err.println("Error saving game: " + ex.getMessage());
            }
        });

        exitButton.addActionListener(e -> System.exit(0));

        gbc.gridy++;
        pauseMenuPanel.add(titleLabel, gbc);
        gbc.gridy++;
        pauseMenuPanel.add(buttonPanel, gbc);

        // Pulsante di pausa con stile migliorato
        JButton menuButton = createPauseButton();

        layeredPanel.add(pauseMenuPanel, Integer.valueOf(4));
        layeredPanel.add(menuButton, Integer.valueOf(3));
    }

    /**
     * Creates a fantasy-themed button with icon and hover effects
     * @param text The text to display on the button
     * @param icon The icon/emoji to display alongside the text
     * @return A JButton with fantasy styling and interactive effects
     */
    private JButton createFantasyButton(String text, String icon) {
        JButton button = new JButton(" " + icon + "  " + text + " ");
        button.setFont(new Font("Serif", Font.BOLD, 18));
        button.setForeground(new Color(245, 222, 179)); // Beige/oro chiaro
        
        // Gradiente per il pulsante
        button.setBackground(new Color(101, 67, 33)); // Marrone scuro
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(280, 50));
        button.setPreferredSize(new Dimension(280, 50));

        // Effetti hover migliorati
        button.addMouseListener(new MouseAdapter() {
            private Color originalBg = new Color(101, 67, 33);
            private Color hoverBg = new Color(139, 117, 82);
            
            @Override
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(hoverBg);
                button.setForeground(new Color(255, 248, 220)); // Quasi bianco
                // Effetto di sollevamento
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createRaisedBevelBorder(),
                    BorderFactory.createEmptyBorder(6, 18, 10, 22)
                ));
            }
            
            @Override
            public void mouseExited(MouseEvent evt) {
                button.setBackground(originalBg);
                button.setForeground(new Color(245, 222, 179));
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createRaisedBevelBorder(),
                    BorderFactory.createEmptyBorder(8, 20, 8, 20)
                ));
            }
            
            @Override
            public void mousePressed(MouseEvent evt) {
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLoweredBevelBorder(),
                    BorderFactory.createEmptyBorder(9, 21, 7, 19)
                ));
            }
            
            @Override
            public void mouseReleased(MouseEvent evt) {
                if (button.contains(evt.getPoint())) {
                    button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createRaisedBevelBorder(),
                        BorderFactory.createEmptyBorder(6, 18, 10, 22)
                    ));
                }
            }
        });

        return button;
    }
    
    /**
     * Creates the pause button that triggers the pause menu display
     * @return A JButton styled as a pause icon that shows the pause menu when clicked
     */
    private JButton createPauseButton() {
        JButton menuButton = new JButton(new ImageIcon(new ImageIcon("images/pauseGame.png")
                .getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
        
        menuButton.setBounds(10, 10, 60, 60);
        menuButton.setBorderPainted(false);
        menuButton.setContentAreaFilled(false);
        menuButton.setFocusPainted(false);
        menuButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        menuButton.addActionListener(e -> {
            Component[] components = layeredPanel.getComponentsInLayer(4);
            if (components.length > 0) {
                components[0].setVisible(true);
            }
        });
        
        return menuButton;
    }
    
    /**
     * Shows a temporary visual confirmation when the game is successfully saved
     * @param saveButton The save button to modify for showing confirmation feedback
     */
    private void showSaveConfirmation(JButton saveButton) {
        String originalText = saveButton.getText();
        saveButton.setText(" ✓  Saved! ");
        saveButton.setBackground(new Color(34, 139, 34));
        
        Timer timer = new Timer(1500, e -> {
            saveButton.setText(originalText);
            saveButton.setBackground(new Color(101, 67, 33));
        });
        timer.setRepeats(false);
        timer.start();
    }
}