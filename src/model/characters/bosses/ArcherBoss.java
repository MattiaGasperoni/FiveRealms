package model.characters.bosses;
import java.util.List;

import model.characters.Archer;
import model.characters.Character;
import model.point.Point;

public class ArcherBoss extends Archer{

	public ArcherBoss() {
		super(); //Random
		super.setImagePath("images/characters/archer/archerBoss.png");
		super.increaseMaxHealth(0.3);
		super.increasePower(0.4);
		super.increaseDefence(0.2);
		super.increaseSpeed(0.5);
	}
	
	//GIMMICK Soulburn: Deals an additional 10% of target's remaining hp as damage after hitting in a Fight
	public void fight(Character attackedCharacter, List<Character> alliedList, List<Character> enemyList) throws IllegalArgumentException {
		super.fight(attackedCharacter, alliedList, enemyList);
		if(attackedCharacter.isAlive())
			attackedCharacter.reduceCurrentHealth((int)Math.round(0.1 * attackedCharacter.getCurrentHealth()));
	}
}