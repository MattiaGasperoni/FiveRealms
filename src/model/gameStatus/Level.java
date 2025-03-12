package model.gameStatus;

import java.util.ArrayList;
import java.util.List;

import controller.StaticCombatManager;
import model.characters.AbstractCharacter;
import view.LevelMap;

public class Level 
{
    private final LevelMap LevelMap;
    private List<AbstractCharacter> enemiesList;
    private List<AbstractCharacter> alliesList;
    private boolean doorIsOpen;

    public Level(LevelMap map, List<AbstractCharacter> enemies, List<AbstractCharacter> allies) 
    {
        this.LevelMap     = map;
        this.enemiesList  = enemies;
        this.alliesList   = allies;
        this.doorIsOpen   = false;
    }

    public List<AbstractCharacter> getEnemies() {
        return this.enemiesList;
    }

    public List<AbstractCharacter> getAllies() {
        return this.alliesList;
    }

    //Metodo Pubblico per giocare il livello
    public boolean playLevel(EnemyManager enemyManager, StaticCombatManager combatManager) {
        
        // Spawn dei nemici
        enemyManager.spawnEnemies(this.enemiesList);

        // Metodo per lo spawn degli alleati
        // ...

        // Il livello continua finche non apri la porta per il prossimo livello o muoiono tutti i tuoi personaggi 
        while (true) // allAlliesDead or this.doorIsOpen == false
        {
            // INIZIO FASE DI ATTACCO

            // con il combatManager se un nemico o un alleate viene ucciso in quella fase di attacco 
            // viene tolto dalla mappa e rimosso dalla lista

            // Logica del combattimento
            // ...

            // Controlla se tutti gli alleati sono morti
            if (false)  // this.alliesDeaths == 3  
            {
                System.out.println("Tutti i tuoi personaggi sono morti. Game Over.");
                return false;
            }

            // FINE FASE DI ATTACCO

            // Controlla se tutti i nemici sono stati sconfitti
            if (enemyManager.allEnemiesDefeated()) 
            {
                // Attiva la porta per il prossimo livello
                this.doorIsOpen = true;
                System.out.println("Hai sconfitto tutti i nemici. La porta per il prossimo livello e' aperta.");
            }
        }

        return true;
    }
}