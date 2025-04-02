package view;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import model.characters.Character;
import model.gameStatus.GameState;
import model.gameStatus.GameStateManager;

public abstract class AbstractMap implements Map{
	
	public static final int GRID_SIZE = 6;
    public static final int BUTTON_SIZE = 120;

    protected JFrame frame;
    protected JPanel gridPanel;
    protected JPanel controlPanel; // Nuovo pannello per i pulsanti
    protected JButton[][] gridButtons = new JButton[GRID_SIZE][GRID_SIZE];
    private GameStateManager gameStateManager;

	private List<Character> enemiesList;          // Lista dei nemici del livello
    private List<Character> alliesList;           // Lista dei personaggi con cui giochiamo il livello
	
    
    public AbstractMap(List<Character> enemiesList, List<Character> alliesList) {
		this.enemiesList = enemiesList;
		this.alliesList = alliesList;
		this.gameStateManager = new GameStateManager();
        initializeFrame();
        initializeGrid();
        initializeControlPanel(); // Inizializza il pannello dei pulsanti

        frame.setVisible(true);
	}
    
    private void initializeFrame() {
        frame = new JFrame("Saga dei 5 Regni");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800); // Aumento larghezza per ospitare il pannello a destra
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout()); // Pannello principale per organizzare griglia e pulsanti
        frame.add(mainPanel, BorderLayout.CENTER);

        gridPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE));
        mainPanel.add(gridPanel, BorderLayout.WEST); // Metti la griglia a sinistra

        controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS)); // Pulsanti in verticale
        mainPanel.add(controlPanel, BorderLayout.EAST); // Metti i pulsanti a destra
    }

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
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
        loadButton.addActionListener(e -> {
			try {
				loadGame();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
        exitButton.addActionListener(e -> System.exit(0));

        controlPanel.add(Box.createVerticalStrut(10)); // Spazio prima dei pulsanti
        controlPanel.add(saveButton);
        controlPanel.add(Box.createVerticalStrut(10)); // Spazio tra i pulsanti
        controlPanel.add(loadButton);
        controlPanel.add(Box.createVerticalStrut(10)); // Spazio tra i pulsanti
        controlPanel.add(exitButton);
        
    }

    private void saveGame() throws IOException {
        List<Character> allies = new ArrayList<>();
        List<Character> enemies = new ArrayList<>();
        int level = 1;

        gameStateManager.saveStatus(allies, enemies, level);
        JOptionPane.showMessageDialog(frame, "Gioco salvato con successo!");
    }

    private void loadGame() throws IOException {
        GameState gameState = gameStateManager.loadStatus();
        if (gameState != null) {
            JOptionPane.showMessageDialog(frame, "Gioco caricato con successo! Livello: " + gameState.getLevel());
        } else {
            JOptionPane.showMessageDialog(frame, "Nessun salvataggio trovato.");
        }
    }

    private void showButtonCoordinates(int row, int col) {
        JOptionPane.showMessageDialog(frame, "Posizione: [" + row + ", " + col + "]");
    }

	public List<Character> getEnemiesList() {
		return enemiesList;
	}

	public List<Character> getAlliesList() {
		return alliesList;
	}
    

}
