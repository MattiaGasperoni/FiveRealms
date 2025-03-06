package model.equipment.weapons;

import model.characters.Character;

public class LongSword extends AbstractWeapon{

	public LongSword(Character character) {
		super(character);
		// Qui devo assegnare il range
	}

	@Override
	public void increasePower() {
		int power = super.getCharacter().getPower();
		power += (power * AbstractWeapon.MEDIUM) / 100;
		super.getCharacter().setPower(power);
	}

	@Override
	public void decreasePower() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void increaseSpeed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void decreaseSpeed() {
		int speed = super.getCharacter().getSpeed();
		speed  -= (speed * AbstractWeapon.LOW) / 100;
		super.getCharacter().setPower(speed );
	}

	@Override
	public void increaseDefence() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void decreaseDefence() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void increaseRange() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void decreaseRange() {
		// TODO Auto-generated method stub
		
	}
	

}
