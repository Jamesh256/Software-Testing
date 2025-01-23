package uk.ac.ed.inf;

import org.junit.jupiter.api.Test;
import uk.ac.ed.inf.OrderValidator;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.*;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.DayOfWeek;

class OrderValidatorTest {

    @Test
    void testCardNumberNull() {
        CreditCardInformation cardInfo = new CreditCardInformation(null, "12/25", "123");
        Order order = new Order();
        order.setCreditCardInformation(cardInfo);

        OrderValidator validator = new OrderValidator();
        Order result = validator.validateOrder(order, new Restaurant[]{});

        assertEquals(OrderValidationCode.CARD_NUMBER_INVALID, result.getOrderValidationCode());
    }

    @Test
    void testExpiryDateNull() {
        CreditCardInformation cardInfo = new CreditCardInformation("1234567812345678", null, "123");
        Order order = new Order();
        order.setCreditCardInformation(cardInfo);

        OrderValidator validator = new OrderValidator();
        Order result = validator.validateOrder(order, new Restaurant[]{});

        assertEquals(OrderValidationCode.EXPIRY_DATE_INVALID, result.getOrderValidationCode());
    }

    @Test
    void testCvvNull() {
        CreditCardInformation cardInfo = new CreditCardInformation("1234567812345678", "12/25", null);
        Order order = new Order();
        order.setCreditCardInformation(cardInfo);

        OrderValidator validator = new OrderValidator();
        Order result = validator.validateOrder(order, new Restaurant[]{});

        assertEquals(OrderValidationCode.CVV_INVALID, result.getOrderValidationCode());
    }

    @Test
    void testPizzasInOrderNull() {
        CreditCardInformation cardInfo = new CreditCardInformation("1234567812345678", "12/25", "123");
        Order order = new Order();
        order.setCreditCardInformation(cardInfo);
        order.setPizzasInOrder(null);

        OrderValidator validator = new OrderValidator();
        Order result = validator.validateOrder(order, new Restaurant[]{});

        assertEquals(OrderValidationCode.PIZZA_NOT_DEFINED, result.getOrderValidationCode());
    }

