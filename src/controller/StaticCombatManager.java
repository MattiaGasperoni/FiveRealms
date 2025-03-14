package controller;
import java.util.List;

import model.characters.AbstractCharacter;

public class StaticCombatManager {

	public static void fight(AbstractCharacter attackingCharacter, AbstractCharacter attackedCharacter, List<AbstractCharacter> alliedList, List<AbstractCharacter> enemyList) {
		//TODO: account for mage AoE attack, do exceptions/errors for stuff like attacking your own ally
		if(isWithinAttackRange(attackingCharacter,attackedCharacter)) {
			attackedCharacter.reduceCurrentHealth(attackingCharacter.getPower() - attackedCharacter.getDefence()); //start of combat
			
			if(attackedCharacter.isAlive() && isWithinAttackRange(attackedCharacter,attackingCharacter)) //if attacked character is still alive, it counterattacks
				attackingCharacter.reduceCurrentHealth(attackedCharacter.getPower() - attackingCharacter.getDefence());
			
			if(!attackedCharacter.isAlive()) {
				removeDeadCharacterFromList(attackedCharacter,alliedList,enemyList);
			}
				
			if(!attackingCharacter.isAlive()) {
				removeDeadCharacterFromList(attackingCharacter,alliedList,enemyList);
			}
		}
	}
	
	private static boolean isWithinAttackRange(AbstractCharacter attackingCharacter, AbstractCharacter attackedCharacter) {
		return attackingCharacter.getDistanceInSquares(attackedCharacter.getPosition()) <= attackingCharacter.getRange();
	}
	
	private static void removeDeadCharacterFromList(AbstractCharacter deadCharacter, List<AbstractCharacter> alliedList, List<AbstractCharacter> enemyList) {
		if(!deadCharacter.isAllied())
			enemyList.remove(deadCharacter);
		else
			alliedList.remove(deadCharacter);
	}
}
