package model.gameStatus;

import java.util.List;

import controller.Controller;
import model.characters.Character;
import view.map.TutorialMap;

public class Tutorial 
{
	private boolean tutorialCompleted;             // Flag che indica se il tutorial è stato completato
	private TutorialMap tutorialMap;               // Mappa del tutorial
	private List<Character> enemiesList;       // Lista dei nemici del tutorial
	private List<Character> alliesList;        // Lista degli alleati del tutorial
	
	public Tutorial(TutorialMap map, Controller controller) 
	{
		
		this.tutorialCompleted = false;
		this.tutorialMap       = map; 
        this.enemiesList       = this.tutorialMap.getEnemiesList();
        this.alliesList        = this.tutorialMap.getAlliesList();

	}

	//Metodo Pubblico per giocare il tutorial, restituisce true se il livello è stato completato, false altrimenti
    public boolean play() 
    {
    	
    	// Faccio comparire la mappa del tutorial
    	this.tutorialMap.start();
    	
    	System.out.print(" Start tutorial ->");
    	
    	//call ai pop-up introduttivi
    	this.tutorialMap.startPopUpTutorial(); 
		
		// Spawn deii personaggi
    	//call al pup-up che ti dice dove spownano i personaggi alleati
		this.tutorialMap.spawnCharacter(this.alliesList); 
		System.out.print(" Spawn alleati ->");
		//call al pup-up che ti dice dove spownano i personaggi nemici
		this.tutorialMap.spawnCharacter(this.enemiesList); 
		System.out.print(" Spawn nemici ->");
		
		
		// Logica combattimento semplificata per il tutorial


		this.tutorialCompleted = true;  //TEMP
		//this.tutorialMap.closeWindow(); //TEMP
		
    	return this.tutorialCompleted;
    }

}
