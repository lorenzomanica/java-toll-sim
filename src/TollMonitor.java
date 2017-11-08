import com.google.gson.Gson;
import com.ning.http.client.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

/**
 * Created by lmanica on 29/10/17.
 */
public class TollMonitor implements Runnable {


    private static final long BASE_TIME = 10000L;
    private List<TollLane> lanes;

    public TollMonitor(List<TollLane> lanes) {
        this.lanes = lanes;
    }

    @Override
    public void run() {
        AsyncHttpClient httpClient = new AsyncHttpClient();
        while (true) {
            try {
                Thread.sleep(BASE_TIME);
                for (TollLane t: lanes) {

                    TollLane.LaneInfo info = t.getLaneInfo();

                    httpClient.preparePut(TollSim.baseUrl + "/toll/status/"+info.getLaneId())
                            .setHeader("Content-Type", "application/json")
                            .addHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString(("elastic:changeme").getBytes(StandardCharsets.UTF_8)))
                            .setBody(new Gson().toJson(info))
                            .execute();

                    StringBuilder sb = new StringBuilder();
                    sb.append(System.currentTimeMillis() + " ")
                            .append("Lane: " + info.getLaneId() + " ")
                            .append("Queue: " + info.getQueue() + " ")
                            .append("Profit: " + info.getProfit() + " ")
                            .append("Tickets: " + info.getTickets() + " ");

                    System.out.println(sb.toString());
                }
            }
            catch (Exception e) {
            }
        }
    }
}
