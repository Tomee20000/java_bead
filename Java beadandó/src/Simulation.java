public class Simulation {

    public static void main(String[] args){
        Base b1 = new Base("Azeroth");
        new Thread(() -> {
            b1.startPreparation();
        }).start();

    }
}
