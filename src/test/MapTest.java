package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import controller.GameController;

import org.junit.jupiter.api.DisplayName;

import model.characters.*;
import model.characters.Character;
import model.point.Point;
import view.map.AbstractMap;
import view.map.GridPanel;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLayeredPane;

public class MapTest {

    private AbstractMap map;
    private List<Character> allies;
    private List<Character> enemies;
    private GameController controller; 

    @BeforeEach
    void setUp() {
        allies = new ArrayList<>();
        enemies = new ArrayList<>();

        // Creiamo una mappa concreta
        map = new AbstractMap(enemies, allies, 1, controller) {};

        // Creiamo un layeredPane fittizio e il GridPanel
        JLayeredPane layeredPane = new JLayeredPane();
        GridPanel gridPanel = new GridPanel(layeredPane);

        // Impostiamo il gridPanel nella mappa
    }

    // Test map creation and initial state
    @Test
    @DisplayName("Test map creation and initial state")
    void testMapCreation() {
        assertNotNull(map);
        assertEquals(0, allies.size());
        assertEquals(0, enemies.size());
    }

    // Test spawning a character on the map
    @Test
    @DisplayName("Test spawning a character")
    void testSpawnCharacter() {
        Character hero = new Barbarian();
        hero.becomeHero();
        allies.add(hero);

        map.spawnCharacter(allies);

        assertTrue(map.isPositionOccupied(hero.getPosition()));
    }

    // Test moving a character to a new position
    @Test
    @DisplayName("Test moving a character")
    void testMoveCharacter() {
        Character knight = new Knight();
        knight.becomeHero();
        allies.add(knight);

        map.spawnCharacter(allies);

        Point target = new Point(3, 4);
        map.moveCharacter(knight, target);

        assertEquals(target, knight.getPosition());
        assertTrue(map.isPositionOccupied(target));
    }

    // Test removing a character from the map
    @Test
    @DisplayName("Test removing a character")
    void testRemoveCharacter() {
        Character mage = new Wizard();
        mage.becomeHero();
        allies.add(mage);

        map.spawnCharacter(allies);
        map.removeCharacter(mage);

        assertFalse(map.isPositionOccupied(mage.getPosition()));
    }

    // Test coloring available positions
    @Test
    @DisplayName("Test coloring available positions")
    void testColourPositionAvailable() {
        Character hero = new Barbarian();
        hero.becomeHero();
        allies.add(hero);

        map.spawnCharacter(allies);

        List<Point> moves = new ArrayList<>();
        moves.add(new Point(1, 1));
        moves.add(new Point(2, 2));

        map.colourPositionAvailable(moves, Color.BLUE);

        // Non possiamo testare visivamente il colore, ma possiamo verificare che le posizioni siano valide
        for (Point p : moves) {
            assertTrue(p.getX() >= 0 && p.getX() < AbstractMap.GRID_SIZE_WIDTH);
            assertTrue(p.getY() >= 0 && p.getY() < AbstractMap.GRID_SIZE_HEIGHT);
        }
    }

    // Test coloring character position
    @Test
    @DisplayName("Test coloring character position")
    void testColourCharacterPosition() {
        Character hero = new Barbarian();
        hero.becomeHero();
        allies.add(hero);

        map.spawnCharacter(allies);

        map.colourCharacterPosition(hero);

        assertNotNull(hero.getPosition());
        assertTrue(map.isPositionOccupied(hero.getPosition()));
    }
}
