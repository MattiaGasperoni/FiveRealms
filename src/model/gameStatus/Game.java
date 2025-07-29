package model.gameStatus;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import model.characters.*;
import model.characters.Character;
import model.characters.bosses.*;
import model.gameStatus.saveSystem.GameStateManager;
import view.*;
import view.map.*;
import view.map.AbstractMap;
import controller.*;

import javax.swing.SwingUtilities;
import javax.swing.Timer;


public class Game 
{
    public static final int TOTAL_LEVEL = 5;          // Numero di livelli del gioco
    public static final int MAX_ALLIES_PER_ROUND = 3; // Numero di personaggi giocabili per round

    private List<Level> gameLevels;          // Lista dei livelli del gioco
    private List<Character> availableAllies; // Lista di tutti i personaggi giocabili
    private List<Character> selectedAllies;  // Lista dei personaggi con cui l'utente giocherà il livello

    private GameStateManager gameStateManager;
    private GameController controller;           
    
    private Timer gameTimer;
    private int currentLevelIndex;
    private ScheduledExecutorService gameExecutor;


    // Oggetti Grafici
    private MainMenu graphicsMenu;
    private TutorialMenu tutorialMenu;
    private CharacterSelectionMenu characterSelectionMenu; // Menu per la scelta dei personaggi

    public Game() 
    {
        this.gameLevels = new ArrayList<>();
        this.availableAllies = new ArrayList<>();
        this.selectedAllies = new ArrayList<>();
       
        this.currentLevelIndex = 0;
        
        // Inizializzazione Oggetti Grafici
        this.graphicsMenu = new MainMenu();
        this.tutorialMenu = new TutorialMenu();
        this.characterSelectionMenu = new CharacterSelectionMenu();

        this.gameStateManager = new GameStateManager();
        
        this.controller = new GameController(this, this.gameStateManager, this.graphicsMenu, this.tutorialMenu, this.characterSelectionMenu); 
        
    }
    

    
    public void start() 
    {
		// Mostro il menù principale
		this.graphicsMenu.show();	
	}

    public void startNewGame() throws IOException 
    {
    	this.controller.startNewGame();
    }

    public boolean startTutorial() 
    {
        // Il tutorial prevede un gameplay statico, sempre uguale percio' non
        // permettiamo la scelta dei personaggi

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
        
        for(Character ally : tutorialAllies) {
			ally.becomeHero();
		}

        // Creo e istanzio l'oggetto tutorial
        Tutorial tutorial = new Tutorial(new TutorialMap(tutorialEnemies, tutorialAllies, this.controller), this.controller);

        // Gioco il Tutorial
        return tutorial.play();
    }

    public void startSelectionCharacter() 
    {
        // Appare il menu per la selezione dei personaggi
        this.controller.startSelectionCharacter();
    }
	
