package view.map;

import model.characters.Archer;
import model.characters.Barbarian;
import model.characters.Character;
import model.point.Point;

import javax.swing.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LevelMap extends AbstractMap{
	
	public LevelMap(List<Character> enemiesList, List<Character> alliesList, int numLevel) {
		super(enemiesList, alliesList, numLevel);
	}
	
    
	@Override
    public JButton getButtonAt(int x, int y) {
        if (x >= 0 && x < GRID_SIZE && y >= 0 && y < GRID_SIZE) {
            return gridPanel.getGridButtons()[x][y];
        }
        return null;
    }

    public static void main(String[] args) 
    {
        SwingUtilities.invokeLater(() -> 
       {
    	   Random rand = new Random();
    	   List<Character> emptyEnemies = new ArrayList<>();
    	   emptyEnemies.add(new Barbarian(new Point(rand.nextInt(0,6),rand.nextInt(0,3))));
    	   emptyEnemies.add(new Archer(new Point(rand.nextInt(0,6),rand.nextInt(0,3))));

    	   List<Character> emptyAllies = new ArrayList<>();
    	   emptyAllies.add(new Barbarian(new Point(rand.nextInt(0,6),rand.nextInt(0,3))));
    	   emptyAllies.add(new Archer(new Point(rand.nextInt(0,6),rand.nextInt(0,3))));


            // Inizializza la mappa con le liste vuote
            LevelMap emptyMap = new LevelMap(emptyEnemies, emptyAllies, 5);
            emptyMap.start();
        });
    }

}
