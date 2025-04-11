package view.map;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import javax.swing.*;
import model.characters.Character;
import model.point.Point;

/**
 * Abstract class representing a map in the game.
 * It includes a grid of buttons and a control panel with game management options.
 */
public abstract class AbstractMap implements Map 
{
    
    public static final int GRID_SIZE   = 20;  // Dimensione della griglia
    public static final int BUTTON_SIZE = 200; // Dimensione dei bottoni 120

    protected JFrame frame;
    protected GridPanel gridPanel;       // Rappresenta il pannello della griglia di gioco
    private JLayeredPane layeredPanel;
    //protected JPanel controlPanel;       // Pannello per i pulsanti di controllo
    //protected JButton hiddenMenuButton;  // Pulsante per aprire/chiudere il menu

    private List<Character> enemiesList; // Lista dei nemici
    private List<Character> alliesList;  // Lista degli alleati
    private final int numLevel;          // Numero del livello
	private TreeMap<Character,Point> characterMap;

    //private GameStateManager gameStateManager;

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
        this.characterMap	  = new TreeMap<>();
       // this.gameStateManager = new GameStateManager();
    }
    
    
    public void start() 
    {
    	System.out.print("Open Level "+this.numLevel+" Frame ->");
    	
    	Timer timer = new Timer(16, e->{
    		JButton[][] button = this.gridPanel.getGridButtons();
    		List<JButton> imageButtonList = this.gridPanel.getImageButtonList();
        	//Logica, controllare tutti i pulsanti
    		System.out.println("Test");
    		
    		//Stream qui (con entrySet)!!
    		
    		
    		/* Scorre la lista con i bottoni con immagine e vede se i bottoni
                	con immagine combacia con la posizione dei personaggi*/
                	
                	/* Caso 1: Un bottone ha un immagine ma il personaggio non si trova li,  
                	 * Scorriamo la lista dei bottoni con immagine (metodo public di GridPanel), andiamo a verificare 
                	 * presi i e j se ce una combinazione con la nostra mappa, se ce apposto, se non ce rimuoviamo quella immagine
                	*/
                	
                	
                	/* Caso 2): Il personaggio si trova in un bottone senza immagine, 
                	 * Dobbiamo verificare se ce un personaggio con quella posizione senza immagine,
                	 * stavolta prendiamo sempre la lista e dobbiamo settargli l'immagine a quel bottone.  
                	 * 
                	*/
               
    		
    	});
    	
    	initializeFrame();
        
        //initializeControlPanel(); 

        frame.setVisible(true);
        timer.start();
    }
    
    
    
    /**
     * Initializes the main frame and layout.
     */
    private void initializeFrame()
    {
        this.frame = new JFrame("Five Realms");                      //Creo la finestra
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   //Quando la finestra viene chiusa, il programma termina

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // Ottego le dimensioni dello schermo
	    int width  = (int) (screenSize.getWidth() * 0.6);                   // 60% della larghezza dello schermo
	    int height = (int) (width * 3.0 / 4.0);                             // Rapporto 4:3
	    this.frame.setSize(width, height);                                  // Imposta le dimensioni della finestra
	         
        this.frame.setLocationRelativeTo(null);   //Quando inizia il gioco posiziona la finestra al centro dello schermo 

        this.frame.setLayout(new BorderLayout());

        // Crea il JLayeredPanel che gestisce i vari layer
        this.layeredPanel = new JLayeredPane();
        this.frame.setContentPane(this.layeredPanel);
   
		// 0. Background 
        this.initializeBackgroundMap();
        
        // 1. Griglia di bottoni trasparente
        this.initializeGridMap();
        
        // 3. Personaggi     
        
        
        

        /*
        this.characterPanel = new JPanel();
        characterPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        layeredPane.add(characterPanel, Integer.valueOf(2));  // Livello 2 (Personaggi sopra la griglia)*/


        /*controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBackground(Color.LIGHT_GRAY); // Sfondo visibile
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 50, 50)); // Margine interno
        mainPanel.add(controlPanel, BorderLayout.EAST); // Pannello sulla destra*/
    }

    
    

    /**
     * Initializes the control panel with buttons for game actions.
     */
    /*private void initializeControlPanel() {
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
    }*/

    private void initializeBackgroundMap() 
    {
        // Ottieni l'immagine associata al livello corrente
        String backgroundFile = "images/background/background" + numLevel + ".jpg";

        // Carica l'immagine dal file
        ImageIcon backgroundImage = new ImageIcon(backgroundFile);

        // Controlla che l'immagine sia valida
        if (backgroundImage.getIconWidth() > 0 && backgroundImage.getIconHeight() > 0) 
        {
        	// Ridimensiona l'immagine per adattarla al frame
            Image image = backgroundImage.getImage();
            Image resizedImage = image.getScaledInstance(frame.getWidth(), frame.getHeight(), Image.SCALE_SMOOTH);
            backgroundImage = new ImageIcon(resizedImage);
        	
            // Crea un JLabel per contenere l'immagine di sfondo
            JLabel backgroundLabel = new JLabel(backgroundImage);
            backgroundLabel.setBounds(0, 0, frame.getWidth(), frame.getHeight()); // Configura le dimensioni

            // Usa il JLayeredPane esistente del frame
            JLayeredPane layeredPane = frame.getLayeredPane();

            // Aggiungi il background al livello inferiore 0
            layeredPane.add(backgroundLabel, Integer.valueOf(0)); 
        } 
        else {
            System.err.println("Errore: immagine di background non trovata per il livello " + numLevel);
        }
    }
    
    
    private void initializeGridMap() 
    {
        // Crea e inizializza il GridPanel se non esiste già
        if (this.gridPanel == null) 
        {
            this.gridPanel = new GridPanel(this.layeredPanel, enemiesList, alliesList);
        }      
        
        // Imposta le dimensioni del gridPanel uguali a quelle del frame
        this. gridPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight()); // Stesse dimensioni del frame

        // Rendi la griglia trasparente per non coprire lo sfondo
        this.gridPanel.setOpaque(false);

        // Aggiungi gridPanel al JLayeredPane nel livello superiore (livello 1)
        this.layeredPanel.add(this.gridPanel, Integer.valueOf(1));

        // Rendi visibile il layout dopo aver aggiunto il componente
        this.layeredPanel.revalidate();
        this.layeredPanel.repaint();
    }
    
    
    /**
     * Saves the current game state.
     * @throws IOException if an error occurs during saving.
     */
    /*private void saveGame() throws IOException {
        List<Character> allies = new ArrayList<>();
        List<Character> enemies = new ArrayList<>();
        int level = 1;

        gameStateManager.saveStatus(allies, enemies, level);
        JOptionPane.showMessageDialog(frame, "Gioco salvato con successo!");
    }*/

    /**
     * Loads a previously saved game state.
     * @throws IOException if an error occurs during loading.
     */
    /*private void loadGame() throws IOException {
        GameState gameState = gameStateManager.loadStatus();
        if (gameState != null) {
            JOptionPane.showMessageDialog(frame, "Game loaded successfully! Level: " + gameState.getLevel());
        } else {
            JOptionPane.showMessageDialog(frame, "No saves found.");
        }
    }*/
    
    

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
    
    //metodo per chiudere il frame manualmente
    public void closeWindow() 
    {
        if (this.frame != null) 
            this.frame.dispose();
        
    }
    
    /**
     * Spawns a character on the given button based on its position on the grid.
     * If a character is present at the specified grid position, the button will display the character's image.
     * If no character is present, the button will become transparent.
     *
     * @param button The button on the grid where the character will be spawned.
     * @param row The row index of the button on the grid.
     * @param col The column index of the button on the grid.
     * @param allies The list of allied characters.
     * @param enemies The list of enemy characters.
     */
	public static void spawnCharacter(List<Character> spwanList) 
	{
		// Fare controllo se ci sono nella lista alleati o nemici
		if(spwanList.get(0).isAllied()) {
			// Lista di alleati e spawnare in basso
			spwanList.get(0).moveTo(new Point(17, 3));
			spwanList.get(1).moveTo(new Point(19, 10));
			spwanList.get(2).moveTo(new Point(17, 16));
			
		}else {
			// LIsta di nemici, spawnare in alto
			spwanList.get(0).moveTo(new Point(2, 3));
			spwanList.get(1).moveTo(new Point(8, 5));
			spwanList.get(2).moveTo(new Point(5, 14));
			
		}
		
	}
			
	
	/* Il metodo rimuove l'immagine dove si trova il personaggio e aggiunge l'immagine del personaggio nel  bottone targhet
	 * moveCharacter usa moveTo e aggiornamento grafico
	*/
	public void moveCharacter(Character character, Point target) {
		
	}
	
    // Dato  x e y restituisce il bottone in quella posizione
	@Override
    public JButton getButtonAt(int x, int y) {
        if (x >= 0 && x < GRID_SIZE && y >= 0 && y < GRID_SIZE) {
            return gridPanel.getGridButtons()[x][y];
        }
        return null;
    }
    
    
	/*Point relevantPosition = new Point(row, col);
    Character characterInPosition;

    // Trova il personaggio nella posizione data
    characterInPosition = allies.stream()
            .filter(character -> character.getPosition().equals(relevantPosition))
            .findFirst()
            .orElse(null);

    if (characterInPosition == null) {
        System.out.println("Nessun personaggio trovato per la posizione: " + relevantPosition);

        characterInPosition = enemies.stream()
                .filter(character -> character.getPosition().equals(relevantPosition))
                .findFirst()
                .orElse(null);
    }

    System.out.println("Spawning character at position: " + relevantPosition); // Debug: stampa la posizione del personaggio

    // Se esiste un personaggio, aggiungilo al JLayeredPane come componente visibile
    if (characterInPosition != null) {
        // Crea un'etichetta con l'immagine del personaggio
        JLabel characterLabel = new JLabel(new ImageIcon(characterInPosition.getImage()));
        
        // Imposta la posizione della label nel JLayeredPane
        characterLabel.setBounds(col * AbstractMap.BUTTON_SIZE, row * AbstractMap.BUTTON_SIZE, AbstractMap.BUTTON_SIZE, AbstractMap.BUTTON_SIZE);

        // Aggiungi il personaggio al livello 2 (sopra la griglia, ma sotto altri elementi)
        layeredPane.add(characterLabel, Integer.valueOf(2)); // Livello 2 per i personaggi
    }
    
    layeredPane.revalidate();
    layeredPane.repaint();*/

	
	
}
