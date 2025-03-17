package model.characters;

import model.equipment.weapons.Axe;
import model.equipment.weapons.ShortSword;
import model.point.Point;

public class Juggernaut extends AbstractCharacter{

	public Juggernaut(Point startingPosition, String image) {
		super(rand.nextInt(80,100), rand.nextInt(10,30), rand.nextInt(50,70), rand.nextInt(70,90), startingPosition, image); //Random
	}
	
	@Override
	public void spawnWeapon() {
		if(rand.nextInt(0,2) == 0) {
			this.setWeapon(new ShortSword());
		} else {
			this.setWeapon(new Axe());
		}
	}
	
	@Override
	public void swapWeapon() {
		if(this.getWeapon() instanceof ShortSword)
			this.setWeapon(new Axe());
		else
			this.setWeapon(new ShortSword());
	}
}
