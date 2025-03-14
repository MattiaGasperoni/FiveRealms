package model.equipment.potions;

public abstract class AbstractPotion implements Potion {
    
    private final int potionValue;
    
    public AbstractPotion(int potionValue) {
    	this.potionValue = potionValue;
    }

	@Override
	public int getPotionValue() {
		return this.potionValue;
	}
   
}
