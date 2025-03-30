package model.characters;

import model.equipment.weapons.Axe;
import model.equipment.weapons.ShortSword;
import model.point.Point;

public class Juggernaut extends AbstractCharacter{

	public Juggernaut(Point startingPosition, String image) {
		super(rand.nextInt(80,100), rand.nextInt(10,30), rand.nextInt(50,70), rand.nextInt(70,90), startingPosition, image); //Random
		this.availableWeapons.add(new ShortSword());
		this.availableWeapons.add(new Axe());
	}
}
