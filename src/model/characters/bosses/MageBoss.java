package model.characters.bosses;

import model.characters.AbstractCharacter;

public class MageBoss extends AbstractCharacter{

	public MageBoss() {
		super(rand.nextInt(10,30), rand.nextInt(10,30), rand.nextInt(10,30), rand.nextInt(10,30)); //Random
	}
	
}
