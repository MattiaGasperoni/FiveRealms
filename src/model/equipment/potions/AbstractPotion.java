package model.equipment.potions;

public abstract class AbstractPotion implements Potion{
	
	private int healthIncreased;

	public AbstractPotion(int healthIncreased) {
		this.healthIncreased = healthIncreased;
	}

	@Override
	public int getHealtIncreased() {
		return this.healthIncreased;
	}
	
	

	

}
