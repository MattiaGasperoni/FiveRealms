package model.characters.bosses;

import model.characters.Archer;
import model.characters.Character;

public class ArcherBoss extends Archer{

	private static final long serialVersionUID = 1L;

	public ArcherBoss() {
		super(); //Random
		super.setImagePath("images/characters/archer/archerBoss.png");
		super.increaseMaxHealth(0.3);
		super.increasePower(0.4);
		super.increaseDefence(0.2);
		super.increaseSpeed(0.5);
	}

	/**
	 *GIMMICK Soulburn: Deals an additional 10% of target's remaining hp as damage after hitting in a Fight
	 */
	public Character fight(Character attackedCharacter) throws IllegalArgumentException {
		Character deadCharacter = super.fight(attackedCharacter);
		if(attackedCharacter.isAlive())
			attackedCharacter.reduceCurrentHealth((int)Math.round(0.1 * attackedCharacter.getCurrentHealth()));
		return deadCharacter;
	}
}