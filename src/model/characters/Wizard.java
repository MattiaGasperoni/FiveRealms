package model.characters;
import model.equipment.weapons.SingleShotWand;
import model.equipment.weapons.ZoneShotWand;
import model.point.Point;

public class Wizard extends AbstractCharacter{

	public Wizard(Point startingPosition, String image) {
		super(rand.nextInt(50,70), rand.nextInt(40,60), rand.nextInt(60,80), rand.nextInt(10,30), startingPosition, image); //Random
	}
	
	@Override
	public void spawnWeapon() {
		if(rand.nextInt(0,2) == 0) {
			this.setWeapon(new SingleShotWand());
		} else {
			this.setWeapon(new ZoneShotWand());
		}
	}
}
