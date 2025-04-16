package model.characters;
import model.equipment.weapons.LongBow;
import model.equipment.weapons.ShortBow;
import model.point.Point;

public class Archer extends AbstractCharacter{

	public Archer() {
		super(rand.nextInt(60,80), rand.nextInt(50,70), rand.nextInt(40,60), rand.nextInt(20,40)); //Random
		this.availableWeapons.add(new ShortBow());
		this.availableWeapons.add(new LongBow());
		super.spawnWeapon();
	}
}
