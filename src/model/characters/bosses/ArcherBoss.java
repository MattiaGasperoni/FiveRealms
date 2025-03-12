package model.characters.bosses;

import model.characters.AbstractCharacter;
import model.characters.Archer;
import model.point.Point;

public class ArcherBoss extends Archer{

	public ArcherBoss(Point startingPosition) {
		super(startingPosition, "images/characters/archer/archerBoss.png"); //Random
		this.increaseMaxHealth(0.3);
		this.increasePower(0.4);
		this.increaseDefence(0.2);
		this.increaseSpeed(0.5);
	}
	
	@Override
	public void setWeapon() {
	}
}