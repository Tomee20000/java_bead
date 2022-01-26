import java.util.concurrent.atomic.AtomicInteger;

public class Resources {

    private static final int CAPACITY_LOWER_LIMIT = UnitType.PEASANT.foodCost * 5;

    private final AtomicInteger gold;
    private final AtomicInteger wood;
    private final AtomicInteger capacityLimit;
    private final AtomicInteger capacity;

    public Resources(){
        this.gold = new AtomicInteger(UnitType.PEASANT.goldCost * 5);
        this.wood = new AtomicInteger(0);
        this.capacityLimit = new AtomicInteger(CAPACITY_LOWER_LIMIT);
        this.capacity = new AtomicInteger(0);
    }

    public void addGold(int amount){
        this.gold.set(this.gold.get() + amount);
    }

    public void addWood(int amount){
        this.wood.set(this.wood.get() + amount);
    }

    public boolean canBuild(int goldCost, int woodCost){
        return gold.get() >= goldCost && wood.get() >= woodCost;
    }

    public boolean canTrain(int goldCost, int woodCost, int foodCost){
        return gold.get() >= goldCost && wood.get() >= woodCost && (capacity.get() + foodCost <= capacityLimit.get());
    }

    public void removeResource(int gold, int wood){
        this.gold.set(this.gold.get() - gold);
        this.wood.set(this.wood.get() - wood);
    }

    public void farmBuilt(){
        this.capacityLimit.set(this.capacityLimit.get() + 10);
    }

    public void updateCapacity(int foodCost){
        this.capacity.set(this.capacity.get() + foodCost);
    }
}
