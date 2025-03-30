package model.characters;

import model.equipment.weapons.Axe;
import model.equipment.weapons.ShortSword;
import model.point.Point;

public class Juggernaut extends AbstractCharacter{

	public Juggernaut(Point startingPosition) {
		super(rand.nextInt(80,100), rand.nextInt(10,30), rand.nextInt(50,70), rand.nextInt(70,90), startingPosition); //Random
		this.availableWeapons.add(new ShortSword());
		this.availableWeapons.add(new Axe());
		super.spawnWeapon();
	}
}
