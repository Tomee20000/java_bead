import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class Base {

    private static final int STARTER_PEASANT_NUMBER = 5;
    private static final int PEASANT_NUMBER_GOAL = 10;

    // lock to ensure only one unit can be trained at one time
    private final ReentrantLock trainingLock = new ReentrantLock();

    private final String name;
    private final Resources resources = new Resources();
    private final List<Peasant> peasants = Collections.synchronizedList(new LinkedList<>());
    private final List<Building> buildings = Collections.synchronizedList(new LinkedList<>());

    public Base(String name){
        this.name = name;

        while (peasants.size() != STARTER_PEASANT_NUMBER){
            Peasant p = createPeasant();
            if (p != null){
                if (peasants.size() < 4) p.startMining();
                else if (peasants.size() == 4) p.startCuttingWood();
            }
        }
    }

    public void startPreparation(){
        Thread building = new Thread(() -> {
            while (!baseReady(UnitType.FARM, 3)){
                Peasant p = this.getFreePeasant();
                if (p != null) p.tryBuilding(UnitType.FARM);         
                sleepForMsec(UnitType.FARM.buildTime);
            }

            while (!baseReady(UnitType.LUMBERMILL, 1)){
                Peasant p = this.getFreePeasant();
                if (p != null) p.tryBuilding(UnitType.LUMBERMILL);
                sleepForMsec(UnitType.LUMBERMILL.buildTime);
            }

            while (!baseReady(UnitType.BLACKSMITH, 1)){
                Peasant p = this.getFreePeasant();
                if (p != null) p.tryBuilding(UnitType.BLACKSMITH);
                sleepForMsec(UnitType.BLACKSMITH.buildTime);
            }
        });
        building.start();

        Thread training = new Thread(() -> {
            synchronized(peasants){
                for (Peasant p : peasants){
                    p.startMining();
                }
            }
            while (peasants.size() != PEASANT_NUMBER_GOAL){
                Peasant p = createPeasant();
                if (p != null){
                    if (peasants.size() < 8) p.startCuttingWood();
                }
            }
        });
        training.start();

        try{
            building.join();
            training.join();
        } catch (InterruptedException ie){
            System.out.println("Main has been interrupted");
        }

        synchronized(peasants){
            for (Peasant p : peasants){
                p.stopHarvesting();
            }
        }
        System.out.println("---------------------------------------");
        System.out.println(this.name + " finished creating a base");
        System.out.println(this.name + " peasants: " + this.peasants.size());
        for(Building b : buildings){
            System.out.println(this.name + " has a  " + b.getUnitType().toString());
        }
    }

    private Peasant createPeasant(){
        Peasant result;
        if(resources.canTrain(UnitType.PEASANT.goldCost, UnitType.PEASANT.woodCost, UnitType.PEASANT.foodCost)){
            synchronized(trainingLock){
                sleepForMsec(UnitType.PEASANT.buildTime);
                this.resources.removeResource(UnitType.PEASANT.goldCost, UnitType.PEASANT.woodCost);
                this.resources.updateCapacity(UnitType.PEASANT.foodCost);
                result = Peasant.createPeasant(this);
                peasants.add(result);
            }
            return result;
        }
        return null;
    }

    private Peasant getFreePeasant(){
        synchronized(this.peasants){
            for (Peasant p : this.peasants){
                if (p.isFree())
                    return p;
            }
        }
        return null;
    }

    public Resources getResources(){
        return this.resources;
    }

    public List<Building> getBuildings(){
        return this.buildings;
    }

    public String getName(){
        return this.name;
    }

    private boolean baseReady(UnitType unitType, int required){
        int building_count = 0;
        synchronized(this.buildings){
            for (Building b : this.buildings){
                if (b.getUnitType().equals(unitType))
                    ++building_count;
            }
        }
        
        if (building_count >= required)
            return true;

        return false;
    }

    private static void sleepForMsec(int sleepTime) {
        try {
            TimeUnit.MILLISECONDS.sleep(sleepTime);
        } catch (InterruptedException e) {
        }
    }

}
