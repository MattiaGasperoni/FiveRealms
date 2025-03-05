package model.characters;

import java.util.Random;

public class Archer extends AbstractCharacter{

    private static final Random rand = new Random();

	public Archer() {
		super(rand.nextInt(10,30), rand.nextInt(10,30), rand.nextInt(10,30), rand.nextInt(10,30)); //Random
	}
	
	

}
