import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lmanica on 29/10/17.
 */
public class TollSim {

    private int size;
    private int scatter; //traffic dispersion
    private Random rand;

    public List<TollLane> lanes;
    public VehicleGenerator generator;

    public static String baseUrl;

    public static void main(String[] args) {
        int size = Integer.parseInt(args[0]);
        int scatter = Integer.parseInt(args[1]);
        baseUrl = args[2];

        TollSim instance = new TollSim(size, scatter);
        instance.start();
    }

    public TollSim(int size, int scatter) {
        this.size = size;
        this.scatter = scatter;
        lanes = new ArrayList<>(size);
        generator = new VehicleGenerator();
        rand = new Random(System.currentTimeMillis());
    }

    public void start() {
        ExecutorService svc = Executors.newFixedThreadPool(size+1);

        for (int i = 0; i<size; i++) {
            TollLane lane = new TollLane(i+1);
            lanes.add(lane);
            svc.execute(lane);
        }

        svc.execute(new TollMonitor(lanes));

        int vFactor = Math.floorDiv(this.scatter, size);
        float tFactor = this.scatter * 1.67f;

        while (true) {
            try {
                long time = (int)(vFactor*tFactor) + rand.nextInt((int)tFactor);
                Thread.sleep(time);
                int nextLane = Math.abs(rand.nextInt() % size);
                lanes.get(nextLane).queue.add(generator.generateVehicle());
            }
            catch (InterruptedException e) {
                System.out.println("Execution Interrupted");
            }
        }
    }



}
