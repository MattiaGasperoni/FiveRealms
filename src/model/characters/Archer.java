package model.characters;
import model.equipment.weapons.LongBow;
import model.equipment.weapons.ShortBow;

public class Archer extends AbstractCharacter{

	private static final long serialVersionUID = 1L;

	public Archer() {
		super(rand.nextInt(100,120), rand.nextInt(70,90), rand.nextInt(105,125), rand.nextInt(35,55)); //Random
		this.availableWeapons.add(new ShortBow());
		this.availableWeapons.add(new LongBow());
		super.spawnWeapon();
	}
}
