package view.menu;

import java.awt.*;
import javax.swing.*;

/**
 * Abstract base class for all menu screens in the FiveRealms game.
 * Provides common functionality, styling, and layout management for consistent UI experience.
 */
public abstract class AbstractMenu extends JPanel{
    
    private static final long serialVersionUID = 1L;
    
	// === COMMON CONSTANTS ===
    protected static final int DEFAULT_WINDOW_WIDTH       = 900;
    protected static final int DEFAULT_WINDOW_HEIGHT      = 700;
    protected static final String DEFAULT_BACKGROUND_PATH = "images/Background/background4.jpg";
    
    // Color scheme constants
    protected static final Color BUTTON_BACKGROUND   = new Color(92, 51, 23);       
    protected static final Color BUTTON_TEXT         = new Color(245, 222, 179);   
    protected static final Color TITLE_TEXT          = new Color(210, 180, 140);
    protected static final Color SUBTITLE_TEXT       = Color.WHITE;
    protected static final Color TITLE_BACKGROUND    = new Color(0, 0, 0, 200);
    protected static final Color SUBTITLE_BACKGROUND = new Color(0, 0, 0, 150);
    
    // === COSTANTI FONT COMPLETAMENTE RINNOVATE ===
    protected static final Font BUTTON_FONT   = new Font("Georgia", Font.BOLD | Font.ITALIC, 18);  
    protected static final Font TITLE_FONT    = new Font("Serif", Font.BOLD, 42);
    protected static final Font SUBTITLE_FONT = new Font("Serif", Font.ITALIC, 16);
    
    // Dimension constants
    protected static final Dimension STANDARD_BUTTON_SIZE = new Dimension(250, 50);
    protected static final int STANDARD_BUTTON_SPACING    = 15;
    
    // === INSTANCE VARIABLES ===
    protected final JFrame frame;
    protected final JPanel mainPanel;
    protected final String windowTitle;
    
    /**
     * Constructor for Menu base class.
     * @param title The title for the window
     */
    protected AbstractMenu(String title) 
    {
        this.windowTitle = title;
        this.frame = createMainFrame();
        this.mainPanel = createMainPanel();
        initializeComponents();
        setupLayout();
        finalizeFrame();
    }
    
    /**
     * Creates and configures the main application frame.
     * @return Configured JFrame
     */
    private JFrame createMainFrame() 
    {
        return createMainFrame(DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
    }
    
    /**
     * Creates and configures the main application frame with custom dimensions.
     * @param width Window width
     * @param height Window height
     * @return Configured JFrame
     */
    private JFrame createMainFrame(int width, int height) 
    {
        JFrame newFrame = new JFrame(windowTitle);
        newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        newFrame.setSize(width, height);
        newFrame.setLocationRelativeTo(null);
        newFrame.setResizable(false);
        return newFrame;
    }
    
    /**
     * Creates the main panel with background image support.
     * @return JPanel with background rendering capability
     */
    protected JPanel createMainPanel() 
    {
        return new JPanel() 
        {
			private static final long serialVersionUID = 1L;
			
			private ImageIcon backgroundImage;
            
            {
                try 
                {
                    backgroundImage = new ImageIcon(getBackgroundImagePath());
                } 
                catch (Exception e)
                {
                    System.err.println("Warning: Could not load background image: " + getBackgroundImagePath());
                }
            }
            
            @Override
            protected void paintComponent(Graphics g) 
            {
                super.paintComponent(g);
                if (backgroundImage != null && backgroundImage.getIconWidth() > 0) 
                {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
                                       RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2d.drawImage(backgroundImage.getImage(), 0, 0, 
                                getWidth(), getHeight(), this);
                    g2d.dispose();
                } 
                else 
                {
                    // Fallback gradient background
                    Graphics2D g2d = (Graphics2D) g.create();
                    GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(139, 69, 19),
                        0, getHeight(), new Color(160, 82, 45)
                    );
                    g2d.setPaint(gradient);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.dispose();
                }
            }
        };
    }
    
    /**
     * Creates a styled button with consistent medieval theme.
     * @param text The text to display on the button
     * @return Styled JButton
     */
    protected JButton createStyledButton(String text) 
    {
        return createStyledButton(text, STANDARD_BUTTON_SIZE);
    }
    
