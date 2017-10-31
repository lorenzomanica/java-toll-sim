import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ning.http.client.*;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by lmanica on 28/10/17.
 */
public class TollLane implements Runnable {

    public static final float PRICE_BIKE = 2.75f;
    public static final float PRICE_CAR = 6.5f;
    public static final float PRICE_TRUCK = 14.5f;

    public static final int BASE_TIME = 1000;


    public Queue<Vehicle> queue;
    private Random rand;
    private int laneId;
    private float total;
    private int cars;

    public TollLane(int id) {
        this.laneId = id;
        this.queue = new LinkedBlockingQueue<>();
        this.rand = new Random(System.currentTimeMillis());
        this.total = 0;
        this.cars = 0;
    }

    public LaneInfo getLaneInfo() {
        return new LaneInfo(System.currentTimeMillis(), laneId, queue.size(), total, cars);
    }

    @Override
    public void run() {
        AsyncHttpClient httpClient = new AsyncHttpClient();
        sendLaneStatus(httpClient);
        while (true) {
            if (!queue.isEmpty()) {
                try {
                    Vehicle v = queue.poll();

                    float val = 0;
                    float tFactor = 1;
                    if (v.getType() == Vehicle.VehicleType.BIKE) {
                        val = PRICE_BIKE;
                        tFactor = 2.5f;
                    }
                    if (v.getType() == Vehicle.VehicleType.CAR) {
                        val = PRICE_CAR;
                        tFactor = 1;
                    }
                    if (v.getType() == Vehicle.VehicleType.TRUCK) {
                        val = PRICE_TRUCK;
                        tFactor = 3;
                    }
                    total = total + val;
                    cars = cars +1;

                    long time = (int) (BASE_TIME * tFactor) + (rand.nextInt(BASE_TIME));

                    Thread.sleep(time);

                    sendTicketProcessStatus(httpClient, v, val);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    private void sendLaneStatus(AsyncHttpClient client) {
        System.out.println("Toll info: Lane" + laneId + "open");

        LaneStatus status = new LaneStatus(System.currentTimeMillis(), laneId, "open");

        client.preparePut(TollSim.baseUrl + "/lane/status/"+laneId)
                .setBody(new Gson().toJson(status))
                .execute(new AsyncHandler<Void>() {
                    @Override
                    public void onThrowable(Throwable throwable) {

                    }

                    @Override
                    public STATE onBodyPartReceived(HttpResponseBodyPart httpResponseBodyPart) throws Exception {
                        return null;
                    }

                    @Override
                    public STATE onStatusReceived(HttpResponseStatus httpResponseStatus) throws Exception {
                        return null;
                    }

                    @Override
                    public STATE onHeadersReceived(HttpResponseHeaders httpResponseHeaders) throws Exception {
                        return null;
                    }

                    @Override
                    public Void onCompleted() throws Exception {
                        return null;
                    }
                });
    }

    private void sendTicketProcessStatus(AsyncHttpClient client, Vehicle v, float val) {
        StringBuilder sb = new StringBuilder();
        sb.append(System.currentTimeMillis() + " ")
                .append("Lane: " + laneId + " ")
                .append("Ticket: " + laneId * 10000 + cars + " ")
                .append("Price: " + val + " ")
                .append("Vehicle: " + v);

        System.out.println(sb.toString());

        TicketProcessStatus ticket = new TicketProcessStatus(
                System.currentTimeMillis(), laneId, (laneId * 10000 + cars), val, v
        );

        client.preparePut(TollSim.baseUrl + "/toll/lane/"+laneId+"/ticket/"+ticket.getTicketNumber())
                .setBody(new Gson().toJson(ticket))
                .execute(new AsyncHandler<Void>() {
                    @Override
                    public void onThrowable(Throwable throwable) {

                    }

                    @Override
                    public STATE onBodyPartReceived(HttpResponseBodyPart httpResponseBodyPart) throws Exception {
                        return null;
                    }

                    @Override
                    public STATE onStatusReceived(HttpResponseStatus httpResponseStatus) throws Exception {
                        return null;
                    }

                    @Override
                    public STATE onHeadersReceived(HttpResponseHeaders httpResponseHeaders) throws Exception {
                        return null;
                    }

                    @Override
                    public Void onCompleted() throws Exception {
                        return null;
                    }
                });

        client.preparePut(TollSim.baseUrl+"/toll/car/"+v.getPlate())
                .setBody(new Gson().toJson(v))
                .execute(new AsyncHandler<Void>() {
                    @Override
                    public void onThrowable(Throwable throwable) {

                    }

                    @Override
                    public STATE onBodyPartReceived(HttpResponseBodyPart httpResponseBodyPart) throws Exception {
                        return null;
                    }

                    @Override
                    public STATE onStatusReceived(HttpResponseStatus httpResponseStatus) throws Exception {
                        return null;
                    }

                    @Override
                    public STATE onHeadersReceived(HttpResponseHeaders httpResponseHeaders) throws Exception {
                        return null;
                    }

                    @Override
                    public Void onCompleted() throws Exception {
                        return null;
                    }
                });

    }


    class LaneStatus {

        private long timestamp;
        private int laneId;
        private String status;

        public LaneStatus() {
        }

        public LaneStatus(long timestamp, int laneId, String status) {
            this.timestamp = timestamp;
            this.laneId = laneId;
            this.status = status;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public int getLaneId() {
            return laneId;
        }

        public void setLaneId(int laneId) {
            this.laneId = laneId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return "{" +
                    "timestamp=" + timestamp + ", " +
                    "laneId=" + laneId + ", " +
                    "status='" + status + '\'' +
                    '}';
        }
    }

    class TicketProcessStatus {

        private long timestamp;
        private int laneId;
        private int ticketNumber;
        private float price;
        private Vehicle vehicle;

        public TicketProcessStatus() {
        }

        public TicketProcessStatus(long timestamp, int laneId, int ticketNumber, float price, Vehicle vehicle) {
            this.timestamp = timestamp;
            this.laneId = laneId;
            this.ticketNumber = ticketNumber;
            this.price = price;
            this.vehicle = vehicle;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public int getLaneId() {
            return laneId;
        }

        public void setLaneId(int laneId) {
            this.laneId = laneId;
        }

        public int getTicketNumber() {
            return ticketNumber;
        }

        public void setTicketNumber(int ticketNumber) {
            this.ticketNumber = ticketNumber;
        }

        public float getPrice() {
            return price;
        }

        public void setPrice(float price) {
            this.price = price;
        }

        public Vehicle getVehicle() {
            return vehicle;
        }

        public void setVehicle(Vehicle vehicle) {
            this.vehicle = vehicle;
        }

        @Override
        public String toString() {
            return "TicketProcessStatus{" +
                    "timestamp=" + timestamp +
                    ", laneId=" + laneId +
                    ", ticketNumber=" + ticketNumber +
                    ", price=" + price +
                    ", vehicle=" + vehicle +
                    '}';
        }
    }

    class LaneInfo {

        private long timestamp;
        private int laneId;
        private int queue;
        private float profit;
        private int tickets;

        public LaneInfo() {}

        public LaneInfo(long timestamp, int laneId, int queue, float profit, int tickets) {
            this.timestamp = timestamp;
            this.laneId = laneId;
            this.queue = queue;
            this.profit = profit;
            this.tickets = tickets;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public int getLaneId() {
            return laneId;
        }

        public void setLaneId(int laneId) {
            this.laneId = laneId;
        }

        public int getQueue() {
            return queue;
        }

        public void setQueue(int queue) {
            this.queue = queue;
        }

        public float getProfit() {
            return profit;
        }

        public void setProfit(float profit) {
            this.profit = profit;
        }

        public int getTickets() {
            return tickets;
        }

        public void setTickets(int tickets) {
            this.tickets = tickets;
        }

        @Override
        public String toString() {
            return "LaneInfo{" +
                    "timestamp=" + timestamp +
                    ", laneId=" + laneId +
                    ", queue=" + queue +
                    ", profit=" + profit +
                    ", tickets=" + tickets +
                    '}';
        }
    }
}
