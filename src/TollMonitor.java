import com.google.gson.Gson;
import com.ning.http.client.*;

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

                    httpClient.preparePut(TollSim.baseUrl + "/toll/lane/info/"+info.getLaneId())
                            .setBody(new Gson().toJson(info))
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
