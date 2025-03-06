package model.equipment.weapons;

public abstract class AbstractWeapon implements Weapon{

	private int speed;
	private int power;
	private int defence;
	private int range;
	
	public AbstractWeapon(int speed, int power, int defence, int range) {
		this.speed = speed;
		this.power = power;
		this.defence = defence;
		this.range = range;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getPower() {
		return power;
	}

	public void setPower(int power) {
		this.power = power;
	}

	public int getDefence() {
		return defence;
	}

	public void setDefence(int defence) {
		this.defence = defence;
	}

	public int getRange() {
		return range;
	}

	public void setRange(int range) {
		this.range = range;
	}
	
	

	
	
	
}
