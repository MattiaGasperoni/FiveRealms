package view.map;

import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import model.characters.Character;
import model.point.Point;

/**
 * Class responsible for spawning characters on the game grid.
 * It sets the character's image on the corresponding button or makes the button transparent if no character is present.
 */
public class CharacterSpawner 
{
    /**
     * Spawns a character on the given button based on its position on the grid.
     * If a character is present at the specified grid position, the button will display the character's image.
     * If no character is present, the button will become transparent.
     *
     * @param button The button on the grid where the character will be spawned.
     * @param row The row index of the button on the grid.
     * @param col The column index of the button on the grid.
     * @param allies The list of allied characters.
     * @param enemies The list of enemy characters.
     */
	public static void spawnCharacter(JButton button, int row, int col, List<Character> allies, List<Character> enemies) {
		Point relevantPosition = new Point(row, col);
        Character characterInPosition;

        // Find character at the given position
        characterInPosition = allies.stream()
                .filter(character -> character.getPosition().equals(relevantPosition))
                .findFirst()
                .orElse(null);

        if (characterInPosition == null) {
            characterInPosition = enemies.stream()
                    .filter(character -> character.getPosition().equals(relevantPosition))
                    .findFirst()
                    .orElse(null);
        }

        // Set icon if character exists, otherwise make the button visible without icon
        if (characterInPosition != null) {
            button.setIcon(new ImageIcon(characterInPosition.getImage())); // Set character's image
        } else {
            button.setVisible(true); // Ensure button visibility
        }
    }


}
