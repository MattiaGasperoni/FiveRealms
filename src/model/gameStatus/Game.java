package model.gameStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import controller.StaticCombatManager;
import model.characters.*;
import model.characters.Character;
import model.point.Point;
import view.GraphicsMenu;
import view.LevelMap;

public class Game 
{   
    private static final int NUMLEVELS = 5;     // Numero di livelli del gioco
    private static final int NUMALLIES = 3;     // Numero di personaggi giocabili per round
    
    private List<Level> levelList;              // Lista dei livelli del gioco  
    private List<Character> alliesList; 		// Lista dei personaggi giocabili 
    
    private EnemyManager enemyManager;  

    private boolean tutorialCompleted;           // Flag che indica se il tutorial è stato completato
    private static final Random rand = new Random();
	
    
    public Game() 
    {
        this.levelList         = new ArrayList<>();
        this.alliesList        = new ArrayList<>();
        this.enemyManager      = new EnemyManager();
        this.tutorialCompleted = false;
        initializeLevels();
        startGame();
    }

    private void initializeLevels() 
    {
        // Inizializziamo la lista dei livelli con il tutorial e i livelli principali

        // Creo e aggiungo il tutorial
        List<Character> tutorialEnemies = new ArrayList<>();
        tutorialEnemies.add(new Barbarian(new Point(rand.nextInt(0,6),rand.nextInt(0,3)),""));
        tutorialEnemies.add(new Archer(new Point(rand.nextInt(0,6),rand.nextInt(0,3)),""));

        // Creo i livelli principali 
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

        //aggiungo tutti i livelli alla lista
        this.levelList.add(new Level(new LevelMap(), tutorialEnemies, this.alliesList));
        this.levelList.add(new Level(new LevelMap(), level1Enemies, this.alliesList));
        this.levelList.add(new Level(new LevelMap(), level2Enemies, this.alliesList));
        this.levelList.add(new Level(new LevelMap(), level3Enemies, this.alliesList));
        this.levelList.add(new Level(new LevelMap(), level4Enemies, this.alliesList));
        this.levelList.add(new Level(new LevelMap(), level5Enemies, this.alliesList));
    }

    public List<Level> getLevelList() {
        return this.levelList;
    }

    public void startGame() {
        // Da fare il controllo se hai cliccato il bottone per saltare il tutorial
        
        // Menù iniziale
        //GraphicsMenu.startMenu();
        
        if (!tutorialCompleted) {
            // Gioca il tutorial
            this.tutorialCompleted = levelList.get(0).playLevel(enemyManager);
        }

        for (int i = 1; i < levelList.size(); i++) {
            // Verifica se il livello è stato completato, altrimenti esce dal ciclo
            boolean levelCompleted = levelList.get(i).playLevel(enemyManager);
            if (!levelCompleted) {
                System.out.println("Il livello non è stato completato, uscita dal ciclo.");
                break;  // Esce dal ciclo se il livello non è completato
            }
            checkAndReplaceDeadAllies();
        }
    }


    private void checkAndReplaceDeadAllies() 
    {
		// Rimuovi alleati morti
		this.alliesList.removeIf(ally -> !ally.isAlive());
		
		// Aggiungi nuovi alleati se necessario
		while (this.alliesList.size() < NUMALLIES) 
		{
			this.alliesList.add(new Archer(new Point(rand.nextInt(0,6),rand.nextInt(0,3)),"")); // Aggiungi un nuovo alleato scelto dall'utente
		}
    }

    public void skipTutorial() {
        this.tutorialCompleted = true;
    }
}