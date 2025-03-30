package model.characters.bosses;
import model.characters.Barbarian;
import model.point.Point;

public class BarbarianBoss extends Barbarian{

	public BarbarianBoss(Point startingPosition) {
		super(startingPosition, "images/characters/barbarian/barbarianBoss.png"); //Random
		this.increaseMaxHealth(1.0);
		this.increasePower(0.3);
		this.increaseDefence(0.2);
		this.increaseSpeed(0.4);
	}
	
	//GIMMICK: Gets stronger every time he gets hit
	@Override
	public void reduceCurrentHealth(int value) {
		super.reduceCurrentHealth(value);
		this.increasePower(0.3);
		this.increaseDefence(0.3);
		this.increaseSpeed(0.3);
	}
}
