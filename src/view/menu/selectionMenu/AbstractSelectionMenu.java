package view.menu.selectionMenu;

import javax.swing.*;
import view.menu.Menu;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for character selection menus.
 * Provides common functionality for selecting characters with fantasy-themed UI styling.
 */
public abstract class AbstractSelectionMenu implements Menu 
{
    // UI Constants for consistent styling
    protected static final Font TITLE_FONT = new Font("Serif", Font.BOLD, 32);
    protected static final Color TITLE_TEXT = new Color(210, 180, 140);
    protected static final Color TITLE_BACKGROUND = new Color(0, 0, 0, 200);
    protected static final Color BUTTON_BACKGROUND = new Color(92, 51, 23);
    
    protected JFrame frame;
    protected JButton nextButton;
    protected final List<JPanel> selectedPanels     = new ArrayList<>();
    protected final List<String> selectedCharacters = new ArrayList<>();
    protected int maxSelectableCharacters;
    
    
    /**
     * Makes the menu visible to the user by displaying its frame.
     */
    @Override
    public void show() 
    {
        this.initializeFrame();
        this.setupBackground();
        this.frame.setVisible(true);
    }

    /**
     * Closes the menu by disposing its frame, releasing any resources associated with it.
     */
    @Override
    public void close() 
    {
        if (this.frame != null) 
        {
            this.frame.dispose();
        }
    }

    /**
     * Returns the root JPanel of this menu.
     *
     * @return the JPanel representing this menu
     */
    @Override
    public JPanel getPanel() {
        return this.frame != null ? (JPanel) this.frame.getContentPane() : null;
    }
    
        
    /**
     * Initialize the main frame with common properties.
     */
    private void initializeFrame() {
    	this.frame = new JFrame("FiveRealms - " + getMenuType() + " Menu");
    	this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	this.frame.setSize(1080, 720);
    	this.frame.setLocationRelativeTo(null);
    	this.frame.setResizable(false);
    }
    
    /**
     * Setup the background and all UI components.
     */
    private void setupBackground() 
    {
        // Random background
        String bgPath    = "images/background/background" + (int)(Math.random() * 6) + ".jpg";
        ImageIcon bgIcon = new ImageIcon(new ImageIcon(bgPath).getImage().getScaledInstance(1080, 720, Image.SCALE_SMOOTH));
        JLabel bgLabel   = new JLabel(bgIcon);
        
        bgLabel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title
        this.setupTitle(bgLabel, gbc);
        
        // Next button
        this.setupNextButton(bgLabel, gbc);
        
        // Characters
        this.setupCharacters(bgLabel, gbc);

        this.frame.add(bgLabel);
    }
    
    /**
     * Setup the title label with fantasy styling.
     */
    private void setupTitle(JLabel bgLabel, GridBagConstraints gbc) {
        JLabel titleLabel;
        
        if (requiresSpecialTitleStyling()) {
            // Use the new styled method for special titles
            titleLabel = createTitleLabel(getTitleText());
        } else {
            // Simple title without background styling
            titleLabel = new JLabel(getTitleText(), SwingConstants.CENTER);
            titleLabel.setFont(TITLE_FONT);
            titleLabel.setForeground(TITLE_TEXT);
        }
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        bgLabel.add(titleLabel, gbc);
    }
    
