package model.equipment.potions;

public abstract class AbstractPotion implements Potion {
    
    private final int healthIncreased;
    private final int defenceIncreased;
    private final int speedIncreased;
    private final int powerIncreased;

    public AbstractPotion(int healthIncreased, int defenceIncreased, int speedIncreased, int powerIncreased) {
        this.healthIncreased = healthIncreased;
        this.defenceIncreased = defenceIncreased;
        this.speedIncreased = speedIncreased;
        this.powerIncreased = powerIncreased;
    }

    @Override
    public int getHealtIncreased() {
        return this.healthIncreased;
    }

    @Override
    public int getDefenceIncreased() {
        return this.defenceIncreased;
    }

    @Override
    public int getPowerIncreased() {
        return this.powerIncreased;
    }

    @Override
    public int getSpeedIncreased() {
        return this.speedIncreased;
    }
}
