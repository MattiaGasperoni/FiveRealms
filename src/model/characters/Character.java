package model.characters;

import java.util.List;

import model.equipment.potions.Potion;
import model.equipment.weapons.Weapon;
import model.point.Point;

public interface Character {

	public void moveTo(Point point) throws IllegalArgumentException;
	public Point getPosition();
	public int getDistanceInSquares(Point point);
	
	public int getMaxHealth();
	public int getCurrentHealth();
	public void reduceCurrentHealth(int value);
	public void increaseCurrentHealth(double percentage);
	public boolean isAlive();
	
	public void usePotion();
	public Potion getPotion();
	public boolean hasPotion();
	public void setPotion(Potion potion);
		
	public int getExperience();
	public void gainExperience(int value);
	
	public int getSpeed();
	
	public int getPower();
	
	public int getDefence();
	
	public Weapon getWeapon();
	public void swapWeapon();
	public int getRange();
	
	public boolean isAllied();
	public void becomeHero();
	
	public String getImage();
	
	public void whatToDo(/*Character character,*/ List<Character> alliedList, List<Character> enemyList);
	public void fight(Character attackedCharacter, List<Character> alliedList, List<Character> enemyList) throws IllegalArgumentException;
}
