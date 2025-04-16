package model.characters.bosses;
import model.characters.Knight;
import model.point.Point;

public class KnightBoss extends Knight{

	public KnightBoss(Point startingPosition) {
		super(startingPosition); //Random
		super.setImagePath("images/characters/knights/knightsBoss.png");
		this.increaseMaxHealth(0.3);
		this.increasePower(0.3);
		this.increaseDefence(0.3);
		this.increaseSpeed(0.3);
	}
}
