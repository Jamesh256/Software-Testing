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

public class FlightPathTests {
    @Test
    public void testFlightPathWithNoOrders() {
        Order[] orders = new Order[0]; // No orders
        Pizza pizzaB = new Pizza("D",10);
        Restaurant restaurantB = new Restaurant("b",
                new LngLat(	-3.1912869215011597,	55.945535152517735),
                new DayOfWeek[]{DayOfWeek.MONDAY,DayOfWeek.THURSDAY},
                new Pizza[]{new Pizza("B",10)});

        Restaurant[] restaurants = {restaurantB};
        Node appletonTower = new Node(new LngLat(-3.186874, 55.944494));
        NamedRegion[] noFlyZones = {};

        List<Move> flightPath = FlightPath.orderFlightPaths(orders, restaurants, appletonTower, noFlyZones);

        // Assert
        assertNotNull(flightPath, "Flight path should not be null");
        assertEquals(0, flightPath.size(), "Flight path should contain no moves for zero orders");
    }

    // Helper method to read orders from a JSON file
    private Order[] readOrdersFromFile(String fileName) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new IOException("File not found: " + fileName);
        }
        String json = new String(inputStream.readAllBytes());
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
                .create();
        return gson.fromJson(json, Order[].class);
    }

    @Test
    public void testFlightPathWithThreeOrders() throws IOException, URISyntaxException, InterruptedException {
        String url = "https://ilp-rest-2024.azurewebsites.net";

        Restaurant[] restaurants = GetHandler.getRestaurants(url);
        Order[] orders = readOrdersFromFile("mockorder3.json");
        Node appletonTower = new Node(new LngLat(-3.186874, 55.944494));
        NamedRegion[] noFlyZones = GetHandler.getNoFlyZones(url);


        List<Move> flightPath = FlightPath.orderFlightPaths(orders, restaurants, appletonTower, noFlyZones);

        // Assert
        assertNotNull(flightPath, "Flight path should not be null");
        assertTrue(flightPath.size() > 0, "Flight path should contain moves ");
        // Manual verification: Save  flightPath for manual validation
        CreateFiles.CreateGeoJSON(flightPath,true,"2025-01-23");
    }
    @Test
    public void testFlightPathWithTenOrders() throws IOException, URISyntaxException, InterruptedException {
        String url = "https://ilp-rest-2024.azurewebsites.net";

        Restaurant[] restaurants = GetHandler.getRestaurants(url);
        Order[] orders = readOrdersFromFile("mockorder10.json");
        Node appletonTower = new Node(new LngLat(-3.186874, 55.944494));
        NamedRegion[] noFlyZones = GetHandler.getNoFlyZones(url);


        List<Move> flightPath = FlightPath.orderFlightPaths(orders, restaurants, appletonTower, noFlyZones);

        // Assert
        assertNotNull(flightPath, "Flight path should not be null");
        assertTrue(flightPath.size() > 0, "Flight path should contain moves ");
        // Manual verification: Save  flightPath for manual validation
        CreateFiles.CreateGeoJSON(flightPath,true,"2025-01-23");
    }
}
