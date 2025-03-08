package model.characters.bosses;

import model.characters.AbstractCharacter;

public class ArcherBoss extends AbstractCharacter{

	public ArcherBoss() {
		super(rand.nextInt(10,30), rand.nextInt(10,30), rand.nextInt(10,30), rand.nextInt(10,30)); //Random
	}
	
}