package uk.ac.ed.inf;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.CreditCardInformation;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Pizza;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.interfaces.OrderValidation;

import java.time.LocalDate;
import java.time.DayOfWeek;

public class OrderValidator implements OrderValidation {


    @Override
    public Order validateOrder(Order orderToValidate, Restaurant[] definedRestaurants) {
        CreditCardInformation cardInfo = orderToValidate.getCreditCardInformation();

        //handle when values are null
        if (cardInfo.getCreditCardNumber() == null){
            orderToValidate.setOrderValidationCode(OrderValidationCode.CARD_NUMBER_INVALID);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
        }
        else if (cardInfo.getCreditCardExpiry() == null){
            orderToValidate.setOrderValidationCode(OrderValidationCode.EXPIRY_DATE_INVALID);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
        }
        else if (cardInfo.getCvv() == null){
            orderToValidate.setOrderValidationCode(OrderValidationCode.CVV_INVALID);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
        }
        else if (orderToValidate.getPizzasInOrder() == null){
            orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_NOT_DEFINED);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
        }
        else if (definedRestaurants == null){
            throw new IllegalArgumentException("Must have restaurants");
        }

        //check card number must be 16 digits long and numerical
        else if (cardInfo.getCreditCardNumber().length() != 16
                || !(isNumeric(cardInfo.getCreditCardNumber()))) {
            orderToValidate.setOrderValidationCode(OrderValidationCode.CARD_NUMBER_INVALID);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
        }
        //check expiry using validateExpiry method. Has to be of form mm/yy and after the current mm/yy
        else if (!(validateExpiry(cardInfo.getCreditCardExpiry()))) {
            orderToValidate.setOrderValidationCode(OrderValidationCode.EXPIRY_DATE_INVALID);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
        }

        //check cvv, see if it's 3 numbers
        else if ((cardInfo.getCvv().length() != 3) || !(isNumeric(cardInfo.getCvv()))) {
            orderToValidate.setOrderValidationCode(OrderValidationCode.CVV_INVALID);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
        }

