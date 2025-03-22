package controller;

import java.util.List;

import model.characters.Character;

public class StaticAILogicManager {
	public static void whatToDo(Character character, List<Character> alliedList, List<Character> enemyList) {
		//part one: movement
		
		
		//part twp: attacking
		Character victim = alliedList.stream()
		.filter(charac -> character.getDistanceInSquares(charac.getPosition()) <= character.getRange())
		.min(null).orElse(null); //need comparator

		StaticCombatManager.fight(character, victim, alliedList, enemyList);
	}
}
