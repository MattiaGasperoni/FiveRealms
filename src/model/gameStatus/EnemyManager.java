package model.gameStatus;

import java.util.List;

import model.characters.AbstractCharacter;

// Questa classe gestisce lo spawn e il despawn dei nemici nel gioco
public class EnemyManager 
{
    private int enemiesRemaining;

    public void spawnEnemies(List<AbstractCharacter> enemies) 
    {
        this.enemiesRemaining = enemies.size();
        // Logica per spawnare i nemici
    }

    public void despawnEnemies() 
    {
        this.enemiesRemaining = 0;
        // Logica per despawnare i nemici
    }

    public boolean allEnemiesDefeated() 
    {
        return this.enemiesRemaining == 0;
    }

    public void enemyDefeated() 
    {
        if (this.enemiesRemaining > 0) 
        {
            this.enemiesRemaining--;
        }
    }
}