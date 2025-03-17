package model.characters;

import model.equipment.weapons.LongBow;
import model.equipment.weapons.ShortBow;
import model.point.Point;

public class Archer extends AbstractCharacter{

	public Archer(Point startingPosition, String image) {
		super(rand.nextInt(60,80), rand.nextInt(50,70), rand.nextInt(40,60), rand.nextInt(20,40), startingPosition, image); //Random
	}

	@Override
	public void spawnWeapon() {
		if(rand.nextInt(0,2) == 0) {
			this.setWeapon(new ShortBow());
		} else {
			this.setWeapon(new LongBow());
		}
	}
}
