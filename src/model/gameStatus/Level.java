package model.gameStatus;

import view.LevelMap;
import java.util.List;

import model.characters.AbstractCharacter;

public class Level {

    private LevelMap levelmap;
    private List<AbstractCharacter> enemies;
    private List<AbstractCharacter> allies;

    public Level(LevelMap levelmap, List<AbstractCharacter> enemies, List<AbstractCharacter> allies) {
        this.levelmap = levelmap;
        this.enemies = enemies;
        this.allies = allies;
    }

    public LevelMap getLevelMap() {
        return  this.levelmap;
    }

    public List<AbstractCharacter> getEnemies() {
        return  this.enemies;
    }

    public List<AbstractCharacter> getAllies() {
        return  this.allies;
    }

    public void setAllies(List<AbstractCharacter> allies) {
        this.allies = allies;
    }
}
