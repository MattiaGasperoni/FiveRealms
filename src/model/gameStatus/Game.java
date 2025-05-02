package model.gameStatus;

import java.io.IOException;
import java.util.*;
import model.characters.*;
import model.characters.Character;
import model.characters.bosses.*;
import model.gameStatus.saveSystem.GameStateManager;
import view.*;
import view.map.*;
import controller.*;
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
        Tutorial tutorial = new Tutorial(new TutorialMap(tutorialEnemies, tutorialAllies), this.controller);

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
    	this.initializeGameLevels(); // Inizializza i livelli di gioco
    	
        // Inizializza il timer con delay (es. ogni 100ms)
        this.gameTimer = new Timer(100, e -> updateGame());

        // Avvia il primo livello
        this.startCurrentLevel();

        this.gameTimer.start(); // parte il loop

    }
    
    private void startCurrentLevel() 
    {
        try 
        {
            Level livello = gameLevels.get(currentLevelIndex);
            livello.start(currentLevelIndex); // nuovo metodo al posto di play sincrono
        } 
        catch (IOException e) 
        {
            System.err.println("Errore durante il livello " + currentLevelIndex + ": " + e.getMessage());
            e.printStackTrace();
            gameTimer.stop();
        }
    }
    
    private void updateGame() 
    {
        Level livello = gameLevels.get(currentLevelIndex);
        livello.update();

        if (livello.isCompleted()) {
            System.out.println("Livello " + currentLevelIndex + " completato.");
            checkAndReplaceDeadAllies(this.selectedAllies);

            currentLevelIndex++;

            if (currentLevelIndex >= Game.TOTAL_LEVEL) {
                System.out.println("Tutti i livelli completati!");
                gameTimer.stop();
            } else {
                startCurrentLevel();
            }
        } else if (livello.isFailed()) {
            System.out.println("Il livello " + currentLevelIndex + " è fallito. Uscita.");
            gameTimer.stop();
        }
    }


    
    private void checkAndReplaceDeadAllies(List<Character> selectedAllies) 
	{
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
            // this.selectedAllies.addAll(newAllies);
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
    
    public void setSelectedCharacters(List<Character> selectedAllies) {
		this.selectedAllies = selectedAllies;
        
        for(Character ally : this.selectedAllies) {
   			ally.becomeHero();
   		}

        
        
	}


    private void initializeGameLevels() {
        // Popolo le liste di nemici dei livelli principali
        List<Character> level1Enemies = new ArrayList<>();
        level1Enemies.add(new KnightBoss());
        level1Enemies.add(new Knight());
        level1Enemies.add(new Knight());
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
        this.gameLevels.add(new Level(new LevelMap(level1Enemies, this.selectedAllies, 1), this.controller));
        this.gameLevels.add(new Level(new LevelMap(level2Enemies, this.selectedAllies, 2), this.controller));
        this.gameLevels.add(new Level(new LevelMap(level3Enemies, this.selectedAllies, 3), this.controller));
        this.gameLevels.add(new Level(new LevelMap(level4Enemies, this.selectedAllies, 4), this.controller));
        this.gameLevels.add(new Level(new LevelMap(level5Enemies, this.selectedAllies, 5), this.controller));
    }


}