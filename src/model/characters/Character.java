package model.characters;

import model.equipment.potions.Potion;
import model.equipment.weapons.Weapon;
import model.point.Point;

public interface Character {

	public void moveTo(Point point);
	public Point getPosition();
	public int getDistanceInSquares(Point point);
	
	public int getMaxHealth();
	public int getCurrentHealth();
	public void reduceCurrentHealth(int value);
	public void increaseCurrentHealth(double percentage);
	boolean isAlive();
	
	public void usePotion();
	public Potion getPotion();
	boolean hasPotion();
		
	public int getExperience();
	public void gainExperience(int value);
	
	public int getSpeed();
	
	public int getPower();
	
	public int getDefence();
	
	public Weapon getWeapon();
	public void spawnWeapon();	
	void swapWeapon();
	public int getRange();
	
	public boolean isAllied();
	public void becomeHero();
	
	public String getImage();
}
