package model.characters.bosses;
import model.characters.Juggernaut;

public class JuggernautBoss extends Juggernaut{

	private static final long serialVersionUID = 1L;

	public JuggernautBoss() {
		super(); //Random
		super.setImagePath("images/characters/juggernaut/juggernautBoss.png");
		super.increaseMaxHealth(0.4);
		super.increasePower(0.3);
		super.increaseDefence(1.5);
		super.increaseSpeed(0.1);
	}

	/**
	 *GIMMICK Ablative Armor: Starts off with extremely high defence, which gets reduced every time he gets hit, even if it deals no damage, but he also gets faster.
	 */
	@Override
	public void reduceCurrentHealth(int value) {
		super.reduceCurrentHealth(value);
		super.increaseDefence(-0.2);
		super.increaseSpeed(0.1);
	}
}
