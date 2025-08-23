package view.map;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import javax.swing.*;
import controller.GameController;
import model.characters.Character;
import model.point.Point;
import view.menu.PauseMenu;

/**
 * Abstract class representing a map in the game.
 * It includes a grid of buttons and a control panel with game management options.
 * This class manages the visual representation of the game world, including character
 * positioning, movement, and various UI elements like banners and tooltips.
 * 
 */
public abstract class AbstractMap implements view.map.Map {

	/** Number of rows in the grid */
	public static final int GRID_SIZE_HEIGHT = 15;

	/** Number of columns in the grid */
	public static final int GRID_SIZE_WIDTH = 20;

	protected JFrame frame;
	protected GridPanel gridPanel;
	private JLayeredPane layeredPanel;

	/** Game banner for displaying messages */
	private BannerPanel banner;

	/** List of enemies in the current level */
	private List<Character> enemiesList;

	/** List of allies in the current level */
	private List<Character> alliesList;

	/** Current level number */
	private final int numLevel;

	/** Map linking characters to their positions */
	private Map<Character, Point> characterMap;

	/** List of positions available for ally spawning */
	private List<Point> alliesPositionList;

	/** List of positions available for enemy spawning */
	private List<Point> enemiesPositionList;

	/** Manager for character tooltips */
	CharacterTooltipManager tooltipManager;

	/** Random number generator for position selection */
	private Random random;

	/** Label displaying the current level */
	private JLabel levelLabel;

	/** Label displaying the number of remaining enemies */
	private JLabel enemiesLabel;

	/** Pause menu for the game */
	private PauseMenu pauseMenu;

	/** Game controller reference */
	private GameController controller;

	/**
	 * Constructor for AbstractMap.
	 * Initializes the map with the given parameters and sets up initial data structures.
	 * 
	 * @param enemiesList List of enemies in the current level
	 * @param alliesList List of allies in the current level
	 * @param numLevel Current level number
	 * @param controller The game controller managing this map
	 */
	public AbstractMap(List<Character> enemiesList, List<Character> alliesList, int numLevel, GameController controller) {
		this.enemiesList = enemiesList;
		this.alliesList = alliesList;
		this.numLevel = numLevel;

		this.characterMap = new HashMap<>();
		this.alliesPositionList = new ArrayList<>();
		this.enemiesPositionList = new ArrayList<>();
		this.random = new Random();
		this.initializePositionList();

		this.tooltipManager = new CharacterTooltipManager();
		this.controller = controller;
		
		// TEMP
		this.initializeFrame();
	}

	/**
	 * Displays the map by initializing the frame and making it visible.
	 * Sets up the pause menu for non-tutorial levels.
	 */
	@Override
	public void show() {
		// Initialize the level frame
		

		// If this is not the tutorial level, initialize the PauseMenu
		if (this.numLevel != 0) {
			this.pauseMenu = new PauseMenu(this.getFrame(), this.getLayeredPanel());
			this.controller.setPauseMenu(this.pauseMenu);
		}

		// Display the frame
		this.frame.setVisible(true);
	}

	/**
	 * Closes the map by disposing of the frame and removing all event listeners.
	 * Ensures proper cleanup of resources.
	 */
	@Override
	public void close() {
		// If the frame exists, dispose of it and remove all attached events
		if (this.frame != null) {
			this.removeAllEvent();
			this.frame.dispose();
		}
	}

	/**
	 * Initializes the main frame and layout.
	 * Sets up the window properties, size, and all UI components.
	 */
	private void initializeFrame() {
	    if (this.numLevel != 0) {
	        this.frame = new JFrame("Five Realms - Level " + this.numLevel);
	    } else {
	        this.frame = new JFrame("Five Realms - Tutorial ");
	    }

	    // When the window is closed, the program terminates
	    this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    this.frame.setResizable(false);

	    int width = 1050;  
	    int height = 680;  
	    
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    if (width > screenSize.width) {
	        width = (int)(screenSize.width * 0.95); 
	    }
	    if (height > screenSize.height - 50) { 
	        height = (int)(screenSize.height * 0.9); 
	    }
	    
	    this.frame.setSize(width, height);

	    // Center the window on screen when the game starts
	    this.frame.setLocationRelativeTo(null);

	    this.frame.setLayout(new BorderLayout());

	    this.layeredPanel = new JLayeredPane();
	    this.frame.setContentPane(this.layeredPanel);

	    this.layeredPanel.setSize(width - 16, height - 39);

	    // Layer 0: Background image
	    this.initializeBackgroundMap();

	    // Layer 1: Button grid
	    this.initializeButtonGrid();

	    // Layer 2: Game banner
	    this.initializeBanner();

	    // Layer 3: Game information labels
	    this.addGameInfoLabels();
	}

