package model.characters;

import java.util.Random;

public class Wizard extends AbstractCharacter{

	private static final Random rand = new Random();

	public Wizard() {
		super(rand.nextInt(10,30), rand.nextInt(10,30), rand.nextInt(10,30), rand.nextInt(10,30)); //Random
	}
	
}
