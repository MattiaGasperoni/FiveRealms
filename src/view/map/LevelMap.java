package view.map;

import model.characters.Character;
import javax.swing.*;
import java.util.List;

public class LevelMap extends AbstractMap{
	
	public LevelMap(List<Character> enemiesList, List<Character> alliesList, int numLevel) {
		super(enemiesList, alliesList, numLevel);
		super.start();
	}
	
    
	@Override
    public JButton getButtonAt(int x, int y) {
        if (x >= 0 && x < GRID_SIZE && y >= 0 && y < GRID_SIZE) {
            return gridPanel.getGridButtons()[x][y];
        }
        return null;
    }

    //metodo per chiudere il frame manualmente
    @Override
    public void closeWindow() 
    {
        if (frame != null) {
            frame.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Liste vuote di nemici e alleati
            List<Character> emptyEnemies = List.of();
            List<Character> emptyAllies = List.of();

            // Inizializza la mappa con le liste vuote
            LevelMap emptyMap = new LevelMap(emptyEnemies, emptyAllies, 5);
        });
    }


    
    
	
}
