package model.characters.bosses;
import java.util.List;

import model.characters.Character;
import model.characters.Knight;
import model.point.Point;

public class KnightBoss extends Knight{
	private final double HEALING_PERCENTAGE = 0.05;

	public KnightBoss() {
		super(); //Random
		super.setImagePath("images/characters/knight/knightBoss.png");
		super.increaseMaxHealth(0.3);
		super.increasePower(0.3);
		super.increaseDefence(0.3);
		super.increaseSpeed(0.3);
	}
	
	//GIMMICK Second Wind: Regains 5% of max hp upon moving or initiating a Fight.
	@Override
	public void moveTo(Point point) throws IllegalArgumentException {
		super.moveTo(point);
		super.increaseCurrentHealth(this.HEALING_PERCENTAGE);
	}

	@Override
	public Character fight(Character attackedCharacter) throws IllegalArgumentException {
		Character deadCharacter = super.fight(attackedCharacter);
		super.increaseCurrentHealth(this.HEALING_PERCENTAGE);
		return deadCharacter;
	}
}
