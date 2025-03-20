package model.gameStatus;

import java.util.*;

import model.characters.*;
import model.characters.Character;
import model.point.Point;

import view.*;
import controller.*;

public class Game 
{   
    private static final int NUM_LEVELS = 5;      // Numero di livelli del gioco
    private static final int NUM_ALLIES = 3;      // Numero di personaggi giocabili per round
    
    private List<Level> levels;                   // Lista dei livelli del gioco  
    private List<Character> allAllies;            // Lista di tutti i personaggi giocabili
    private List<Character> selectedAllies;       // Lista dei personaggi con cui l'utente giocherà il livello
    
    private GameStateManager manager;             // Gestore dello stato del gioco
    
    private static final Random rand = new Random();
	
    
    public Game() 
    {
        this.levels              = new ArrayList<>();
        this.allAllies           = new ArrayList<>();
        this.selectedAllies      = new ArrayList<>();

        this.manager = new GameStateManager();

        // Avvio il menù grafico
        GraphicsMenu.startMenu(this); 
    }

    private void createAllies()
    {
        // Popolo la lista di personaggi giocabili
        this.allAllies.add(new Barbarian(new Point(0,0),""));
        this.allAllies.add(new Archer(new Point(0,0),""));
        //... Aggiungere altri personaggi
    }

    private void initializeTutorial()
    {
        // Il tutorial prevede un gameplay statico, sempre uguale percio' non permettiamo la scelta dei personaggi

        // Popolo la lista di nemici del tutorial
        List<Character> tutorialEnemies = new ArrayList<>();
        tutorialEnemies.add(new Barbarian(new Point(rand.nextInt(0,6),rand.nextInt(0,3)),""));
        tutorialEnemies.add(new Archer(new Point(rand.nextInt(0,6),rand.nextInt(0,3)),""));

        List<Character> tutorialAllies = new ArrayList<>();
        tutorialAllies.add(new Barbarian(new Point(rand.nextInt(0,6),rand.nextInt(0,3)),""));
        tutorialAllies.add(new Archer(new Point(rand.nextInt(0,6),rand.nextInt(0,3)),""));

        // Aggiungo il tutorial alla lista dei livelli
        this.levels.add(new Level(new LevelMap(), tutorialEnemies, tutorialAllies));
    }

    private void initializeLevels() 
    {
        // Popolo le liste di nemici dei livelli principali 
        List<Character> level1Enemies = new ArrayList<>();
        level1Enemies.add(new Barbarian(new Point(rand.nextInt(0,6),rand.nextInt(0,3)),""));
        level1Enemies.add(new Archer(new Point(rand.nextInt(0,6),rand.nextInt(0,3)),""));

        List<Character> level2Enemies = new ArrayList<>();
        level2Enemies.add(new Barbarian(new Point(rand.nextInt(0,6),rand.nextInt(0,3)),""));
        level2Enemies.add(new Archer(new Point(rand.nextInt(0,6),rand.nextInt(0,3)),""));

        List<Character> level3Enemies = new ArrayList<>();
        level3Enemies.add(new Barbarian(new Point(rand.nextInt(0,6),rand.nextInt(0,3)),""));
        level3Enemies.add(new Archer(new Point(rand.nextInt(0,6),rand.nextInt(0,3)),""));

        List<Character> level4Enemies = new ArrayList<>();
        level4Enemies.add(new Barbarian(new Point(rand.nextInt(0,6),rand.nextInt(0,3)),""));
        level4Enemies.add(new Archer(new Point(rand.nextInt(0,6),rand.nextInt(0,3)),""));

        List<Character> level5Enemies = new ArrayList<>();
        level5Enemies.add(new Barbarian(new Point(rand.nextInt(0,6),rand.nextInt(0,3)),""));
        level5Enemies.add(new Archer(new Point(rand.nextInt(0,6),rand.nextInt(0,3)),""));

        // Agggiungo tutti i livelli alla lista dei 
        // PROBLEM :  se qualche personaggio mi muore poi nei livelli la lista viene aggiornata o tengono questa originale
        this.levels.add(new Level(new LevelMap(), level1Enemies, this.selectedAllies));
        this.levels.add(new Level(new LevelMap(), level2Enemies, this.selectedAllies));
        this.levels.add(new Level(new LevelMap(), level3Enemies, this.selectedAllies));
        this.levels.add(new Level(new LevelMap(), level4Enemies, this.selectedAllies));
        this.levels.add(new Level(new LevelMap(), level5Enemies, this.selectedAllies));
    }

    public void startGame() 
    {       
        // Tutorial Menu 
    	TutorialMenu.startTutorialMenu(); 
        
        // Scegli se saltare il tutorial o giocarlo 
    	if(TutorialMenu.tutorialSelected) 
        {
            this.initializeTutorial();
            this.levels.get(0).playTutorial();
    	}
    	
    	// Inizio del gioco effettivo

        // Crea tutti i personaggi giocabili
        this.createAllies();

        // Scegli graficamente i 3 personaggi con cui giocare
    	this.selectedAllies = CharactersMenu.selectedAllies(this.allAllies, NUM_ALLIES);
      
        // Inizializzo le liste dei nemici e dei livelli
        this.initializeLevels();

    	// Gioca i livelli
        for (int i = 1; i <= Game.NUM_LEVELS; i++) 
        {
            // Gioca il livello, playLevel ritorna true se il livello è stato completato
            boolean levelCompleted = this.levels.get(i).playLevel(manager, i);

            // Se il livello non è stato completato, esce dal ciclo
            if (!levelCompleted) 
            {
                System.out.println("Il livello non è stato completato, uscita dal ciclo.");
                break;
            }

            this.checkAndReplaceDeadAllies();
            System.out.println("Passaggio al livello " + (i + 1));
            


       } 
    }

    // Metodo per sostituire gli alleati morti con nuovi alleati scelti dall'utente
    private void checkAndReplaceDeadAllies() 
    {
        // Calcola quanti alleati mancano
        int alliesToChange = NUM_ALLIES - this.selectedAllies.size();
    
        if (alliesToChange > 0) 
        {
            System.out.println("Sostituzione di " + alliesToChange + " personaggi morti.");
    
            // Seleziona nuovi alleati
            List<Character> newAllies = GraphicsMenu.replaceDeadAlliesMenu(this.allAllies, alliesToChange);
    
            // Aggiungi i nuovi alleati
            this.selectedAllies.addAll(newAllies);
        }
    }    
}