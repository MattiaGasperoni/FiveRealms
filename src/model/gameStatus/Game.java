package model.gameStatus;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import model.characters.*;
import model.characters.Character;
import model.characters.bosses.*;
import model.gameStatus.saveSystem.GameStateManager;
import view.CharacterReplaceMenu;
import view.CharacterSelectionMenu;
import view.map.*;
import view.menu.EndGameMenu;
import view.menu.LoadGameMenu;
import view.menu.MainMenu;
import view.menu.TutorialMenu;
import controller.*;

public class Game 
{
    public static final int TOTAL_LEVEL = 5;          // Numero di livelli del gioco
    public static final int MAX_ALLIES_PER_ROUND = 3; // Numero di personaggi giocabili per round

    private List<Level> gameLevels;          // Lista dei livelli del gioco
    private List<Character> availableAllies; // Lista di tutti i personaggi giocabili
    private List<Character> selectedAllies;  // Lista dei personaggi con cui l'utente giocherà il livello

    private boolean waitingForCharacterReplacement = false;

    private GameStateManager gameStateManager;
    private GameController controller;           
    
    private int currentLevelIndex;
    private ScheduledExecutorService gameExecutor;


    // Oggetti Grafici
    private MainMenu mainMenu;
    private TutorialMenu tutorialMenu;
    private CharacterSelectionMenu characterSelectionMenu;
    private CharacterReplaceMenu characterReplaceMenu;
    private EndGameMenu endGameMenu;
	private LoadGameMenu loadGameMenu;
    

    public Game() 
    {
        this.gameLevels      = new ArrayList<>();
        this.availableAllies = new ArrayList<>();
        this.selectedAllies  = new ArrayList<>();
       
        this.currentLevelIndex = 0;
        
        // Inizializzazione Oggetti Grafici
        this.mainMenu               = new MainMenu();
        this.loadGameMenu           = new LoadGameMenu();
        this.tutorialMenu           = new TutorialMenu();
        this.endGameMenu            = new EndGameMenu();
        
        this.characterSelectionMenu = new CharacterSelectionMenu();
        this.characterReplaceMenu   = new CharacterReplaceMenu();
        
        this.gameStateManager       = new GameStateManager();
        
        this.controller = new GameController(this, this.gameStateManager, this.mainMenu, this.loadGameMenu, this.tutorialMenu, this.characterSelectionMenu,this.characterReplaceMenu, this.endGameMenu); 
    }
        
    public void start() 
    {
		this.mainMenu.show();	
	}

    public void startNewGame() throws IOException 
    {
    	this.controller.startNewGame();
    }

