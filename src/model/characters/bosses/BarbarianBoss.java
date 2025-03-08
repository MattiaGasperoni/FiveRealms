package model.characters.bosses;

import model.characters.AbstractCharacter;

public class BarbarianBoss extends AbstractCharacter{

	public BarbarianBoss() {
		super(rand.nextInt(10,30), rand.nextInt(10,30), rand.nextInt(10,30), rand.nextInt(10,30)); //Random
	}
}
