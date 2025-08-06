package model.equipment.potions;

import java.io.Serializable;

public abstract class AbstractPotion implements Potion, Serializable {
    
	private static final long serialVersionUID = 1L;  // per salvare lo stato del gioco

    private final int potionValue;
    
    public AbstractPotion(int potionValue) {
    	this.potionValue = potionValue;
    }

	@Override
	public int getPotionValue() {
		return this.potionValue;
	}
   
}
