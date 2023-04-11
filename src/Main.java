import com.google.gson.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.apache.http.util.*;
import java.time.*;
import java.time.temporal.*;
import java.util.*;
import static spark.Spark.*;

public class Main {
    public static void main(String[] args) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();

        // Make the API calls
        String nextLaunchUrl = "https://api.spacexdata.com/v4/launches/next";
        HttpGet nextLaunchRequest = new HttpGet(nextLaunchUrl);
        String nextLaunchResponse = client.execute(nextLaunchRequest, response -> EntityUtils.toString(response.getEntity()));
        Launch nextLaunch = new Gson().fromJson(nextLaunchResponse, Launch.class);

        String latestLaunchUrl = "https://api.spacexdata.com/v4/launches/latest";
        HttpGet latestLaunchRequest = new HttpGet(latestLaunchUrl);
        String latestLaunchResponse = client.execute(latestLaunchRequest, response -> EntityUtils.toString(response.getEntity()));
        Launch latestLaunch = new Gson().fromJson(latestLaunchResponse, Launch.class);

        // Calculate the countdown
        Instant now = Instant.now();
        Duration timeUntilLaunch = Duration.between(now, nextLaunch.date_utc.toInstant());

        // Render the UI
        port(4567);
        Map<String, Object> model = new HashMap<>();
        model.put("nextLaunch", nextLaunch);
        model.put("latestLaunch", latestLaunch);
        model.put("timeUntilLaunch", timeUntilLaunch.toMillis());
        get("/", (req, res) -> new ModelAndView(model, "index.html"), new VelocityTemplateEngine());
    }
}

class Launch {
    String name;
    String details;
    String date_utc;
    // TODO: add more fields as needed
}