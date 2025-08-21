package model.gameStatus.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import model.characters.*;
import model.characters.Character;
import model.characters.bosses.*;
import model.gameStatus.Game;
import model.gameStatus.level.GameLevel;
import view.map.LevelMap;
import controller.GameController;

/**
 * Manages all level-related operations including creation, progression,
 * and state management of game levels.
 */
public class LevelManager 
{    
    /** List of all game levels */
    private List<GameLevel> gameLevels;
    
    /** Index of the current level being played */
    private int currentLevelIndex;
    
    /** Reference to the game controller */
    private GameController controller;
    
    /**
     * Constructs a new LevelManager.
     * 
     * @param controller the game controller
     */
    public LevelManager(GameController controller) 
    {
        this.gameLevels = new ArrayList<>();
        this.currentLevelIndex = 0;
        this.controller = controller;
    }
    
    /**
     * Initializes all five game levels with their respective enemy compositions.
     * Each level features different enemy types and bosses with increasing difficulty.
     * 
     * @param selectedAllies the allies to use in all levels
     */
    public void initializeGameLevels(List<Character> selectedAllies)
    {
        this.gameLevels.clear();
        
        List<Character> level1Enemies = createLevel1Enemies();
        List<Character> level2Enemies = createLevel2Enemies();
        List<Character> level3Enemies = createLevel3Enemies();
        List<Character> level4Enemies = createLevel4Enemies();
        List<Character> level5Enemies = createLevel5Enemies();
        
        this.gameLevels.add(new GameLevel(new LevelMap(level1Enemies, selectedAllies, 1, this.controller), this.controller));
        this.gameLevels.add(new GameLevel(new LevelMap(level2Enemies, selectedAllies, 2, this.controller), this.controller));
        this.gameLevels.add(new GameLevel(new LevelMap(level3Enemies, selectedAllies, 3, this.controller), this.controller));
        this.gameLevels.add(new GameLevel(new LevelMap(level4Enemies, selectedAllies, 4, this.controller), this.controller));
        this.gameLevels.add(new GameLevel(new LevelMap(level5Enemies, selectedAllies, 5, this.controller), this.controller));
    }
    
    /**
     * Creates enemies for level 1.
     */
    private List<Character> createLevel1Enemies() 
    {
        List<Character> enemies = new ArrayList<>();
        enemies.add(new KnightBoss());
        enemies.add(new Knight());
        enemies.add(new Barbarian());
        return enemies;
    }
    
    /**
     * Creates enemies for level 2.
     */
    private List<Character> createLevel2Enemies() 
    {
        List<Character> enemies = new ArrayList<>();
        enemies.add(new BarbarianBoss());
        enemies.add(new Barbarian());
        enemies.add(new Barbarian());
        enemies.add(new Archer());
        return enemies;
    }
    
    /**
     * Creates enemies for level 3.
     */
    private List<Character> createLevel3Enemies() 
    {
        List<Character> enemies = new ArrayList<>();
        enemies.add(new ArcherBoss());
        enemies.add(new Archer());
        enemies.add(new Archer());
        enemies.add(new Juggernaut());
        return enemies;
    }
    
    /**
     * Creates enemies for level 4.
     */
    private List<Character> createLevel4Enemies() 
    {
        List<Character> enemies = new ArrayList<>();
        enemies.add(new JuggernautBoss());
        enemies.add(new Barbarian());
        enemies.add(new Wizard());
        enemies.add(new Knight());
        enemies.add(new Juggernaut());
        return enemies;
    }
    
    /**
     * Creates enemies for level 5.
     */
    private List<Character> createLevel5Enemies() 
    {
        List<Character> enemies = new ArrayList<>();
        enemies.add(new WizardBoss());
        enemies.add(new JuggernautBoss());
        enemies.add(new KnightBoss());
        enemies.add(new BarbarianBoss());
        enemies.add(new ArcherBoss());
        return enemies;
    }
    
    /**
     * Starts the current level based on the current level index.
     * This method initializes the level, starts appropriate music, and begins level play.
     * 
     * @throws IOException if there are issues starting the level
     */
    public void startCurrentLevel() throws IOException
    {
        GameLevel level = this.gameLevels.get(this.currentLevelIndex);
        this.controller.startLevelMusic(this.currentLevelIndex + 1);
        level.play();
    }
    
    /**
     * Updates the current level.
     */
    public void updateCurrentLevel() 
    {
        if (this.currentLevelIndex < this.gameLevels.size()) 
        {
            this.gameLevels.get(this.currentLevelIndex).update();
        }
    }
    
    /**
     * Checks if the current level is completed.
     * 
     * @return true if current level is completed
     */
    public boolean isCurrentLevelCompleted() 
    {
        return this.currentLevelIndex < this.gameLevels.size() && 
               this.gameLevels.get(this.currentLevelIndex).isCompleted();
    }
    
    /**
     * Checks if the current level has failed.
     * 
     * @return true if current level has failed
     */
    public boolean isCurrentLevelFailed() 
    {
        return this.currentLevelIndex < this.gameLevels.size() && 
               this.gameLevels.get(this.currentLevelIndex).isFailed();
    }
    
    /**
     * Advances to the next level.
     * 
     * @return true if there is a next level, false if all levels are completed
     */
    public boolean advanceToNextLevel() 
    {
        this.currentLevelIndex++;
        return this.currentLevelIndex < Game.TOTAL_LEVEL;
    }
    
    /**
     * Checks if all levels are completed.
     * 
     * @return true if all levels are completed
     */
    public boolean areAllLevelsCompleted() 
    {
        return this.currentLevelIndex >= Game.TOTAL_LEVEL;
    }
    
    /**
     * Gets the current level index.
     * 
     * @return the index of the current level (0-based)
     */
    public int getCurrentLevelIndex() 
    {
        return this.currentLevelIndex;
    }
    
    /**
     * Sets the current level index.
     * 
     * @param levelIndex the new level index to set
     */
    public void setCurrentLevelIndex(int levelIndex) 
    {
        this.currentLevelIndex = levelIndex;
    }
    
    /**
     * Gets the list of all game levels.
     * 
     * @return the list of game levels
     */
    public List<GameLevel> getGameLevels() 
    {
        return this.gameLevels;
    }
    
    /**
     * Gets the current game level.
     * 
     * @return the current game level, or null if invalid index
     */
    public GameLevel getCurrentLevel() 
    {
        if (this.currentLevelIndex < this.gameLevels.size()) 
        {
            return this.gameLevels.get(this.currentLevelIndex);
        }
        return null;
    }
    
    /**
     * Sets the allies list for all levels.
     * 
     * @param allies the allies to set for all levels
     */
    public void setAlliesForAllLevels(List<Character> allies) 
    {
        for (GameLevel level : this.gameLevels) 
        {
            level.setAlliesList(allies);
        }
    }
    
    /**
     * Sets the enemies list for a specific level.
     * 
     * @param levelIndex the level index
     * @param enemies the enemies to set
     */
    public void setEnemiesForLevel(int levelIndex, List<Character> enemies) 
    {
        if (levelIndex < this.gameLevels.size()) 
        {
            this.gameLevels.get(levelIndex).setEnemiesList(enemies);
        }
    }
    
    /**
     * Closes the current level map.
     */
    public void closeCurrentLevelMap() 
    {
        GameLevel currentLevel = getCurrentLevel();
        if (currentLevel != null) 
        {
            currentLevel.getLevelMap().close();
        }
    }
}