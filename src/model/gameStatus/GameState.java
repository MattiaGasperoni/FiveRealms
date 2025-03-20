package model.gameStatus;

import model.characters.Character;
import java.io.Serializable;
import java.util.List;

// classe che rappresenta lo stato del gioco in un determinato momento
public class GameState implements Serializable 
{
    private static final long serialVersionUID = 1L;
    
    private final int level;
    private List<Character> allies;
    private List<Character> enemies;

    public GameState(int level, List<Character> allies, List<Character> enemies) 
    {
        this.level   = level;
        this.allies  = allies;
        this.enemies = enemies;
    }

    public int getLevel() {
        return this.level;
    }

    public List<Character> getAllies() {
        return this.allies;
    }

    public List<Character> getEnemies() {
        return this.enemies;
    }

    @Override
    public String toString() {
        return "Livello: " + this.level + "\nAlleati: " + this.allies + "\nNemici: " + this.enemies;
    }
}
