package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.*;
import uk.ac.ed.inf.ilp.interfaces.LngLatHandling;
import uk.ac.ed.inf.ilp.interfaces.OrderValidation;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

public class TestIlpJar {
    public static void main(String[] args) {
        System.out.println("ILP Test Application using the IlpDataObjects.jar file");

        var order = new Order();
        order.setOrderNo(String.format("%08X", ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE)));
        order.setOrderDate(LocalDate.of(2023, 9, 1));

        order.setCreditCardInformation(
                new CreditCardInformation(
                        "0000000000000000",
                        "12/29",
                        "222"
                )
        );

        // every order has the defined outcome
        order.setOrderStatus(OrderStatus.UNDEFINED);
        order.setOrderValidationCode(OrderValidationCode.UNDEFINED);

        // pizza object for testing
        Pizza pizzaB = new Pizza("D",10);

        // and load the order items plus the price
        order.setPizzasInOrder(new Pizza[]{new Pizza("A", 2300),pizzaB,pizzaB,pizzaB});
        order.setPriceTotalInPence(2330 + SystemConstants.ORDER_CHARGE_IN_PENCE);

        //second restaurant for testing
        Restaurant restaurantB = new Restaurant("b",
                new LngLat(10,10),
                new DayOfWeek[]{DayOfWeek.MONDAY,DayOfWeek.THURSDAY},
                new Pizza[]{new Pizza("B",10)});

        Order validatedOrder =
                new OrderValidator().validateOrder(order,
                        new Restaurant[]{new Restaurant("myRestaurant",
                                new LngLat(55.945535152517735, -3.1912869215011597),
                                new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.FRIDAY},
                                new Pizza[]{new Pizza("A", 2300),pizzaB})
                });

        if (validatedOrder != null){
            System.out.println("order validation resulted in status: " +
                    validatedOrder.getOrderStatus() +
                    " and validation code: " +
                    validatedOrder.getOrderValidationCode());
        } else {
            System.out.println("no order validated");
        }
    }

    public static void formermain(){
        //formerMain();
        LngLat pos1 = new LngLat(0,0);
        LngLat pos2 = new LngLat(0,1);
        LngLat pos3 = new LngLat(1,1);
        LngLat pos4 = new LngLat(1,0);
        double k = new LngLatHandler().distanceTo(new LngLat(1,1),new LngLat(2,1));
        boolean inBox = new LngLatHandler().isInRegion(new LngLat(0,0),
                new NamedRegion("a",new LngLat[]{pos2,pos1,pos4,pos3}));
        LngLat move = new LngLatHandler().nextPosition(pos1,90);
        System.out.println(move.lng()+ " y: "+ move.lat());
    }
}