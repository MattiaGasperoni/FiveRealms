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
import model.point.Point;
import view.*;
import view.map.AbstractMap;
import view.map.LevelMap;

/**
 * Represents a game level that manages the battle phases, turn order, and win/lose conditions.
 * This class handles the complete lifecycle of a level, including initialization, battle phases,
 * character turns (both player and AI), and level completion checks.
 */
public class GameLevel implements Level 
{
	/**
	 * Enumeration representing the different phases of a level.
	 */
	private enum LevelPhase {
		/** Active battle phase where characters take turns */
		BATTLE_PHASE,
		/** End-of-round check for victory/defeat conditions */
		CHECK_END_LEVEL,
		/** Level has ended (victory or defeat) */
		DONE
	}

	/**
	 * Enumeration representing the different states within a battle phase.
	 */
	private enum RoundState {
		/** Preparing to start a new round */
		INITIALIZING_TURN,
		/** Waiting for user to move their character */
		WAITING_FOR_MOVEMENT,
		/** Waiting for user to select a target */
		WAITING_FOR_TARGET,
		/** Current turn has been completed */
		TURN_COMPLETED
	}

	private LevelPhase currentLevelPhase;
	private RoundState currentTurnState;
	private List<Character> enemiesList;
	private List<Character> alliesList;
	private boolean levelCompleted;
	private boolean levelFailed;
	private boolean levelPause;
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
	 */
	public GameLevel(LevelMap map, GameController controller) {
		this.levelMap = map;
		this.controller = controller;
		this.movementPhaseManager = new BattlePhaseView(this.levelMap, this.controller);

		this.enemiesList = this.levelMap.getEnemiesList();
		this.alliesList = this.levelMap.getAlliesList();

		this.levelCompleted = false;
		this.levelFailed = false;
		this.levelPause = false;

		// Initialize available positions for AI turn calculations
		this.availablePositions = new ArrayList<>();
		for (int i = 0; i < AbstractMap.GRID_SIZE_WIDTH; i++) {
			for (int j = 0; j < AbstractMap.GRID_SIZE_HEIGHT; j++) {
				availablePositions.add(new Point(i, j));
			}
		}
	}

	/**
	 * Starts the level by showing the map, spawning characters, and initializing the battle phase.
	 * This method sets up the initial state of the level and prepares it for player interaction.
	 * @return 
	 * 
	 * @throws IOException If there's an error during level initialization or map display
	 */
	public boolean play() throws IOException {
		this.levelMap.show();

		this.levelMap.spawnCharacter(this.enemiesList);
		this.levelMap.spawnCharacter(this.alliesList);

		this.currentLevelPhase = LevelPhase.BATTLE_PHASE;
		this.currentTurnState = RoundState.INITIALIZING_TURN;
		
		return this.levelCompleted;
	}

	/**
	 * Main update method that advances the level state based on the current phase.
	 * This method should be called continuously in the game loop to progress the level.
	 */
	public void update() {
		if (this.currentLevelPhase == null)
			return;

		switch (this.currentLevelPhase) {
		case BATTLE_PHASE:
			this.handleBattlePhase();
			break;

		case CHECK_END_LEVEL:
			this.checkEndLevel();
			break;

		case DONE:   
			break;

		default:
			throw new IllegalStateException("Unknown Phase: " + this.currentLevelPhase);
		}
	}

	/**
	 * Handles the battle phase logic based on the current turn state.
	 * Manages the flow between different battle states while respecting pause conditions
	 * and user input requirements.
	 */
	private void handleBattlePhase() {
		if (this.levelPause || this.currentTurnState == RoundState.WAITING_FOR_MOVEMENT || this.currentTurnState == RoundState.WAITING_FOR_TARGET) {
			// Waiting for user input
			return; 
		}

		switch (this.currentTurnState) {
		case INITIALIZING_TURN:
			this.initializeBattleRound();
			break;

		case TURN_COMPLETED:
			this.checkEndTurn();
			break;

		default:
			throw new IllegalStateException("Unknown battle state: " + this.currentTurnState);
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
			this.currentTurnState = RoundState.TURN_COMPLETED;
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
			this.currentTurnState = RoundState.TURN_COMPLETED;
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
			this.currentTurnState = RoundState.TURN_COMPLETED;
			return;
		}

		// If there are living characters, set the new attacker and start the turn
		this.currentAttacker = nextAttacker;
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
		this.currentTurnState = RoundState.WAITING_FOR_MOVEMENT;
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
		this.currentTurnState = RoundState.WAITING_FOR_TARGET;

		this.movementPhaseManager.chooseTarget(this.enemiesList, this.currentAttacker, () -> {
			this.currentTurnState = RoundState.TURN_COMPLETED;
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
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Character victim = alliesList.stream()
				.min(Comparator.comparing(charac -> charac.getDistanceInSquares(currentAttacker.getPosition())))
				.orElse(null);

		if (victim == null) {
			this.currentTurnState = RoundState.TURN_COMPLETED;
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

		this.currentTurnState = RoundState.TURN_COMPLETED;
	}

	/**
	 * Checks the end of a character's turn and determines next actions.
	 * Evaluates whether to:
	 * - Proceed to level end checks (if one team is eliminated)
	 * - Start a new round (if current round is complete)
	 * - Continue with the next character's turn
	 */
	private void checkEndTurn() {
		if (this.alliesList.isEmpty() || this.enemiesList.isEmpty()) {
			// If one of the two teams is gone, proceed to level end checks
			this.currentLevelPhase = LevelPhase.CHECK_END_LEVEL;
			return;
		} else if (this.currentTurnOrder.isEmpty()) {
			// If no one else needs to play after us, start a new turn
			this.currentLevelPhase = LevelPhase.BATTLE_PHASE;
			this.currentTurnState = RoundState.INITIALIZING_TURN;
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
			this.levelFailed = true;
			this.currentLevelPhase = LevelPhase.DONE;
			this.levelMap.close();
			return;
		} else if (this.enemiesList.isEmpty()) {
			this.levelCompleted = true;
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
			this.currentLevelPhase = LevelPhase.DONE;
			this.levelMap.close();
			return;
		}

		// Enemies and allies still alive, continue
		currentLevelPhase = LevelPhase.BATTLE_PHASE;
		currentTurnState = RoundState.INITIALIZING_TURN;
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
		return this.levelCompleted; 
	}

	/**
	 * Checks if the level has been failed.
	 * 
	 * @return true if level is failed (all allies defeated), false otherwise
	 */
	public boolean isFailed() { 
		return this.levelFailed; 
	}

	/**
	 * Sets the paused state of the level.
	 * When paused, the level will not process turn updates until unpaused.
	 * 
	 * @param paused true to pause the level, false to unpause
	 */
	public void setLevelPaused(boolean paused) {
		this.levelPause = paused;
	}

	/**
	 * Sets the list of enemy characters for this level.
	 * Also updates the level map with the new enemy list.
	 * 
	 * @param enemiesList New list of enemy characters
	 */
	public void setEnemiesList(List<Character> enemiesList) {
		this.enemiesList = enemiesList;
		this.levelMap.setEnemiesList(this.enemiesList);
	}

	/**
	 * Sets the list of allied characters for this level.
	 * Also updates the level map with the new ally list.
	 * 
	 * @param alliesList New list of allied characters
	 */
	public void setAlliesList(List<Character> alliesList) {
		this.alliesList = alliesList;
		this.levelMap.setAlliesList(this.alliesList);
	}
}