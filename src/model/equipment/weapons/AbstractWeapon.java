package model.equipment.weapons;
import model.characters.Character;


public abstract class AbstractWeapon implements Weapon{

	
	public static final int LOW = 20;
	public static final int MEDIUM = 40;
	public static final int HIGH = 60;
	
	private int range;
	private Character character;
	
	public AbstractWeapon(Character character) {
		this.range = 0;
		this.character = character;
	}
	
	public abstract void increasePower();
	public abstract void decreasePower();
	
	public abstract void increaseSpeed();
	public abstract void decreaseSpeed();
	
	public abstract void increaseDefence();
	public abstract void decreaseDefence();
	
	public abstract void increaseRange();
	public abstract void decreaseRange();
	
	public int getRange() {
		return range;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public Character getCharacter() {
		return character;
	}
	
	
}
