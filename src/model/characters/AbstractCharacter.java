package model.characters;
import java.util.Random;
import model.equipment.potions.*;
import model.equipment.weapons.Weapon;
import model.point.Point;
public abstract class AbstractCharacter implements Character{
	//TODO: implement exceptions/error messages
	public static final int EXP_LEVELUP_THRESHOLD = 1000; //threshold at which you level up each time. placeholder value
	//private final double LEVELUP_STAT_PERCENTUAL_INCREASE = 0.10; //by what percentage of the stat do you increase each stat when you levelup? placeholder value, 10% as is
	//those maybe don't need to be constants since they're only used within one method each?
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
	
	public AbstractCharacter(int health, int speed, int power, int defence, Point startingPosition, String image) {
		this.maxHealth = health;
		this.currentHealth = this.maxHealth;
		this.speed = speed;
		this.power = power;
		this.defence = defence;
		this.experience = 0;
		this.setWeapon();
		this.potion = null;
		this.position = startingPosition;
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
		if(potion instanceof PotionHealth) {
			this.increaseCurrentHealth(potion.getPotionValue());
			
		}else if(potion instanceof PotionDefence) {
			this.increaseDefence(potion.getPotionValue());
			
		}else if(potion instanceof PotionPower) {
			this.increasePower(potion.getPotionValue());
			
		}else if(potion instanceof PotionSpeed){
			this.increaseSpeed(potion.getPotionValue());
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
		while(this.experience >= AbstractCharacter.EXP_LEVELUP_THRESHOLD) { //why While instead of if? this way, you can immediatly give, say, 5 thresholds worth of exp to an enemy and he levels up to 5 immediatly
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
		}
	}
	
	private void increaseMaxHealth(double percentage) {
		this.maxHealth += this.maxHealth * percentage;
	}
	
	private void increasePower(double percentage) {
		this.power += this.power * percentage;
	}
	
	private void increaseDefence(double percentage) {
		this.defence += this.defence * percentage;
	}
	
	private void increaseSpeed(double percentage) {
		this.speed += this.speed * percentage;
	}
	
	//needs checks for class instance (or using subclasses to do that, depends)
	//needs a lot more than that even
	@Override
	public abstract void setWeapon();
	
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
	public String getImage() {
		return image;
	}

	@Override
	public Potion getPotion() {
		return this.potion;
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
