package view.map;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.*;

import controller.GameController;
import model.characters.Character;
import model.point.Point;
import view.PauseMenu;

/**
 * Abstract class representing a map in the game.
 * It includes a grid of buttons and a control panel with game management options.
 */
public abstract class AbstractMap 
{
    
    public static final int GRID_SIZE_HEIGHT  = 15;  // Righe della griglia 
    public static final int GRID_SIZE_WIDTH   = 20;  // Colonne della griglia 
    
    protected JFrame frame;				 // Finestra principale del gioco
    protected GridPanel gridPanel;       // Griglia di bottoni
    private JLayeredPane layeredPanel;   // Gestisce i vari layer del frame

    private List<Character> enemiesList; // Lista dei nemici
    private List<Character> alliesList;  // Lista degli alleati
    private final int numLevel;          // Numero del livello
    
    private Map<Character, Point> characterMap;
    private List<Point> alliesPositionList;	// per lo spawn
    private List<Point> enemiesPositionList; // per lo spawn
    
    private Random random;
    
    private GameController controller;

    /**
     * Costruttore
     * @param enemiesList Lista dei nemici nel livello corrente.
     * @param alliesList Lista degli alleati nel livello corrente.
     * @param numLevel Numero del livello corrente.
     */
    public AbstractMap(List<Character> enemiesList, List<Character> alliesList, int numLevel) 
    {
        this.enemiesList      = enemiesList;
        this.alliesList       = alliesList;
        this.numLevel         = numLevel;
        
        this.characterMap = new HashMap<>();
        this.alliesPositionList = new ArrayList<>();
        this.enemiesPositionList = new ArrayList<>();
        this.random = new Random();
        this.initializePositionList();
    }
    
    
	public void start() 
    {
		 if (this.numLevel == 0){
		        System.out.print(" Open Tutorial Frame ->");
		 } 
		 else{
		        System.out.print("Open Level " + this.numLevel + " Frame ->");
		 }
		 
    	initializeFrame();
        
        this.frame.setVisible(true);
    }
    
    
    
    /**
     * Initializes the main frame and layout.
     */
    private void initializeFrame()
    {
        this.frame = new JFrame("Five Realms");                      		//Creo la finestra
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   		//Quando la finestra viene chiusa, il programma termina
        this.frame.setResizable(false);

        // Imposta le dimensioni della finestra in base alla risoluzione dello schermo
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // Ottego le dimensioni dello schermo
	    int width  = (int) (screenSize.getWidth() * 0.6);                   // 60% della larghezza dello schermo
	    int height = (int) (width * 3.0 / 4.0);                             // Rapporto 4:3
	    this.frame.setSize(width, height);                                  // Imposta le dimensioni della finestra
	         
        this.frame.setLocationRelativeTo(null);   							//Quando inizia il gioco posiziona la finestra al centro dello schermo 

        this.frame.setLayout(new BorderLayout());

        // Crea il JLayeredPanel che gestisce i vari layer
        this.layeredPanel = new JLayeredPane();
        this.frame.setContentPane(this.layeredPanel);
        
        // Imposta le dimesioni del JLayeredPane
        this.layeredPanel.setSize(width -13, height -35); 
        
        //TODO: Da studiare il metodo getContentPane() per settare dinamicamente la size del JLayeredPane rispetto al frame
        
        
        // layer 0. Background 
        this.initializeBackgroundMap();
        
        // layer 1. Griglia di bottoni trasparente
        this.initializeButtonGrid();
        
        // layer 2. Menu di Pausa
        //this.initializePauseMenu();
        PauseMenu pauseMenu = new PauseMenu(this.frame, this.layeredPanel, enemiesList, alliesList, numLevel, this.controller);
        pauseMenu.initializePauseMenu();

        
    }
    


	/**
	 * Initializes the background map for the current level.
	 */
    private void initializeBackgroundMap() 
    {
        // Ottieni l'immagine associata al livello corrente
        String backgroundFile = "images/background/background" + numLevel + ".jpg";

        // Carica l'immagine dal file
        ImageIcon backgroundImage = new ImageIcon(backgroundFile);

        // Controlla che l'immagine sia valida
        if (backgroundImage.getIconWidth() > 0 && backgroundImage.getIconHeight() > 0) 
        {
        	// Ridimensiona l'immagine per adattarla al frame (Problema qui)
        	Dimension panelSize = this.layeredPanel.getSize();
            int panelWidth = panelSize.width;
            int panelHeight = panelSize.height;

            Image image = backgroundImage.getImage();
            Image resizedImage = image.getScaledInstance(panelWidth, panelHeight, Image.SCALE_SMOOTH);
            backgroundImage = new ImageIcon(resizedImage);

            JLabel backgroundLabel = new JLabel(backgroundImage);
            backgroundLabel.setBounds(0, 0, panelWidth, panelHeight);
            
            // Aggiungi il background al livello inferiore 0
            this.layeredPanel.add(backgroundLabel, Integer.valueOf(0)); 
            
            // Rendi visibile il layout dopo aver aggiunto il componente
            this.layeredPanel.revalidate();
            this.layeredPanel.repaint();
        } 
        else {
            System.err.println("Errore: immagine di background non trovata per il livello " + numLevel);
        }
    }
    
