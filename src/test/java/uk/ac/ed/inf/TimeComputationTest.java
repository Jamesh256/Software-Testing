package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.gsonUtils.LocalDateDeserializer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TimeComputationTest {
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
    public void testSmallClose() throws IOException, URISyntaxException, InterruptedException {
        // Arrange
        String url = "https://ilp-rest-2024.azurewebsites.net";

        Restaurant[] restaurants = GetHandler.getRestaurants(url);
        Order[] orders = readOrdersFromFile("timeorderSmallClose.json");
        Node appletonTower = new Node(new LngLat(-3.186874, 55.944494));
        NamedRegion[] noFlyZones = GetHandler.getNoFlyZones(url);


        // Measure the start time
        long startTime = System.currentTimeMillis();

        // Action
        List<Move> flightPath = FlightPath.orderFlightPaths(orders, restaurants, appletonTower, noFlyZones);

        // Measure the end time
        long endTime = System.currentTimeMillis();

        // Calculate the elapsed time
        long elapsedTime = endTime - startTime;

        // Assert
        assertNotNull(flightPath, "Flight path should not be null");
        assertTrue(flightPath.size() > 0, "Flight path should contain moves");
        assertTrue(elapsedTime < 60000, "Flight path ordering should take less than 60 seconds");

    }
    @Test
    public void testBigClose() throws IOException, URISyntaxException, InterruptedException {
        // Arrange
        String url = "https://ilp-rest-2024.azurewebsites.net";

        Restaurant[] restaurants = GetHandler.getRestaurants(url);
        Order[] orders = readOrdersFromFile("timeorderBigClose.json");
        Node appletonTower = new Node(new LngLat(-3.186874, 55.944494));
        NamedRegion[] noFlyZones = GetHandler.getNoFlyZones(url);


        // Measure the start time
        long startTime = System.currentTimeMillis();

        // Action
        List<Move> flightPath = FlightPath.orderFlightPaths(orders, restaurants, appletonTower, noFlyZones);

        // Measure the end time
        long endTime = System.currentTimeMillis();

        // Calculate the elapsed time
        long elapsedTime = endTime - startTime;

        // Assert
        assertNotNull(flightPath, "Flight path should not be null");
        assertTrue(flightPath.size() > 0, "Flight path should contain moves");
        assertTrue(elapsedTime < 60000, "Flight path ordering should take less than 60 seconds");

    }
    @Test
    public void testBigSpread() throws IOException, URISyntaxException, InterruptedException {
        // Arrange
        String url = "https://ilp-rest-2024.azurewebsites.net";

        Restaurant[] restaurants = GetHandler.getRestaurants(url);
        Order[] orders = readOrdersFromFile("timeorderBigSpread.json");
        Node appletonTower = new Node(new LngLat(-3.186874, 55.944494));
        NamedRegion[] noFlyZones = GetHandler.getNoFlyZones(url);

        // Measure the start time
        long startTime = System.currentTimeMillis();

        // Action
        List<Move> flightPath = FlightPath.orderFlightPaths(orders, restaurants, appletonTower, noFlyZones);

        // Measure the end time
        long endTime = System.currentTimeMillis();

        // Calculate the elapsed time
        long elapsedTime = endTime - startTime;

        // Assert
        assertNotNull(flightPath, "Flight path should not be null");
        assertTrue(flightPath.size() > 0, "Flight path should contain moves");
        assertTrue(elapsedTime < 60000, "Flight path ordering should take less than 60 seconds");

    }
    @Test
    public void testSmallSpread() throws IOException, URISyntaxException, InterruptedException {
        // Arrange
        String url = "https://ilp-rest-2024.azurewebsites.net";

        Restaurant[] restaurants = GetHandler.getRestaurants(url);
        Order[] orders = readOrdersFromFile("timeOrderSmallSpread.json");
        Node appletonTower = new Node(new LngLat(-3.186874, 55.944494));
        NamedRegion[] noFlyZones = GetHandler.getNoFlyZones(url);


        // Measure the start time
        long startTime = System.currentTimeMillis();

        // Action
        List<Move> flightPath = FlightPath.orderFlightPaths(orders, restaurants, appletonTower, noFlyZones);

        // Measure the end time
        long endTime = System.currentTimeMillis();

        // Calculate the elapsed time
        long elapsedTime = endTime - startTime;

        // Assert
        assertNotNull(flightPath, "Flight path should not be null");
        assertTrue(flightPath.size() > 0, "Flight path should contain moves");
        assertTrue(elapsedTime < 60000, "Flight path ordering should take less than 60 seconds");

    }
}
