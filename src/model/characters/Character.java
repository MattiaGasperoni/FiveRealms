package model.characters;

import model.equipment.weapons.AbstractWeapon;
import model.equipment.weapons.Weapon;
import model.point.Point;

public interface Character {

	public void moveTo(Point point);

	public void getPosition();
	
	public void getDistance(Point point);
	
	public void reduceHealth(int value);
	
	public void increaseHealth(int value);
	
	public void usePotion();
	
	public void getPotion();
	
	public void setWeapon(AbstractWeapon weapon);
	
	public void levelUp();
	
	public int getExperience();
	public void setExperience(int value);
	
	public int getHealth();
	public void setHealth(int health);
	
	public int getSpeed();
	public void setSpeed(int speed);
	
	public int getPower();
	public void setPower(int power);
	
	public int getDefence();
	public void setDefence(int defence);
	
	public Weapon getWeapon();
	public void setWeapon(Weapon weapon);
	
}
