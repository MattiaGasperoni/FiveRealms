package controller;
import model.characters.Character;
public class CombatManager {

	//NOTE: two ways to do this, this can be a static inner class of Level if this should handle character removal from list OR character removal is handled within Level and NOT here
	//same goes for attack range/distance validation
	public static void fight(Character attackingCharacter, Character attackedCharacter) {
		//WIP
		//if range is valid then
		attackedCharacter.reduceHealth(attackingCharacter.getPower() - attackedCharacter.getDefence()); //start of combat
		if(attackedCharacter.isAlive()) //if attacked character is still alive, it counterattacks
			attackingCharacter.reduceHealth(attackedCharacter.getPower() - attackingCharacter.getDefence());
		
		if(!attackedCharacter.isAlive()) {
			//delete this character from the list of living ones
		}
			
		if(!attackingCharacter.isAlive()) {
				//delete this character from the list of living ones
		}
		
	}
}
