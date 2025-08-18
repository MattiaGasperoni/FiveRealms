package model.characters;

import model.equipment.weapons.Axe;
import model.equipment.weapons.LongSword;

public class Barbarian extends AbstractCharacter{

	public Barbarian() {
		super(rand.nextInt(160,180), rand.nextInt(30,50), rand.nextInt(145,165), rand.nextInt(40,60)); //Random
		this.availableWeapons.add(new Axe());
		this.availableWeapons.add(new LongSword());
		super.spawnWeapon();
	}
}