    public void startLoadGame(File saveFile) 
    {
        try 
        {
            // Inizializza l'executor se non esiste
            if (this.gameExecutor == null) 
            {
                this.gameExecutor = Executors.newScheduledThreadPool(1);
            }
            
            // Inizializza tutti i livelli
            this.initializeGameLevels();

            // Carichiamo i dati del salvataggio specifico
            this.gameStateManager.loadFileInfo(saveFile);
            
            // Estraiamo i dati usando i getter
            List<Character> allies  = this.gameStateManager.getLoadedAllies();
            List<Character> enemies = this.gameStateManager.getLoadedEnemies();
            int numLevel            = this.gameStateManager.getLoadedLevel();
            
            // Reinizializza i character riottenendo le immagini
            allies  = this.reinitializeCharacters(allies);
            enemies = this.reinitializeCharacters(enemies);	
            
            // Impostiamo i settaggi del livello da cui vogliamo ripartire con quelli caricati
            this.setGameLevelValue(numLevel, allies, enemies);
            
            // Avviamo il livello
            this.startCurrentLevel();

            // Puliamo la cache per liberare memoria
            this.gameStateManager.clearLoadedGameState();

            // Avviamo il game loop
            this.gameExecutor.scheduleAtFixedRate(() -> 
            {
                try 
                {
                    this.updateGameSafe();
                } 
                catch (Exception e) 
                {
                    e.printStackTrace();
                }
            }, 0, 100, TimeUnit.MILLISECONDS);
            
        } 
        catch (IOException | ClassNotFoundException e) 
        {
            System.err.println("Errore nel caricamento del salvataggio: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public boolean startTutorial() 
    {
        // Popolo la lista di nemici del tutorial
        List<Character> tutorialEnemies = new ArrayList<>();
        tutorialEnemies.add(new Barbarian());
        tutorialEnemies.add(new Archer());
        tutorialEnemies.add(new Knight());

        // Popolo la lista di personaggi con cui giocheremo il tutorial
        List<Character> tutorialAllies = new ArrayList<>();
        tutorialAllies.add(new Barbarian());
        tutorialAllies.add(new Archer());
        tutorialAllies.add(new Knight());
        
        for(Character ally : tutorialAllies) 
        {
			ally.becomeHero();
		}

        // Creo e istanzio l'oggetto tutorial
        Tutorial tutorial = new Tutorial(new TutorialMap(tutorialEnemies, tutorialAllies, this.controller),this.controller);

        // Gioco il Tutorial
        return tutorial.play();
    }

    public void startSelectionCharacter() 
    {
        this.controller.startSelectionCharacter();
    }
	
    public void startNewLevel() 
    {
        this.initializeGameLevels(); 
        
        this.startCurrentLevel();

        // Avvia un thread separato per la logica di gioco
        this.gameExecutor = Executors.newSingleThreadScheduledExecutor();
        
        this.gameExecutor.scheduleAtFixedRate(() -> 
        {
            try 
            {
                this.updateGameSafe();
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
    }
    
    public void startCurrentLevel() 
    {
        try 
        {
            Level livello = this.gameLevels.get(this.currentLevelIndex);
            System.out.print("Inizia il livelloooooooooooooooooooo");

            livello.play();
        } 
        catch (IOException e) 
        {
            System.err.println("Errore durante il livello " + this.currentLevelIndex + ": " + e.getMessage());
            e.printStackTrace();
        	this.stopGameLoop();
        }
    }
    
    private void updateGameSafe()
    {
        Level livello = this.gameLevels.get(this.currentLevelIndex);

        livello.update();

        if (livello.isCompleted() && !this.isWaitingForCharacterReplacement())
        {
            System.out.println("Livello " + (this.currentLevelIndex+1) + " completato.");

            if (this.currentLevelIndex >= Game.TOTAL_LEVEL)
            {
                System.out.println("Tutti i livelli completati!");
                this.stopGameLoop();
                this.showEndGameMenu(true);
            }
            else
            {
                if(this.selectedAllies.size() < 3)
                {
                    this.setWaitingForCharacterReplacement(true);
                    this.controller.startReplaceDeadAllies(Game.MAX_ALLIES_PER_ROUND - this.selectedAllies.size());
                }
                else
                {
                    // Imposta le posizioni a null per lo spawn nel livello successivo
                    for (Character character : this.selectedAllies)
                    {
                        character.setPosition(null);
                    }

                    this.currentLevelIndex++;
                    this.startCurrentLevel();
                }
            }
        }
        else if (livello.isFailed())
        {
            System.out.println("Il livello " + this.currentLevelIndex + " è fallito. Uscita.");
            this.stopGameLoop();
            this.showEndGameMenu(false);
        }
    }
    

    public boolean isWaitingForCharacterReplacement() 
    {
        return this.waitingForCharacterReplacement;
    }

    public void setWaitingForCharacterReplacement(boolean waiting) {
        this.waitingForCharacterReplacement = waiting;
    }

    public void markCharacterReplacementCompleted()
    {
        this.waitingForCharacterReplacement = false;
        
        // Imposta le posizioni a null per lo spawn nel livello successivo
        for (Character character : this.selectedAllies)
        {
            character.setPosition(null);
        }
        
        this.currentLevelIndex++;
        System.out.print("Incremento il livello");

        this.startCurrentLevel();
    }
    
    private void showEndGameMenu(boolean result) 
    {
    	System.out.print(result);
        this.endGameMenu.setGameResult(result);
    	// Appare il menu per la selezione dei personaggi
        this.endGameMenu.show();
        
    }
    
    private void stopGameLoop() 
    {
        if (this.gameExecutor != null && !this.gameExecutor.isShutdown()) 
        {
        	this.gameExecutor.shutdownNow();
        }
    }

    public List<Character> createAllies() 
    {
        // Popolo la lista di personaggi giocabili
        this.availableAllies.add(new Barbarian());
        this.availableAllies.add(new Archer());
        this.availableAllies.add(new Knight());
        this.availableAllies.add(new Wizard());
        this.availableAllies.add(new Juggernaut());
                
        return this.availableAllies;
    }
    
    public void setSelectedCharacters(List<Character> selectedAllies) 
    {
		this.selectedAllies = selectedAllies;
        
        for(Character ally : this.selectedAllies) 
        {
   			ally.becomeHero();
   		}
	}
    
    public void addSelectedCharacters(List<Character> selectedAllies) 
    {
    	for(Character character : selectedAllies)
    	{
    		this.selectedAllies.add(character);
    		character.becomeHero();
    	}
	}
    
    private void initializeGameLevels()
    {
        // Popolo le liste dei nemici e dei livelli principali
    	
        List<Character> level1Enemies = new ArrayList<>();
        level1Enemies.add(new KnightBoss());
        level1Enemies.add(new Knight());
        level1Enemies.add(new Barbarian());

        List<Character> level2Enemies = new ArrayList<>();
        level2Enemies.add(new BarbarianBoss());
        level2Enemies.add(new Barbarian());
        level2Enemies.add(new Barbarian());
        level2Enemies.add(new Archer());

        List<Character> level3Enemies = new ArrayList<>();
        level3Enemies.add(new ArcherBoss());
        level3Enemies.add(new Archer());
        level3Enemies.add(new Archer());
        level3Enemies.add(new Juggernaut());

        List<Character> level4Enemies = new ArrayList<>();
        level4Enemies.add(new JuggernautBoss());
        level4Enemies.add(new Barbarian());
        level4Enemies.add(new Wizard());
        level4Enemies.add(new Knight());
        level4Enemies.add(new Juggernaut());

        List<Character> level5Enemies = new ArrayList<>();
        level5Enemies.add(new WizardBoss());
        level5Enemies.add(new JuggernautBoss());
        level5Enemies.add(new KnightBoss());
        level5Enemies.add(new BarbarianBoss());
        level5Enemies.add(new ArcherBoss());

        
        // TODO
        // PROBLEM : selectedAllies viene aggiornata o tengono quella fornita originalmente?

		// Inizializzo i livelli        
        this.gameLevels.add(new Level(new LevelMap(level1Enemies, this.selectedAllies, 1, this.controller), this.controller));
        this.gameLevels.add(new Level(new LevelMap(level2Enemies, this.selectedAllies, 2, this.controller), this.controller));
        this.gameLevels.add(new Level(new LevelMap(level3Enemies, this.selectedAllies, 3, this.controller), this.controller));
        this.gameLevels.add(new Level(new LevelMap(level4Enemies, this.selectedAllies, 4, this.controller), this.controller));
        this.gameLevels.add(new Level(new LevelMap(level5Enemies, this.selectedAllies, 5, this.controller), this.controller));
    }

    public int getCurrentLevelIndex() {
		return this.currentLevelIndex;
	}
    
	public void setCurrentLevelIndex(int currentLevelIndex) {
		this.currentLevelIndex = currentLevelIndex;
	}

	public List<Level> getGameLevels() {
		return this.gameLevels;
	}   
	
	public List<Character> getSelectedAllies() {
		return this.selectedAllies;
	}

	private void setGameLevelValue(int index, List<Character> allies, List<Character> enemy)
	{
		this.currentLevelIndex = index;
		
		// Imposta i nemici caricati solo per il livello index
		this.gameLevels.get(index).setEnemiesList(enemy);
		
	    // Imposta gli alleati per tutti i livelli
		for (Level level : this.gameLevels)
		{
		    level.setAlliesList(allies);
		}
		
		this.selectedAllies = allies;
	}
	
	/**
	 * Re-inizializza una lista di personaggi dopo il caricamento,
	 * ricaricando immagini e altri dati non serializzabili.
	 */
	private List<Character> reinitializeCharacters(List<Character> characters) 
	{
	    List<Character> reinitializedCharacters = new ArrayList<>();
	    
	    for (Character character : characters) 
	    {
	        try 
	        {
	            // Controlla se il personaggio e' valido
	            if (!this.isCharacterValid(character)) 
	            {
	            	// Se non lo e' lo salta
	                continue; 
	            }
	            
	            // Re-inizializza il personaggio (ricarica immagini, icone)
	            character.reinitializeAfterLoad();
	            
	            reinitializedCharacters.add(character);
	        } 
	        catch (Exception e) 
	        {
	            System.err.println("Errore nella re-inizializzazione del personaggio: " + e.getMessage());
	        }
	    }
	    
	    return reinitializedCharacters;
	}
	
	private boolean isCharacterValid(Character character) 
	{
	    if (character == null) {
	        return false;
	    }
	    
	    // Controlla se ha posizione valida
	    if (character.getPosition() == null) 
	    {
	        System.out.println("Character has null position: " + character.getClass().getSimpleName());
	        return false;
	    }
	    
	    // Controlla se è ancora vivo
	    if (character.getCurrentHealth() <= 0) 
	    {
	        System.out.println("Character is dead: " + character.getClass().getSimpleName());
	        return false;
	    }
	    System.out.println("Validooo");
	    return true;
	}
	
	public void closeAll()
	{
		this.gameLevels.get(this.currentLevelIndex).getLevelMap().closeWindow();
	}
}