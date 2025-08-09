package model.characters;

import model.equipment.weapons.Wand;
import model.equipment.weapons.Staff;
import model.point.Point;

public class Wizard extends AbstractCharacter{

	public Wizard() {
		super(rand.nextInt(80,100), rand.nextInt(50,70), rand.nextInt(145,165), rand.nextInt(30,50)); //Random
		this.availableWeapons.add(new Wand());
		this.availableWeapons.add(new Staff());
		super.spawnWeapon();
	}
}
