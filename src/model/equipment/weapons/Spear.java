package model.equipment.weapons;

import model.characters.Character;

public class Spear extends AbstractWeapon{

	public Spear(Character character) {
		super(character);
		
	}

	@Override
	public void increasePower() {
		int power = super.getCharacter().getPower();
		power += (power * AbstractWeapon.HIGH) / 100;
		super.getCharacter().setPower(power);
	}

	@Override
	public void decreasePower() {
		
	}

	@Override
	public void increaseSpeed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void decreaseSpeed() {
		int speed = super.getCharacter().getSpeed();
		speed  -= (speed * AbstractWeapon.LOW) / 100;
		super.getCharacter().setPower(speed);
	}

	@Override
	public void increaseDefence() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void decreaseDefence() {
		int defence = super.getCharacter().getDefence();
		defence  -= (defence * AbstractWeapon.LOW) / 100;
		super.getCharacter().setDefence(defence);
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
