package model.characters;

import model.point.Point;

public class Barbarian extends AbstractCharacter{

	public Barbarian(Point startingPosition, String image) {
		super(rand.nextInt(120,140), rand.nextInt(20,40), rand.nextInt(70,90), rand.nextInt(10,30), startingPosition, image); //Random
	}
	
	@Override
	public void setWeapon() {
	}
}
