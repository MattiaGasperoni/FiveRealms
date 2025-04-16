package model.characters.bosses;
import model.characters.Archer;
import model.point.Point;

public class ArcherBoss extends Archer{

	public ArcherBoss(Point startingPosition) {
		super(startingPosition); //Random
		super.setImagePath("images/characters/archer/archerBoss.png");
		this.increaseMaxHealth(0.3);
		this.increasePower(0.4);
		this.increaseDefence(0.2);
		this.increaseSpeed(0.5);
	}
}