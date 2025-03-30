package controller;

import java.util.List;

import model.characters.*;
import model.characters.Character;
import model.equipment.weapons.ZoneShotWand;

public class StaticCombatManager {

	public static void fight(Character attackingCharacter, Character attackedCharacter, List<Character> alliedList, List<Character> enemyList) throws IllegalArgumentException {
		if(attackingCharacter.isAllied() == attackedCharacter.isAllied())
			throw new IllegalArgumentException("You cannot attack someone belonging to your own faction!");
		if (!isWithinAttackRange(attackingCharacter, attackedCharacter))
			throw new IllegalArgumentException("You cannot attack someone outside of your weapon's attack range!");
		
		if (attackingCharacter.getWeapon() instanceof ZoneShotWand) { //AOE
			List<Character> relevantList;
			if(attackingCharacter.isAllied()) {
				relevantList = enemyList;
			} else {
				relevantList = alliedList;
			}

			relevantList.stream()
			.filter(charact -> charact.getDistanceInSquares(attackedCharacter.getPosition()) <= 2) //need to find a better way to get the radius of AOE
			.forEach(charact -> charact.reduceCurrentHealth(attackingCharacter.getPower())); //AOE victims can't counterattack
		} else {
			attackedCharacter.reduceCurrentHealth(attackingCharacter.getPower() - attackedCharacter.getDefence()); // start of combat

			if (attackedCharacter.isAlive() && isWithinAttackRange(attackedCharacter, attackingCharacter)) // //if attacked character is still alive and his weapon can reach you, it counterattacks
				attackingCharacter.reduceCurrentHealth(attackedCharacter.getPower() - attackingCharacter.getDefence());

			if (!attackedCharacter.isAlive()) {
				removeDeadCharacterFromList(attackedCharacter, alliedList, enemyList);
				attackingCharacter.gainExperience(AbstractCharacter.EXP_LEVELUP_THRESHOLD/3);
			}

			if (!attackingCharacter.isAlive()) {
				removeDeadCharacterFromList(attackingCharacter, alliedList, enemyList);
				attackedCharacter.gainExperience(AbstractCharacter.EXP_LEVELUP_THRESHOLD/3);
			}
			//need to add random potion drop
		}

	}

	private static boolean isWithinAttackRange(Character attackingCharacter, Character attackedCharacter) {
		return attackingCharacter.getDistanceInSquares(attackedCharacter.getPosition()) <= attackingCharacter
				.getRange();
	}

	private static void removeDeadCharacterFromList(Character deadCharacter, List<Character> alliedList, List<Character> enemyList) {
		if (!deadCharacter.isAllied())
			enemyList.remove(deadCharacter);
		else
			alliedList.remove(deadCharacter);
	}
}
