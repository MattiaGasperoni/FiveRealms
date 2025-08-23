package model.gameStatus.level;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import controller.*;
import model.characters.AbstractCharacter;
import model.characters.Character;
import model.gameStatus.Game;
import model.gameStatus.manager.StateManager;
import model.point.Point;
import view.*;
import view.map.AbstractMap;
import view.map.LevelMap;

/**
 * Represents a game level that manages the battle phases, turn order, and win/lose conditions.
 * This class handles the complete lifecycle of a level, including initialization, battle phases,
 * character turns (both player and AI), and level completion checks.
 * 
 * Now integrated with StateManager for unified state management.
 */
public class GameLevel implements Level 
{
	/** The unified state manager for this level */
	private final StateManager stateManager;
	
	private List<Character> enemiesList;
	private List<Character> alliesList;
	private PriorityQueue<Character> currentTurnOrder;

	/** Character currently taking their turn */
	private Character currentAttacker;

	/** List of all available positions on the map for AI movement calculations */
	private List<Point> availablePositions;

	/** The visual map representation of this level */
	private final LevelMap levelMap;

	/** Manager for handling battle phase UI interactions */
	private final BattlePhaseView movementPhaseManager;

	/** Reference to the main game controller */
	private final GameController controller;

	/**
	 * Constructs a new Level with the specified map and controller.
	 * Initializes all necessary components and prepares the level for gameplay.
	 * 
	 * @param map The LevelMap that provides the visual representation and character lists
	 * @param controller The GameController managing the overall game flow
	 * @param stateManager The StateManager for unified state management
	 */
	public GameLevel(LevelMap map, GameController controller, StateManager stateManager) 
	{
		this.levelMap     = map;
		this.controller   = controller;
		this.stateManager = stateManager;
		this.movementPhaseManager = new BattlePhaseView(this.levelMap, this.controller);

		this.enemiesList = this.levelMap.getEnemiesList();
		this.alliesList  = this.levelMap.getAlliesList();

		// Set character lists in state manager
		this.stateManager.setEnemiesList(this.enemiesList);
		this.stateManager.setAlliesList(this.alliesList);

		// Initialize available positions for AI turn calculations
		this.availablePositions = new ArrayList<>();
		for (int i = 0; i < AbstractMap.GRID_SIZE_WIDTH; i++) 
		{
			for (int j = 0; j < AbstractMap.GRID_SIZE_HEIGHT; j++) 
			{
				availablePositions.add(new Point(i, j));
			}
		}
	}

	/**
	 * Alternative constructor for backward compatibility
	 */
	public GameLevel(LevelMap map, GameController controller) {
		this(map, controller, new StateManager());
	}

	/**
	 * Starts the level by showing the map, spawning characters, and initializing the battle phase.
	 * This method sets up the initial state of the level and prepares it for player interaction.
	 * 
	 * @return true if level is completed, false otherwise
	 * @throws IOException If there's an error during level initialization or map display
	 */
	public boolean play() throws IOException {
		this.levelMap.show();

		this.levelMap.spawnCharacter(this.enemiesList);
		this.levelMap.spawnCharacter(this.alliesList);

		// Ensure we're in the playing level game phase
		this.stateManager.startPlayingLevel();
		
		// Initialize level state through StateManager
		this.stateManager.setCurrentLevelPhase(StateManager.LevelPhase.BATTLE_PHASE);
		this.stateManager.setCurrentBattleState(StateManager.BattleState.INITIALIZING_TURN);
				
		return this.stateManager.isCompleted();
	}

	/**
	 * Main update method that advances the level state based on the current phase.
	 * This method should be called continuously in the game loop to progress the level.
	 */
	public void update() {
		StateManager.LevelPhase levelPhase = this.stateManager.getCurrentLevelPhase();	
		
		// Only update if we can according to state manager
		if (!this.stateManager.canUpdate()) {
			return;
		}

		if (levelPhase == null) {
			return;
		}

		switch (levelPhase) {
			case BATTLE_PHASE:
				this.handleBattlePhase();
				break;

			case CHECK_END_LEVEL:
				this.checkEndLevel();
				break;

			case LEVEL_COMPLETED:
			case TRANSITIONING:
				// Level is done, no further updates needed
				break;

			case INITIALIZING:
				// Should not happen during update, but handle gracefully
				this.stateManager.startBattlePhase();
				break;

			default:
				throw new IllegalStateException("Unknown Level Phase: " + levelPhase);
		}
	}

