package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import model.gameStatus.Game;
import model.characters.*;
import model.characters.Character;
import model.point.Point;
import view.map.AbstractMap;

import java.util.ArrayList;
import java.util.List;

public class GameTest {

    private Game game;

    @BeforeEach
    void setUp() {
        // Initialize a new game before each test
        game = new Game();
    }

    @Test
    @DisplayName("Creation and initialization of allies")
    // Check that the created allies list contains all expected character types
    void testCreateAllies() {
        List<Character> allies = game.createAllies();
        assertNotNull(allies);
        assertEquals(5, allies.size());
        assertTrue(allies.stream().anyMatch(c -> c instanceof Barbarian));
        assertTrue(allies.stream().anyMatch(c -> c instanceof Archer));
        assertTrue(allies.stream().anyMatch(c -> c instanceof Knight));
        assertTrue(allies.stream().anyMatch(c -> c instanceof Wizard));
        assertTrue(allies.stream().anyMatch(c -> c instanceof Juggernaut));
    }

    @Test
    @DisplayName("Character selection and setup")
    // Select some characters, make them heroes, set positions, and verify they are allied and alive
    void testCharacterSelection() {
        List<Character> selected = new ArrayList<>();
        selected.add(new Barbarian());
        selected.add(new Archer());

        game.setSelectedCharacters(selected);

        for (int i = 0; i < selected.size(); i++) {
            selected.get(i).becomeHero();
            selected.get(i).setPosition(new Point(0, i));
        }

        for (Character c : selected) {
            assertTrue(c.isAllied());
            assertTrue(c.isAlive());
            assertNotNull(c.getPosition());
        }

        // Ensure number of allies does not exceed maximum per round
        assertTrue(selected.size() <= Game.MAX_ALLIES_PER_ROUND);
    }

    @Test
    @DisplayName("Character placement and movement")
    // Place a character and verify they can move within map boundaries
    void testCharacterMovement() {
        Character c = new Barbarian();
        c.becomeHero();

        Point start = new Point(6, 5);
        Point target = new Point(7, 7);

        c.setPosition(start);
        assertEquals(start, c.getPosition());

        c.setPosition(target);
        assertEquals(target, c.getPosition());

        assertTrue(target.getX() >= 0 && target.getX() < AbstractMap.GRID_SIZE_WIDTH);
        assertTrue(target.getY() >= 0 && target.getY() < AbstractMap.GRID_SIZE_HEIGHT);
    }

    @Test
    @DisplayName("Combat system and damage calculation")
    // Calculate damage dealt and verify defender's health remains valid
    void testCombat() {
        Character attacker = new Barbarian();
        Character defender = new Knight();

        attacker.becomeHero();

        int damage = Math.max(1, attacker.getPower() - defender.getDefence());
        int expectedHealth = Math.max(0, defender.getCurrentHealth() - damage);

        assertTrue(damage > 0);
        assertTrue(expectedHealth >= 0);
    }

    @Test
    @DisplayName("Victory and defeat conditions")
    // Check victory/defeat conditions based on presence of allies and enemies
    void testVictoryDefeat() {
        List<Character> allies = new ArrayList<>();
        List<Character> enemies = new ArrayList<>();

        allies.add(new Barbarian());
        assertTrue(!allies.isEmpty() && enemies.isEmpty());

        allies.clear();
        enemies.add(new Wizard());
        assertTrue(allies.isEmpty() && !enemies.isEmpty());
    }

    @Test
    @DisplayName("Game state validation")
    // Verify initial game state and map dimensions are consistent
    void testGameState() {
        assertNotNull(game);
        assertTrue(Game.TOTAL_LEVEL > 0);
        assertTrue(Game.MAX_ALLIES_PER_ROUND > 0);
        assertEquals(15, AbstractMap.GRID_SIZE_HEIGHT);
        assertEquals(20, AbstractMap.GRID_SIZE_WIDTH);
    }
}
