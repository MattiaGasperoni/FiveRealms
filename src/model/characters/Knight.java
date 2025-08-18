package model.characters;

import model.equipment.weapons.LongSword;
import model.equipment.weapons.Spear;

public class Knight extends AbstractCharacter{

	public Knight() {
		super(rand.nextInt(120,140), rand.nextInt(50,70), rand.nextInt(130,150), rand.nextInt(70,90)); //Random
		this.availableWeapons.add(new Spear());
		this.availableWeapons.add(new LongSword());
		super.spawnWeapon();
	}
}
