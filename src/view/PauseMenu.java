package view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class PauseMenu 
{
    private JFrame frame;
    private JLayeredPane layeredPanel;
    
    private JButton resumeButton;
    private JButton saveButton;
    private JButton exitButton;
	private JButton menuIcon;
	private JPanel pauseMenuPanel;	
	
    /**
     * Constructor that initializes the pause menu with game state information
     * @param frame The main game frame
     * @param layeredPanel The layered panel where the pause menu will be displayed
     */
    public PauseMenu(JFrame frame, JLayeredPane layeredPanel)
    {
        this.frame        = frame;
        this.layeredPanel = layeredPanel;
        
        this.initialize();
    }

    /**
     * Initializes and sets up the pause menu interface with fantasy-themed styling
     * Creates the pause menu panel, buttons, and pause trigger button
     */
    private void initialize() 
    {
        this.pauseMenuPanel = new JPanel() 
        {
            private static final long serialVersionUID = 1L;

			@Override
            protected void paintComponent(Graphics g) 
            {
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
            private void drawCornerDecorations(Graphics2D g2d, int x, int y, int width, int height) 
            {
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

        this.pauseMenuPanel.setSize(this.frame.getSize());
        this.pauseMenuPanel.setOpaque(false);
        this.pauseMenuPanel.setLayout(new GridBagLayout());
        this.pauseMenuPanel.setVisible(false);

        // Blocca i click fuori dai pulsanti
        this.pauseMenuPanel.addMouseListener(new MouseAdapter() 
        {
            @Override
            public void mouseClicked(MouseEvent e) 
            {
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

        this.resumeButton = createButton("Resume", "⚔");
        this.saveButton   = createButton("Save Game", "📜");
        this.exitButton   = createButton("Exit", "🚪");

        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(resumeButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        buttonPanel.add(saveButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        buttonPanel.add(exitButton);

        gbc.gridy++;
        this.pauseMenuPanel.add(titleLabel, gbc);
        gbc.gridy++;
        this.pauseMenuPanel.add(buttonPanel, gbc);

        this.createPauseButtonIcon();
        
        this.layeredPanel.add(this.pauseMenuPanel, Integer.valueOf(4));
        this.layeredPanel.add(this.menuIcon, Integer.valueOf(3));
    }
    
    /**
     * Makes the Pause menu panel visible to the user
     */
    public void show() 
    {
        if (this.pauseMenuPanel != null) 
        {
            this.pauseMenuPanel.setVisible(true);
            this.layeredPanel.repaint();
        }
    }
    
    /**
     * Makes the Pause menu panel invisible to the user
     */
    public void hide() 
    {
        if (this.pauseMenuPanel != null) 
        {
            this.pauseMenuPanel.setVisible(false);
            this.layeredPanel.repaint();
        }
    }
    
    /**
     * Adds an ActionListener to the resume button
     * @param listener The ActionListener to handle start button click events
     */
    public void addResumeListener(ActionListener listener) {
        this.resumeButton.addActionListener(listener);
    }
    
    /**
     * Adds an ActionListener to the save game button
     * @param listener The ActionListener to handle load button click events
     */
    public void addSaveListener(ActionListener listener) {
        this.saveButton.addActionListener(listener);
    }
    
    /**
     * Adds an ActionListener to the exit button
     * @param listener The ActionListener to handle exit button click events
     */
    public void addExitListener(ActionListener listener) {
        this.exitButton.addActionListener(listener);
    }
    
    /**
     * Adds an ActionListener to the pause icon button
     * @param listener The ActionListener to handle exit button click events
     */
    public void addPauseListener(ActionListener listener) {
        this.menuIcon.addActionListener(listener);
    }


    /**
     * Creates a button with icon and hover effects
     * @param text The text to display on the button
     * @param icon The icon/emoji to display alongside the text
     * @return A JButton with fantasy styling and interactive effects
     */
    private JButton createButton(String text, String icon)
    {
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
            public void mouseEntered(MouseEvent evt)
            {
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
    private void createPauseButtonIcon() 
    {
        this.menuIcon = new JButton(new ImageIcon(new ImageIcon("images/pauseGame.png")
                .getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
        
        this.menuIcon.setBounds(10, 10, 60, 60);
        this.menuIcon.setBorderPainted(false);
        this.menuIcon.setContentAreaFilled(false);
        this.menuIcon.setFocusPainted(false);
        this.menuIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}