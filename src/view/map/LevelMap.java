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
	

	public static void main(String[] args) 
    {
        SwingUtilities.invokeLater(() -> 
       {
    	   Random rand = new Random();
    	   List<Character> alliesList = new ArrayList<>();
    	   alliesList.add(new Barbarian(new Point(rand.nextInt(0,6),rand.nextInt(0,3))));
    	   alliesList.add(new Archer(new Point(rand.nextInt(0,6),rand.nextInt(0,3))));
    	   alliesList.add(new Barbarian(new Point(rand.nextInt(0,6),rand.nextInt(0,3))));


    	   List<Character> enemiesList = new ArrayList<>();
    	   enemiesList.add(new Barbarian(new Point(rand.nextInt(0,6),rand.nextInt(0,3))));
    	   enemiesList.add(new Archer(new Point(rand.nextInt(0,6),rand.nextInt(0,3))));
    	   enemiesList.add(new Barbarian(new Point(rand.nextInt(0,6),rand.nextInt(0,3))));

    	   // Prima erano tutti emptyEnemies

            // Inizializza la mappa con le liste vuote
            LevelMap emptyMap = new LevelMap(enemiesList, alliesList, 5);
            
            emptyMap.start();
            
            
        });
    }

}
