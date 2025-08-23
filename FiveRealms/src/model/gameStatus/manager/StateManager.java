package model.gameStatus.manager;

import java.util.List;
import model.characters.Character;

/**
 * Unified state manager that handles all game states from high-level game phases
 * down to individual battle turn states. This single class replaces both 
 * GameStateManager and LevelStateManager, providing a centralized state management solution.
 */
public class StateManager 
{
    /**
     * High-level game phases
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
    
    /**
     * Level-specific phases within PLAYING_LEVEL game phase
     */
    public enum LevelPhase 
    {
        /** Level is being initialized */
        INITIALIZING,
        /** Active battle phase where characters take turns */
        BATTLE_PHASE,
        /** End-of-round check for victory/defeat conditions */
        CHECK_END_LEVEL,
        /** Level has ended (victory or defeat) */
        LEVEL_COMPLETED,
        /** Level transition in progress */
        TRANSITIONING
    }

    /**
     * Battle states within BATTLE_PHASE level phase
     */
    public enum BattleState 
    {
        /** Preparing to start a new round */
        INITIALIZING_TURN,
        /** Waiting for user to move their character */
        WAITING_FOR_MOVEMENT,
        /** Waiting for user to select a target */
        WAITING_FOR_TARGET,
        /** AI character is taking their turn */
        AI_TURN_IN_PROGRESS,
        /** Current turn has been completed */
        TURN_COMPLETED,
        /** Round is complete, preparing for next round */
        ROUND_COMPLETED
    }

    /**
     * Interface for listening to state transitions
     */
    public interface StateTransitionListener 
    {
        void onStateTransition(Object oldState, Object newState, String context);
    }

    // ===========================================
    // STATE FIELDS
    // ===========================================
    
    // Game-level state
    private GamePhase currentGamePhase;
    private boolean waitingForCharacterReplacement;
    private boolean isPaused;
    private boolean gameEnded;
    private boolean playerWon;
    
    // Level-specific state  
    private LevelPhase currentLevelPhase;
    private BattleState currentBattleState;
    private boolean levelCompleted;
    private boolean levelFailed;
    
    // Battle/Turn tracking
    private String currentCharacterTurn;
    private int currentRoundNumber;
    private List<Character> alliesList;
    private List<Character> enemiesList;
    
    // Event listeners
    private StateTransitionListener gamePhaseListener;
    private StateTransitionListener levelPhaseListener;
    private StateTransitionListener battleStateListener;

    // ===========================================
    // CONSTRUCTOR
    // ===========================================

    /**
     * Constructs a new UnifiedStateManager with initial state.
     */
    public StateManager() 
    {
        this.resetToInitialState();
    }

    /**
     * Resets all state to initial values.
     */
    private void resetToInitialState() 
    {
        // Game-level state
        this.currentGamePhase = GamePhase.MAIN_MENU;
        this.waitingForCharacterReplacement = false;
        this.isPaused  = false;
        this.gameEnded = false;
        this.playerWon = false;
        
        // Level-specific state
        this.currentLevelPhase  = LevelPhase.INITIALIZING;
        this.currentBattleState = BattleState.INITIALIZING_TURN;
        this.levelCompleted = false;
        this.levelFailed    = false;
        
        // Battle/Turn tracking
        this.currentCharacterTurn = null;
        this.currentRoundNumber   = 0;
        this.alliesList  = null;
        this.enemiesList = null;
    }

    // ===========================================
    // GAME PHASE MANAGEMENT
    // ===========================================
    
    /**
     * Gets the current game phase.
     */
    public GamePhase getCurrentGamePhase() 
    {
        return this.currentGamePhase;
    }
    
    /**
     * Sets the current game phase and updates related state.
     */
    public void setCurrentGamePhase(GamePhase phase) 
    {
        GamePhase oldPhase = this.currentGamePhase;
        this.currentGamePhase = phase;
        
        this.updateStateForGamePhase(phase);
        
        if (gamePhaseListener != null) 
        {
            gamePhaseListener.onStateTransition(oldPhase, phase, "Game phase change");
        }
    }
    
    /**
     * Updates state based on game phase changes.
     */
    private void updateStateForGamePhase(GamePhase phase) 
    {
        switch (phase) 
        {
            case WAITING_CHARACTER_REPLACEMENT:
                this.waitingForCharacterReplacement = true;
                break;
                
            case PLAYING_LEVEL:
                this.waitingForCharacterReplacement = false;
                this.isPaused = false;
                // Initialize level state when starting to play
                this.currentLevelPhase = LevelPhase.INITIALIZING;
                this.currentBattleState = BattleState.INITIALIZING_TURN;
                this.levelCompleted = false;
                this.levelFailed = false;
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
                resetLevelState();
                break;
        }
    }

