package model.characters.bosses;
import model.characters.Wizard;
import model.point.Point;

public class WizardBoss extends Wizard{

	public WizardBoss() {
		super(); //Random
		super.setImagePath("images/characters/wizards/wizardBoss.png");
		this.increaseMaxHealth(0.4);
		this.increasePower(0.5);
		this.increaseDefence(0.3);
		this.increaseSpeed(0.2);
	}
}