	/**
	 * Handles the battle phase logic based on the current battle state.
	 * Manages the flow between different battle states while respecting pause conditions
	 * and user input requirements.
	 */
	private void handleBattlePhase() {
		StateManager.BattleState currentState = this.stateManager.getCurrentBattleState();
		
		// Check if we should wait for user input
		if (this.stateManager.canAcceptUserInput()) {
			// Waiting for user input - don't auto-progress
			return; 
		}
		
		// Check if we should auto-progress
		if (!this.stateManager.shouldAutoProgress()) {
			return;
		}

		switch (currentState) {
			case INITIALIZING_TURN:
				this.initializeBattleRound();
				break;

			case TURN_COMPLETED:
				this.checkEndTurn();
				break;

			case ROUND_COMPLETED:
				this.stateManager.startNewRound();
				break;

			case AI_TURN_IN_PROGRESS:
				// AI turn should be handled elsewhere, but ensure it completes
				break;

			default:
				throw new IllegalStateException("Unknown battle state: " + currentState);
		}
	}

	/**
	 * Initializes a new battle round by resetting the grid colors and generating turn order.
	 * This method is called only when starting a new round of movement/attack.
	 */
	private void initializeBattleRound() {		
		this.levelMap.resetGridColors();
		// Generate the movement/attack order for characters in this round
		this.currentTurnOrder = this.getTurnOrder(this.alliesList, this.enemiesList);


		if (this.currentTurnOrder.isEmpty()) {
			this.stateManager.setCurrentBattleState(StateManager.BattleState.TURN_COMPLETED);
			return;
		}

		this.startNextTurn();
	}

	/**
	 * Starts the turn of the next character in the round.
	 * Finds the next living character in the turn order and initiates their turn,
	 * handling both player and AI characters appropriately.
	 */
	private void startNextTurn() {
		
		this.levelMap.resetGridColors();

		if (this.currentTurnOrder.isEmpty()) {
			this.stateManager.setCurrentBattleState(StateManager.BattleState.TURN_COMPLETED);
			return;
		}

		// Find the next living character
		Character nextAttacker = null;

		while (!this.currentTurnOrder.isEmpty()) {
			Character candidate = this.currentTurnOrder.poll();

			if (candidate.isAlive()) {
				nextAttacker = candidate;
				break;
			}
		}

		// If no living characters remain, end the round
		if (nextAttacker == null) {
			this.stateManager.setCurrentBattleState(StateManager.BattleState.TURN_COMPLETED);
			return;
		}

		// If there are living characters, set the new attacker and start the turn
		this.currentAttacker = nextAttacker;
		this.stateManager.setCurrentCharacterTurn(nextAttacker.getClass().getSimpleName());
		this.levelMap.colourCharacterPosition(this.currentAttacker);


		if (this.currentAttacker.isAllied()) {
			this.startPlayerTurn();
		} else {
			this.startAITurn();
		}
	}

	/**
	 * Initiates a player character's turn by setting up movement phase.
	 * Sets the state to wait for user input and configures the movement phase manager
	 * with appropriate callbacks for turn completion.
	 */
	private void startPlayerTurn() {
		this.stateManager.setCurrentBattleState(StateManager.BattleState.WAITING_FOR_MOVEMENT);
		this.levelMap.updateBannerMessage("Waiting for movement of: " + currentAttacker.getClass().getSimpleName(), false);

		// Configure movement with callback
		this.movementPhaseManager.movementPhase(currentAttacker, () -> {
			this.onMovementCompleted();
		});
	}

	/**
	 * Callback method called when player movement is completed.
	 * Transitions to the target selection phase and sets up the appropriate callback
	 * for turn completion.
	 */
	private void onMovementCompleted() {
		this.stateManager.setCurrentBattleState(StateManager.BattleState.WAITING_FOR_TARGET);

		this.movementPhaseManager.chooseTarget(this.enemiesList, this.currentAttacker, () -> {
			this.stateManager.setCurrentBattleState(StateManager.BattleState.TURN_COMPLETED);
		});
	}

