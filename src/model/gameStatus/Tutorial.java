package model.gameStatus;

import java.util.List;
import model.characters.Character;
import view.map.TutorialMap;

public class Tutorial 
{
	private boolean tutorialCompleted;             // Flag che indica se il tutorial è stato completato
	private TutorialMap tutorialMap;               // Mappa del tutorial
	private List<Character> enemies;       // Lista dei nemici del tutorial
	private List<Character> allies;        // Lista degli alleati del tutorial
	
	public Tutorial(TutorialMap map, List<Character> tutorialEnemies, List<Character> tutorialAllies) 
	{
		
		this.tutorialCompleted = false;
		this.tutorialMap       = map; 
		this.enemies           = tutorialEnemies; 	
		this.allies            = tutorialAllies; 

	}

	//Metodo Pubblico per giocare il tutorial, restituisce true se il livello è stato completato, false altrimenti
    public boolean play() 
    {
    	
    	this.tutorialMap.start(); //Chiama i metodi di AbstractMap
    	
    	// Inizializza i PopUp di TutorialMap
		
    	this.tutorialMap.startPopUpTutorial(); 
		
		// Spawna i personaggi
		//this.tutorialMap.spawnCharacters(this.allies, this.enemies); 
		

		// Logica combattimento semplificata per il tutorial
		
		if(this.enemies.isEmpty())
		{
			this.tutorialCompleted = true; 
		}

		this.tutorialCompleted = true;  //TEMP
		this.tutorialMap.closeWindow(); //TEMP
		
    	return this.tutorialCompleted;
    }

}
