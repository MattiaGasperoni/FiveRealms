package model.characters;

import model.point.Point;

public class Wizard extends AbstractCharacter{

	public Wizard(Point startingPosition, String image) {
		super(rand.nextInt(50,70), rand.nextInt(40,60), rand.nextInt(60,80), rand.nextInt(10,30), startingPosition, image); //Random
	}
	
	@Override
	public void setWeapon() {
	}
}
