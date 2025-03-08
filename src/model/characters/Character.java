package model.characters;

import model.equipment.potions.Potion;
import model.equipment.weapons.Weapon;
import model.point.Point;

public interface Character {

	public void moveTo(Point point);
	public Point getPosition();
	public int getDistance(Point point);
	
	public void reduceHealth(int value);
	public void increaseHealth(int value);
	
	public void usePotion();
	public Potion getPotion();
		
	public void levelUp();
	public int getExperience();
	public void gainExperience(int value);
	
	public int getHealth();
	public void setHealth(int health);
	boolean isAlive();
	
	public int getSpeed();
	public void setSpeed(int speed);
	
	public int getPower();
	public void setPower(int power);
	
	public int getDefence();
	public void setDefence(int defence);
	
	public Weapon getWeapon();
	public void setWeapon(Weapon weapon);	
}
