package controller;

import model.gameStatus.manager.MusicManager;

/**
 * Manages all music-related operations in the game.
 * <p>
 * This controller handles background music, level-specific music,
 * and sound effects for different game states.
 */
public class MusicController 
{
    
    private final MusicManager musicManager;
    
    /**
     * Creates a new MusicController instance.
     */
    public MusicController() 
    {
        this.musicManager = new MusicManager();
    }
    
    /**
     * Plays background music for menus.
     */
    public void playBackgroundMusic() 
    {
        this.musicManager.stop();
        this.musicManager.play("background", true);
    }
    
    /**
     * Plays tutorial music.
     */
    public void playTutorialMusic() 
    {
        this.musicManager.stop();
        this.musicManager.play("tutorial", true);
    }
    
    /**
     * Plays level-specific music.
     *
     * @param levelIndex the level number (1-based)
     */
    public void playLevelMusic(int levelIndex) 
    {
        this.musicManager.stop();
        this.musicManager.play("level" + levelIndex, true);
    }
    
    /**
     * Plays win music.
     */
    public void playWinMusic() 
    {
        this.musicManager.stop();
        this.musicManager.play("win", true);
    }
    
    /**
     * Plays lose music.
     */
    public void playLoseMusic() 
    {
        this.musicManager.stop();
        this.musicManager.play("lose", true);
    }
    
    /**
     * Stops all music.
     */
    public void stopMusic() 
    {
        this.musicManager.stop();
    }
    
    /**
     * Resumes level music after pause.
     *
     * @param levelIndex the current level index
     */
    public void resumeLevelMusic(int levelIndex) 
    {
        this.musicManager.play("level" + (levelIndex + 1), true);
    }
}