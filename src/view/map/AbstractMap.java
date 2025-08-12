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
    
    private JLabel levelLabel;
    private JLabel enemiesLabel;
	private PauseMenu pauseMenu;
	private GameController controller;


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
        
        this.characterMap = new HashMap<>();
        this.alliesPositionList = new ArrayList<>();
        this.enemiesPositionList = new ArrayList<>();
        this.random = new Random();
        this.initializePositionList();
        
        this.tooltipManager = new CharacterTooltipManager();
        this.controller = controller;
        

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
    	
        this.pauseMenu = new PauseMenu(this.getFrame(), this.getLayeredPanel());
        this.controller.setPauseMenu(this.pauseMenu);
        
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
               
        // layer 3. Etichette di informazioni sul gioco
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
        this.banner = new BannerPanel(this.getWidth(), this.getHeight());

        this.layeredPanel.add(this.banner, Integer.valueOf(2));

        this.layeredPanel.revalidate();
        this.layeredPanel.repaint();
    }
    
    private void addGameInfoLabels() 
    {
        int paddingRight = 10;
        int paddingTop = 10;
        int labelWidth = 220;  // più piccolo
        int labelHeight = 30;  // più piccolo

        if (this.numLevel == 0) 
        {
            this.levelLabel = createStyledLabel("🎯 Tutorial");
        } 
        else 
        {
            this.levelLabel = createStyledLabel("🎯 Level: " + this.numLevel);
        }
        this.enemiesLabel = createStyledLabel("⚔️ Enemies Remaining: " + this.enemiesList.size());
        
        // Posizioniamo a destra, quindi x = larghezza panel - larghezza label - paddingRight
        int xPos = layeredPanel.getWidth() - labelWidth - paddingRight;

        this.levelLabel.setBounds(100, paddingTop, labelWidth, labelHeight);
        this.enemiesLabel.setBounds(xPos-60, paddingTop, labelWidth, labelHeight);

        this.layeredPanel.add(levelLabel, Integer.valueOf(4));
        this.layeredPanel.add(enemiesLabel, Integer.valueOf(4));
    }



    
    private JLabel createStyledLabel(String text) 
    {
        JLabel label = new JLabel(text);
        
        // Font medievale/fantasy
        label.setFont(new Font("Serif", Font.BOLD, 16));
        label.setForeground(new Color(245, 230, 200)); // Beige chiaro come nel tuo menu
        
        // Sfondo marrone/arancione come i tuoi bottoni
        label.setBackground(new Color(139, 69, 19)); // Marrone scuro
        label.setOpaque(true);
        
        // Bordo che simula quello dei tuoi bottoni
        label.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(), // Effetto rilievo
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(101, 67, 33), 2), // Bordo marrone scuro
                BorderFactory.createEmptyBorder(4, 8, 4, 8) // Padding interno
            )
        ));
        
        label.setHorizontalAlignment(SwingConstants.CENTER);
        
        return label;
    }
	
	private void updateInfoLabels() 
	{
	    if (this.levelLabel != null) {
	        this.levelLabel.setText("🎯 Level: " + this.numLevel);
	    }

	    if (this.enemiesLabel != null) {
	        this.enemiesLabel.setText("⚔️ Enemies Remaining: " + this.enemiesList.size());
	    }
	}

    /*Modifiche effettuate per il banner*/
    public int getWidth() {
    	return this.layeredPanel.getWidth();
    }

	public int getHeight() {
		return this.layeredPanel.getHeight();
	}


    public void updateBannerMessage(String msg, boolean fullScreen) 
    {
        if (this.banner != null) 
        {
            if (fullScreen) 
            {
                this.banner.showFullScreenMessage();
            } 
            else 
            {
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
                	semiTransparentGray = new Color(80, 80, 80, 160); //Abbassare l ultimo valore per farlo piu trasparente
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
        {
        	this.frame.dispose();
        	this.removeAllEvent();
        }
        
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
	    Point chosenPosition = character.getPosition();

	    if (chosenPosition == null) 
	    {
	        // Se la posizione non è già impostata, scegli una casuale dalla lista
	        int target = random.nextInt(positionList.size());
	        chosenPosition = positionList.remove(target);
	        character.setPosition(chosenPosition);
	    }
	    else
	    {
	        // Se la posizione è già impostata, rimuovila dalla lista solo se presente
	        if (positionList.contains(chosenPosition)) 
		{
	            positionList.remove(chosenPosition);
	        }
	    }
		
	    // Aggiorna characterMap
	    this.characterMap.put(character, chosenPosition);
	
	    JButton button = this.gridPanel.getGridButtons()[chosenPosition.getX()][chosenPosition.getY()];
	    button.setIcon(new ImageIcon(character.getImage()));
	    button.setContentAreaFilled(false);
	
	    // Aggiungi al personaggio il ToolTip
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

	    if (target == null) 
	    {
	        throw new IllegalArgumentException("Target point must not be null");
	    }

	    if (!this.characterMap.containsKey(character)) 
	    {
	        System.err.println("Character not found in the map: " + character.getClass().getSimpleName());
	        return;
	    }	    
	    
	    if (character.getPosition().equals(target)) 
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
	    this.removeCharacter(character);
	    this.characterMap.put(character, target);
	    
	    // Imposta l'immagine nel nuovo bottone
	    JButton targetButton = this.gridPanel.getGridButtons()[target.getX()][target.getY()];
	    targetButton.setIcon(new ImageIcon(character.getImage()));

	    this.updateToolTip();	    

	    System.out.print(character.getClass().getSimpleName() + " spostato con successo da " + character.getPosition() + " a " + target);	    
	}

	
	//rimuova dalla mappe il personaggio 
	public void removeCharacter(Character character) 
	{
		Point target = character.getPosition();

	    if (character == null || target == null) {
	        throw new IllegalArgumentException("Character and/or target point must not be null");
	    }
	    
	    System.out.println("\nTentativo di rimuovere il personaggio: " + character.getClass().getSimpleName() +
	            " dalla posizione " + target + ", esso ha salute: " + character.getCurrentHealth());

	    if (!this.characterMap.containsKey(character)) {
	        System.err.println("Character not found in the map: " + character.getClass().getSimpleName());
	        return;
	    }	    
	   
	    // Rimuove il personaggio dalla mappa
	    this.characterMap.remove(character);

	    // Rimuove l'immagine dal bottone
	    JButton targetButton = this.gridPanel.getGridButtons()[target.getX()][target.getY()];
	    targetButton.setIcon(null);
	    
	    this.tooltipManager.removeCharacterTooltip(targetButton);   
	    
	    this.updateToolTip();	    
	    
	    System.out.println("Personaggio " + character.getClass().getSimpleName() + 
	            " rimosso con successo dalla posizione " + target);
	    
	    this.updateInfoLabels();
	}
	
	public void updateToolTip()
	{
		// Per un aggiornamnto dei tooltip costante
	    for(Entry<Character, Point> entry : this.characterMap.entrySet())
	    {
	    	this.tooltipManager.removeCharacterTooltip(this.gridPanel.getGridButtons()[entry.getValue().getX()][entry.getValue().getY()]);
	    }
	    
	    for(Entry<Character, Point> entry : this.characterMap.entrySet())
	    {
		    this.tooltipManager.showCharacterTooltip(entry.getKey(), this.gridPanel.getGridButtons()[entry.getValue().getX()][entry.getValue().getY()]);
	    }
	}
	
	
	public void removeAllEvent() 
	{
	    JButton[][] buttonGrid = this.gridPanel.getGridButtons();

	    for (JButton[] row : buttonGrid)
	    {
	        for (JButton button : row) 
	        {
	            if (button == null) 
	            {
	                continue; // salta eventuali celle vuote
	            }

	            // Rimuove ActionListener se presenti
	            ActionListener[] als = button.getActionListeners();
	            if (als != null && als.length > 0) 
	            {
	                for (ActionListener al : als) 
	                {
	                    button.removeActionListener(al);
	                }
	            }

	            // Rimuove MouseListener se presenti
	            MouseListener[] mls = button.getMouseListeners();
	            if (mls != null && mls.length > 0) 
	            {
	                for (MouseListener ml : mls) 
	                {
	                    button.removeMouseListener(ml);
	                }
	            }

	            // Rimuove MouseMotionListener se presenti
	            MouseMotionListener[] mmls = button.getMouseMotionListeners();
	            if (mmls != null && mmls.length > 0) 
	            {
	                for (MouseMotionListener mml : mmls) 
	                {
	                    button.removeMouseMotionListener(mml);
	                }
	            }

	            // Rimuove tooltip solo se esiste
	            if (button.getToolTipText() != null) 
	            {
	            	this.tooltipManager.removeCharacterTooltip(button);
	            }
	        }
	    }
	    
	    this.tooltipManager.removeAllTooltips(); // Pulisce tutti i tooltip gestiti
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
	
	public JFrame getFrame() {
		return this.frame;
	}

	public JLayeredPane getLayeredPanel() {
		return this.layeredPanel;
	}
	
	public void setEnemiesList(List<Character> enemiesList) {
		this.enemiesList = enemiesList;
	}


	public void setAlliesList(List<Character> alliesList) {
		this.alliesList = alliesList;
	}

	// Metodo che ti dice se è occupata gia la posizione di un personaggio
	public boolean isPositionOccupied(Point point) 
	{
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

