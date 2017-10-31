/**
 * Created by lmanica on 28/10/17.
 */
public class Vehicle {

    private String plate;
    private VehicleType type;

    public Vehicle(String plate, VehicleType type) {
        this.plate = plate;
        this.type = type;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public VehicleType getType() {
        return type;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        if (type == VehicleType.BIKE)
            sb.append("Bike ");
        if (type == VehicleType.CAR)
            sb.append("Car ");
        if (type == VehicleType.TRUCK)
            sb.append("Truck ");
        sb.append(plate);

        return sb.toString();
    }

    public enum VehicleType { BIKE, CAR, TRUCK }
}
