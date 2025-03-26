package model.characters.bosses;
import model.characters.Barbarian;
import model.point.Point;

public class BarbarianBoss extends Barbarian{

	public BarbarianBoss(Point startingPosition) {
		super(startingPosition, "images/characters/barbarian/barbarianBoss.png"); //Random
		this.increaseMaxHealth(0.5);
		this.increasePower(0.3);
		this.increaseDefence(0.2);
		this.increaseSpeed(0.4);
	}
}
