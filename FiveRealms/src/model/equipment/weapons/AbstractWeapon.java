package model.equipment.weapons;

import java.io.Serializable;

public abstract class AbstractWeapon implements Weapon , Serializable{

	private static final long serialVersionUID = 1L;

	private int speed;
	private int power;
	private int defence;
	private int range;

	/**
	 * Constructs a new AbstractWeapon with the specified statistics.
	 * All parameters should be values representing the bonuses or maluses
	 * this weapon provides to a character's base statistics.
	 * 
	 * @param speed the speed bonus this weapon provides to its wielder
	 * @param power the power bonus this weapon provides to its wielder
	 * @param defence the defence bonus this weapon provides to its wielder
	 * @param range the attack range of this weapon in grid squares
	 */
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
