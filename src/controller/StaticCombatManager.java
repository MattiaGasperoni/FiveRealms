package controller;
import java.util.List;

import model.characters.*;
import model.characters.Character;

public class StaticCombatManager {

	public static void fight(Character attackingCharacter, Character attackedCharacter, List<Character> alliedList, List<Character> enemyList) {
		//TODO: account for mage AoE attack, do exceptions/errors for stuff like attacking your own ally
		if(isWithinAttackRange(attackingCharacter,attackedCharacter)) {
			attackedCharacter.reduceCurrentHealth(attackingCharacter.getPower() - attackedCharacter.getDefence()); //start of combat
			
			if(attackedCharacter.isAlive() && isWithinAttackRange(attackedCharacter,attackingCharacter)) //if attacked character is still alive, it counterattacks
				attackingCharacter.reduceCurrentHealth(attackedCharacter.getPower() - attackingCharacter.getDefence());
			
			if(!attackedCharacter.isAlive()) {
				removeDeadCharacterFromList(attackedCharacter,alliedList,enemyList);
				attackingCharacter.gainExperience(500);
			}
				
			if(!attackingCharacter.isAlive()) {
				removeDeadCharacterFromList(attackingCharacter,alliedList,enemyList);
				attackedCharacter.gainExperience(500);
			}
		}
	}
	
	private static boolean isWithinAttackRange(Character attackingCharacter, Character attackedCharacter) {
		return attackingCharacter.getDistanceInSquares(attackedCharacter.getPosition()) <= attackingCharacter.getRange();
	}
	
	private static void removeDeadCharacterFromList(Character deadCharacter, List<Character> alliedList, List<Character> enemyList) {
		if(!deadCharacter.isAllied())
			enemyList.remove(deadCharacter);
		else
			alliedList.remove(deadCharacter);
	}
}