    /**
	 * Initializes the button grid for the current level.
	 */
    private void initializeButtonGrid() 
    {
        // Crea e inizializza il GridPanel se non esiste già
        if (this.gridPanel == null) 
        {
            this.gridPanel = new GridPanel(this.layeredPanel);
        }      
        
        // Imposta le dimensioni del gridPanel uguali a quelle del frame
        this.gridPanel.setBounds(0, 0, this.layeredPanel.getWidth(), this.layeredPanel.getHeight());

        // Aggiungi gridPanel al JLayeredPane nel livello superiore (livello 1)
        this.layeredPanel.add(this.gridPanel, Integer.valueOf(1));
    	
        // Rendi il pannello della griglia trasparente
        this.gridPanel.setOpaque(false); 
        
        // Rendi visibile il layout dopo aver aggiunto il componente
        this.layeredPanel.revalidate();
        this.layeredPanel.repaint();
    }
     
    
    public void colourPositionAvailable(List<Point> availableMoves) 
    {
        if (availableMoves == null || availableMoves.isEmpty()) 
        {
            System.err.println("La lista delle posizioni disponibili è vuota o nulla.");
            return;
        }

        JButton[][] buttonGrid = this.gridPanel.getGridButtons();

        int rows = buttonGrid.length;
        int cols = buttonGrid[0].length;

        for (Point p : availableMoves) 
        {
            int y = p.getX();
            int x = p.getY();

            // Controllo bounds per evitare eccezioni
            if (y >= 0 && y < rows && x >= 0 && x < cols) 
            {
                JButton button = buttonGrid[y][x];

                Color semiTransparentGray = new Color(200, 200, 200, 100);

                // Per rendere la trasparenza visibile bisogna disegnare su un componente non opaco
                button.setBackground(semiTransparentGray);
                button.setOpaque(true);                 // Importante: deve essere false!
                button.setContentAreaFilled(true);       // Importante: true per disegnare il colore
                button.setBorderPainted(false);          // opzionale
                button.setFocusPainted(false);
                button.setRolloverEnabled(false);
            } 
        }

        // Rinfresca la griglia per mostrare i cambiamenti
        this.gridPanel.revalidate();
        this.gridPanel.repaint();
    }
    
    public void resetGridColors() 
    {
        JButton[][] buttonGrid = this.gridPanel.getGridButtons();

        for (int y = 0; y < buttonGrid.length; y++) 
        {
            for (int x = 0; x < buttonGrid[y].length; x++) 
            {
                JButton button = buttonGrid[y][x];
                button.setOpaque(false);         
            }
        }

        this.gridPanel.revalidate();
        this.gridPanel.repaint();
    }


    
    
    /**
	 * Closes the game window.
	 */
    public void closeWindow() 
    {
        if (this.frame != null) 
            this.frame.dispose();
        
    }
    
  
    /**
     * Returns the button at the specified coordinates in the grid.
     * @param x
     * @param y
     * @return
     */
	public JButton getButtonAt(int x, int y) 
	{
        if (x >= 0 && x < GRID_SIZE_WIDTH && y >= 0 && y < GRID_SIZE_HEIGHT) {
            return gridPanel.getGridButtons()[x][y];
        }
        return null;
    }
    
	/**
	 * Spawns characters on the map.
	 * @param spawnList List of characters to spawn.
	 */
	public void spawnCharacter(List<Character> spawnList)
	{
	    if (spawnList == null || spawnList.isEmpty()) 
	    {
	        System.err.println("Spawn list is null or empty.");
	        return;
	    }

	    List<Point> positionList = spawnList.get(0).isAllied() ? this.alliesPositionList : this.enemiesPositionList;

	    if (spawnList.size() > positionList.size()) {
	        System.err.println("Not enough spawn positions for characters.");
	        return;
	    }

	    for (int i = 0; i < spawnList.size(); i++) 
	    {
	        spawnCharacterHelper(spawnList.get(i), positionList);
	    }
	}

	private void spawnCharacterHelper(Character character, List<Point> positionList) {
	    int target = random.nextInt(positionList.size());
	    Point chosenPosition = positionList.remove(target);
	    character.setPosition(chosenPosition);

	    // Aggiorna characterMap solo ora che il personaggio ha posizione
	    this.characterMap.put(character, chosenPosition);

	    JButton button = this.gridPanel.getGridButtons()[chosenPosition.getX()][chosenPosition.getY()];
	    button.setIcon(new ImageIcon(character.getImage()));
	    button.setContentAreaFilled(false);
	}

