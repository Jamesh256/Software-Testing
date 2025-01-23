package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.gsonUtils.LocalDateDeserializer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.Arrays;
public class GetHandler {
    public static Restaurant[] getRestaurants(String url) {
        try {
            //Get restaurants
            Gson gson = new GsonBuilder().create();

            HttpClient httpClient = HttpClient.newHttpClient();

            HttpRequest getRequestRestaurants = HttpRequest.newBuilder().uri(new URI(url + "/restaurants")).build();
            HttpResponse<String> getResponseRestaurants = httpClient.send(getRequestRestaurants, HttpResponse.BodyHandlers.ofString());
            if (getResponseRestaurants.statusCode() == 200) {
                return gson.fromJson(getResponseRestaurants.body(), Restaurant[].class);
            } else {
                System.err.println("Failed to get restaurants. Status code: " + getResponseRestaurants.statusCode());
            }        }
        catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    public static Order[] getOrdersOld(String date, String url) throws URISyntaxException, IOException, InterruptedException {
        //Get the orders for the given date
        //Must add date deserializer to the gson builder
        Gson gson = new GsonBuilder().
                registerTypeAdapter(LocalDate.class, new LocalDateDeserializer()).create();
        HttpClient httpClient = HttpClient.newHttpClient();


        HttpRequest getRequestOrders = HttpRequest.newBuilder().uri(new URI(url + "/orders/" + date)).build();
        HttpResponse<String> getResponseOrders = httpClient.send(getRequestOrders, HttpResponse.BodyHandlers.ofString());
        return gson.fromJson(getResponseOrders.body(),Order[].class);
    }
    public static Order[] getOrders(String date, String url, HttpClient httpClient, Gson gson) throws URISyntaxException, IOException, InterruptedException {
        // Parse  date
        LocalDate targetDate = LocalDate.parse(date);



        // Fetch all orders from /orders
        HttpRequest getRequestOrders = HttpRequest.newBuilder()
                .uri(new URI(url + "/orders"))
                .build();

        HttpResponse<String> getResponseOrders = httpClient.send(getRequestOrders, HttpResponse.BodyHandlers.ofString());

        //all orders into an array
        Order[] allOrders = gson.fromJson(getResponseOrders.body(), Order[].class);

        // Filter orders that match the target date
        // Return the filtered orders as an array
        return Arrays.stream(allOrders)
                .filter(order -> order.getOrderDate().equals(targetDate))
                .toArray(Order[]::new);
    }


    public static NamedRegion[] getNoFlyZones(String url) throws IOException, InterruptedException, URISyntaxException {
        Gson gson = new GsonBuilder().create();

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest getRequestNoFlyZones = HttpRequest.newBuilder().uri(new URI(url+"/noFlyZones")).build();
        HttpResponse<String> getResponseNoFlyZones = httpClient.send(getRequestNoFlyZones, HttpResponse.BodyHandlers.ofString());
        return gson.fromJson(getResponseNoFlyZones.body(),NamedRegion[].class);
    }

    public static NamedRegion getCentralArea(String url) throws IOException, InterruptedException, URISyntaxException {
        Gson gson = new GsonBuilder().create();

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest getRequestCentralArea = HttpRequest.newBuilder().uri(new URI(url+"/centralArea")).build();
        HttpResponse<String> getResponseCentralArea = httpClient.send(getRequestCentralArea, HttpResponse.BodyHandlers.ofString());
        return gson.fromJson(getResponseCentralArea.body(),NamedRegion.class);
    }



}
