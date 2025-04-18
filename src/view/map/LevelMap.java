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
		System.out.println("Java version:"+System.getProperty("java.version"));

        SwingUtilities.invokeLater(() ->  
       {
    	   Random rand = new Random();
    	   List<Character> alliesList = new ArrayList<>();
    	   alliesList.add(new Barbarian());
    	   alliesList.add(new Archer());
    	   alliesList.add(new Barbarian());

    	   for(int i = 0; i < 3; i++) {
    		   alliesList.get(i).becomeHero();
    	   }
    	   
    	   List<Character> enemiesList = new ArrayList<>();
    	   enemiesList.add(new Barbarian());
    	   enemiesList.add(new Archer());
    	   enemiesList.add(new Barbarian());
    	   
    	   
           // Inizializza la mappa con le liste vuote
           LevelMap emptyMap = new LevelMap(enemiesList, alliesList, 5);
            
           emptyMap.start();
           
    	    emptyMap.spawnCharacter(enemiesList);
    	    emptyMap.spawnCharacter(alliesList); 
            
        });
    }

}