	private void initializePositionList() 
    {
		this.alliesPositionList.add(new Point(16,4));
		this.alliesPositionList.add(new Point(14,7));
		this.alliesPositionList.add(new Point(16,10));
		
		this.enemiesPositionList.add(new Point(4,10));
		this.enemiesPositionList.add(new Point(4,4));
		this.enemiesPositionList.add(new Point(6,7));
		this.enemiesPositionList.add(new Point(6,4));
		this.enemiesPositionList.add(new Point(3,7));
		this.enemiesPositionList.add(new Point(2,12));
		this.enemiesPositionList.add(new Point(5,10));
	}

	
	/* Il metodo rimuove l'immagine dove si trova il personaggio e aggiunge l'immagine del personaggio nel  bottone targhet
	 * moveCharacter usa moveTo e aggiornamento grafico
	*/
	public void moveCharacter(Character character, Point target) 
	{
	    if (character == null || target == null) 
	    {
	        throw new IllegalArgumentException("Character and target point must not be null");
	    }

	    System.out.println("\nTentativo di spostare il personaggio: " + character.getClass().getSimpleName() +
	                       " da " + character.getPosition() + " a " + target);

	    if (!this.characterMap.containsKey(character)) 
	    {
	        System.err.println("Character not found in the map: " + character.getClass().getSimpleName());
	        return;
	    }

	    Point currentPosition = character.getPosition();

	    if (currentPosition.equals(target)) 
	    {
	        System.out.println("Il personaggio è già nella posizione target: " + target);
	        return;
	    }

	    if (isPositionOccupied(target)) 
	    {
	        System.err.println("La posizione " + target + " è già occupata da un altro personaggio.");
	        return;
	    }

	    // Aggiorna la mappa con la nuova posizione
	    this.characterMap.put(character, target);

	    // Rimuove l'immagine dal bottone corrente
	    JButton currentButton = this.gridPanel.getGridButtons()[currentPosition.getX()][currentPosition.getY()];
	    currentButton.setIcon(null);

	    // Imposta l'immagine nel nuovo bottone
	    JButton targetButton = this.gridPanel.getGridButtons()[target.getX()][target.getY()];
	    targetButton.setIcon(new ImageIcon(character.getImage()));
	    

	    System.out.println("\nPersonaggio " + character.getClass().getSimpleName() + " spostato con successo.");
	}

	
	//rimuova dalla mappe il personaggio 
	public void removeCharacter(Character character, Point target) {
		
	}


	public List<Character> getEnemiesList() {
		return enemiesList;
	}


	public List<Character> getAlliesList() {
		return alliesList;
	}
	
	public JButton[][] getGridButtons() {
        return this.gridPanel.getGridButtons();
    }
	
	// Metodo che ti dice se è occupata gia la posizione di un personaggio
	public boolean isPositionOccupied(Point point) {
		return this.alliesList.stream().anyMatch(a -> a.getPosition().equals(point)) || this.enemiesList.stream().anyMatch(a -> a.getPosition().equals(point));
	}
	
	
	public void updateMap()
	{
	    Map<JButton, Point> imageButtonList = this.gridPanel.getImageButtonList();
	    JButton[][] buttonGrid = this.gridPanel.getGridButtons();

	    System.out.println("Grid dimensioni: " + buttonGrid.length + " righe, " + buttonGrid[0].length + " colonne");

	    // Crea un set delle posizioni occupate per efficienza O(1) lookup
	    Set<Point> occupiedPositions = this.characterMap.values().stream()
	        .filter(Objects::nonNull)
	        .collect(Collectors.toSet());

	    // 1) Rimuove le immagini dai bottoni che non hanno più un personaggio
	    for (Map.Entry<JButton, Point> entry : imageButtonList.entrySet())
	    {
	        JButton button = entry.getKey();
	        Point point = entry.getValue();

	        if (!occupiedPositions.contains(point))
	        {
	            button.setIcon(null);
	            //button.setToolTipText(""); //may not be necessary?
	        }
	    }

	    // 2) Aggiunge immagini a bottoni dove è presente un personaggio
	    for (Map.Entry<Character, Point> entry : this.characterMap.entrySet())
	    {
	        Character character = entry.getKey();
	        Point point = entry.getValue();

	        if (point == null)
	        {
	            System.err.println("ERRORE: Personaggio " +
	                character.getClass().getSimpleName() + " ha posizione null");
	            continue;
	        }

	        // CORREZIONE: Usa x per righe e y per colonne per essere consistente
	        int row = point.getX();  // x rappresenta la riga
	        int col = point.getY();  // y rappresenta la colonna

	        // Controllo bounds dell'array
	        if (row < 0 || row >= buttonGrid.length || col < 0 || col >= buttonGrid[0].length)
	        {
	            System.err.println("ERRORE: Coordinate fuori dai limiti per " +
	                character.getClass().getSimpleName() + ": " + point + 
	                " (Grid: " + buttonGrid.length + "x" + buttonGrid[0].length + ")");
	            continue;
	        }

	        JButton button = buttonGrid[row][col];
	        if (button.getIcon() == null)
	        {
	            button.setIcon(new ImageIcon(character.getImage()));
	            button.setToolTipText(character.toString());
	        }
	    }
	}
}
