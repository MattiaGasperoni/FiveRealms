package view.map;

import model.characters.Character;
import controller.GameController;
import java.util.List;

public class LevelMap extends AbstractMap{
	
	public LevelMap(List<Character> enemiesList, List<Character> alliesList, int numLevel, GameController controller) 
	{
		super(enemiesList, alliesList, numLevel, controller);
	}
}
