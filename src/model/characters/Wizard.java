package model.characters;

import model.equipment.weapons.SingleShotWand;
import model.equipment.weapons.ZoneShotWand;
import model.point.Point;

public class Wizard extends AbstractCharacter{

	public Wizard(Point startingPosition) {
		super(rand.nextInt(50,70), rand.nextInt(40,60), rand.nextInt(60,80), rand.nextInt(10,30), startingPosition); //Random
		this.availableWeapons.add(new SingleShotWand());
		this.availableWeapons.add(new ZoneShotWand());
		super.spawnWeapon();
	}
}
