package model.characters;

import model.equipment.weapons.AbstractWeapon;
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
	
	public void setExperience(int value);
	
}
