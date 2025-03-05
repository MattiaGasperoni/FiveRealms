package model.characters;

import java.util.Random;

public class Knight extends AbstractCharacter{

	private static final Random rand = new Random();

	public Knight() {
		super(rand.nextInt(10,30), rand.nextInt(10,30), rand.nextInt(10,30), rand.nextInt(10,30)); //Random
	}
	
}
