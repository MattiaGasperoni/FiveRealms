package view.map;

import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

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
	public static void spawnCharacter(JLayeredPane layeredPane, List<Character> allies, List<Character> enemies, int row, int col) 
	{
	    Point relevantPosition = new Point(row, col);
	    Character characterInPosition;

	    // Trova il personaggio nella posizione data
	    characterInPosition = allies.stream()
	            .filter(character -> character.getPosition().equals(relevantPosition))
	            .findFirst()
	            .orElse(null);

	    if (characterInPosition == null) {
	        System.out.println("Nessun personaggio trovato per la posizione: " + relevantPosition);

	        characterInPosition = enemies.stream()
	                .filter(character -> character.getPosition().equals(relevantPosition))
	                .findFirst()
	                .orElse(null);
	    }

	    System.out.println("Spawning character at position: " + relevantPosition); // Debug: stampa la posizione del personaggio

	    // Se esiste un personaggio, aggiungilo al JLayeredPane come componente visibile
	    if (characterInPosition != null) {
	        // Crea un'etichetta con l'immagine del personaggio
	        JLabel characterLabel = new JLabel(new ImageIcon(characterInPosition.getImage()));
	        
	        // Imposta la posizione della label nel JLayeredPane
	        characterLabel.setBounds(col * AbstractMap.BUTTON_SIZE, row * AbstractMap.BUTTON_SIZE,
	                AbstractMap.BUTTON_SIZE, AbstractMap.BUTTON_SIZE);

	        // Aggiungi il personaggio al livello 2 (sopra la griglia, ma sotto altri elementi)
	        layeredPane.add(characterLabel, Integer.valueOf(2)); // Livello 2 per i personaggi
	    }
	    
	    layeredPane.revalidate();
	    layeredPane.repaint();

	}



}
