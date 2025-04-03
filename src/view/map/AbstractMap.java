package view.map;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import model.characters.Character;
import model.gameStatus.GameState;
import model.gameStatus.GameStateManager;

/**
 * Abstract class representing a map in the game.
 * It includes a grid of buttons and a control panel with game management options.
 */
public abstract class AbstractMap implements Map 
{
    
	/**
     * Attributes
     */
    public static final int GRID_SIZE = 10; // Size of the grid
    public static final int BUTTON_SIZE = 120; // Button dimensions

    protected JFrame frame;
    protected JPanel gridPanel;
    protected JPanel controlPanel; // Panel for control buttons
    protected JButton hiddenMenuButton; // Il pulsante per aprire/chiudere il menu

    protected JButton[][] gridButtons = new JButton[GRID_SIZE][GRID_SIZE];
    private GameStateManager gameStateManager;

    private List<Character> enemiesList; // List of enemies
    private List<Character> alliesList;  // List of allies

    /**
     * Constructor
     * @param enemiesList List of enemy characters in the current level.
     * @param alliesList  List of allied characters.
     */
    public AbstractMap(List<Character> enemiesList, List<Character> alliesList) {
        this.enemiesList = enemiesList;
        this.alliesList = alliesList;
        this.gameStateManager = new GameStateManager();

        initializeFrame();
        initializeGrid();
        initializeControlPanel(); // Initializes the button panel
        
        frame.setVisible(true);
    }
    
    /**
     * Initializes the main frame and layout.
     */
    private void initializeFrame() 
    {
        frame = new JFrame("Saga dei 5 Regni");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800); // Increased width to accommodate control panel
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout()); // Main panel to organize layout
        frame.add(mainPanel, BorderLayout.CENTER);

        gridPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE));
        mainPanel.add(gridPanel, BorderLayout.WEST); // Grid on the left

        controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS)); // Vertical buttons
        mainPanel.add(controlPanel, BorderLayout.EAST); // Buttons on the right
       
    }
    
    
    /**
     * Initializes the grid panel with buttons.
     */
    private void initializeGrid() {
        gridPanel.removeAll();
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                final int row = i;
                final int col = j;
                gridButtons[i][j] = new JButton();
                gridButtons[i][j].setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
                gridButtons[i][j].addActionListener(e -> showButtonCoordinates(row, col));
                gridPanel.add(gridButtons[i][j]);
            }
        }
        gridPanel.repaint();
        gridPanel.revalidate();
    }

    /**
     * Initializes the control panel with buttons for game actions.
     */
    private void initializeControlPanel() {
        JButton saveButton = new JButton("Save Game");
        JButton loadButton = new JButton("Load Game");
        JButton exitButton = new JButton("Exit");

        saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        saveButton.addActionListener(e -> {
            try {
                saveGame();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        loadButton.addActionListener(e -> {
            try {
                loadGame();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        exitButton.addActionListener(e -> System.exit(0));

        controlPanel.add(Box.createVerticalStrut(10)); // Space before buttons
        controlPanel.add(saveButton);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(loadButton);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(exitButton);
    }

    /**
     * Saves the current game state.
     * @throws IOException if an error occurs during saving.
     */
    private void saveGame() throws IOException {
        List<Character> allies = new ArrayList<>();
        List<Character> enemies = new ArrayList<>();
        int level = 1;

        gameStateManager.saveStatus(allies, enemies, level);
        JOptionPane.showMessageDialog(frame, "Gioco salvato con successo!");
    }

    /**
     * Loads a previously saved game state.
     * @throws IOException if an error occurs during loading.
     */
    private void loadGame() throws IOException {
        GameState gameState = gameStateManager.loadStatus();
        if (gameState != null) {
            JOptionPane.showMessageDialog(frame, "Game loaded successfully! Level: " + gameState.getLevel());
        } else {
            JOptionPane.showMessageDialog(frame, "No saves found.");
        }
    }

    /**
     * Displays the coordinates of the clicked grid button.
     * @param row The row index of the button.
     * @param col The column index of the button.
     */
    private void showButtonCoordinates(int row, int col) {
        JOptionPane.showMessageDialog(frame, "Position: [" + row + ", " + col + "]");
    }

    /**
     * Gets the list of enemies in the current level.
     * @return List of enemy characters.
     */
    public List<Character> getEnemiesList() {
        return enemiesList;
    }

    /**
     * Gets the list of allies characters.
     * @return List of allies.
     */
    public List<Character> getAlliesList() {
        return alliesList;
    }
    
}