	/**
	 * Handles an AI character's turn by automatically selecting movement and target.
	 * Uses intelligent algorithms to:
	 * 1. Find the closest enemy target
	 * 2. Calculate optimal movement position (either for attack or to get closer)
	 * 3. Execute attack if possible
	 * 
	 * The AI prioritizes positions that allow immediate attack, falling back to
	 * chase behavior if no attack positions are available.
	 */
	private void startAITurn() {
		this.stateManager.setCurrentBattleState(StateManager.BattleState.AI_TURN_IN_PROGRESS);
		
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Character victim = alliesList.stream()
				.min(Comparator.comparing(charac -> charac.getDistanceInSquares(currentAttacker.getPosition())))
				.orElse(null);

		if (victim == null) {
			this.stateManager.setCurrentBattleState(StateManager.BattleState.TURN_COMPLETED);
			return;
		}

		Set<Point> occupiedPositions = new HashSet<>();
		alliesList.stream().forEach(character -> occupiedPositions.add(character.getPosition()));
		enemiesList.stream().forEach(character -> occupiedPositions.add(character.getPosition()));

		movementPhaseManager.graphicMovementCharacterToPoint(currentAttacker, this.availablePositions.stream()
				.filter(point -> currentAttacker.getDistanceInSquares(point) <= (currentAttacker.getSpeed() / AbstractCharacter.SPEED_TO_MOVEMENT))
				.filter(point -> !occupiedPositions.contains(point))
				.filter(point -> victim.getDistanceInSquares(point) <= currentAttacker.getRange()) // Within attack range
				.min(Comparator.comparing(point -> currentAttacker.getDistanceInSquares(point))) // Closest to current position
				.orElseGet(() -> // If no immediate attack positions available, simply chase
				availablePositions.stream()
				.filter(point -> currentAttacker.getDistanceInSquares(point) <= (currentAttacker.getSpeed() / AbstractCharacter.SPEED_TO_MOVEMENT))
				.filter(point -> !occupiedPositions.contains(point))
				.min(Comparator.comparing(point -> victim.getDistanceInSquares(point))) // Closest to victim
				.orElse(currentAttacker.getPosition())
						));

		try {
			this.controller.fight(currentAttacker, victim, alliesList, enemiesList, levelMap);
		} catch (IllegalArgumentException e) {
			// Handle the case where even the closest enemy is still out of attack range after movement
		}

		this.stateManager.setCurrentBattleState(StateManager.BattleState.TURN_COMPLETED);
	}

	/**
	 * Checks the end of a character's turn and determines next actions.
	 * Evaluates whether to:
	 * - Proceed to level end checks (if one team is eliminated)
	 * - Start a new round (if current round is complete)
	 * - Continue with the next character's turn
	 */
	private void checkEndTurn() {
		// Update character lists in case any died during the turn
		this.alliesList.removeIf(character -> !character.isAlive());
		this.enemiesList.removeIf(character -> !character.isAlive());
		
		// Update state manager with current lists
		this.stateManager.setAlliesList(this.alliesList);
		this.stateManager.setEnemiesList(this.enemiesList);

		if (this.alliesList.isEmpty() || this.enemiesList.isEmpty()) {
			// If one of the two teams is gone, proceed to level end checks
			this.stateManager.transitionToEndLevelCheck();
			return;
		} else if (this.currentTurnOrder.isEmpty()) {
			// If no one else needs to play after us, start a new round
			this.stateManager.setCurrentBattleState(StateManager.BattleState.ROUND_COMPLETED);
		} else {
			// Enemies and allies still alive and there are still characters that need to play, continue
			this.startNextTurn();
		}
	}

