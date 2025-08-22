package model.characters;
import java.awt.Image;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.ImageIcon;
import model.equipment.potions.*;
import model.equipment.weapons.Weapon;
import model.point.Point;
public abstract class AbstractCharacter implements Character, Serializable 
{
	/** Threshold of experience points required to level up */
	public static final int EXP_LEVELUP_THRESHOLD = 1000;
	/** Conversion factor from speed stat to movement points */
	public static final int SPEED_TO_MOVEMENT = 10;
	/** Total number of weapons a character can choose from */
	public static final int TOTAL_WEAPONS = 2;
	private int maxHealth;
	private int currentHealth;
	private int speed;
	private int power;
	private int defence;
	private int experience;
	private Weapon weapon;
	private Potion potion;
	private Point position; 
	private String imagePath; //filepath
	private transient Image image;
	private transient ImageIcon icon;
	private boolean isAllied;
	protected ArrayList<Weapon> availableWeapons;
	protected static final Random rand = new Random();
	/** Serial version UID for serialization compatibility */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new AbstractCharacter with the specified base statistics.
	 * 
	 * @param health the maximum health points for this character
	 * @param speed the base speed statistic for movement and initiative
	 * @param power the base power statistic for attack damage
	 * @param defence the base defence statistic for damage reduction
	 */
	public AbstractCharacter(int health, int speed, int power, int defence) {
		this.maxHealth = health;
		this.currentHealth = this.maxHealth;
		this.speed = speed;
		this.power = power;
		this.defence = defence;
		this.experience = 0;
		this.potion = null;
		this.isAllied = false;
		this.generateDefaultImage();
		this.availableWeapons = new ArrayList<>(AbstractCharacter.TOTAL_WEAPONS);
	}

	/**
	 * Reinitializes transient fields after deserialization.
	 * This method must be called after loading a character from a save file
	 * to restore the image-related fields that cannot be serialized.
	 */
	@Override
	public void reinitializeAfterLoad()
	{
		this.setImagePath(this.imagePath);
	}

	/**
	 * Moves this character to the specified point on the game map.
	 * 
	 * @param point the destination point to move to
	 * @throws IllegalArgumentException if the point is invalid or unreachable
	 */
	@Override
	public void moveTo(Point point) throws IllegalArgumentException {
		this.position = point;

	}

	/**
	 * Calculates the distance in grid squares between this character and the specified point.
	 * Diagonal movement is counted as moving through each adjacent square (one diagonal is two squares).
	 * 
	 * @param point the target point to measure distance to
	 * @return the distance in grid squares
	 */	@Override
	 public int getDistanceInSquares(Point point) {
		 return this.position.distanceFrom(point);
	 }

	 /**
	  * Calculates the distance in grid squares between this character and another character.
	  * overload for ease of use
	  * 
	  * @param character the target character to measure distance to
	  * @return the distance in grid squares
	  */
	 @Override
	 public int getDistanceInSquares(Character character) {
		 return this.position.distanceFrom(character.getPosition());
	 }

