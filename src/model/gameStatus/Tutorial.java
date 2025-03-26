package model.gameStatus;

import java.util.List;
import model.characters.Character;
import view.map.TutorialMap;

public class Tutorial 
{
	private boolean tutorialCompleted;
	
	
	public Tutorial(TutorialMap tutorialMap, List<Character> tutorialEnemies, List<Character> tutorialAllies) 
	{
		
		this.tutorialCompleted = false;
		
	}

	//Metodo Pubblico per giocare il tutorial, restituisce true se il livello è stato completato, false altrimenti
    public boolean play() 
    {
    	return this.tutorialCompleted;
    }

}
