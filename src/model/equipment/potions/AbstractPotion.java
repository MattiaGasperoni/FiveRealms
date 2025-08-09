package model.equipment.potions;

import java.io.Serializable;

public abstract class AbstractPotion implements Potion, Serializable {
    
	private static final long serialVersionUID = 1L;  // per salvare lo stato del gioco

    private final double potionValue;
    
    public AbstractPotion(double d) {
    	this.potionValue = d;
    }

	@Override
	public double getPotionValue() {
		return this.potionValue;
	}
   
}