	 /**
	  * Uses the currently held potion and applies its effect to this character.
	  * The potion is consumed and removed from inventory after use.
	  * Different potion types provide different benefits:
	  * 
	  * Health potions restore health points
	  * Defence potions permanently increase defence
	  * Power potions permanently increase power
	  * Speed potions permanently increase speed
	  */
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
			 this.potion = null;
		 }
	 }

	 /**
	  * Increases all character statistics by 10% and fully restores health.
	  * This is called automatically when the character gains enough experience.
	  */
	 private void levelUp() {
		 double statIncreasePercentage = 0.10;  

		 this.increaseMaxHealth(statIncreasePercentage);
		 this.increasePower(statIncreasePercentage);
		 this.increaseDefence(statIncreasePercentage);
		 this.increaseSpeed(statIncreasePercentage);
		 this.currentHealth = this.maxHealth; //levelling up fully heals you
	 }

	 /**
	  * Awards experience points to this character and handles automatic leveling.
	  * If the character gains enough experience, they will level up immediately.
	  * Multiple level-ups can occur from a single call if enough experience is awarded.
	  * 
	  * @param value the amount of experience points to award (must be non-negative)
	  */
	 @Override
	 public void gainExperience(int value) {		
		 this.experience += value;
		 while(this.experience >= AbstractCharacter.EXP_LEVELUP_THRESHOLD) {
			 this.levelUp();
			 this.experience -= AbstractCharacter.EXP_LEVELUP_THRESHOLD;
		 }
	 }

	 /**
	  * Reduces this character's current health by the specified amount.
	  * 
	  * @param value the amount of damage to deal (must be non-negative)
	  */
	 @Override
	 public void reduceCurrentHealth(int value) {
		 if(value >= 0)
			 this.currentHealth -= value;
	 }

	 /**
	  * Increases this character's current health by a percentage of their maximum health.
	  * Current health is capped at the maximum health value.
	  * 
	  * @param percentage the percentage of max health to restore (0.0 to 1.0)
	  */
	 @Override
	 public void increaseCurrentHealth(double percentage) {
		 if(percentage >= 0) {
			 this.currentHealth += this.maxHealth * percentage;
			 if(this.currentHealth > this.maxHealth)
				 this.currentHealth = this.maxHealth;
		 }
	 }

	 /**
	  * Converts this character into a hero (playable character), significantly boosting their statistics
	  * and changing their appearance.
	  * Heroes gain a 40% increase to all stats and switch to hero-themed token/image.
	  */	
	 @Override
	 public void becomeHero() {
		 double heroStatIncreasePercentage = 0.4;

		 if(!this.isAllied) {
			 this.isAllied = true;
			 this.increaseMaxHealth(heroStatIncreasePercentage);
			 this.increasePower(heroStatIncreasePercentage);
			 this.increaseDefence(heroStatIncreasePercentage);
			 this.increaseSpeed(heroStatIncreasePercentage);
			 this.setImagePath("images/characters/" + getClass().getSimpleName().toLowerCase() + "/" + getClass().getSimpleName().toLowerCase() + "Hero.png");
		 }
	 }

	 /**
	  * Sets the position of this character on the game map.
	  * 
	  * @param position the new position for this character
	  */
	 @Override
	 public void setPosition(Point position) {
		 this.position = position;
	 }



	 /**
	  * Initiates combat between this character and the target character.
	  * 
	  * Combat mechanics:
	  * 
	  * This character automatically uses any beneficial potion before attacking
	  * Damage dealt equals attacker's power minus defender's defence
	  * If the target survives and can counterattack, they deal damage back
	  * Experience is awarded to the survivor
	  * Potions may randomly drop when killing an enemy
	  * 
	  * @param attackedCharacter the character to attack
	  * @return the character that died in combat, or null if both survive
	  * @throws IllegalArgumentException if trying to attack an ally or target out of range
	  */	
	 @Override
	 public Character fight(Character attackedCharacter) throws IllegalArgumentException {
		 Character deadCharacter = null;

		 if(this.isAllied() == attackedCharacter.isAllied())
			 throw new IllegalArgumentException("You cannot attack someone belonging to your own faction!");
		 if (!this.isWithinAttackRange(attackedCharacter))
			 throw new IllegalArgumentException("You cannot attack someone outside of your weapon's attack range!");

		 // Use potion if beneficial (not health potion when at max health)
		 if(this.hasPotion() && !(this.getPotion() instanceof PotionHealth && this.getCurrentHealth() == this.getMaxHealth())) { 
			 this.usePotion();
		 }

		 attackedCharacter.reduceCurrentHealth(this.getPower() - attackedCharacter.getDefence());

		 if (attackedCharacter.isAlive() && attackedCharacter.isWithinAttackRange(this))
			 this.reduceCurrentHealth(attackedCharacter.getPower() - this.getDefence());

		 if (!attackedCharacter.isAlive()) {
			 deadCharacter = attackedCharacter;
			 this.gainExperience(AbstractCharacter.EXP_LEVELUP_THRESHOLD/2);
			 //50% chance of getting a potion, if so get one of the four randomly
			 switch(rand.nextInt(0,9)) {
			 case 5:
				 this.setPotion(new PotionHealth());
				 break;
			 case 6:
				 this.setPotion(new PotionDefence());
				 break;
			 case 7:
				 this.setPotion(new PotionPower());
				 break;
			 case 8:
				 this.setPotion(new PotionSpeed());
				 break;				
			 }
		 }

		 if (!this.isAlive()) {
			 deadCharacter = this;
			 attackedCharacter.gainExperience(AbstractCharacter.EXP_LEVELUP_THRESHOLD/2);
		 }

		 return deadCharacter;
	 }

	 /**
	  * Checks if the specified point is within this character's weapon attack range.
	  * 
	  * @param attackedPoint the point to check
	  * @return true if the point is within attack range, false otherwise
	  */
	 @Override
	 public boolean isWithinAttackRange(Point attackedPoint) {
		 return this.getDistanceInSquares(attackedPoint) <= this.getRange();
	 }

	 /**
	  * Checks if the specified character is within this character's weapon attack range.
	  * overload for ease of use
	  * 
	  * @param attackedCharacter the character to check
	  * @return true if the character is within attack range, false otherwise
	  */
	 @Override
	 public boolean isWithinAttackRange(Character attackedCharacter) {
		 return this.getDistanceInSquares(attackedCharacter) <= this.getRange();
	 }

	 /**
	  * Increases maximum health by the specified percentage and heals accordingly.
	  * 
	  * @param percentage the percentage increase (0.0 to 1.0)
	  */
	 protected void increaseMaxHealth(double percentage) {		
		 this.maxHealth += this.maxHealth * percentage;
		 this.increaseCurrentHealth(percentage);
	 }

	 /**
	  * Increases power statistic by the specified percentage.
	  * 
	  * @param percentage the percentage increase (0.0 to 1.0)
	  */
	 protected void increasePower(double percentage) {
		 this.power += this.power * percentage;
	 }

	 /**
	  * Increases defence statistic by the specified percentage.
	  * 
	  * @param percentage the percentage increase (0.0 to 1.0)
	  */
	 protected void increaseDefence(double percentage) {
		 this.defence += this.defence * percentage;
	 }

	 /**
	  * Increases speed statistic by the specified percentage.
	  * 
	  * @param percentage the percentage increase (0.0 to 1.0)
	  */
	 protected void increaseSpeed(double percentage) {
		 this.speed += this.speed * percentage;
	 }

	 /**
	  * Randomly selects and equips a weapon from this character's available weapons.
	  */
	 protected void spawnWeapon() {
		 this.setWeapon(this.availableWeapons.get(rand.nextInt(0,AbstractCharacter.TOTAL_WEAPONS)));
	 }

	 /**
	  * Swaps to the other available weapon in this character's inventory.
	  * If currently using the first weapon, switches to the second, and vice versa.
	  */
	 @Override
	 public void swapWeapon() {
		 if(this.availableWeapons.getFirst().equals(this.getWeapon()))
			 this.setWeapon(this.availableWeapons.getLast());
		 else
			 this.setWeapon(this.availableWeapons.getFirst());
	 }

	 /**
	  * Equips the specified weapon for this character.
	  * 
	  * @param weapon the weapon to equip
	  */
	 protected void setWeapon(Weapon weapon) {
		 this.weapon = weapon;
	 }

	 /**
	  * Gives this character a potion if they don't already have one.
	  * Prints a message indicating the potion was received.
	  * 
	  * @param potion the potion to give to this character
	  */
	 @Override
	 public void setPotion(Potion potion) {
		 if(!this.hasPotion()) {
			 this.potion = potion;
			 System.out.println( this.getClass().getSimpleName() + " got a " + potion.getClass().getSimpleName() + " potion!");
		 }
	 }

	 /**
	  * Generates a default image path for this character based on their class name.
	  * Randomly selects from 3 available image variants.
	  */
	 private void generateDefaultImage() {
		 this.setImagePath("images/characters/" + this.getClass().getSimpleName().toLowerCase() + "/" + this.getClass().getSimpleName().toLowerCase() + this.rand.nextInt(1,4) + ".png");
	 }

	 /**
	  * Gets this character's current position on the game map.
	  * 
	  * @return the current position
	  */
	 @Override
	 public Point getPosition() {
		 return this.position;
	 }

	 /**
	  * Gets this character's current health points.
	  * 
	  * @return the current health (0 or higher)
	  */
	 @Override
	 public int getCurrentHealth() {
		 return this.currentHealth;
	 }

	 /**
	  * Gets this character's maximum health points.
	  * 
	  * @return the maximum health
	  */
	 @Override
	 public int getMaxHealth() {
		 return this.maxHealth;
	 }

	 /**
	  * Gets this character's effective speed (base speed plus weapon bonus).
	  * 
	  * @return the total speed value
	  */
	 @Override
	 public int getSpeed() {
		 return this.speed + this.weapon.getSpeed();
	 }

	 /**
	  * Gets this character's effective power (base power plus weapon bonus).
	  * 
	  * @return the total power value
	  */
	 @Override
	 public int getPower() {
		 return this.power + this.weapon.getPower();
	 }

	 /**
	  * Gets this character's effective defence (base defence plus weapon bonus).
	  * 
	  * @return the total defence value
	  */
	 @Override
	 public int getDefence() {
		 return this.defence + this.weapon.getDefence();
	 }

	 /**
	  * Gets this character's weapon attack range.
	  * 
	  * @return the attack range in grid squares
	  */
	 @Override
	 public int getRange() {
		 return this.weapon.getRange();
	 }

	 /**
	  * Gets this character's current experience points.
	  * 
	  * @return the experience points
	  */
	 @Override
	 public int getExperience() {
		 return this.experience;
	 }

	 /**
	  * Gets this character's currently equipped weapon.
	  * 
	  * @return the current weapon
	  */
	 @Override
	 public Weapon getWeapon() {
		 return this.weapon;
	 }

	 /**
	  * Gets this character's currently held potion.
	  * 
	  * @return the current potion, or null if none is held
	  */
	 @Override
	 public Potion getPotion() {
		 return this.potion;
	 }

	 /**
	  * Checks if this character is currently holding a potion.
	  * 
	  * @return true if a potion is held, false otherwise
	  */
	 @Override
	 public boolean hasPotion() {
		 return this.potion != null;
	 }

	 /**
	  * Gets the file path to this character's image.
	  * 
	  * @return the image file path
	  */
	 @Override
	 public String getImagePath() {
		 return imagePath;
	 }

	 /**
	  * Gets the scaled image object for rendering this character.
	  * 
	  * @return the character image
	  */
	 @Override
	 public Image getImage() {
		 return this.image;
	 }

	 /**
	  * Sets the image path for this character and updates the rendered image.
	  * The image is automatically scaled to 75x45 pixels for map display.
	  * 
	  * @param image the path to the new image file
	  */
	 protected void setImagePath(String image) {
		 this.imagePath = image;
		 ImageIcon icon = new ImageIcon(this.imagePath);
		 this.image = icon.getImage().getScaledInstance(75, 45, Image.SCALE_AREA_AVERAGING);
	 }

	 /**
	  * Checks if this character is allied with the player.
	  * 
	  * @return true if allied, false if enemy
	  */
	 @Override
	 public boolean isAllied() {
		 return isAllied;
	 }

	 /**
	  * Checks if this character is still alive.
	  * 
	  * @return true if current health is greater than 0, false otherwise
	  */
	 @Override
	 public boolean isAlive() {
		 return this.currentHealth > 0;
	 }

	 /**
	  * Returns a string representation of this character including all key statistics.
	  * 
	  * @return a detailed string describing this character's current state
	  */
	 @Override
	 public String toString() {
		 return this.getClass().getSimpleName() + ": [maxHealth="+ maxHealth + ", currentHealth=" + currentHealth + ", speed=" + speed
				 + ", power=" + power + ", defence=" + defence + ", experience=" + experience + ", weapon=" + weapon.getClass().getSimpleName()
				 + ", potion=" + potion + ", position=" + position + ", isAllied=" + isAllied + "]";
	 }
}
