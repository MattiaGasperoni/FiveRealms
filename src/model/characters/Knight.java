package model.characters;

import model.equipment.weapons.LongSword;
import model.equipment.weapons.Spear;
import model.point.Point;

public class Knight extends AbstractCharacter{

	public Knight(Point startingPosition, String image) {
		super(rand.nextInt(80,90), rand.nextInt(40,50), rand.nextInt(70,80), rand.nextInt(50,60), startingPosition, image); //Random
		this.availableWeapons.add(new Spear());
		this.availableWeapons.add(new LongSword());
		super.spawnWeapon();
	}
}
