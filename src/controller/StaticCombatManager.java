package controller;

import java.util.List;

import model.characters.*;
import model.characters.Character;
import model.equipment.weapons.ZoneShotWand;

public class StaticCombatManager {

	public static void fight(Character attackingCharacter, Character attackedCharacter, List<Character> alliedList, List<Character> enemyList) {
		if (isWithinAttackRange(attackingCharacter, attackedCharacter) && attackingCharacter.isAllied() != attackedCharacter.isAllied()) {
			if (attackingCharacter.getWeapon() instanceof ZoneShotWand) { //AOE
				List<Character> relevantList;
					if(attackingCharacter.isAllied()) {
						relevantList = enemyList;
					} else {
						relevantList = alliedList;
					}
					
					relevantList.stream()
					.filter(charact -> charact.getDistanceInSquares(attackedCharacter.getPosition()) <= 2) //gotta find a better way to get the radius of AOE
					.forEach(charact -> charact.reduceCurrentHealth(attackingCharacter.getPower())); //AOE victims can't counterattack
			} else {
				attackedCharacter.reduceCurrentHealth(attackingCharacter.getPower() - attackedCharacter.getDefence()); // start of combat

				if (attackedCharacter.isAlive() && isWithinAttackRange(attackedCharacter, attackingCharacter)) // //if attacked character is still alive and his weapon can reach you, it counterattacks
					attackingCharacter.reduceCurrentHealth(attackedCharacter.getPower() - attackingCharacter.getDefence());

				if (!attackedCharacter.isAlive()) {
					removeDeadCharacterFromList(attackedCharacter, alliedList, enemyList);
					attackingCharacter.gainExperience(500);
				}

				if (!attackingCharacter.isAlive()) {
					removeDeadCharacterFromList(attackingCharacter, alliedList, enemyList);
					attackedCharacter.gainExperience(500);
				}
			}
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