    // ===========================================
    // LEVEL PHASE MANAGEMENT
    // ===========================================

    /**
     * Gets the current level phase.
     */
    public LevelPhase getCurrentLevelPhase() 
    {
        return this.currentLevelPhase;
    }

    /**
     * Sets the current level phase and updates related state.
     */
    public void setCurrentLevelPhase(LevelPhase phase) 
    {
        LevelPhase oldPhase = this.currentLevelPhase;
        this.currentLevelPhase = phase;
        
        updateStateForLevelPhase(phase);
        
        if (levelPhaseListener != null) {
            levelPhaseListener.onStateTransition(oldPhase, phase, "Level phase change");
        }
    }
    
    /**
     * Updates state based on level phase changes.
     */
    private void updateStateForLevelPhase(LevelPhase phase) 
    {
        switch (phase) {
            case INITIALIZING:
                this.levelCompleted = false;
                this.levelFailed = false;
                this.currentRoundNumber = 0;
                this.currentCharacterTurn = null;
                break;
                
            case BATTLE_PHASE:
                // Ensure we start with proper battle state
                if (this.currentBattleState == BattleState.ROUND_COMPLETED) {
                    this.currentBattleState = BattleState.INITIALIZING_TURN;
                }
                break;
                
            case LEVEL_COMPLETED:
            	if (!this.levelCompleted && !this.levelFailed)
            		this.levelCompleted = true;
                break;
                
            case CHECK_END_LEVEL:
            case TRANSITIONING:
                // No specific state changes needed
                break;
        }
    }

    // ===========================================
    // BATTLE STATE MANAGEMENT
    // ===========================================

    /**
     * Gets the current battle state.
     */
    public BattleState getCurrentBattleState() 
    {
        return this.currentBattleState;
    }

    /**
     * Sets the current battle state and updates related state.
     */
    public void setCurrentBattleState(BattleState state) 
    {
        BattleState oldState = this.currentBattleState;
        this.currentBattleState = state;
        
        updateStateForBattleState(state);
        
        if (battleStateListener != null) {
            battleStateListener.onStateTransition(oldState, state, "Battle state change");
        }
    }
    
    /**
     * Updates state based on battle state changes.
     */
    private void updateStateForBattleState(BattleState state) 
    {
        switch (state) {
            case INITIALIZING_TURN:
                this.currentCharacterTurn = null;
                break;
                
            case ROUND_COMPLETED:
                this.currentRoundNumber++;
                this.currentCharacterTurn = null;
                break;
                
            case AI_TURN_IN_PROGRESS:
            case WAITING_FOR_MOVEMENT:
            case WAITING_FOR_TARGET:
            case TURN_COMPLETED:
                // No specific state updates needed
                break;
        }
    }

    // ===========================================
    // CHARACTER AND TURN MANAGEMENT
    // ===========================================
    
    /**
     * Sets the character currently taking their turn.
     */
    public void setCurrentCharacterTurn(String characterName) 
    {
        this.currentCharacterTurn = characterName;
    }

    /**
     * Gets the character currently taking their turn.
     */
    public String getCurrentCharacterTurn() 
    {
        return this.currentCharacterTurn;
    }

    /**
     * Gets the current round number.
     */
    public int getCurrentRoundNumber() 
    {
        return this.currentRoundNumber;
    }

    /**
     * Sets the list of allied characters.
     */
    public void setAlliesList(List<Character> alliesList) 
    {
        this.alliesList = alliesList;
    }

    /**
     * Gets the list of allied characters.
     */
    public List<Character> getAlliesList() 
    {
        return this.alliesList;
    }

    /**
     * Sets the list of enemy characters.
     */
    public void setEnemiesList(List<Character> enemiesList) 
    {
        this.enemiesList = enemiesList;
    }

    /**
     * Gets the list of enemy characters.
     */
    public List<Character> getEnemiesList() 
    {
        return this.enemiesList;
    }

    // ===========================================
    // INTELLIGENT STATE QUERIES
    // ===========================================

    /**
     * Checks if the game can accept user input based on current states.
     */
    public boolean canAcceptUserInput() 
    {
        return this.currentGamePhase == GamePhase.PLAYING_LEVEL &&
               this.currentLevelPhase == LevelPhase.BATTLE_PHASE &&
               (this.currentBattleState == BattleState.WAITING_FOR_MOVEMENT ||
                this.currentBattleState == BattleState.WAITING_FOR_TARGET);
    }

