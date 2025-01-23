package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import uk.ac.ed.inf.ilp.data.Order;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CreateFiles {
    public static void CreateDeliveryFile(Order[] orders,boolean isOrders,String date) {
        //Create the delivery file by making each order compatible with the required format

        List<Delivery> deliveries = new ArrayList<>();
        for (Order order:orders){
            Delivery delivery = new Delivery(order.getOrderNo(),order.getOrderStatus(),order.getOrderValidationCode()
            ,order.getPriceTotalInPence());
            deliveries.add(delivery);
        }
        //create new file in json format with attributes for orderNo,orderStatus,orderValidationCode and costInPence
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(deliveries);
        String filePath = "resultfiles/deliveries-"+date+".json";
        Path path = Paths.get(filePath);
        try {
            Files.writeString(path, json);
        }
        catch (IOException e){
            System.err.println("IOException");
            System.exit(1);
        }
    }
    public static void CreateFlightPathFile(List<Move> flightPath, boolean isOrders,String date){
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(flightPath);
        String filePath = "resultfiles/flightpath-"+date+".json";
        Path path = Paths.get(filePath);
        try {
            Files.writeString(path, json);
        }
        catch (IOException e){
            System.err.println("IOException");
            System.exit(1);
        }
    }
    public static void CreateGeoJSON(List<Move> flightPath,boolean isOrders,String date) {
        if (isOrders) {
            List<Point> routeCoordinates = new ArrayList<>();
            routeCoordinates.add(Point.fromLngLat(-3.186874, 55.944494));
            for (Move point : flightPath) {
                routeCoordinates.add(Point.fromLngLat(point.getToLongitude(), point.getToLatitude()));
            }

            LineString lineString = LineString.fromLngLats(routeCoordinates);
            Feature feature = Feature.fromGeometry(lineString);
            FeatureCollection featureCollection = FeatureCollection.fromFeature(feature);
            String geoJson = featureCollection.toJson();

            String filePath = "resultfiles/drone-" + date + ".geojson";
            Path path = Paths.get(filePath);
            try {
                Files.writeString(path, geoJson);
            } catch (IOException e) {
                System.err.println("IOException");
                System.exit(1);
            }
        }
        else{
            String geoJson = "{ \"type\": \"FeatureCollection\", \"features\": [] }";
            String filePath = "resultfiles/drone-" + date + ".geojson";
            Path path = Paths.get(filePath);
            try {
                Files.writeString(path, geoJson);
            } catch (IOException e) {
                System.err.println("IOException");
                System.exit(1);
            }

        }
    }
}
