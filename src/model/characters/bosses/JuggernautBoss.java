package model.characters.bosses;
import model.characters.Juggernaut;
import model.point.Point;

public class JuggernautBoss extends Juggernaut{

	public JuggernautBoss(Point startingPosition) {
		super(startingPosition, "images/characters/juggernaut/juggernautBoss.png"); //Random
		this.increaseMaxHealth(1.4);
		this.increasePower(1.3);
		this.increaseDefence(1.5);
		this.increaseSpeed(0.1);
	}

	//GIMMICK: Starts off very strong, gets weaker every time he's hit, even if it deals no damage
	@Override
	public void reduceCurrentHealth(int value) {
		super.reduceCurrentHealth(value);
		this.increaseMaxHealth(-0.2);
		this.increasePower(-0.2);
		this.increaseDefence(-0.2);
	}
}
