package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.gsonUtils.LocalDateDeserializer;
import uk.ac.ed.inf.ilp.interfaces.OrderValidation;


import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

public class Main
{
    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {

        OrderValidation validation = new OrderValidator();
        final Node APPLETON_TOWER = new Node(new LngLat(-3.186874,55.944494));

        String date = args[0];
        ValidateFormat.validateDate(date);
        String urlInput = args[1];
        //Validate string and get rid of final / if there
        ValidateFormat.isValidURI(urlInput);
        String url = ValidateFormat.removeTrailingSlash(urlInput);

        //Get all the relevant information from the server


        Restaurant[] restaurants = GetHandler.getRestaurants(url);
        // Create a Gson instance with a LocalDate deserializer
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
                .create();
        HttpClient httpClient = HttpClient.newHttpClient();

        Order[] orders = GetHandler.getOrders(date, url,httpClient,gson);
        NamedRegion[] noFlyZones = GetHandler.getNoFlyZones(url);
        NamedRegion centralArea = GetHandler.getCentralArea(url);

        //call validate order to see find valid orders
        for (Order order:orders) {
            validation.validateOrder(order,restaurants);
        }

        //create result files directory if needed
        String directoryName = "resultfiles";
        Path relativePath = Paths.get(directoryName);
        if (!Files.exists(relativePath)) {
            try {
                // Create the directory
                Files.createDirectories(relativePath);
                System.out.println("Directory created successfully at: " + relativePath);
            } catch (IOException e) {
                // Handle the exception
                System.err.println("Failed to create directory: " + e.getMessage());
            }
        }

        boolean isOrders = orders.length != 0;
        //create files
        List<Move> flightPath = FlightPath.orderFlightPaths(orders,restaurants,APPLETON_TOWER,noFlyZones);
        CreateFiles.CreateFlightPathFile(flightPath,isOrders,date);
        CreateFiles.CreateGeoJSON(flightPath,isOrders,date);
        CreateFiles.CreateDeliveryFile(orders,isOrders,date);

        System.out.println("finished");

    }

}