	/**
	 * Checks for level end conditions and handles victory/defeat scenarios.
	 * Determines if:
	 * - Level failed (all allies defeated)
	 * - Level completed (all enemies defeated)
	 * - Level continues (both teams have living members)
	 * 
	 * For completed levels, displays victory message and handles transition timing.
	 */
	private void checkEndLevel() {
		if (this.alliesList.isEmpty()) {
			// Level failed
			this.stateManager.completeLevelWithResult(false);
			this.levelMap.close();
			return;
		} else if (this.enemiesList.isEmpty()) {
			// Level completed successfully
			this.stateManager.completeLevelWithResult(true);
			
			// Check that we haven't completed all levels
			if ((this.controller.getLevelIndex() + 1) < Game.TOTAL_LEVEL) {
				this.controller.disablePauseButton();
				this.levelMap.removeAllEvent();
				this.levelMap.updateBannerMessage("You have defeated all enemies. Level Completed", true);
				// Keep the banner open for 7 seconds
				try {
					Thread.sleep(7000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			this.levelMap.close();
			return;
		}

		// Enemies and allies still alive, continue
		this.stateManager.startBattlePhase();
	}

	/**
	 * Creates a priority queue for turn order based on character speed.
	 * Only includes living characters in the turn order, with faster characters
	 * going first.
	 * 
	 * @param allies List of allied characters to include in turn order
	 * @param enemies List of enemy characters to include in turn order
	 * @return PriorityQueue ordered by character speed (highest first)
	 */
	private PriorityQueue<Character> getTurnOrder(List<Character> allies, List<Character> enemies) {
		PriorityQueue<Character> queue = new PriorityQueue<>((a, b) -> Integer.compare(b.getSpeed(), a.getSpeed()));
		queue.addAll(allies.stream().filter(Character::isAlive).toList());
		queue.addAll(enemies.stream().filter(Character::isAlive).toList());
		return queue;
	}

	// ===========================================
	// PUBLIC ACCESSORS AND STATE QUERIES
	// ===========================================

	/**
	 * Gets the state manager for this level.
	 * 
	 * @return The StateManager instance
	 */
	public StateManager getStateManager() {
		return this.stateManager;
	}

	/**
	 * Gets the level map associated with this level.
	 * 
	 * @return The LevelMap instance for this level
	 */
	public LevelMap getLevelMap() { 
		return this.levelMap; 
	}

	/**
	 * Gets the list of enemy characters in this level.
	 * 
	 * @return List of enemy characters
	 */
	public List<Character> getEnemies() { 
		return this.enemiesList; 
	}

	/**
	 * Gets the list of allied characters in this level.
	 * 
	 * @return List of allied characters
	 */
	public List<Character> getAllies() { 
		return this.alliesList; 
	}

	/**
	 * Checks if the level has been completed successfully.
	 * 
	 * @return true if level is completed (all enemies defeated), false otherwise
	 */
	public boolean isCompleted() { 
		return this.stateManager.isCompleted();
	}

	/**
	 * Checks if the level has been failed.
	 * 
	 * @return true if level is failed (all allies defeated), false otherwise
	 */
	public boolean isFailed() { 
		return this.stateManager.isFailed();
	}

	/**
	 * Sets the paused state of the level through the state manager.
	 * When paused, the level will not process turn updates until unpaused.
	 * 
	 * @param paused true to pause the level, false to unpause
	 */
	public void setLevelPaused(boolean paused) {
		if (paused) {
			this.stateManager.pauseGame();
		} else {
			this.stateManager.resumeGame();
		}
	}

	/**
	 * Sets the list of enemy characters for this level.
	 * Also updates the level map and state manager with the new enemy list.
	 * 
	 * @param enemiesList New list of enemy characters
	 */
	public void setEnemiesList(List<Character> enemiesList) {
		this.enemiesList = enemiesList;
		this.levelMap.setEnemiesList(this.enemiesList);
		this.stateManager.setEnemiesList(this.enemiesList);
	}

	/**
	 * Sets the list of allied characters for this level.
	 * Also updates the level map and state manager with the new ally list.
	 * 
	 * @param alliesList New list of allied characters
	 */
	public void setAlliesList(List<Character> alliesList) {
		this.alliesList = alliesList;
		this.levelMap.setAlliesList(this.alliesList);
		this.stateManager.setAlliesList(this.alliesList);
	}

	// ===========================================
	// CONVENIENCE METHODS FOR EXTERNAL CONTROL
	// ===========================================

	/**
	 * Checks if the level can accept user input based on current state.
	 * 
	 * @return true if user input is expected, false otherwise
	 */
	public boolean canAcceptUserInput() {
		return this.stateManager.canAcceptUserInput();
	}

	/**
	 * Gets the current character taking their turn.
	 * 
	 * @return Character currently taking their turn, or null if none
	 */
	public Character getCurrentAttacker() {
		return this.currentAttacker;
	}

	/**
	 * Gets the current turn character name from state manager.
	 * 
	 * @return Name of character currently taking their turn
	 */
	public String getCurrentCharacterTurn() {
		return this.stateManager.getCurrentCharacterTurn();
	}

	/**
	 * Gets the current round number.
	 * 
	 * @return Current round number
	 */
	public int getCurrentRoundNumber() {
		return this.stateManager.getCurrentRoundNumber();
	}

	/**
	 * Gets a summary of the current gameplay state.
	 * 
	 * @return String describing current level state
	 */
	public String getGameplaySummary() {
		return this.stateManager.getGameplaySummary();
	}

	/**
	 * Gets detailed debug information about the current state.
	 * 
	 * @return Detailed state information for debugging
	 */
	public String getDetailedStateDump() {
		return this.stateManager.getDetailedStateDump();
	}
}