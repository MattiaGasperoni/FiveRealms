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

	@Override
	public int getSpeed() {
		return this.speed;
	}

	@Override
	public void setSpeed(int speed) {
		this.speed = speed;
	}

	@Override
	public int getPower() {
		return this.power;
	}

	@Override
	public void setPower(int power) {
		this.power = power;
	}

	@Override
	public int getDefence() {
		return this.defence;
	}

	@Override
	public void setDefence(int defence) {
		this.defence = defence;
	}

	@Override
	public int getRange() {
		return this.range;
	}

	@Override
	public void setRange(int range) {
		this.range = range;
	}
	
}
