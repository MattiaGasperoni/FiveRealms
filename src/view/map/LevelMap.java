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
    	   alliesList.add(new Barbarian(new Point(2,5)));
    	   alliesList.add(new Archer(new Point(5,9)));
    	   alliesList.add(new Barbarian(new Point(2,13)));


    	   List<Character> enemiesList = new ArrayList<>();
    	   enemiesList.add(new Barbarian(new Point(16,5)));
    	   enemiesList.add(new Archer(new Point(15,9)));
    	   enemiesList.add(new Barbarian(new Point(16,13)));


           // Inizializza la mappa con le liste vuote
           LevelMap emptyMap = new LevelMap(enemiesList, alliesList, 5);
            
           emptyMap.start();
           
           SwingUtilities.invokeLater(() -> {
        	    emptyMap.spawnCharacter(enemiesList);
        	    emptyMap.spawnCharacter(alliesList); 
        	});

           
            
        });
    }

}
