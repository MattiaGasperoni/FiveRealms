package model.characters.bosses;
import model.characters.Wizard;
import model.point.Point;

public class WizardBoss extends Wizard{
	private int unnaturalBodyCounter;

	public WizardBoss() {
		super();
		super.setImagePath("images/characters/wizards/wizardBoss.png");
		super.increaseMaxHealth(0.4);
		super.increasePower(0.5);
		super.increaseDefence(0.3);
		super.increaseSpeed(0.2);
		super.setWeapon(super.availableWeapons.get(1)); //always has the AOE wand
		this.unnaturalBodyCounter = 0;
	}
	
	//GIMMICK Unnatural Body: Only takes damage every fourth hit, ignoring each one before that.
	@Override
	public void reduceCurrentHealth(int value) {
		super.reduceCurrentHealth(value);
		if(this.unnaturalBodyCounter < 3) {
			this.unnaturalBodyCounter++;
			super.increaseCurrentHealth((value / super.getMaxHealth())); //to get the correct percentage out of the integer value
		} else
			this.unnaturalBodyCounter = 0;
	}
}
