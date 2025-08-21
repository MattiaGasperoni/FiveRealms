package model.gameStatus.manager;

/**
 * Manages the overall game state including flags, phase tracking,
 * and state transitions during gameplay.
 */
public class GameStateManager 
{
    /**
     * Enumeration of possible game phases
     */
    public enum GamePhase 
    {
        MAIN_MENU,
        CHARACTER_SELECTION,
        TUTORIAL,
        PLAYING_LEVEL,
        WAITING_CHARACTER_REPLACEMENT,
        GAME_OVER_WIN,
        GAME_OVER_LOSE,
        PAUSED
    }
    
    /** Current game phase */
    private GamePhase currentPhase;
    
    /** Flag indicating if the game is waiting for character replacement between levels */
    private boolean waitingForCharacterReplacement;
    
    /** Flag indicating if the game is paused */
    private boolean isPaused;
    
    /** Flag indicating if the game has ended */
    private boolean gameEnded;
    
    /** Flag indicating if the player won (valid only when gameEnded is true) */
    private boolean playerWon;
    
    /**
     * Constructs a new GameStateManager with initial state.
     */
    public GameStateManager() 
    {
        this.currentPhase = GamePhase.MAIN_MENU;
        this.waitingForCharacterReplacement = false;
        this.isPaused = false;
        this.gameEnded = false;
        this.playerWon = false;
    }
    
    /**
     * Gets the current game phase.
     * 
     * @return the current game phase
     */
    public GamePhase getCurrentPhase() 
    {
        return this.currentPhase;
    }
    
    /**
     * Sets the current game phase.
     * 
     * @param phase the new game phase
     */
    public void setCurrentPhase(GamePhase phase) 
    {
        GamePhase previousPhase = this.currentPhase;
        this.currentPhase = phase;
        
        System.out.println("Game phase changed from " + previousPhase + " to " + phase);
        
        // Update related flags based on the new phase
        updateStateBasedOnPhase(phase);
    }
    
    /**
     * Updates internal state flags based on the current phase.
     * 
     * @param phase the current phase
     */
    private void updateStateBasedOnPhase(GamePhase phase) 
    {
        switch (phase) 
        {
            case WAITING_CHARACTER_REPLACEMENT:
                this.waitingForCharacterReplacement = true;
                break;
            case PLAYING_LEVEL:
                this.waitingForCharacterReplacement = false;
                this.isPaused = false;
                break;
            case GAME_OVER_WIN:
                this.gameEnded = true;
                this.playerWon = true;
                break;
            case GAME_OVER_LOSE:
                this.gameEnded = true;
                this.playerWon = false;
                break;
            case PAUSED:
                this.isPaused = true;
                break;
            case MAIN_MENU:
            case CHARACTER_SELECTION:
            case TUTORIAL:
                this.gameEnded = false;
                this.playerWon = false;
                this.isPaused = false;
                this.waitingForCharacterReplacement = false;
                break;
        }
    }
    
    /**
     * Checks if the game is currently waiting for character replacement.
     * 
     * @return true if waiting for character replacement, false otherwise
     */
    public boolean isWaitingForCharacterReplacement() 
    {
        return this.waitingForCharacterReplacement;
    }
    
    /**
     * Sets the waiting for character replacement flag and updates the phase accordingly.
     * 
     * @param waiting true to set the game as waiting for replacement, false otherwise
     */
    public void setWaitingForCharacterReplacement(boolean waiting) 
    {
        this.waitingForCharacterReplacement = waiting;
        if (waiting) 
        {
            this.setCurrentPhase(GamePhase.WAITING_CHARACTER_REPLACEMENT);
        } 
        else if (this.currentPhase == GamePhase.WAITING_CHARACTER_REPLACEMENT) 
        {
            this.setCurrentPhase(GamePhase.PLAYING_LEVEL);
        }
    }
    
    /**
     * Marks the character replacement process as completed.
     */
    public void markCharacterReplacementCompleted() 
    {
        this.waitingForCharacterReplacement = false;
        this.setCurrentPhase(GamePhase.PLAYING_LEVEL);
    }
    
    /**
     * Checks if the game is paused.
     * 
     * @return true if the game is paused
     */
    public boolean isPaused() 
    {
        return this.isPaused;
    }
    
    /**
     * Pauses the game.
     */
    public void pauseGame() 
    {
        if (this.currentPhase == GamePhase.PLAYING_LEVEL) 
        {
            this.setCurrentPhase(GamePhase.PAUSED);
        }
    }
    
    /**
     * Resumes the game from pause.
     */
    public void resumeGame() 
    {
        if (this.currentPhase == GamePhase.PAUSED) 
        {
            this.setCurrentPhase(GamePhase.PLAYING_LEVEL);
        }
    }
    
    /**
     * Checks if the game has ended.
     * 
     * @return true if the game has ended (win or lose)
     */
    public boolean isGameEnded() 
    {
        return this.gameEnded;
    }
    
    /**
     * Checks if the player won (only valid when game has ended).
     * 
     * @return true if the player won
     */
    public boolean hasPlayerWon() 
    {
        return this.gameEnded && this.playerWon;
    }
    
    /**
     * Ends the game with the specified result.
     * 
     * @param playerWon true if the player won, false if they lost
     */
    public void endGame(boolean playerWon) 
    {
        this.gameEnded = true;
        this.playerWon = playerWon;
        this.setCurrentPhase(playerWon ? GamePhase.GAME_OVER_WIN : GamePhase.GAME_OVER_LOSE);
    }
    
    /**
     * Resets the game state to initial values.
     */
    public void resetGameState() 
    {
        this.currentPhase = GamePhase.MAIN_MENU;
        this.waitingForCharacterReplacement = false;
        this.isPaused = false;
        this.gameEnded = false;
        this.playerWon = false;
        System.out.println("Game state reset to initial values");
    }
    
    /**
     * Starts a new game by setting the appropriate phase.
     */
    public void startNewGame() 
    {
        this.resetGameState();
        this.setCurrentPhase(GamePhase.CHARACTER_SELECTION);
    }
    
    /**
     * Starts the tutorial mode.
     */
    public void startTutorial() 
    {
        this.setCurrentPhase(GamePhase.TUTORIAL);
    }
    
    /**
     * Starts playing a level.
     */
    public void startPlayingLevel() 
    {
        this.setCurrentPhase(GamePhase.PLAYING_LEVEL);
    }
    
    /**
     * Returns to the main menu.
     */
    public void returnToMainMenu() 
    {
        this.setCurrentPhase(GamePhase.MAIN_MENU);
    }
    
    /**
     * Checks if the game is currently in an active playing state.
     * 
     * @return true if actively playing (not paused, not in menus)
     */
    public boolean isActivelyPlaying() 
    {
        return this.currentPhase == GamePhase.PLAYING_LEVEL || 
               this.currentPhase == GamePhase.WAITING_CHARACTER_REPLACEMENT;
    }
    
    /**
     * Gets a string representation of the current game state.
     * 
     * @return string describing current state
     */
    @Override
    public String toString() 
    {
        return String.format("GameState[Phase=%s, WaitingReplacement=%b, Paused=%b, Ended=%b, Won=%b]", 
                            this.currentPhase, this.waitingForCharacterReplacement, 
                            this.isPaused, this.gameEnded, this.playerWon);
    }
}