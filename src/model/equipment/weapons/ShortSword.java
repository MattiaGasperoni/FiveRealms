package model.equipment.weapons;
import model.characters.Character;

public class ShortSword extends AbstractWeapon{

	public ShortSword(Character character) {
		super(character);
	}

	@Override
	public void increasePower() {
		int power = super.getCharacter().getPower();
		power += (power * AbstractWeapon.LOW) / 100;
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
		// TODO Auto-generated method stub
		
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