    /**
     * Creates a styled title label with fantasy theme.
     * 
     * @param title The title text to display
     * @return A JLabel with fantasy styling applied
     */
    protected JLabel createTitleLabel(String title) {
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TITLE_TEXT);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(TITLE_BACKGROUND);
        titleLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BUTTON_BACKGROUND, 2),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));
        return titleLabel;
    }
    
    /**
     * Setup the next button with fantasy styling.
     */
    private void setupNextButton(JLabel bgLabel, GridBagConstraints gbc) 
    {
    	this.nextButton = createFantasyButton("NEXT");
    	this.nextButton.setEnabled(false);
        gbc.gridx = 1;
        gbc.gridy = 3;
        bgLabel.add(this.nextButton, gbc);
    }
    
    /**
     * Setup all character panels.
     */
    private void setupCharacters(JLabel bgLabel, GridBagConstraints gbc) 
    {
    	this.addCharacter(bgLabel, "Archer", "A skilled archer, master of the bow.", 
                   "images/characters/archer/archerHero.png", 0, 1, gbc);
    	this.addCharacter(bgLabel, "Barbarian", "A fierce warrior with immense strength.", 
                   "images/characters/barbarian/barbarianHero.png", 2, 1, gbc);
    	this.addCharacter(bgLabel, "Juggernaut", "A massive tank, unstoppable force.", 
                   "images/characters/juggernaut/juggernautHero.png", 1, 2, gbc);
    	this.addCharacter(bgLabel, "Knight", "A noble knight, brave and strong.", 
                   "images/characters/knight/knightHero.png", 0, 3, gbc);
    	this.addCharacter(bgLabel, "Wizard", "A master of the arcane arts.", 
                   "images/characters/wizard/wizardHero.png", 2, 3, gbc);
    }

    /**
     * Creates a styled fantasy-themed button with hover effects.
     * 
     * @param text The text to display on the button
     * @return A JButton with fantasy styling applied
     */
    protected JButton createFantasyButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Serif", Font.BOLD, 18));
        button.setForeground(new Color(245, 222, 179)); // Beige/oro chiaro
        button.setBackground(new Color(101, 67, 33)); // Marrone scuro
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(250, 60));

        button.addMouseListener(new MouseAdapter() {
            private Color originalBg = new Color(101, 67, 33);
            private Color hoverBg = new Color(139, 117, 82);
            
            public void mouseEntered(MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(hoverBg);
                    button.setForeground(new Color(255, 248, 220));
                }
            }
            
            public void mouseExited(MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(originalBg);
                    button.setForeground(new Color(245, 222, 179));
                } else {
                    button.setBackground(new Color(60, 40, 20));
                    button.setForeground(new Color(120, 120, 120));
                }
            }
        });

        // Gestione stato disabled
        button.addPropertyChangeListener("enabled", evt -> {
            if (!button.isEnabled()) {
                button.setBackground(new Color(60, 40, 20));
                button.setForeground(new Color(120, 120, 120));
            } else {
                button.setBackground(new Color(101, 67, 33));
                button.setForeground(new Color(245, 222, 179));
            }
        });

        return button;
    }

    /**
     * Adds a selectable character panel to the background label at specified grid position.
     * 
     * @param bgLabel The background label container to add the character panel to
     * @param name The character's name to display
     * @param desc The character's description text
     * @param imgPath The file path to the character's image
     * @param x The grid x position for layout placement
     * @param y The grid y position for layout placement
     * @param gbc The GridBagConstraints object for layout configuration
     */
    protected void addCharacter(JLabel bgLabel, String name, String desc, String imgPath, int x, int y, GridBagConstraints gbc) 
    {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setOpaque(true);
		panel.setBackground(new Color(20, 20, 20, 220));
		panel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
		
		JLabel imgLabel = new JLabel(new ImageIcon(new ImageIcon(imgPath).getImage()
		.getScaledInstance(120, 120, Image.SCALE_SMOOTH)), SwingConstants.CENTER);
		
		// Testo del personaggio con stile fantasy
		JLabel lbl = new JLabel("<html><center>" + name + "<br>" + desc + "</center></html>", 
		             SwingConstants.CENTER);
		lbl.setFont(new Font("Serif", Font.BOLD, 14));
		lbl.setForeground(new Color(245, 222, 179)); // Beige/oro chiaro
		
		panel.add(imgLabel, BorderLayout.NORTH);
		panel.add(lbl, BorderLayout.SOUTH);
		
		// Character selection logic
		panel.addMouseListener(new MouseAdapter() 
		{
			public void mouseClicked(MouseEvent e) 
			{		
				if (selectedPanels.contains(panel)) 
				{
				  selectedPanels.remove(panel);
				  selectedCharacters.remove(name);
				  panel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
				} 
				else if (selectedPanels.size() < maxSelectableCharacters) 
				{
				  selectedPanels.add(panel);
				  selectedCharacters.add(name);
				  panel.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 3)); // glow selezione
				}
				nextButton.setEnabled(selectedPanels.size() == maxSelectableCharacters);
			}
		
		public void mouseEntered(MouseEvent e) {
		if (!selectedPanels.contains(panel)) {
		  panel.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
		}
		}
		
		public void mouseExited(MouseEvent e) {
		if (!selectedPanels.contains(panel)) {
		  panel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
		}
		}
		});
		
		gbc.gridx = x;
		gbc.gridy = y;
		bgLabel.add(panel, gbc);
	}

    /**
     * Adds an ActionListener to the next button for handling click events.
     * 
     * @param listener The ActionListener to be added to the next button
     */
    public void addNextButtonListener(ActionListener listener) 
    {
    	this.nextButton.addActionListener(listener);
    }

    /**
     * Returns a copy of the list containing the names of currently selected characters.
     * 
     * @return ArrayList containing the names of selected characters
     */
    public List<String> getSelectedCharacterNames() 
    {
        return new ArrayList<>(this.selectedCharacters);
    }

    
    // Abstract methods that subclasses must implement
    
    /**
     * Returns the type of menu for display purposes.
     * 
     * @return String representing the menu type (e.g., "Characters Selection", "Characters Replace")
     */
    protected abstract String getMenuType();
    
    /**
     * Returns the title text to be displayed at the top of the menu.
     * 
     * @return String containing the title text
     */
    protected abstract String getTitleText();
    
    /**
     * Determines if the title requires special styling (background, border).
     * 
     * @return true if special styling should be applied to the title
     */
    protected abstract boolean requiresSpecialTitleStyling();
}