package model.gameStatus;

import java.util.ArrayList;
import java.util.List;

import model.characters.AbstractCharacter;
import model.characters.Archer;
import model.characters.Barbarian;
import view.LevelMap;

public class Game 
{   
	private static final int NUMLEVELS = 5;

    private List<Level> levelList;    
	private List<AbstractCharacter> currentAllies;
	
	
    private EnemyManager enemyManager;

	private boolean tutorialCompleted;

	public Game() 
	{
		this.levelList         = new ArrayList<>();
		this.currentAllies     = new ArrayList<>();
		this.enemyManager      = new EnemyManager();
		this.tutorialCompleted = false;
		initializeLevels();
		startGame();
	}

	private void initializeLevels() 
	{
		// Inizializziamo la lista dei livelli con il tutorial e i livelli principali

        // Creo e aggiungo il tutorial
        this.levelList.add(new Level(new LevelMap(), generateTutorialEnemies(), this.currentAllies));

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
		this.levelList.add(new Level(new LevelMap(), level1Enemies, this.currentAllies));
		this.levelList.add(new Level(new LevelMap(), level2Enemies, this.currentAllies));
		this.levelList.add(new Level(new LevelMap(), level3Enemies, this.currentAllies));
		this.levelList.add(new Level(new LevelMap(), level4Enemies, this.currentAllies));
		this.levelList.add(new Level(new LevelMap(), level5Enemies, this.currentAllies));
    }

    private List<AbstractCharacter> generateTutorialEnemies() 
	{
		// Creo la lista dei nemici del tutorial
        List<AbstractCharacter> tutorialEnemies = new ArrayList<>();
        
		// Popolo la lista con i nemici
		tutorialEnemies.add(new Barbarian());
        tutorialEnemies.add(new Archer());

        // Aggiungi altri nemici specifici per il tutorial
        return tutorialEnemies;
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
            playLevel(levelList.get(0)); 
            this.tutorialCompleted = true;
        }

        for (int i = 1; i < levelList.size(); i++) 
		{
            playLevel(levelList.get(i));
        }
    }

    private void playLevel(Level level) 
	{
		// Spawn dei nemici
        enemyManager.spawnEnemies(level.getEnemies());

        while (!enemyManager.allEnemiesDefeated()) 
		{
            // Logica del combattimento
        }
		// Despawn dei nemici
        enemyManager.despawnEnemies();

		// Aggiorna la lista degli alleati
        currentAllies = updateAllies(level.getAllies());
        level.setAllies(currentAllies);
    }

    private List<AbstractCharacter> updateAllies(List<AbstractCharacter> allies) 
	{
        List<AbstractCharacter> updatedAllies = new ArrayList<>();
        for (AbstractCharacter ally : allies) 
		{
			// Da fare metodo per capire se un personaggio e' ancora giocabile
            if (ally.isPlayable()) 
			{
                updatedAllies.add(ally);
            }
        }
        return updatedAllies;
    }

    public void skipTutorial() {
        this.tutorialCompleted = true;
    }
}