	/**
	 * Initializes the background map for the current level.
	 * Loads and scales the background image appropriate for the level.
	 */
	private void initializeBackgroundMap() {
		// Get the image associated with the current level
		String backgroundFile = "images/background/background" + numLevel + ".jpg";

		// Load the image from file
		ImageIcon backgroundImage = new ImageIcon(backgroundFile);

		// Check that the image is valid
		if (backgroundImage.getIconWidth() > 0 && backgroundImage.getIconHeight() > 0) {
			// Resize the image to fit the frame
			Dimension panelSize = this.layeredPanel.getSize();
			int panelWidth = panelSize.width;
			int panelHeight = panelSize.height;

			Image image = backgroundImage.getImage();
			Image resizedImage = image.getScaledInstance(panelWidth, panelHeight, Image.SCALE_SMOOTH);
			backgroundImage = new ImageIcon(resizedImage);

			JLabel backgroundLabel = new JLabel(backgroundImage);
			backgroundLabel.setBounds(0, 0, panelWidth, panelHeight);

			// Add the background to the bottom layer 0
			this.layeredPanel.add(backgroundLabel, Integer.valueOf(0)); 

			// Make the layout visible after adding the component
			this.layeredPanel.revalidate();
			this.layeredPanel.repaint();
		} else {
			System.err.println("Error: background image not found for level " + numLevel);
		}
	}

	/**
	 * Initializes the button grid for the current level.
	 * Creates a transparent grid overlay on top of the background.
	 */
	private void initializeButtonGrid() {
		// Create and initialize GridPanel if it doesn't already exist
		if (this.gridPanel == null) {
			this.gridPanel = new GridPanel(this.layeredPanel);
		}      

		// Set gridPanel dimensions equal to those of the frame
		this.gridPanel.setBounds(0, 0, this.layeredPanel.getWidth(), this.layeredPanel.getHeight());

		// Add gridPanel to JLayeredPane on the upper layer (layer 1)
		this.layeredPanel.add(this.gridPanel, Integer.valueOf(1));

		// Make the grid panel transparent
		this.gridPanel.setOpaque(false); 

		// Make the layout visible after adding the component
		this.layeredPanel.revalidate();
		this.layeredPanel.repaint();
	}

	/**
	 * Initializes the game banner for displaying messages.
	 * Sets up the banner component on layer 2.
	 */
	private void initializeBanner() {
		this.banner = new BannerPanel(this.getWidth(), this.getHeight());
		this.layeredPanel.add(this.banner, Integer.valueOf(2));
		this.layeredPanel.revalidate();
		this.layeredPanel.repaint();
	}

	/**
	 * Adds game information labels to the UI.
	 * Creates and positions labels for level information and enemy count.
	 */
	private void addGameInfoLabels() {
		int paddingRight = 10;
		int paddingTop = 10;
		int labelWidth = 220;
		int labelHeight = 30;

		if (this.numLevel == 0) {
			this.levelLabel = createStyledLabel("🎯 Tutorial");
		} else {
			this.levelLabel = createStyledLabel("🎯 Level: " + this.numLevel);
		}
		this.enemiesLabel = createStyledLabel("⚔️ Enemies Remaining: " + this.enemiesList.size());

		// Position on the right, so x = panel width - label width - paddingRight
		int xPos = layeredPanel.getWidth() - labelWidth - paddingRight;

		this.levelLabel.setBounds(100, paddingTop, labelWidth, labelHeight);
		this.enemiesLabel.setBounds(xPos - 60, paddingTop, labelWidth, labelHeight);

		this.layeredPanel.add(levelLabel, Integer.valueOf(4));
		this.layeredPanel.add(enemiesLabel, Integer.valueOf(4));
	}

