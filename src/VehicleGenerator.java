import java.util.Random;

/**
 * Created by lmanica on 29/10/17.
 */
public class VehicleGenerator {

    private Random rand;

    public VehicleGenerator() {
        this.rand = new Random(System.currentTimeMillis());
    }

    private String generatePlate() {
        String plate = "";
        for (int i = 0; i < 3; i++)
            plate += (char) (65 + Math.abs(rand.nextInt() % 26));
        plate += (char) 0x2d;
        plate += String.format("%04d", rand.nextInt(10000));
        return plate;
    }

    public Vehicle generateVehicle() {
        return new Vehicle(generatePlate(), generateRandomType());
    }
    
    private Vehicle.VehicleType generateRandomType() {
        int x = rand.nextInt(100);
        if (x < 14) return Vehicle.VehicleType.BIKE;
        else if (x < 89) return Vehicle.VehicleType.CAR;
        else return Vehicle.VehicleType.TRUCK;
    }
}
