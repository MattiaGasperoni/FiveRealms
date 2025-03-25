package controller;
import java.util.Comparator;
import java.util.Comparator;
import java.util.List;

import model.characters.Character;

public class StaticAILogicManager {
	public static void whatToDo(Character character, List<Character> alliedList, List<Character> enemyList) {
		//pick target
		Character victim = alliedList.stream()
		.min(Comparator.comparing(charac -> charac.getDistanceInSquares(character.getPosition()))) //NOTE: If that's the wrong order (hard to test right now), put .reversed() on it.
		.orElse(null);
		
		//part one: movement
		character.moveTo(null); //this is hard.
		
		//part twp: attacking
		StaticCombatManager.fight(character, victim, alliedList, enemyList);
	}
}
