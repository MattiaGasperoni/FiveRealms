package model.characters.bosses;
import model.characters.Wizard;
import model.point.Point;

public class WizardBoss extends Wizard{

	public WizardBoss() {
		super();
		super.setImagePath("images/characters/wizard/wizardBoss.png");
		super.increaseMaxHealth(0.4);
		super.increasePower(0.5);
		super.increaseDefence(0.3);
		super.increaseSpeed(0.2);
		super.setWeapon(super.availableWeapons.get(1));
	}

	/**
	 *GIMMICK Unnatural Body: Takes halved damage from attacks
	 */
	@Override
	public void reduceCurrentHealth(int value) {
		super.reduceCurrentHealth(value/2);
	}
}