    public void startLevel() 
    {
    	
        this.initializeGameLevels(); 
        this.startCurrentLevel();

        // Avvia un thread separato per la logica di gioco
        this.gameExecutor = Executors.newSingleThreadScheduledExecutor();
        
        this.gameExecutor.scheduleAtFixedRate(() -> 
        {
            try 
            {
                this.updateGameSafe(); // logica separata
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 100, TimeUnit.MILLISECONDS); // ogni 100 ms
    }
    
    private void startCurrentLevel() 
    {
        try 
        {
            Level livello = this.gameLevels.get(currentLevelIndex);
            livello.play(); // inizializza nemici, fasi ecc.
        } 
        catch (IOException e) 
        {
            System.err.println("Errore durante il livello " + currentLevelIndex + ": " + e.getMessage());
            e.printStackTrace();
            stopGameLoop();
        }
    }
    
    private void updateGameSafe() 
    {
        Level livello = gameLevels.get(currentLevelIndex);
        livello.update(); // esegue logica

        if (livello.isCompleted()) 
        {
        	System.out.println("Livello " + (currentLevelIndex+1) + " completato.");
            //checkAndReplaceDeadAllies(this.selectedAllies);

            currentLevelIndex++;
            if (currentLevelIndex >= Game.TOTAL_LEVEL) 
            {           	
                System.out.println("Tutti i livelli completati!");
                
                stopGameLoop();
            } 
            else 
            {
            	// Avvia un timer che attende 5 secondi (5000 ms) prima di avviare il livello
            	/*Timer delayTimer = new Timer(5000, e -> {
            	    System.out.println("Sono passati 5 secondi!");
            	    
            	});
            	delayTimer.setRepeats(false);
            	delayTimer.start();*/
            	startCurrentLevel();
            	
            }
        } 
        else if (livello.isFailed()) 
        {
            SwingUtilities.invokeLater(() -> {
                System.out.println("Il livello " + currentLevelIndex + " è fallito. Uscita.");
                stopGameLoop();
            });
        }
    }

    private void stopGameLoop() 
    {
        if (gameExecutor != null && !gameExecutor.isShutdown()) 
        {
            gameExecutor.shutdownNow();
        }
    }


    
    private void checkAndReplaceDeadAllies(List<Character> selectedAllies) 
	{
    	/*
		// Implementa la logica per controllare e sostituire gli alleati morti
		// Ad esempio, puoi rimuovere gli alleati morti dalla lista e aggiungere nuovi alleati
		// ...
    	
        // Calcola quanti alleati sono morti
        int alliesToChange = Game.MAX_ALLIES_PER_ROUND - selectedAllies.size();

        if (alliesToChange > 0) {
            System.out.println("Sostituzione di " + alliesToChange + " personaggi morti.");
        }
            // Seleziona nuovi alleati
            // List<Character> newAllies =
            // CharacterSelectionMenu.replaceDeadAlliesMenu(this.availableAllies ,
            // alliesToChange);

            // Aggiungi i nuovi alleati
            // this.selectedAllies.addAll(newAllies);*/
        this.controller.startSelectionCharacter();
    	
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
    
    private void initializeGameLevels() {
        // Popolo le liste di nemici dei livelli principali
        List<Character> level1Enemies = new ArrayList<>();
        //level1Enemies.add(new KnightBoss()); //KnightBoss
        level1Enemies.add(new Knight());
        //level1Enemies.add(new Knight());
        level1Enemies.add(new Barbarian());

        List<Character> level2Enemies = new ArrayList<>();
        level2Enemies.add(new BarbarianBoss());
        level2Enemies.add(new Barbarian());
        level2Enemies.add(new Knight());
        level2Enemies.add(new Barbarian());
        level2Enemies.add(new Archer());

        List<Character> level3Enemies = new ArrayList<>();
        level3Enemies.add(new ArcherBoss());
        level3Enemies.add(new Archer());
        level3Enemies.add(new Knight());
        level3Enemies.add(new Archer());
        level3Enemies.add(new Juggernaut());

        List<Character> level4Enemies = new ArrayList<>();
        level4Enemies.add(new JuggernautBoss());
        level4Enemies.add(new Barbarian());
        level4Enemies.add(new Archer());
        level4Enemies.add(new Wizard());
        level4Enemies.add(new Knight());
        level4Enemies.add(new Juggernaut());

        List<Character> level5Enemies = new ArrayList<>();
        level5Enemies.add(new WizardBoss());
        level5Enemies.add(new Wizard());
        level5Enemies.add(new Wizard());
        level5Enemies.add(new Wizard());
        level5Enemies.add(new Archer());
        level5Enemies.add(new Archer());
        level5Enemies.add(new Juggernaut());

        // Agggiungo tutti i livelli alla lista dei
        // PROBLEM : se qualche personaggio mi muore poi nei livelli prima poi la lista
        // dei selectedAllies viene aggiornata o tengono quella fornita originalmente
        // PROBLEM : LevelMap non funziona e quando chiamo il metodo
        // initializeGameLevels mi crea gia le istanze delle finestre
        // per capire in che livello siamo a leveMap gli passo un numeroche lo
        // rappresenta.
        
		// Inizializzo i livelli        
        this.gameLevels.add(new Level(new LevelMap(level1Enemies, this.selectedAllies, 1, this.controller), this.controller));
        this.gameLevels.add(new Level(new LevelMap(level2Enemies, this.selectedAllies, 2, this.controller), this.controller));
        this.gameLevels.add(new Level(new LevelMap(level3Enemies, this.selectedAllies, 3, this.controller), this.controller));
        this.gameLevels.add(new Level(new LevelMap(level4Enemies, this.selectedAllies, 4, this.controller), this.controller));
        this.gameLevels.add(new Level(new LevelMap(level5Enemies, this.selectedAllies, 5, this.controller), this.controller));
    }


}