package model.characters.bosses;

import model.characters.AbstractCharacter;

public class JuggernautBoss extends AbstractCharacter{

	public JuggernautBoss() {
		super(rand.nextInt(10,30), rand.nextInt(10,30), rand.nextInt(10,30), rand.nextInt(10,30)); //Random
	}
	
}
