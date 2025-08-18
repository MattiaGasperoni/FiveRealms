package model.characters;

import model.equipment.weapons.Axe;
import model.equipment.weapons.ShortSword;

public class Juggernaut extends AbstractCharacter{

	public Juggernaut() {
		super(rand.nextInt(160,180), rand.nextInt(40,60), rand.nextInt(135,155), rand.nextInt(110,130)); //Random
		this.availableWeapons.add(new ShortSword());
		this.availableWeapons.add(new Axe());
		super.spawnWeapon();
	}
}
