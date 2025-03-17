package model.characters.bosses;
import model.characters.Juggernaut;
import model.point.Point;

public class JuggernautBoss extends Juggernaut{

	public JuggernautBoss(Point startingPosition) {
		super(startingPosition, "images/characters/juggernaut/juggernautBoss.png"); //Random
		this.increaseMaxHealth(0.4);
		this.increasePower(0.3);
		this.increaseDefence(0.5);
		this.increaseSpeed(0.2);
	}
}
