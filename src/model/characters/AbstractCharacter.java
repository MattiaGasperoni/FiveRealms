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
	public void moveTo(Point point) {
		
	}
	public void getPosition() {
		
	}
	public void getDistance(Point point) {
		
	}
	public void reduceHealth(int value) {
		
	}
	public void increaseHealth(int value) {
		
	}
	public void usePotion() {
		
	}
	public void getPotion() {
		
	}
	public void setWeapon(AbstractWeapon weapon) {
		
	}
	public void levelUp() {
		
	}
	public void setExperience(int value) {
		
	}
	
}
