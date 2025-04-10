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
    protected GridPanel gridPanel; // Fare una classe a parte chiamata gridPanel che estende
    protected JPanel controlPanel; // Panel for control buttons
    protected JButton hiddenMenuButton; // Il pulsante per aprire/chiudere il menu, SE LO PREMO MI APPAIONO I 3 PULSANTI in un JDialog nel mezzo dello schermo nero

    private GameStateManager gameStateManager;

    private List<Character> enemiesList; // List of enemies
    private List<Character> alliesList;  // List of allies
    private final int numLevel;			 // Num level				

    /**
     * Constructor
     * @param enemiesList List of enemy characters in the current level.
     * @param alliesList  List of allied characters.
     */
    public AbstractMap(List<Character> enemiesList, List<Character> alliesList, int numLevel) {
        this.enemiesList = enemiesList;
        this.alliesList = alliesList;
        this.gameStateManager = new GameStateManager();
        this.numLevel = numLevel;
    }
    
    public void start() 
    {
    	System.out.print("Open Level Frame ->");
    	initializeFrame();
        initializeControlPanel(); // Initializes the button panel
        setBackgroundMap();
        this.gridPanel = new GridPanel(frame, enemiesList, alliesList);
        frame.setVisible(true);
    }
    
    
    
    /**
     * Initializes the main frame and layout.
     */
    private void initializeFrame() {
        frame = new JFrame("Saga dei 5 Regni");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800); // Più spazio per i pulsanti
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.add(mainPanel, BorderLayout.CENTER);

        gridPanel = new GridPanel(frame, enemiesList, alliesList);
        mainPanel.add(gridPanel, BorderLayout.CENTER); // Grid centrale

        controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBackground(Color.LIGHT_GRAY); // Sfondo visibile
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 50, 50)); // Margine interno
        mainPanel.add(controlPanel, BorderLayout.EAST); // Pannello sulla destra
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
    
    private void setBackgroundMap() {
        // Percorso della cartella contenente i background
        String backgroundsFolder = "images/background/";

        // Determina il file associato al livello corrente
        String backgroundFile = backgroundsFolder + "background" + numLevel + ".jpg";

        // Carica l'immagine dal file
        ImageIcon backgroundImage = new ImageIcon(backgroundFile);

        // Controlla che l'immagine sia valida
        if (backgroundImage.getIconWidth() > 0 && backgroundImage.getIconHeight() > 0) {
            // Crea un JLabel per contenere l'immagine di sfondo
            JLabel backgroundLabel = new JLabel(backgroundImage);
            backgroundLabel.setBounds(0, 0, frame.getWidth(), frame.getHeight()); // Configura dimensioni

            // Configura il pannello principale come un JLayeredPane
            JLayeredPane layeredPane = new JLayeredPane();
            layeredPane.setLayout(null); // Usa null layout per la sovrapposizione
            frame.setContentPane(layeredPane);

            // Aggiungi il background al livello inferiore
            layeredPane.add(backgroundLabel, Integer.valueOf(0)); // Livello inferiore (background)

            // Aggiungi la griglia al livello superiore
            gridPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight()); // Stesse dimensioni del frame
            gridPanel.setOpaque(false); // Rendi la griglia trasparente per mostrare lo sfondo
            layeredPane.add(gridPanel, Integer.valueOf(1)); // Livello superiore (griglia)
        } else {
            System.err.println("Errore: immagine di background non trovata per il livello " + numLevel);
        }
    }



}