    /**
     * Checks if the game should auto-progress.
     */
    public boolean shouldAutoProgress() 
    {
        return this.currentGamePhase == GamePhase.PLAYING_LEVEL &&
               this.currentLevelPhase == LevelPhase.BATTLE_PHASE &&
               (this.currentBattleState == BattleState.AI_TURN_IN_PROGRESS ||
                this.currentBattleState == BattleState.TURN_COMPLETED ||
                this.currentBattleState == BattleState.INITIALIZING_TURN ||
                this.currentBattleState == BattleState.ROUND_COMPLETED);
    }

    /**
     * Checks if the level is waiting for victory/defeat resolution.
     */
    public boolean isWaitingForLevelResolution() 
    {
        return this.currentLevelPhase == LevelPhase.CHECK_END_LEVEL;
    }

    /**
     * Checks if currently in an active gameplay state.
     */
    public boolean isActivelyPlaying() 
    {
        return this.currentGamePhase == GamePhase.PLAYING_LEVEL || 
               this.currentGamePhase == GamePhase.WAITING_CHARACTER_REPLACEMENT;
    }

    /**
     * Checks if the game state can be updated.
     */
    public boolean canUpdate() 
    {
        return this.currentGamePhase == GamePhase.PLAYING_LEVEL && !this.isPaused;
    }

    // ===========================================
    // CONVENIENCE METHODS FOR COMMON TRANSITIONS
    // ===========================================
    
    /**
     * Starts the battle phase.
     */
    public void startBattlePhase() 
    {
        setCurrentLevelPhase(LevelPhase.BATTLE_PHASE);
        setCurrentBattleState(BattleState.INITIALIZING_TURN);
    }

    /**
     * Starts a new round.
     */
    public void startNewRound() 
    {
        setCurrentLevelPhase(LevelPhase.BATTLE_PHASE);
        setCurrentBattleState(BattleState.INITIALIZING_TURN);
    }

    /**
     * Transitions to end level check.
     */
    public void transitionToEndLevelCheck() 
    {
        setCurrentLevelPhase(LevelPhase.CHECK_END_LEVEL);
    }

    /**
     * Completes the level with specified result.
     */
    public void completeLevelWithResult(boolean success) 
    {
        this.levelCompleted = success;
        this.levelFailed = !success;
        setCurrentLevelPhase(LevelPhase.LEVEL_COMPLETED);
    }

    /**
     * Resets level-specific state.
     */
    public void resetLevelState() 
    {
        this.currentLevelPhase = LevelPhase.INITIALIZING;
        this.currentBattleState = BattleState.INITIALIZING_TURN;
        this.levelCompleted = false;
        this.levelFailed = false;
        this.currentCharacterTurn = null;
        this.currentRoundNumber = 0;
    }

    // ===========================================
    // ORIGINAL GAMESTATE METHODS (Compatibility)
    // ===========================================
    
    public boolean isWaitingForCharacterReplacement() 
    {
        return this.waitingForCharacterReplacement;
    }
    
    public void setWaitingForCharacterReplacement(boolean waiting) 
    {
        this.waitingForCharacterReplacement = waiting;
        if (waiting) {
            setCurrentGamePhase(GamePhase.WAITING_CHARACTER_REPLACEMENT);
        } else if (this.currentGamePhase == GamePhase.WAITING_CHARACTER_REPLACEMENT) {
            setCurrentGamePhase(GamePhase.PLAYING_LEVEL);
        }
    }
    
    public void markCharacterReplacementCompleted() 
    {
        this.waitingForCharacterReplacement = false;
        setCurrentGamePhase(GamePhase.PLAYING_LEVEL);
    }
    
    public boolean isPaused() 
    {
        return this.isPaused;
    }
    
    public void pauseGame() 
    {
        if (this.currentGamePhase == GamePhase.PLAYING_LEVEL) {
            setCurrentGamePhase(GamePhase.PAUSED);
        }
    }
    
    public void resumeGame() 
    {
        if (this.currentGamePhase == GamePhase.PAUSED) {
            setCurrentGamePhase(GamePhase.PLAYING_LEVEL);
        }
    }
    
    public boolean isGameEnded() 
    {
        return this.gameEnded;
    }
    
    public boolean hasPlayerWon() 
    {
        return this.gameEnded && this.playerWon;
    }
    
