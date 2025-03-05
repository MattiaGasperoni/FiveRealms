package model.characters;

import java.util.Random;

public class Barbarian extends AbstractCharacter{

	private static final Random rand = new Random();

	public Barbarian() {
		super(rand.nextInt(10,30), rand.nextInt(10,30), rand.nextInt(10,30), rand.nextInt(10,30)); //Random
	}
	
	
}