    @Test
    void testDefinedRestaurantsNull() {
        CreditCardInformation cardInfo = new CreditCardInformation("1234567812345678", "12/25", "123");
        Order order = new Order();
        order.setCreditCardInformation(cardInfo);

        OrderValidator validator = new OrderValidator();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validator.validateOrder(order, null);
        });

        assertEquals("Must have restaurants", exception.getMessage());
    }

    @Test
    void testCardNumberInvalid() {
        CreditCardInformation cardInfo = new CreditCardInformation("12345ABCD", "12/25", "123");
        Order order = new Order();
        order.setCreditCardInformation(cardInfo);

        OrderValidator validator = new OrderValidator();
        Order result = validator.validateOrder(order, new Restaurant[]{});

        assertEquals(OrderValidationCode.CARD_NUMBER_INVALID, result.getOrderValidationCode());
    }

    @Test
    void testExpiryDateInvalid() {
        CreditCardInformation cardInfo = new CreditCardInformation("1234567812345678", "13/22", "123");
        Order order = new Order();
        order.setCreditCardInformation(cardInfo);

        OrderValidator validator = new OrderValidator();
        Order result = validator.validateOrder(order, new Restaurant[]{});

        assertEquals(OrderValidationCode.EXPIRY_DATE_INVALID, result.getOrderValidationCode());
    }

    @Test
    void testCvvInvalid() {
        CreditCardInformation cardInfo = new CreditCardInformation("1234567812345678", "12/25", "12A");
        Order order = new Order();
        order.setCreditCardInformation(cardInfo);

        OrderValidator validator = new OrderValidator();
        Order result = validator.validateOrder(order, new Restaurant[]{});

        assertEquals(OrderValidationCode.CVV_INVALID, result.getOrderValidationCode());
    }

    @Test
    void testExceedMaxPizzaCount() {
        Pizza[] pizzas = new Pizza[5];
        CreditCardInformation cardInfo = new CreditCardInformation("1234567812345678", "12/25", "123");
        Order order = new Order();
        order.setCreditCardInformation(cardInfo);
        order.setPizzasInOrder(pizzas);

        OrderValidator validator = new OrderValidator();
        Order result = validator.validateOrder(order, new Restaurant[]{});

        assertEquals(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED, result.getOrderValidationCode());
    }

    @Test
    void testPizzasNotExist() {
        Pizza[] pizzas = new Pizza[] { new Pizza("NonExistingPizza", 500) };
        Restaurant[] restaurants = new Restaurant[]{
                new Restaurant("TestRestaurant", new LngLat(10,10), new DayOfWeek[]{DayOfWeek.MONDAY}, new Pizza[]{new Pizza("Margherita", 500)})
        };
        CreditCardInformation cardInfo = new CreditCardInformation("1234567812345678", "12/25", "123");
        Order order = new Order();
        order.setCreditCardInformation(cardInfo);
        order.setPizzasInOrder(pizzas);

        OrderValidator validator = new OrderValidator();
        Order result = validator.validateOrder(order, restaurants);

        assertEquals(OrderValidationCode.PIZZA_NOT_DEFINED, result.getOrderValidationCode());
    }

    @Test
    void testPizzasFromMultipleRestaurants() {
        Pizza[] pizzas = new Pizza[] { new Pizza("Margherita", 500), new Pizza("Pepperoni", 600) };
        Restaurant[] restaurants = new Restaurant[]{
                new Restaurant("RestaurantA", new LngLat(10,10), new DayOfWeek[]{DayOfWeek.MONDAY}, new Pizza[]{new Pizza("Margherita", 500)}),
                new Restaurant("RestaurantB", new LngLat(10,10), new DayOfWeek[]{DayOfWeek.MONDAY}, new Pizza[]{new Pizza("Pepperoni", 600)})
        };
        CreditCardInformation cardInfo = new CreditCardInformation("1234567812345678", "12/25", "123");
        Order order = new Order();
        order.setCreditCardInformation(cardInfo);
        order.setPizzasInOrder(pizzas);

        OrderValidator validator = new OrderValidator();
        Order result = validator.validateOrder(order, restaurants);

        assertEquals(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS, result.getOrderValidationCode());
    }

    @Test
    void testRestaurantClosed() {
        Pizza[] pizzas = new Pizza[] { new Pizza("Margherita", 500) };
        Restaurant[] restaurants = new Restaurant[]{
                new Restaurant("TestRestaurant", new LngLat(10,10), new DayOfWeek[]{DayOfWeek.MONDAY}, new Pizza[]{new Pizza("Margherita", 500)})
        };
        CreditCardInformation cardInfo = new CreditCardInformation("1234567812345678", "12/25", "123");
        Order order = new Order();
        order.setCreditCardInformation(cardInfo);
        order.setPizzasInOrder(pizzas);
        order.setOrderDate(LocalDate.of(2025, 1, 23)); // Assume it's a Wednesday

        OrderValidator validator = new OrderValidator();
        Order result = validator.validateOrder(order, restaurants);

        assertEquals(OrderValidationCode.RESTAURANT_CLOSED, result.getOrderValidationCode());
    }

    @Test
    void testTotalIncorrect() {
        Pizza[] pizzas = new Pizza[] { new Pizza("Margherita", 550) };
        Restaurant[] restaurants = new Restaurant[]{
                new Restaurant("TestRestaurant", new LngLat(10,10), new DayOfWeek[]{DayOfWeek.MONDAY}, new Pizza[]{new Pizza("Margherita", 500)})
        };
        CreditCardInformation cardInfo = new CreditCardInformation("1234567812345678", "12/25", "123");
        Order order = new Order();
        order.setCreditCardInformation(cardInfo);
        order.setPizzasInOrder(pizzas);
        order.setPriceTotalInPence(600); // Incorrect total

        OrderValidator validator = new OrderValidator();
        Order result = validator.validateOrder(order, restaurants);

        assertEquals(OrderValidationCode.TOTAL_INCORRECT, result.getOrderValidationCode());
    }

    @Test
    void testValidOrder() {
        Pizza[] pizzas = new Pizza[] { new Pizza("Margherita", 500) };
        Restaurant[] restaurants = new Restaurant[]{
                new Restaurant("TestRestaurant", new LngLat(10,10), new DayOfWeek[]{DayOfWeek.MONDAY}, new Pizza[]{new Pizza("Margherita", 500)})
        };
        CreditCardInformation cardInfo = new CreditCardInformation("1234567812345678", "12/25", "123");
        Order order = new Order();
        order.setCreditCardInformation(cardInfo);
        order.setPizzasInOrder(pizzas);
        order.setPriceTotalInPence(600); // Total includes delivery charge

        OrderValidator validator = new OrderValidator();
        Order result = validator.validateOrder(order, restaurants);

        assertEquals(OrderValidationCode.NO_ERROR, result.getOrderValidationCode());
        assertEquals(OrderStatus.VALID_BUT_NOT_DELIVERED, result.getOrderStatus());
    }
}
