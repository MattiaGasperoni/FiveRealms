package model.characters;

import java.util.Random;

public class Boss extends AbstractCharacter{

	private static final Random rand = new Random();

	public Boss() {
		super(rand.nextInt(10,30), rand.nextInt(10,30), rand.nextInt(10,30), rand.nextInt(10,30)); //Random
	}
	
}
