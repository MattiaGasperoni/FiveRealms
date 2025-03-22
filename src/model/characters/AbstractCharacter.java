package model.characters;
import java.io.Serializable;
import java.util.Random;
import model.equipment.potions.*;
import model.equipment.weapons.Weapon;
import model.point.Point;
public abstract class AbstractCharacter implements Character, Serializable {
	//TODO: implement exceptions/error messages
	public static final int EXP_LEVELUP_THRESHOLD = 1000; //threshold at which you level up each time. placeholder value
	private int maxHealth;
	private int currentHealth;
	private int speed;
	private int power;
	private int defence;
	private int experience;
	private Weapon weapon;
	private Potion potion;
	private Point position; 
	private String image; //filepath
	private boolean isAllied;
	protected static final Random rand = new Random();
	private static final long serialVersionUID = 1L;  // per salvare lo stato del gioco
	
	public AbstractCharacter(int health, int speed, int power, int defence, Point startingPosition, String image) {
		this.maxHealth = health;
		this.currentHealth = this.maxHealth;
		this.speed = speed;
		this.power = power;
		this.defence = defence;
		this.experience = 0;
		this.spawnWeapon();
		this.potion = null;
		this.position = startingPosition;
		this.isAllied = false;
		this.image = image;
	}
	
	@Override
	public void moveTo(Point point) {
		int speedToMovementFactor = 10;
		
		if(this.getDistanceInSquares(point) <= this.speed / speedToMovementFactor) //movement per-turn depends on speed, the actual value is placeholder as of now
			this.position = point;
	}

	//note that a diagonal spot is counted as two squares away, which is fine
	@Override
	public int getDistanceInSquares(Point point) {
		return Math.abs(point.getX() - this.position.getX()) + Math.abs(point.getY() - this.position.getY());
	}

	@Override
	public void usePotion() {
		if (this.hasPotion()) {
			if(this.potion instanceof PotionHealth) {
				this.increaseCurrentHealth(this.potion.getPotionValue());
				
			}else if(potion instanceof PotionDefence) {
				this.increaseDefence(this.potion.getPotionValue());
				
			}else if(potion instanceof PotionPower) {
				this.increasePower(this.potion.getPotionValue());
				
			}else if(potion instanceof PotionSpeed){
				this.increaseSpeed(this.potion.getPotionValue());
			}
		}
	}

	private void levelUp() {
		double statIncreasePercentage = 0.10;
		
		this.increaseMaxHealth(statIncreasePercentage);
		this.increasePower(statIncreasePercentage);
		this.increaseDefence(statIncreasePercentage);
		this.increaseSpeed(statIncreasePercentage);
		this.currentHealth = this.maxHealth; //levelling up fully heals you
	}

	@Override
	public void gainExperience(int value) {		
		this.experience += value;
		while(this.experience >= AbstractCharacter.EXP_LEVELUP_THRESHOLD) { //why While instead of if? this way, you can immediatly give, say, 5 thresholds worth of exp to a character and he levels up to 5 immediatly
			this.levelUp();
			this.experience -= AbstractCharacter.EXP_LEVELUP_THRESHOLD;
		}
	}
	
	@Override
	public void reduceCurrentHealth(int value) {
		if(value >= 0)
			this.currentHealth -= value;
	}

	@Override
	public void increaseCurrentHealth(double percentage) {
		if(percentage >= 0) {
			this.currentHealth += this.maxHealth * percentage;
			if(this.currentHealth > this.maxHealth)
				this.currentHealth = this.maxHealth;
		}
	}
	
	//used only to create the playable characters basically
	@Override
	public void becomeHero() {
		double heroStatIncreasePercentage = 0.30;
		
		if(!this.isAllied) {
			this.isAllied = true;
			this.increaseMaxHealth(heroStatIncreasePercentage);
			this.increasePower(heroStatIncreasePercentage);
			this.increaseDefence(heroStatIncreasePercentage);
			this.increaseSpeed(heroStatIncreasePercentage);
			this.image = "images/characters/" + getClass().getSimpleName().toLowerCase() + "/" + getClass().getSimpleName().toLowerCase() + "Hero.png"; //not sure this is correct, needs testing
		}
	}
	
	protected void increaseMaxHealth(double percentage) {
		this.maxHealth += this.maxHealth * percentage;
	}
	
	protected void increasePower(double percentage) {
		this.power += this.power * percentage;
	}
	
	protected void increaseDefence(double percentage) {
		this.defence += this.defence * percentage;
	}
	
	protected void increaseSpeed(double percentage) {
		this.speed += this.speed * percentage;
	}
	
	@Override
	public abstract void spawnWeapon();
	
	//the implementation is ugly but I see no better way to do it
	@Override
	public abstract void swapWeapon();
	
	protected void setWeapon(Weapon weapon) {
		this.weapon = weapon;
	}
	
	@Override
	public void setPotion(Potion potion) {
		if(!this.hasPotion())
			this.potion = potion;
	}
	
	@Override
	public Point getPosition() {
		return this.position;
	}

	@Override
	public int getCurrentHealth() {
		return this.currentHealth;
	}
	
	@Override
	public int getMaxHealth() {
		return this.maxHealth;
	}

	@Override
	public int getSpeed() {
		return this.speed + this.weapon.getSpeed();
	}

	@Override
	public int getPower() {
		return this.power + this.weapon.getPower();
	}

	@Override
	public int getDefence() {
		return this.defence + this.weapon.getDefence();
	}
	
	@Override
	public int getRange() {
		return this.weapon.getRange();
	}
	
	@Override
	public int getExperience() {
		return this.experience;
	}

	@Override
	public Weapon getWeapon() {
		return this.weapon;
	}
	
	@Override
	public Potion getPotion() {
		return this.potion;
	}
	
	@Override
	public boolean hasPotion() {
		return this.potion != null;
	}
	
	@Override
	public String getImage() {
		return image;
	}

	@Override
	public boolean isAllied() {
		return isAllied;
	}
	
	@Override
	public boolean isAlive() {
		return this.currentHealth > 0;
	}
}