    public void endGame(boolean playerWon) 
    {
        this.gameEnded = true;
        this.playerWon = playerWon;
        setCurrentGamePhase(playerWon ? GamePhase.GAME_OVER_WIN : GamePhase.GAME_OVER_LOSE);
    }
    
    public void resetGameState() 
    {
        resetToInitialState();
    }
    
    public void startNewGame() 
    {
        resetGameState();
        setCurrentGamePhase(GamePhase.CHARACTER_SELECTION);
    }
    
    public void startTutorial() 
    {
        setCurrentGamePhase(GamePhase.TUTORIAL);
    }
    
    public void startPlayingLevel() 
    {
        setCurrentGamePhase(GamePhase.PLAYING_LEVEL);
    }
    
    public void returnToMainMenu() 
    {
        setCurrentGamePhase(GamePhase.MAIN_MENU);
    }

    public boolean isCompleted() 
    {
        return this.levelCompleted;
    }

    public boolean isFailed() 
    {
        return this.levelFailed;
    }

    // ===========================================
    // LISTENER MANAGEMENT
    // ===========================================

    public void setGamePhaseListener(StateTransitionListener listener) 
    {
        this.gamePhaseListener = listener;
    }

    public void setLevelPhaseListener(StateTransitionListener listener) 
    {
        this.levelPhaseListener = listener;
    }

    public void setBattleStateListener(StateTransitionListener listener) 
    {
        this.battleStateListener = listener;
    }

    // ===========================================
    // DEBUGGING AND UTILITIES
    // ===========================================
    
    /**
     * Gets a comprehensive string representation of all current states.
     */
    @Override
    public String toString() 
    {
        return String.format(
            "UnifiedState[Game=%s, Level=%s, Battle=%s, WaitingReplacement=%b, " +
            "Paused=%b, GameEnded=%b, Won=%b, LevelCompleted=%b, LevelFailed=%b, " +
            "CurrentTurn=%s, Round=%d, Allies=%d, Enemies=%d]",
            this.currentGamePhase, this.currentLevelPhase, this.currentBattleState,
            this.waitingForCharacterReplacement, this.isPaused, this.gameEnded, this.playerWon,
            this.levelCompleted, this.levelFailed, this.currentCharacterTurn, this.currentRoundNumber,
            this.alliesList != null ? this.alliesList.size() : 0,
            this.enemiesList != null ? this.enemiesList.size() : 0
        );
    }

    /**
     * Gets a summary focused on current gameplay state.
     */
    public String getGameplaySummary() 
    {
        if (this.currentGamePhase != GamePhase.PLAYING_LEVEL) {
            return "Not in gameplay: " + this.currentGamePhase;
        }
        
        return String.format("Gameplay[Level=%s, Battle=%s, Turn=%s, Round=%d]",
            this.currentLevelPhase, this.currentBattleState, 
            this.currentCharacterTurn, this.currentRoundNumber);
    }

    /**
     * Gets a detailed state dump for debugging.
     */
    public String getDetailedStateDump() 
    {
        StringBuilder sb = new StringBuilder();
        sb.append("=== UNIFIED STATE MANAGER ===\n");
        sb.append("Game Phase: ").append(currentGamePhase).append("\n");
        sb.append("Level Phase: ").append(currentLevelPhase).append("\n");
        sb.append("Battle State: ").append(currentBattleState).append("\n");
        sb.append("Flags: paused=").append(isPaused)
          .append(", gameEnded=").append(gameEnded)
          .append(", playerWon=").append(playerWon)
          .append(", waitingReplacement=").append(waitingForCharacterReplacement).append("\n");
        sb.append("Level: completed=").append(levelCompleted)
          .append(", failed=").append(levelFailed).append("\n");
        sb.append("Turn: character=").append(currentCharacterTurn)
          .append(", round=").append(currentRoundNumber).append("\n");
        sb.append("Characters: allies=").append(alliesList != null ? alliesList.size() : "null")
          .append(", enemies=").append(enemiesList != null ? enemiesList.size() : "null").append("\n");
        sb.append("Query Results:\n");
        sb.append("  canAcceptUserInput: ").append(canAcceptUserInput()).append("\n");
        sb.append("  shouldAutoProgress: ").append(shouldAutoProgress()).append("\n");
        sb.append("  isWaitingForLevelResolution: ").append(isWaitingForLevelResolution()).append("\n");
        sb.append("  canUpdate: ").append(canUpdate()).append("\n");
        sb.append("=============================");
        return sb.toString();
    }
}