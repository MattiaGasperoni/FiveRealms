package model.characters;

import model.equipment.weapons.Axe;
import model.equipment.weapons.LongSword;
import model.point.Point;

public class Barbarian extends AbstractCharacter{

	public Barbarian(Point startingPosition, String image) {
		super(rand.nextInt(120,140), rand.nextInt(20,40), rand.nextInt(70,90), rand.nextInt(10,30), startingPosition, image); //Random
	}
	
	@Override
	public void spawnWeapon() {
		if(rand.nextInt(0,2) == 0) {
			this.setWeapon(new Axe());
		} else {
			this.setWeapon(new LongSword());
		}
	}
}
