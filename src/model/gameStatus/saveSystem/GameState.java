package model.gameStatus.saveSystem;

import model.characters.Character;
import java.io.Serializable;
import java.util.List;
import java.util.Collections;
import java.util.Objects;

/**
 * Class representing the state of the game at a specific moment.
 */
public class GameState implements Serializable 
{
    private static final long serialVersionUID = 1L;

    private final int level;
    private final List<Character> allies;
    private final List<Character> enemies;
    
    /*
     * Eventualmente aggiungere la data di salvataggio e il nome/descrizione del salvataggio
     */

    /**
     * Constructs a new GameState.
     *
     * @param level   The current level of the game.
     * @param allies  The list of allied characters. If null, it will be initialized as an empty list.
     * @param enemies The list of enemy characters. If null, it will be initialized as an empty list.
     */
    public GameState(int level, List<Character> allies, List<Character> enemies) 
    {
        this.level   = level;
        this.allies  = Collections.unmodifiableList(Objects.requireNonNullElse(allies, Collections.emptyList()));
        this.enemies = Collections.unmodifiableList(Objects.requireNonNullElse(enemies, Collections.emptyList()));
    }

    /**
     * Gets the current level of the game.
     *
     * @return The current level.
     */
    public int getLevel() {
        return this.level;
    }

    /**
     * Gets an unmodifiable copy of the list of allied characters.
     *
     * @return The list of allies.
     */
    public List<Character> getAllies() {
        return List.copyOf(allies);
    }

    /**
     * Gets an unmodifiable copy of the list of enemy characters.
     *
     * @return The list of enemies.
     */
    public List<Character> getEnemies() {
        return List.copyOf(enemies);
    }

    /**
     * Checks if this GameState is equal to another object.
     *
     * @param obj The object to compare with.
     * @return True if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) 
    {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        GameState gameState = (GameState) obj;
        return level == gameState.level &&
               Objects.equals(allies, gameState.allies) &&
               Objects.equals(enemies, gameState.enemies);
    }

    /**
     * Computes the hash code for this GameState.
     *
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(level, allies, enemies);
    }

    /**
     * Returns a string representation of the GameState.
     *
     * @return A string describing the current game state.
     */
    @Override
    public String toString() 
    {
        return String.format("Level: %d%nAllies: %s%nEnemies: %s",
                level,
                allies.isEmpty() ? "None" : String.join(", ", allies.toString()),
                enemies.isEmpty() ? "None" : String.join(", ", enemies.toString())
        );
    }
}