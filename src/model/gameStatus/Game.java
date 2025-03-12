package model.gameStatus;

import java.util.ArrayList;
import java.util.List;

import controller.CombatManager;
import model.characters.AbstractCharacter;
import model.characters.Archer;
import model.characters.Barbarian;
import view.LevelMap;

public class Game 
{   
    private static final int NUMLEVELS = 5;
    private static final int NUMALLIES = 3;
    private List<Level> levelList;    
    private List<AbstractCharacter> alliesList;
    
    private EnemyManager enemyManager;
    private CombatManager combatManager;

    private boolean tutorialCompleted;

    public Game() 
    {
        this.levelList         = new ArrayList<>();
        this.alliesList        = new ArrayList<>();
        this.enemyManager      = new EnemyManager();
        this.combatManager     = new CombatManager();
        this.tutorialCompleted = false;
        initializeLevels();
        startGame();
    }

    private void initializeLevels() 
    {
        // Inizializziamo la lista dei livelli con il tutorial e i livelli principali

        // Creo e aggiungo il tutorial
        List<AbstractCharacter> tutorialEnemies = new ArrayList<>();
        tutorialEnemies.add(new Barbarian());
        tutorialEnemies.add(new Archer());

        // Creo i livelli principali 
        List<AbstractCharacter> level1Enemies = new ArrayList<>();
        level1Enemies.add(new Barbarian());
        level1Enemies.add(new Archer());

        List<AbstractCharacter> level2Enemies = new ArrayList<>();
        level2Enemies.add(new Barbarian());
        level2Enemies.add(new Archer());

        List<AbstractCharacter> level3Enemies = new ArrayList<>();
        level3Enemies.add(new Barbarian());
        level3Enemies.add(new Archer());

        List<AbstractCharacter> level4Enemies = new ArrayList<>();
        level4Enemies.add(new Barbarian());
        level4Enemies.add(new Archer());

        List<AbstractCharacter> level5Enemies = new ArrayList<>();
        level5Enemies.add(new Barbarian());
        level5Enemies.add(new Archer());

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

    public void startGame() 
    {
        // Da fare il controllo se hai cliccato il bottone per saltare il tutorial 
        
        if (!tutorialCompleted) 
        {
            // Gioca il tutorial
           	this.tutorialCompleted = levelList.get(0).playLevel(enemyManager, combatManager);
        }

        for (int i = 1; i < levelList.size(); i++) 
        {
            if(levelList.get(i).playLevel(enemyManager, combatManager))
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
			this.alliesList.add(new Archer()); // Aggiungi un nuovo alleato scelto dall'utente
		}
    }

    public void skipTutorial() {
        this.tutorialCompleted = true;
    }
}