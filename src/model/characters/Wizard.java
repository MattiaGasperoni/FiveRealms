package model.characters;

import model.equipment.weapons.Wand;
import model.equipment.weapons.Staff;
import model.point.Point;

public class Wizard extends AbstractCharacter{

	public Wizard() {
		super(rand.nextInt(50,70), rand.nextInt(40,60), rand.nextInt(60,80), rand.nextInt(10,30)); //Random
		this.availableWeapons.add(new Wand());
		this.availableWeapons.add(new Staff());
		super.spawnWeapon();
	}
}
