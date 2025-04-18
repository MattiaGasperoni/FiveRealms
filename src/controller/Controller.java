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
 */
public class Controller {

    private final GameStateManager gameStateManager;

    public Controller() {
        this.gameStateManager = new GameStateManager();
    }

    public void saveGame(List<Character> allies, List<Character> enemies, int level) throws IOException {
        gameStateManager.saveStatus(allies, enemies, level);
    }

    public GameState loadGame() throws IOException {
        return gameStateManager.loadStatus();
    }
    
    // Move
    public void move(AbstractMap map, Character character, Point point) {
    	map.moveCharacter(character, point);
    	character.moveTo(point);
    }
    
}
