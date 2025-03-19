package model.gameStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import controller.StaticCombatManager;
import model.characters.*;
import model.characters.Character;
import model.point.Point;
import view.CharactersMenu;
import view.GraphicsMenu;
import view.LevelMap;
import view.LoadGameMenu;
import view.TutorialMenu;

public class Game 
{   
    private static final int NUM_LEVELS = 5;      // Numero di livelli del gioco
    private static final int NUM_ALLIES = 3;      // Numero di personaggi giocabili per round
    
    private List<Level> levels;                   // Lista dei livelli del gioco  
    private List<Character> allAllies;            // Lista di tutti i personaggi giocabili
    private List<Character> selectedAllies;       // Lista dei personaggi con cui l'utente giocherà il livello
    
    private static final Random rand = new Random();
	
    
    public Game() 
    {
        this.levels              = new ArrayList<>();
        this.allAllies           = new ArrayList<>();
        this.selectedAllies      = new ArrayList<>();
        //initializeLevels();
        GraphicsMenu.startMenu(this); 
    	
    }

    private void initializeLevels() 
    {
    	
        // Popolo la lista di personaggi giocabili
        this.allAllies.add(new Barbarian(new Point(0,0),""));
        this.allAllies.add(new Archer(new Point(0,0),""));

        // Popolo le liste di nemici del tutorial
        List<Character> tutorialEnemies = new ArrayList<>();
        tutorialEnemies.add(new Barbarian(new Point(rand.nextInt(0,6),rand.nextInt(0,3)),""));
        tutorialEnemies.add(new Archer(new Point(rand.nextInt(0,6),rand.nextInt(0,3)),""));

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

        // Agggiungo tutti i livelli e il tutorial alla lista dei livelli
        this.levels.add(new Level(new LevelMap(), tutorialEnemies, this.selectedAllies));
        this.levels.add(new Level(new LevelMap(), level1Enemies, this.selectedAllies));
        this.levels.add(new Level(new LevelMap(), level2Enemies, this.selectedAllies));
        this.levels.add(new Level(new LevelMap(), level3Enemies, this.selectedAllies));
        this.levels.add(new Level(new LevelMap(), level4Enemies, this.selectedAllies));
        this.levels.add(new Level(new LevelMap(), level5Enemies, this.selectedAllies));
    }

    public void startGame() 
    {       
        // Menù iniziale
    	
    	TutorialMenu.startTutorialMenu(); // Apre il menu tutorial
        
    	if(TutorialMenu.tutorialSelected == true) {
            this.levels.get(0).playTutorial(); 

    	}
    	
    	// Scelta dei personaggi con cui l'utente vuole giocare
    	this.selectedAllies = CharactersMenu.selectedAllies(this.allAllies, NUM_ALLIES);

    	// Gioca i livelli principali
        for (int i = 1; i <= Game.NUM_LEVELS; i++) 
        {
        	
            // Gioca il livello
            boolean levelCompleted = levels.get(i).playLevel();

            // Verifica se il livello è stato completato, altrimenti esce dal ciclo
            if (!levelCompleted) 
            {
                System.out.println("Il livello non è stato completato, uscita dal ciclo.");
                break;
            }

            checkAndReplaceDeadAllies();
            System.out.println("Passaggio al livello " + (i + 1));
       } 
       
    }

    // Metodo per sostituire gli alleati morti con nuovi alleati scelti dall'utente
    private void checkAndReplaceDeadAllies() 
    {
        GraphicsMenu.replaceDeadAlliesMenu();
    }
}