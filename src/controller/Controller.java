package controller;

import java.io.IOException;
import java.util.List;
import model.characters.Character;
import model.gameStatus.GameState;
import model.gameStatus.GameStateManager;
import model.point.Point;
import view.map.AbstractMap;

/**
 * Controller class that coordinates user actions with the game logic.
 * It interacts with the GameStateManager to handle game saving/loading,
 * and manages the movement of characters on the map.
 */
public class Controller {

    private final GameStateManager gameStateManager;

    /**
     * Constructor for the Controller class.
     * Initializes the GameStateManager instance to handle game state management.
     */
    public Controller() {
        this.gameStateManager = new GameStateManager();
    }

    /**
     * Saves the current game state, including allies, enemies, and the level.
     * @param allies List of ally characters.
     * @param enemies List of enemy characters.
     * @param level The current game level.
     * @throws IOException If an error occurs during saving.
     */
    public void saveGame(List<Character> allies, List<Character> enemies, int level) throws IOException {
        gameStateManager.saveStatus(allies, enemies, level);  // Delegate saving to GameStateManager
    }

    /**
     * Loads the most recent saved game state.
     * @return The loaded GameState object.
     * @throws IOException If an error occurs during loading.
     */
    public GameState loadGame() throws IOException {
        return gameStateManager.loadStatus();  // Delegate loading to GameStateManager
    }

    // Movement functionality
    
    /**
     * Moves the given character to a new point on the map.
     * @param map The game map where the character resides.
     * @param character The character to be moved.
     * @param point The destination point on the map.
     */
    public void move(AbstractMap map, Character character, Point point) {
        map.moveCharacter(character, point);  // Move the character on the map
        character.moveTo(point);  // Update the character's position
    }
}
