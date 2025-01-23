package uk.ac.ed.inf;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import uk.ac.ed.inf.ilp.data.*;
import uk.ac.ed.inf.ilp.gsonUtils.LocalDateDeserializer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class APITest {
    @Test
    public void restaurantTest() throws IOException, URISyntaxException, InterruptedException {
        String url = "https://ilp-rest-2024.azurewebsites.net";

        Restaurant[] restaurants = GetHandler.getRestaurants(url);

        assertNotNull(restaurants, "restaurants retrieved");
    }
    @Test
    public void noFlyZoneTest() throws IOException, URISyntaxException, InterruptedException {
        String url = "https://ilp-rest-2024.azurewebsites.net";

        NamedRegion[] noFlyZones = GetHandler.getNoFlyZones(url);

        assertNotNull(noFlyZones, "no fly zones retrieved");
    }
}
