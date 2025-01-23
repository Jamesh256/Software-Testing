package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Pizza;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.util.*;

public class FlightPath {

    public static List<Move> orderFlightPaths(Order[] orders, Restaurant[] restaurants, Node AppletonTower
            , NamedRegion[] noFlyZones){

        Map<String,List<Move>> restaurantsFlightPath = new HashMap<>();
        List<Move> ordersFlightPaths = new ArrayList<>();
        //Find the flight paths for every restaurant

        for (Restaurant restaurant : restaurants){
             restaurantsFlightPath.put(restaurant.name(),
             AStarSolver.findShortestPath(AppletonTower,new Node(restaurant.location()),noFlyZones));
        }


        for (Order order : orders){
            //for every valid order get the restaurants flight path then also get the reverse for the flight back
            //adding a hover to the end of the flight back then add this to the total ordersFlightPaths
            //also change the orderNo on each move to the current orderNo
            if (order.getOrderValidationCode().equals(OrderValidationCode.NO_ERROR)) {
                order.setOrderStatus(OrderStatus.DELIVERED);
                String orderNo = order.getOrderNo();
                List<Move> orderFlightPath = restaurantsFlightPath.get(orderRestaurant(order, restaurants).name());
                //create new ArrayList so reverse doesn't reverse both flight paths
                List<Move> orderFlightBack = new ArrayList<>(orderFlightPath);
                //reverse order and toCoords and fromCoords
                Collections.reverse(orderFlightBack);
                List<Move> orderFlightPathReversed= reverseCoordinates(orderFlightBack);

                //remove hover at start and add new hover at end
                orderFlightPathReversed.remove(0);
                Move lastMove = orderFlightPath.get(0);
                Move hover = new Move(lastMove.getFromLongitude(),lastMove.getFromLatitude(),999
                        ,lastMove.getFromLongitude(), lastMove.getFromLatitude());
                hover.setOrderNo(orderNo);
                orderFlightPathReversed.add(hover);

                ordersFlightPaths.addAll(setMoveOrderNo(orderFlightPath,orderNo));
                ordersFlightPaths.addAll(setMoveOrderNo(orderFlightPathReversed,orderNo));
            }
        }
        return ordersFlightPaths;
    }

    // Helper method to retrieve RestaurantFlightPath for a specific restaurant


    private static Restaurant orderRestaurant(Order order, Restaurant[] restaurants){
        //already verified that the order has pizzas from one menu so taking first item is fine
        Pizza pizza = order.getPizzasInOrder()[0];
        //check every restaurant menu for this pizza
        for (Restaurant restaurant : restaurants) {
            Pizza[] restaurantPizzas = restaurant.menu();
            for (Pizza i : restaurantPizzas) {
                if (i.name().equals(pizza.name())) {
                    return restaurant;
                }
            }
        }
        return null;
    }
    private static List<Move> setMoveOrderNo(List<Move> path,String orderNo){
        //have to create new memory location for move otherwise the orderNo will be the same for each restaurant route
        List<Move> newPath = new ArrayList<>();
        for (Move move :path){
            Move newMove = new Move(move.getFromLongitude(), move.getFromLatitude(), move.getAngle(), move.getToLongitude(),
                    move.getToLatitude());
            newMove.setOrderNo(orderNo);
            newPath.add(newMove);
        }
        return newPath;
    }
    private static List<Move> reverseCoordinates(List<Move> path){
        List<Move> reversePath = new ArrayList<>();
        for (Move move: path){
            //must get the opposite angle as moving in opposite direction
            double  newAngle = (180 + move.getAngle())%360;
            Move newMove = new Move(move.getToLongitude(), move.getToLatitude(),newAngle ,move.getFromLongitude(), move.getFromLatitude());
            reversePath.add(newMove);
        }
        return reversePath;
    }
}