    /**
     * Creates a styled button with custom size.
     * @param text The text to display on the button
     * @param size The preferred size for the button
     * @return Styled JButton
     */
    /**
     * Creates a styled button with custom size.
     * @param text The text to display on the button
     * @param size The preferred size for the button
     * @return Styled JButton
     */
    protected JButton createStyledButton(String text, Dimension size) 
    {
        JButton button = new JButton(text); 
        button.setFont(BUTTON_FONT);
        button.setForeground(BUTTON_TEXT);
        button.setBackground(BUTTON_BACKGROUND);
        button.setPreferredSize(size);
        button.setMaximumSize(size);
        button.setMinimumSize(size);
        button.setFocusPainted(false);
        
        // Enhanced border with medieval look
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 165, 32), 1), 
                BorderFactory.createRaisedBevelBorder()
            ),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        
        // Add hover effect
        addHoverEffect(button);        
        return button;
    }
    
    /**
     * Adds hover effect to a button.
     * @param button The button to add hover effect to
     */
    protected void addHoverEffect(JButton button) 
    {
        Color originalBackground = button.getBackground();
        Color hoverBackground = new Color(139, 90, 43);   // Marrone più chiaro per hover
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(hoverBackground);
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(originalBackground);
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }
    
    /**
     * Creates a styled title label.
     * @param title The title text
     * @return Styled JLabel for title
     */
    protected JLabel createTitleLabel(String title) 
    {
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TITLE_TEXT);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(TITLE_BACKGROUND);
        titleLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BUTTON_BACKGROUND, 2),
            BorderFactory.createEmptyBorder(15, 25, 15, 25)
        ));
        return titleLabel;
    }
    
    /**
     * Creates a styled subtitle label.
     * @param subtitle The subtitle text
     * @return Styled JLabel for subtitle
     */
    protected JLabel createSubtitleLabel(String subtitle) 
    {
        JLabel subtitleLabel = new JLabel(subtitle, SwingConstants.CENTER);
        subtitleLabel.setFont(SUBTITLE_FONT);
        subtitleLabel.setForeground(SUBTITLE_TEXT);
        subtitleLabel.setOpaque(true);
        subtitleLabel.setBackground(SUBTITLE_BACKGROUND);
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return subtitleLabel;
    }
    
    /**
     * Creates a panel for buttons with proper alignment.
     * @param buttons The buttons to add to the panel
     * @return JPanel containing the buttons
     */
    protected JPanel createButtonPanel(JButton... buttons) 
    {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);
        
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            buttonPanel.add(buttons[i]);
            
            // Add spacing between buttons (except after the last one)
            if (i < buttons.length - 1) {
                buttonPanel.add(Box.createVerticalStrut(STANDARD_BUTTON_SPACING));
            }
        }
        
        return buttonPanel;
    }
    
    /**
     * Finalizes the frame setup.
     */
    private void finalizeFrame() 
    {
        this.frame.add(this.mainPanel);
        this.frame.revalidate();
        this.frame.repaint();
    }
    
    // === ABSTRACT METHODS ===
    
    /**
     * Initializes menu-specific components. Must be implemented by subclasses.
     */
    protected abstract void initializeComponents();
    
    /**
     * Sets up the layout for the specific menu. Must be implemented by subclasses.
     */
    protected abstract void setupLayout();
    
    // === TEMPLATE METHODS (can be overridden) ===
    
    /**
     * Gets the background image path. Can be overridden by subclasses.
     * @return Path to the background image
     */
    protected String getBackgroundImagePath() 
    {
        return DEFAULT_BACKGROUND_PATH;
    }
    
    // === PUBLIC INTERFACE ===
    
    /**
     * Makes the menu visible to the user.
     */
    public void show() 
    {
    	this.frame.setVisible(true);
    }
    
    /**
     * Closes and disposes of the menu frame.
     */
    public void close() 
    {
    	this.frame.dispose();
    }
    
    /**
     * Gets the main frame (useful for modal dialogs).
     * @return The main JFrame
     */
    public JFrame getFrame() 
    {
        return this.frame;
    }
}