package view.map;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.*;
import model.characters.Character;
import model.point.Point;

/**
 * Abstract class representing a map in the game.
 * It includes a grid of buttons and a control panel with game management options.
 */
public abstract class AbstractMap 
{
    
    public static final int GRID_SIZE_HEIGHT  = 15;  // Righe della griglia 
    public static final int GRID_SIZE_WIDTH   = 20;  // Colonne della griglia 
    
    protected JFrame frame;
    protected GridPanel gridPanel;       // Rappresenta il pannello della griglia di gioco
    private JLayeredPane layeredPanel;

    private List<Character> enemiesList; // Lista dei nemici
    private List<Character> alliesList;  // Lista degli alleati
    private final int numLevel;          // Numero del livello
    private Map<Character, Point> characterMap;

    private List<Point> alliesPositionList;
    private List<Point> enemiesPositionList;
    
    private Random random;

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

        // Popola la mappa con tutti i personaggi
        for (Character enemy : enemiesList) {
            characterMap.put(enemy, enemy.getPosition());
        }

        for (Character ally : alliesList) {
            characterMap.put(ally, ally.getPosition());
        }
    }
    
    private void initializePositionList() 
    {
		this.alliesPositionList.add(new Point(16,4));
		this.alliesPositionList.add(new Point(14,7));
		this.alliesPositionList.add(new Point(16,10));
		
		this.enemiesPositionList.add(new Point(4,10));
		this.enemiesPositionList.add(new Point(4,4));
		this.enemiesPositionList.add(new Point(6,7));
		
	}

	public void start() 
    {
		 if (this.numLevel == 0){
		        System.out.print(" Open Tutorial Frame ->");
		 } 
		 else{
		        System.out.print("Open Level " + this.numLevel + " Frame ->");
		 }
		 
    	Timer timer = new Timer(16, e -> {
            //button.setToolTipText("<html>" + "Righe"+row + "<br>" + "Colonne"+col + "</html>"); // Sarebbe da fare solo con bottoni con immagine

    	    /*Map<JButton, Point> imageButtonList = this.gridPanel.getImageButtonList();

    	    // CASO 1: Un bottone ha immagine ma NESSUN personaggio si trova in quella posizione
    	    for (Map.Entry<JButton, Point> entry : imageButtonList.entrySet()) 
    	    {
    	        JButton button = entry.getKey();
    	        Point buttonPos = entry.getValue();
    	        boolean personFound = false;

    	        // Stampa di debug per capire se un bottone ha un'immagine
    	        if (button.getIcon() != null) {
    	            System.out.println("Bottone con immagine trovato nella posizione " + buttonPos);
    	        }

    	        // Controlla se un personaggio è presente nella stessa posizione
    	        for (Point characterPos : characterMap.values()) {
    	            if (characterPos.equals(buttonPos)) {
    	                personFound = true;
    	                break;
    	            }
    	        }

    	        // Se non ci sono personaggi nella posizione del bottone, rimuovi l'immagine
    	        if (!personFound && button.getIcon() != null) {
    	            System.out.println("Nessun personaggio trovato in " + buttonPos + ", rimuovo immagine");
    	            button.setIcon(null); // Rimuovi immagine
    	        }
    	    }

    	    // CASO 2: Un personaggio si trova su una posizione dove il bottone NON ha immagine
    	    for (Map.Entry<Character, Point> entry : characterMap.entrySet()) {
    	        Character character = entry.getKey();
    	        Point characterPos = entry.getValue();
    	        JButton button = this.gridPanel.getGridButtons()[characterPos.getY()][characterPos.getX()];

    	        // Stampa di debug per capire se il personaggio ha un'immagine
    	        System.out.println("Controllando personaggio " + character + " nella posizione " + characterPos);

    	        if (button.getIcon() == null) {
    	            System.out.println("Nessuna immagine nel bottone alla posizione " + characterPos + ", aggiungo immagine del personaggio");
    	            ImageIcon originalIcon = new ImageIcon(character.getImage());
    	            Image scaledImage = originalIcon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH); // adatta la misura
    	            button.setIcon(new ImageIcon(scaledImage));
    	        }
    	    }*/
    	});

    	
    	initializeFrame();
        
        this.frame.setVisible(true);
        
        //timer.start();
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
        
        // layer 2. Menu di controllo
        this.initializeControlMenu();
        
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
    
    private void initializeControlMenu() {
        // Pulsante per aprire il menu
        JButton menuButton = new JButton();
        
        // Carica e ridimensiona l'immagine del pulsante
        ImageIcon originalIcon = new ImageIcon("images/pauseGame.png"); // quella nuova tipo cartone
        Image scaledImage = originalIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(scaledImage);
        
        menuButton.setIcon(resizedIcon);
        menuButton.setBounds(10, 10, 60, 60); // Più grande
        menuButton.setBorderPainted(false);
        menuButton.setContentAreaFilled(false);
        menuButton.setFocusPainted(false);

        // Pannello del menu
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(3, 1, 10, 10));
        controlPanel.setSize(300, 200);
        controlPanel.setVisible(false);

        // Centra il pannello nella finestra
        int centerX = (frame.getWidth() - controlPanel.getWidth()) / 2;
        int centerY = (frame.getHeight() - controlPanel.getHeight()) / 2;
        controlPanel.setLocation(centerX, centerY);

        // Pulsanti
        JButton resumeButton = new JButton("Resume");
        JButton saveButton = new JButton("Save Game");
        JButton exitButton = new JButton("Exit");

        resumeButton.addActionListener(e -> {
        	controlPanel.setVisible(false);
            System.out.println("Resume button pressed");
        });
        saveButton.addActionListener(e -> {
        	controlPanel.setVisible(false);
            System.out.println("Save button pressed");
        });
        exitButton.addActionListener(e -> {
            frame.dispose();
            System.exit(0);
            System.out.println("Exit pressed");
            
        });

        controlPanel.add(resumeButton);
        controlPanel.add(saveButton);
        controlPanel.add(exitButton);

        // Toggle visibilità
        menuButton.addActionListener(e -> controlPanel.setVisible(true));

        // Aggiunta ai livelli del layeredPane
        this.layeredPanel.add(menuButton, Integer.valueOf(2));        // Pulsante su layer 2
        this.layeredPanel.add(controlPanel, Integer.valueOf(2));      // Menu sopra tutto
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
	    if (spawnList == null || spawnList.size() < 3) 
	    {
	        System.err.println("Spawn list is null or has less than 3 characters.");
	        return;
	    }

	    List<Point> positionList = spawnList.get(0).isAllied() ? this.alliesPositionList : this.enemiesPositionList;

	    for (int i = 0; i < 3; i++) {
	        spawnCharacterHelper(spawnList.get(i), positionList);
	    }
	}
    
	
	private void spawnCharacterHelper(Character character, List<Point> positionList) 
	{
	    int target = random.nextInt(0, positionList.size());
	    character.setPosition(positionList.get(target));
	    positionList.remove(target);

	    JButton button = this.gridPanel.getGridButtons()[character.getPosition().getX()][character.getPosition().getY()];
	    
	    // Setto il bottone con l'immagine del personaggio
	    button.setIcon(new ImageIcon(character.getImage()));
	    
	    //Bordino bianco vicino all'immagine del personaggio
	    button.setContentAreaFilled(false);    
	}
    
	
	/* Il metodo rimuove l'immagine dove si trova il personaggio e aggiunge l'immagine del personaggio nel  bottone targhet
	 * moveCharacter usa moveTo e aggiornamento grafico
	*/
	public void moveCharacter(Character character, Point target) {
		
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
	
	
}
