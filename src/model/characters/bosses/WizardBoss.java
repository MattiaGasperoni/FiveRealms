package model.characters.bosses;

import model.characters.AbstractCharacter;

public class WizardBoss extends AbstractCharacter{

	public WizardBoss() {
		super(rand.nextInt(10,30), rand.nextInt(10,30), rand.nextInt(10,30), rand.nextInt(10,30)); //Random
	}
	
}