	/**
	 * Creates a styled label with medieval/fantasy theme.
	 * 
	 * @param text The text to display on the label
	 * @return A JLabel with custom styling applied
	 */
	private JLabel createStyledLabel(String text) {
		JLabel label = new JLabel(text);

		// Medieval/fantasy font
		label.setFont(new Font("Serif", Font.BOLD, 16));
		label.setForeground(new Color(245, 230, 200)); // Light beige

		// Brown/orange background like the buttons
		label.setBackground(new Color(139, 69, 19)); // Dark brown
		label.setOpaque(true);

		// Border that simulates button borders
		label.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createRaisedBevelBorder(), // Relief effect
				BorderFactory.createCompoundBorder(
						BorderFactory.createLineBorder(new Color(101, 67, 33), 2), // Dark brown border
						BorderFactory.createEmptyBorder(4, 8, 4, 8) // Internal padding
						)
				));

		label.setHorizontalAlignment(SwingConstants.CENTER);

		return label;
	}

	/**
	 * Updates the information labels with current game state.
	 * Refreshes level and enemy count displays.
	 */
	private void updateInfoLabels() {
		if (this.levelLabel != null) {
			this.levelLabel.setText("🎯 Level: " + this.numLevel);
		}

		if (this.enemiesLabel != null) {
			this.enemiesLabel.setText("⚔️ Enemies Remaining: " + this.enemiesList.size());
		}
	}

	/**
	 * Gets the width of the layered panel.
	 * 
	 * @return The width of the layered panel
	 */
	public int getWidth() {
		return this.layeredPanel.getWidth();
	}

	/**
	 * Gets the height of the layered panel.
	 * 
	 * @return The height of the layered panel
	 */
	public int getHeight() {
		return this.layeredPanel.getHeight();
	}

	/**
	 * Updates the banner message display.
	 * 
	 * @param msg The message to display
	 * @param fullScreen Whether to display in full screen mode
	 */
	public void updateBannerMessage(String msg, boolean fullScreen) {
		if (this.banner != null) {
			if (fullScreen) {
				this.banner.showFullScreenMessage();
			} else {
				this.banner.showMessage(msg);
			}
		}
	}

	/**
	 * Colors the specified positions with the given color to show available moves.
	 * 
	 * @param availableMoves List of points representing available move positions
	 * @param colour The color to use for highlighting
	 */
	public void colourPositionAvailable(List<Point> availableMoves, Color colour) {
		if (availableMoves == null || availableMoves.isEmpty()) {
			System.err.println("The list of available positions is empty or null.");
			return;
		}

		JButton[][] buttonGrid = this.gridPanel.getGridButtons();

		int rows = buttonGrid.length;
		int cols = buttonGrid[0].length;

		for (Point p : availableMoves) {
			int y = p.getX();
			int x = p.getY();

			// Bounds checking to avoid exceptions
			if (y >= 0 && y < rows && x >= 0 && x < cols) {
				JButton button = buttonGrid[y][x];

				// To make transparency visible, draw on a non-opaque component
				button.setBackground(colour);
				button.setOpaque(true);
				button.setContentAreaFilled(true);
				button.setBorderPainted(false);
				button.setFocusPainted(false);
				button.setRolloverEnabled(false);
			} 
		}

		// Refresh the grid to show changes
		this.gridPanel.revalidate();
		this.gridPanel.repaint();
	}

	/**
	 * Colors the position of a specific character.
	 * 
	 * @param character The character whose position should be colored
	 */
	public void colourCharacterPosition(Character character) {
		if (character == null || character.getPosition() == null) {
			System.err.println("The character or its position is null.");
			return;
		}

		JButton[][] buttonGrid = this.gridPanel.getGridButtons();

		int rows = buttonGrid.length;
		int cols = buttonGrid[0].length;

		Point position = character.getPosition();
		int y = position.getX();
		int x = position.getY();

		// Bounds checking
		if (y >= 0 && y < rows && x >= 0 && x < cols) {
			JButton button = buttonGrid[y][x];

			button.setBackground(new Color(0, 180, 0, 160)); // Green
			button.setOpaque(true);
			button.setContentAreaFilled(true);
			button.setBorderPainted(false);
			button.setFocusPainted(false);
			button.setRolloverEnabled(false);
		}

		this.gridPanel.revalidate();
		this.gridPanel.repaint();
	}

	/**
	 * Resets all grid button colors to their default transparent state.
	 */
	public void resetGridColors() {
		JButton[][] buttonGrid = this.gridPanel.getGridButtons();

		for (int y = 0; y < buttonGrid.length; y++) {
			for (int x = 0; x < buttonGrid[y].length; x++) {
				JButton button = buttonGrid[y][x];
				button.setOpaque(false);         
			}
		}

		this.gridPanel.revalidate();
		this.gridPanel.repaint();
	}

	/**
	 * Returns the button at the specified coordinates in the grid.
	 * 
	 * @param x The x coordinate
	 * @param y The y coordinate
	 * @return The JButton at the specified position, or null if coordinates are invalid
	 */
	public JButton getButtonAt(int x, int y) {
		if (x >= 0 && x < GRID_SIZE_WIDTH && y >= 0 && y < GRID_SIZE_HEIGHT) {
			return gridPanel.getGridButtons()[x][y];
		}
		return null;
	}

	/**
	 * Spawns characters on the map at available positions.
	 * 
	 * @param spawnList List of characters to spawn
	 */
	public void spawnCharacter(List<Character> spawnList) {		
		if (spawnList == null || spawnList.isEmpty()) {
			System.err.println("Spawn list is null or empty.");
			return;
		}

		List<Point> positionList = spawnList.get(0).isAllied() ? this.alliesPositionList : this.enemiesPositionList;

		if (spawnList.size() > positionList.size()) {
			System.err.println("Not enough spawn positions for characters.");
			return;
		}

		for (int i = 0; i < spawnList.size(); i++) {
			this.spawnCharacterHelper(spawnList.get(i), positionList);
		}
	}

	/**
	 * Helper method to spawn a single character at an available position.
	 * 
	 * @param character The character to spawn
	 * @param positionList List of available positions for spawning
	 */
	private void spawnCharacterHelper(Character character, List<Point> positionList) {
		Point chosenPosition = character.getPosition();

		if (chosenPosition == null) {
			// If position is not already set, choose a random one from the list
			int target = random.nextInt(positionList.size());
			chosenPosition = positionList.remove(target);
			character.setPosition(chosenPosition);
		} else {
			// If position is already set, remove it from the list only if present
			if (positionList.contains(chosenPosition)) {
				positionList.remove(chosenPosition);
			}
		}

		// Update characterMap
		this.characterMap.put(character, chosenPosition);

		JButton button = this.gridPanel.getGridButtons()[chosenPosition.getX()][chosenPosition.getY()];
		button.setIcon(new ImageIcon(character.getImage()));
		button.setContentAreaFilled(false);

		// Add tooltip to the character
		this.tooltipManager.showCharacterTooltip(character, button);
	}

	/**
	 * Initializes the lists of available spawn positions for allies and enemies.
	 */
	private void initializePositionList() {
		this.alliesPositionList.add(new Point(16, 4));
		this.alliesPositionList.add(new Point(14, 7));
		this.alliesPositionList.add(new Point(16, 10));

		this.enemiesPositionList.add(new Point(4, 10));
		this.enemiesPositionList.add(new Point(4, 4));
		this.enemiesPositionList.add(new Point(6, 7));
		this.enemiesPositionList.add(new Point(6, 4));
		this.enemiesPositionList.add(new Point(3, 7));
		this.enemiesPositionList.add(new Point(2, 12));
		this.enemiesPositionList.add(new Point(5, 10));
	}

	/**
	 * Moves a character from its current position to a target position.
	 * Updates both the visual representation and internal data structures.
	 * 
	 * @param character The character to move
	 * @param target The target position to move to
	 */
	public void moveCharacter(Character character, Point target) {
		if (target == null) {
			throw new IllegalArgumentException("Target point must not be null");
		}

		if (!this.characterMap.containsKey(character)) {
			System.err.println("Character not found in the map: " + character.getClass().getSimpleName());
			return;
		}	    

		if (character.getPosition().equals(target)) {
			return;
		}

		if (isPositionOccupied(target)) {
			System.err.println("The position " + target + " is already occupied by another character.");
			return;
		}

		// Update the map with the new position
		this.removeCharacter(character);
		this.characterMap.put(character, target);

		// Set the image in the new button
		JButton targetButton = this.gridPanel.getGridButtons()[target.getX()][target.getY()];
		targetButton.setIcon(new ImageIcon(character.getImage()));

		this.updateToolTip();	    

	}

	/**
	 * Removes a character from the map.
	 * Clears the visual representation and updates internal data structures.
	 * 
	 * @param character The character to remove
	 */
	public void removeCharacter(Character character) {
		Point target = character.getPosition();

		if (character == null || target == null) {
			throw new IllegalArgumentException("Character and/or target point must not be null");
		}

		if (!this.characterMap.containsKey(character)) {
			System.err.println("Character not found in the map: " + character.getClass().getSimpleName());
			return;
		}	    

		// Remove character from map
		this.characterMap.remove(character);

		// Remove image from button
		JButton targetButton = this.gridPanel.getGridButtons()[target.getX()][target.getY()];
		targetButton.setIcon(null);

		this.tooltipManager.removeCharacterTooltip(targetButton);   

		this.updateToolTip();	    

		this.updateInfoLabels();
	}

	/**
	 * Updates all character tooltips.
	 * Removes and recreates all tooltips to ensure consistency.
	 */
	public void updateToolTip() {
		// For constant tooltip updates
		for (Entry<Character, Point> entry : this.characterMap.entrySet()) {
			this.tooltipManager.removeCharacterTooltip(this.gridPanel.getGridButtons()[entry.getValue().getX()][entry.getValue().getY()]);
		}

		for (Entry<Character, Point> entry : this.characterMap.entrySet()) {
			this.tooltipManager.showCharacterTooltip(entry.getKey(), this.gridPanel.getGridButtons()[entry.getValue().getX()][entry.getValue().getY()]);
		}
	}

	/**
	 * Removes all event listeners from grid buttons.
	 * Used for cleanup when closing the map.
	 */
	public void removeAllEvent() {
		JButton[][] buttonGrid = this.gridPanel.getGridButtons();

		for (JButton[] row : buttonGrid) {
			for (JButton button : row) {
				if (button == null) {
					continue; // Skip any empty cells
				}

				// Remove ActionListeners if present
				ActionListener[] als = button.getActionListeners();
				if (als != null && als.length > 0) {
					for (ActionListener al : als) {
						button.removeActionListener(al);
					}
				}

				// Remove MouseListeners if present
				MouseListener[] mls = button.getMouseListeners();
				if (mls != null && mls.length > 0) {
					for (MouseListener ml : mls) {
						button.removeMouseListener(ml);
					}
				}

				// Remove MouseMotionListeners if present
				MouseMotionListener[] mmls = button.getMouseMotionListeners();
				if (mmls != null && mmls.length > 0) {
					for (MouseMotionListener mml : mmls) {
						button.removeMouseMotionListener(mml);
					}
				}

				// Remove tooltip only if it exists
				if (button.getToolTipText() != null) {
					this.tooltipManager.removeCharacterTooltip(button);
				}
			}
		}

		this.tooltipManager.removeAllTooltips(); // Clean up all managed tooltips
	}

	/**
	 * Gets the list of enemies on this map.
	 * 
	 * @return The list of enemies
	 */
	public List<Character> getEnemiesList() {
		return enemiesList;
	}

	/**
	 * Gets the list of allies on this map.
	 * 
	 * @return The list of allies
	 */
	public List<Character> getAlliesList() {
		return alliesList;
	}

	/**
	 * Gets the grid of buttons representing the map.
	 * 
	 * @return 2D array of JButtons representing the grid
	 */
	public JButton[][] getGridButtons() {
		return this.gridPanel.getGridButtons();
	}

	/**
	 * Gets the main frame of the map.
	 * 
	 * @return The JFrame containing the map
	 */
	public JFrame getFrame() {
		return this.frame;
	}

	/**
	 * Gets the layered panel managing the UI layers.
	 * 
	 * @return The JLayeredPane managing UI components
	 */
	public JLayeredPane getLayeredPanel() {
		return this.layeredPanel;
	}

	/**
	 * Sets the list of enemies on this map.
	 * 
	 * @param enemiesList The new list of enemies
	 */
	public void setEnemiesList(List<Character> enemiesList) {
		this.enemiesList = enemiesList;
	}

	/**
	 * Sets the list of allies on this map.
	 * 
	 * @param alliesList The new list of allies
	 */
	public void setAlliesList(List<Character> alliesList) {
		this.alliesList = alliesList;
	}

	/**
	 * Checks if a position is already occupied by a character.
	 * 
	 * @param point The point to check for occupation
	 * @return true if the position is occupied, false otherwise
	 */
	public boolean isPositionOccupied(Point point) {
		if (point == null) return false;

		return this.alliesList.stream()
				.filter(ally -> ally != null && ally.getPosition() != null)
				.anyMatch(ally -> ally.getPosition().equals(point)) 
				|| 
				this.enemiesList.stream()
				.filter(enemy -> enemy != null && enemy.getPosition() != null)
				.anyMatch(enemy -> enemy.getPosition().equals(point));
	}
}