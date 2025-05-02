package view.map;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.*;

import controller.GameController;
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
    
    protected JFrame frame;				 // Finestra principale del gioco
    protected GridPanel gridPanel;       // Griglia di bottoni
    private JLayeredPane layeredPanel;   // Gestisce i vari layer del frame

    private List<Character> enemiesList; // Lista dei nemici
    private List<Character> alliesList;  // Lista degli alleati
    private final int numLevel;          // Numero del livello
    
    private Map<Character, Point> characterMap;
    private List<Point> alliesPositionList;
    private List<Point> enemiesPositionList;
    
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
		this.enemiesPositionList.add(new Point(6,4));
		this.enemiesPositionList.add(new Point(3,7));
		this.enemiesPositionList.add(new Point(2,12));

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
        this.initializePauseMenu();
        
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
     
    /**
     * Initializes the Pause menu with buttons for resuming, saving, and exiting the game.
     */
    private void initializePauseMenu() {
        // **Pannello unico che copre tutta la finestra**
        JPanel pauseMenuPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon backgroundImage = new ImageIcon("images/background/backgroundMenu.jpg");
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };

        pauseMenuPanel.setSize(frame.getSize());
        pauseMenuPanel.setOpaque(false); // Mantiene la trasparenza del background
        pauseMenuPanel.setLayout(new GridBagLayout()); // Layout per centrare elementi
        pauseMenuPanel.setVisible(false);

        // **Impedisce i clic su elementi esterni**
        pauseMenuPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                e.consume(); // Impedisce qualsiasi clic fuori dal menu
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 20, 0); // Spaziatura
        gbc.anchor = GridBagConstraints.CENTER;

        // **Titolo centrato**
        JLabel titleLabel = new JLabel("PAUSE MENU", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);

        // **Pannello interno per i pulsanti**
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);

        // Creazione dei pulsanti
        JButton resumeButton = new JButton("Resume");
        JButton saveButton = new JButton("Save Game");
        JButton exitButton = new JButton("Exit");

        for (JButton button : new JButton[]{resumeButton, saveButton, exitButton}) {
            button.setFont(new Font("Arial", Font.PLAIN, 20));
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setMaximumSize(new Dimension(200, 50));
            buttonPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Spaziatura tra i bottoni
            buttonPanel.add(button);
        }

        // **Azioni dei pulsanti**
        resumeButton.addActionListener(e -> pauseMenuPanel.setVisible(false));

        saveButton.addActionListener(e -> {
            try {
                this.controller.saveGame(this.alliesList, this.enemiesList, this.numLevel);
                System.out.println("Game saved successfully!");
                pauseMenuPanel.setVisible(false);
                
            } catch (IOException ex) {
                System.err.println("Error saving game: " + ex.getMessage());
            }
        });

        exitButton.addActionListener(e -> {
            System.out.println("You chose to close the game");
            System.exit(0);
        });

        gbc.gridy++; // Posiziona sotto il titolo
        pauseMenuPanel.add(titleLabel, gbc);
        gbc.gridy++;
        pauseMenuPanel.add(buttonPanel, gbc); // Aggiunge i pulsanti centrati

        // **Mostra il menu quando si clicca sull'immagine**
        JButton menuButton = new JButton(new ImageIcon(new ImageIcon("images/pauseGame.png")
            .getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH)));

        menuButton.setBounds(10, 10, 60, 60);
        menuButton.setBorderPainted(false);
        menuButton.setContentAreaFilled(false);
        menuButton.setFocusPainted(false);
        menuButton.addActionListener(e -> pauseMenuPanel.setVisible(true));

        this.layeredPanel.add(pauseMenuPanel, Integer.valueOf(3));
        this.layeredPanel.add(menuButton, Integer.valueOf(2));
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
	public void spawnCharacter(List<Character> spawnList) {
	    // Controllo lista nulla o con meno di 3 personaggi
	    if (spawnList == null || spawnList.size() < 3) {
	        System.err.println("Spawn list is null or has less than 3 characters.");
	        return;
	    }

	    // Prende la lista corretta e ne fa una copia difensiva
	    List<Point> originalList = spawnList.get(0).isAllied() ? this.alliesPositionList : this.enemiesPositionList;

	    if (originalList == null || originalList.size() < 3) {
	        System.err.println("Original position list is null or does not have enough positions.");
	        return;
	    }

	    // Copia difensiva: non modifico la lista originale
	    List<Point> positionList = new ArrayList<>(originalList);

	    // Spawna massimo 3 personaggi o meno se non ci sono abbastanza posizioni
	    int spawnCount = Math.min(3, positionList.size());

	    for (int i = 0; i < spawnCount; i++) {
	        spawnCharacterHelper(spawnList.get(i), positionList);
	    }
	}

	private void spawnCharacterHelper(Character character, List<Point> positionList) {
	    if (positionList == null || positionList.isEmpty()) {
	        System.err.println("Position list is empty. Cannot spawn character.");
	        return;
	    }

	    int target = random.nextInt(positionList.size());
	    Point chosenPosition = positionList.remove(target);
	    character.setPosition(chosenPosition);

	    JButton button = this.gridPanel.getGridButtons()[chosenPosition.getX()][chosenPosition.getY()];

	    // Imposta immagine del personaggio sul bottone
	    button.setIcon(new ImageIcon(character.getImage()));
	    button.setContentAreaFilled(false);  // Sfondo trasparente
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
