package model.characters;

import model.equipment.potions.Potion;
import model.equipment.weapons.AbstractWeapon;
import model.equipment.weapons.Weapon;
import model.point.Point;

public abstract class AbstractCharacter implements Character{
	
	private int health;
	private int speed;
	private int power;
	private int defence;
	private int experience;
	private Weapon weapon;
	private Potion potion;
	
	public AbstractCharacter(int health, int speed, int power, int defence) {
		this.health = health;
		this.speed = speed;
		this.power = power;
		this.defence = defence;
		this.experience = 0;
		this.weapon = null;
		this.potion = null;
	}

	@Override
	public void moveTo(Point point) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getPosition() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getDistance(Point point) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reduceHealth(int value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void increaseHealth(int value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void usePotion() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getPotion() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void levelUp() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean isAlive() {
		return this.health > 0;
	}

	@Override
	public int getExperience() {
		return this.experience;
	}

	@Override
	public void setExperience(int value) {
		this.experience = value;
	}

	@Override
	public int getHealth() {
		return this.health;
	}

	@Override
	public void setHealth(int health) {
		this.health = health;
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
		return this.power + this.weapon.getPower();
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
	public Weapon getWeapon() {
		return this.weapon;
	}

	@Override
	public void setWeapon(Weapon weapon) {
		this.weapon = weapon;
	}
	
}
