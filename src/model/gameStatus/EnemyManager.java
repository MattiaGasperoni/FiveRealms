package model.gameStatus;

import java.util.List;

import model.characters.AbstractCharacter;

public class EnemyManager {
    private int enemiesRemaining;

    public void spawnEnemies(List<AbstractCharacter> enemies) 
    {
        this.enemiesRemaining = enemies.size();
        // Logica per spawnare i nemici
    }

    public void despawnEnemies() {
        this.enemiesRemaining = 0;
        // Logica per despawnare i nemici
    }

    public boolean allEnemiesDefeated() {
        return enemiesRemaining == 0;
    }

    public void enemyDefeated() {
        if (enemiesRemaining > 0) {
            enemiesRemaining--;
        }
    }
}