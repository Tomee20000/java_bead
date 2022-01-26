import java.util.concurrent.atomic.AtomicBoolean;

public class Peasant extends Unit {

    private static final int HARVEST_WAIT_TIME = 100;
    private static final int HARVEST_AMOUNT = 10;

    private AtomicBoolean isHarvesting = new AtomicBoolean(false);
    private AtomicBoolean isBuilding = new AtomicBoolean(false);

    private Peasant(Base owner) {
        super(owner, UnitType.PEASANT);
    }

    public static Peasant createPeasant(Base owner){
        return new Peasant(owner);
    }

    public void startMining(){
        this.isHarvesting.set(true);
        Thread t = new Thread(() -> {
            while (this.isHarvesting.get()){
                sleepForMsec(HARVEST_WAIT_TIME);
                this.getOwner().getResources().addGold(HARVEST_AMOUNT);
            }
        });
        t.start();
        System.out.println(randomQuoteGenerator() + " (mining GOLD)");
    }

    public void startCuttingWood(){
        this.isHarvesting.set(true);
        Thread t = new Thread(() -> {
            while (this.isHarvesting.get()){
                sleepForMsec(HARVEST_WAIT_TIME);
                this.getOwner().getResources().addWood(HARVEST_AMOUNT);
            }
        });
        t.start();
        System.out.println(randomQuoteGenerator() + " (cutting WOOD)");
    }

    public void stopHarvesting(){
        this.isHarvesting.set(false);
    }

    public boolean tryBuilding(UnitType buildingType){
        if (this.getOwner().getResources().canBuild(buildingType.goldCost, buildingType.woodCost)){
            Thread t = new Thread(() -> this.startBuilding(buildingType));
            t.start();
            return true;
        }
        return false;
    }

    private void startBuilding(UnitType buildingType){
        synchronized(isBuilding){
            this.isBuilding.set(true);
            this.getOwner().getResources().removeResource(buildingType.goldCost, buildingType.woodCost);

            Building building = Building.createBuilding(buildingType, this.getOwner());
            this.getOwner().getBuildings().add(building);
            sleepForMsec(buildingType.buildTime);

            this.isBuilding.set(false);
        }
    }

    public boolean isFree(){
        return !isHarvesting.get() && !isBuilding.get();
    }

    public String randomQuoteGenerator(){

        switch(getRandomNumber(1,10)) {
            case 1:
                return "I can do that.";
            case 2:
                return "Be happy to.";
            case 3:
                return "Okie dokie.";
            case 4:
                return "Me not that kind of orc!";
            case 5:
                return "No time for play.";
            case 6:
                return "Yes, milord.";
            case 7:
                return "Off I go, then!";
            case 8:
                return "You're the king? Well, I didn't vote for you.";
            case 9:
                return "Say the word.";
            case 10:
                return "Why you poking me again?";

            default:
                break;
        }
        return "Ready to work.";

    }

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
