package view.map;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.swing.*;
import controller.GameController;
import model.characters.Character;
import model.point.Point;
import view.BannerPanel;
import view.CharacterTooltipManager;
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
    private BannerPanel banner;          // Banner di gioco per messaggi

    private List<Character> enemiesList; // Lista dei nemici
    private List<Character> alliesList;  // Lista degli alleati
    private final int numLevel;          // Numero del livello
    
    private Map<Character, Point> characterMap;
    private List<Point> alliesPositionList;	// per lo spawn
    private List<Point> enemiesPositionList; // per lo spawn
    
    CharacterTooltipManager tooltipManager;

    private Random random;
    
    private GameController controller;

    private JLabel levelLabel;
    private JLabel alliesLabel;
    private JLabel enemiesLabel;


    /**
     * Costruttore
     * @param enemiesList Lista dei nemici nel livello corrente.
     * @param alliesList Lista degli alleati nel livello corrente.
     * @param numLevel Numero del livello corrente.
     */
    public AbstractMap(List<Character> enemiesList, List<Character> alliesList, int numLevel, GameController controller) 
    {
        this.enemiesList      = enemiesList;
        this.alliesList       = alliesList;
        this.numLevel         = numLevel;
        this.controller		  = controller;
        
        this.characterMap = new HashMap<>();
        this.alliesPositionList = new ArrayList<>();
        this.enemiesPositionList = new ArrayList<>();
        this.random = new Random();
        this.initializePositionList();
        
        this.tooltipManager = new CharacterTooltipManager();

    }
    
    
	public void start() 
    {
		 if (this.numLevel == 0)
		 {
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
        
        // layer 0. Immagine di Background 
        this.initializeBackgroundMap();
        
        // layer 1. Griglia di bottoni 
        this.initializeButtonGrid();
        
        // layer 2. Banner di gioco
        this.initializeBanner();
        
        // layer 3. Menu di Pausa
        PauseMenu pauseMenu = new PauseMenu(this.frame, this.layeredPanel, enemiesList, alliesList, numLevel, this.controller);
        pauseMenu.initializePauseMenu();
        
        // layer 4. Etichette di informazioni sul gioco
        this.addGameInfoLabels();
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
        else 
        {
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
     
    private void initializeBanner()
    {
    	int width = this.layeredPanel.getWidth();
        int height = this.layeredPanel.getHeight();

        this.banner = new BannerPanel(width, height);

        // Aggiungilo sopra tutti gli altri componenti
        this.layeredPanel.add(this.banner, Integer.valueOf(2));

        this.layeredPanel.revalidate();
        this.layeredPanel.repaint();
    }
    
    private void addGameInfoLabels() {
        int paddingRight = 10;
        int paddingTop = 10;
        int labelWidth = 200;  // più piccolo
        int labelHeight = 30;  // più piccolo
        int spacing = 35;      // distanza verticale tra i label

        this.levelLabel = createStyledLabel("Livello: " + this.numLevel);
        this.alliesLabel = createStyledLabel("Alleati: " + this.alliesList.size());
        this.enemiesLabel = createStyledLabel("Nemici: " + this.enemiesList.size());

        // Posizioniamo a destra, quindi x = larghezza panel - larghezza label - paddingRight
        int xPos = layeredPanel.getWidth() - labelWidth - paddingRight;

        this.levelLabel.setBounds(100, paddingTop, labelWidth, labelHeight);
        this.alliesLabel.setBounds(xPos, paddingTop, labelWidth, labelHeight);
        this.enemiesLabel.setBounds(xPos, paddingTop + 35, labelWidth, labelHeight);

        this.layeredPanel.add(levelLabel, Integer.valueOf(3));
        this.layeredPanel.add(alliesLabel, Integer.valueOf(3));
        this.layeredPanel.add(enemiesLabel, Integer.valueOf(3));
    }



    
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Stencil", Font.BOLD, 28));
        label.setForeground(Color.WHITE);
        label.setBackground(new Color(34, 34, 34));
        label.setOpaque(true);
        label.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 3));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }


    
    /*Modifiche effettuate per il banner*/
    public int getWidth() {
    	return this.layeredPanel.getWidth();
    }

	public int getHeight() {
		return this.layeredPanel.getHeight();
	}
	
	private void updateInfoLabels() {
	    if (this.levelLabel != null) {
	        this.levelLabel.setText("Livello: " + this.numLevel);
	    }
	    if (this.alliesLabel != null) {
	        this.alliesLabel.setText("Alleati: " + this.alliesList.size());
	    }
	    if (this.enemiesLabel != null) {
	        this.enemiesLabel.setText("Nemici: " + this.enemiesList.size());
	    }
	}




    public void updateBannerMessage(String msg, boolean fullScreen) {
        if (this.banner != null) {
            if (fullScreen) {
                this.banner.showFullScreenMessage(msg, getWidth(), getHeight());
            } else {
                this.banner.showMessage(msg);
            }
        }
    }

    public void colourPositionAvailable(List<Point> availableMoves, String colour) 
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
                Color semiTransparentGray;

                if(colour.equals("red")) {
                    semiTransparentGray = new Color(255, 0, 0, 100);
                }else {
                    semiTransparentGray = new Color(200, 200, 200, 100);
                }

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
	        this.spawnCharacterHelper(spawnList.get(i), positionList);
	    }
	}

	private void spawnCharacterHelper(Character character, List<Point> positionList) 
	{
	    int target = random.nextInt(positionList.size());
	    Point chosenPosition = positionList.remove(target);
	    character.setPosition(chosenPosition);

	    // Aggiorna characterMap solo ora che il personaggio ha posizione
	    this.characterMap.put(character, chosenPosition);

	    JButton button = this.gridPanel.getGridButtons()[chosenPosition.getX()][chosenPosition.getY()];
	    button.setIcon(new ImageIcon(character.getImage()));
	    button.setContentAreaFilled(false);
	    
	    // Aggiungi al personaggio il suo ToolTip
	    this.tooltipManager.showCharacterTooltip(character, button);
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
	    this.characterMap.remove(character, currentPosition);
	    this.characterMap.put(character, target);
	    
	    // Rimuove l'immagine dal bottone corrente
	    JButton currentButton = this.gridPanel.getGridButtons()[currentPosition.getX()][currentPosition.getY()];
	    currentButton.setIcon(null);
	    
	    //this.removeCharacterTooltip(currentButton);
	    // Rimuovi un tooltip di un bottone specifico
	    this.tooltipManager.removeCharacterTooltip(currentButton);
	    
	    // Imposta l'immagine nel nuovo bottone
	    JButton targetButton = this.gridPanel.getGridButtons()[target.getX()][target.getY()];
	    targetButton.setIcon(new ImageIcon(character.getImage()));

	    //this.showCharacterTooltip(character, targetButton);
	    this.tooltipManager.showCharacterTooltip(character, targetButton);


	    System.out.print(character.getClass().getSimpleName() + " spostato con successo da " + character.getPosition() + " a " + target);

	}

	
	//rimuova dalla mappe il personaggio 
	public void removeCharacter(Character character) 
	{
	    this.updateInfoLabels(); // Aggiorna le etichette delle informazioni

		Point target = character.getPosition();
		
	    if (character == null || target == null) {
	        throw new IllegalArgumentException("Character and target point must not be null");
	    }

	    System.out.println("\nTentativo di rimuovere il personaggio: " + character.getClass().getSimpleName() +
	            " dalla posizione " + target + ", esso ha salute: " + character.getCurrentHealth());

	    if (!this.characterMap.containsKey(character)) {
	        System.err.println("Character not found in the map: " + character.getClass().getSimpleName());
	        return;
	    }	    
	   
	    // Rimuove il personaggio dalla mappa
	    this.characterMap.remove(character);
	    this.updateInfoLabels(); // Aggiorna le etichette delle informazioni

	    // Rimuove l'immagine dal bottone
	    JButton targetButton = this.gridPanel.getGridButtons()[target.getX()][target.getY()];
	    targetButton.setIcon(null);
	    
	    /* PERSONAGGIO SI BLOCCA QUA !!!*/
	    System.out.println("Tentativo di rimuovere il tooltip ");
	    this.tooltipManager.removeCharacterTooltip(targetButton);   
	    System.out.println("Tooltip rimosso con successo");

	    
	    System.out.println("Personaggio " + character.getClass().getSimpleName() + 
	            " rimosso con successo dalla posizione " + target);
	    
	    this.updateInfoLabels();
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
}
