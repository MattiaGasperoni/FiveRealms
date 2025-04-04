package view.map;

import model.characters.Character;
import javax.swing.*;
import java.util.List;

public class LevelMap extends AbstractMap{
	
	public LevelMap(List<Character> enemiesList, List<Character> alliesList) {
		super(enemiesList, alliesList);

	}
    
	@Override
    public JButton getButtonAt(int x, int y) {
        if (x >= 0 && x < GRID_SIZE && y >= 0 && y < GRID_SIZE) {
            return gridButtons[x][y];
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

	

    
    
	
}
