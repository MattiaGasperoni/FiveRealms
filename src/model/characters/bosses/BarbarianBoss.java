package model.characters.bosses;
import model.characters.Barbarian;
import model.point.Point;

public class BarbarianBoss extends Barbarian{
	private final double STAT_INCREASE_PERCENTAGE = 0.2;

	public BarbarianBoss() {
		super(); //Random
		super.setImagePath("images/characters/barbarian/barbarianBoss.png");
		super.increaseMaxHealth(1.0);
		super.increasePower(0.3);
		super.increaseDefence(0.2);
		super.increaseSpeed(0.2);
	}
	
	//GIMMICK Berserker: Gets stronger every time he gets hit
	@Override
	public void reduceCurrentHealth(int value) {
		super.reduceCurrentHealth(value);
		super.increasePower(this.STAT_INCREASE_PERCENTAGE);
		super.increaseDefence(this.STAT_INCREASE_PERCENTAGE);
		super.increaseSpeed(this.STAT_INCREASE_PERCENTAGE);
	}
}
