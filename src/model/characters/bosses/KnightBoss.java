package model.characters.bosses;

import model.characters.AbstractCharacter;

public class KnightBoss extends AbstractCharacter{

	public KnightBoss() {
		super(rand.nextInt(10,30), rand.nextInt(10,30), rand.nextInt(10,30), rand.nextInt(10,30)); //Random
	}

}