        //check that there is not more than max pizzas per order (currently 4)
        else if (orderToValidate.getPizzasInOrder().length > SystemConstants.MAX_PIZZAS_PER_ORDER) {
            orderToValidate.setOrderValidationCode(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
        }
        //check if pizzas all exist using pizzaExists method before other pizza checks,
        else if (!(pizzasExists(orderToValidate.getPizzasInOrder(), definedRestaurants))) {
            orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_NOT_DEFINED);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
        }
        else if (orderToValidate.getPizzasInOrder().length == 0){
            orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_NOT_DEFINED);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
        }
        //check if the different pizzas are from multiple restaurant (uses multipleRestaurants method)
        else if (!(multipleRestaurants(orderToValidate.getPizzasInOrder(), definedRestaurants))) {
            orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
        }
        //check restaurant is open on the order date,
        else if (!restaurantOpen(orderToValidate.getOrderDate().getDayOfWeek(),
                pizzaRestaurant(orderToValidate.getPizzasInOrder()[0], definedRestaurants).openingDays())) {
            orderToValidate.setOrderValidationCode(OrderValidationCode.RESTAURANT_CLOSED);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
        }
        //check total, also checks whether the pizza prices in an order are the same as in the restaurant
        else if (!validateTotal(orderToValidate.getPriceTotalInPence(), orderToValidate.getPizzasInOrder(),
                pizzaRestaurant(orderToValidate.getPizzasInOrder()[0], definedRestaurants))) {
            orderToValidate.setOrderValidationCode(OrderValidationCode.TOTAL_INCORRECT);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
        }
        //No errors found
        else {
            orderToValidate.setOrderValidationCode(OrderValidationCode.NO_ERROR);
            orderToValidate.setOrderStatus(OrderStatus.VALID_BUT_NOT_DELIVERED);
        }
        return orderToValidate;
    }


    private static boolean validateExpiry(String expiryDate){
        if (expiryDate.length() != 5){
            return false;
        }
        //regex to check whether date is of format MM/YY, checks first char is either 0 or 1
        //then second char is in valid range, then for '/' and finally if last two are numbers
        if (expiryDate.matches("^(0[1-9]|1[0-2])/\\d{2}")){
            //need to check if after current date
            int month = LocalDate.now().getMonthValue();
            int year = LocalDate.now().getYear();
            //get last two digits
            year = year%100;
            //convert expiry date into an integer value for month and year
            String expiryMonthString = expiryDate.substring(0,2);
            int expiryMonth = Integer.parseInt(expiryMonthString);
            String expiryYearString = expiryDate.substring(3,5);
            int expiryYear = Integer.parseInt(expiryYearString);
            // check whether expiry is after current year
            if (expiryYear>year){
                return true;
            }
            //if expiry is this year check it isn't a previous month
            return (year == expiryYear) && (expiryMonth >= month);
        }
        else{
            return false;
        }
    }

    private static boolean pizzasExists(Pizza[] pizzas, Restaurant[] restaurants) {
        for (Pizza pizza : pizzas) {
            boolean exists = false;
            //check every restaurant menu for this pizza
            for (Restaurant restaurant : restaurants) {
                Pizza[] restaurantPizzas = restaurant.menu();
                for (Pizza i : restaurantPizzas) {
                    //only check if names are the same as wrong prices raises TOTAL_INCORRECT validation code
                    if (i.name().equals(pizza.name())) {
                        exists = true;
                        break;
                    }
                }
                if (exists) {
                    break;
                }
            }
            if (!exists) {
                //if one pizza doesn't exist then return false
                return false;
            }
        }
        //all pizzas exist so return true
        return true;
    }
    // finds what restaurant a pizza comes from
    private static Restaurant pizzaRestaurant(Pizza pizza, Restaurant[] restaurants){
        //check every restaurant menu for this pizza
        for (Restaurant restaurant : restaurants) {
            Pizza[] restaurantPizzas = restaurant.menu();
            for (Pizza i : restaurantPizzas) {
                //only check if names are the same as wrong prices raises different validation code
                if (i.name().equals(pizza.name())) {
                    return restaurant;
                }
            }
        }
        return null;
    }

    private static boolean multipleRestaurants(Pizza[] pizzas, Restaurant[] restaurants){
        //find what the first pizza's restaurant is
        Restaurant firstRestaurant = pizzaRestaurant(pizzas[0],restaurants);
        for (Pizza i : pizzas ){
            assert firstRestaurant != null;
            if (!(firstRestaurant.equals(pizzaRestaurant(i,restaurants)))){
                return false;
            }
        }
        return true;
    }

    private static boolean restaurantOpen(DayOfWeek orderDay, DayOfWeek[] restaurantOpenDays){
        for (DayOfWeek i : restaurantOpenDays){
            if (orderDay == i){
                return true;
            }
        }
        return false;
    }

    private static boolean validateTotal(int priceTotal, Pizza[] pizzas,Restaurant restaurant){
        int pizzasPrice = 0;
        //check that the pizzas in the order are the same price as the menu pizza if not return false
        for (Pizza pizza: pizzas){
            for (Pizza restaurantPizza : restaurant.menu()){
                if (pizza.name().equals(restaurantPizza.name())){
                    if (pizza.priceInPence() != restaurantPizza.priceInPence()){
                        return false;
                    }
                }
            }
        }
        for (Pizza i: pizzas){
            pizzasPrice = pizzasPrice + i.priceInPence();
        }
        //price must have added £1 delivery charge to the price
        return priceTotal == (pizzasPrice + SystemConstants.ORDER_CHARGE_IN_PENCE);

    }
    //test whether a given string is numerical
    private static boolean isNumeric(String str) {
        //regular expression for numeric \d is for digit + if for matching multiple character to \d
        return str.matches("\\d+");
    }
}
