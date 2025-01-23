package uk.ac.ed.inf;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.gsonUtils.LocalDateDeserializer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
public class OrderServerTest {

    @Test
    public void testGetOrders() throws URISyntaxException, IOException, InterruptedException {
        //Moch JSOn
        String date = "2025-01-22";
        String url = "http://example.com";
        String mockResponseJson = "["
                + "{"
                + "\"orderNo\": \"5873CFAA\","
                + "\"orderDate\": \"2025-01-22\","
                + "\"orderStatus\": \"INVALID\","
                + "\"orderValidationCode\": \"CARD_NUMBER_INVALID\","
                + "\"priceTotalInPence\": 2600,"
                + "\"pizzasInOrder\": ["
                + "{\"name\": \"R2: Meat Lover\", \"priceInPence\": 1400},"
                + "{\"name\": \"R2: Vegan Delight\", \"priceInPence\": 1100}"
                + "],"
                + "\"creditCardInformation\": {"
                + "\"creditCardNumber\": \"6096696930330\","
                + "\"creditCardExpiry\": \"05/25\","
                + "\"cvv\": \"876\""
                + "}"
                + "}"
                + "]";

        // Mock HttpClient and HttpResponse
        HttpClient mockHttpClient = mock(HttpClient.class);
        HttpResponse<String> mockHttpResponse = mock(HttpResponse.class);

        when(mockHttpResponse.body()).thenReturn(mockResponseJson);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockHttpResponse);

        // Inject mocked HttpClient into GetHandler
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
                .create();

        Order[] result = GetHandler.getOrders(date, url, mockHttpClient, gson);

        Order order = result[0];
        // Assertions
        assertNotNull(result);
        assertEquals(1, result.length); // Only two orders should match the date
        assertEquals("5873CFAA", order.getOrderNo());
        assertEquals("INVALID", order.getOrderStatus().toString());
    }
    @Test
    public void testGetOrdersWithInvalidFormat() throws URISyntaxException, IOException, InterruptedException {
        String date = "2025-01-22";
        String url = "http://example.com";

        // Mock invalid JSON response price is invalid
        String invalidResponseJson = "["
                + "{"
                + "\"orderNo\": \"5873CFAA\","
                + "\"orderDate\": \"2025-01-22\","
                + "\"orderStatus\": \"INVALID\","
                + "\"orderValidationCode\": \"CARD_NUMBER_INVALID\","
                + "\"priceTotalInPence\": \"invalid_format\","
                + "\"pizzasInOrder\": ["
                + "{\"name\": \"R2: Meat Lover\", \"priceInPence\": 1400},"
                + "{\"name\": \"R2: Vegan Delight\", \"priceInPence\": 1100}"
                + "],"
                + "\"creditCardInformation\": {"
                + "\"creditCardNumber\": \"6096696930330\","
                + "\"creditCardExpiry\": \"05/25\","
                + "\"cvv\": \"876\""
                + "}"
                + "}"
                + "]";

        // Mock HttpClient and HttpResponse
        HttpClient mockHttpClient = mock(HttpClient.class);
        HttpResponse<String> mockHttpResponse = mock(HttpResponse.class);

        when(mockHttpResponse.body()).thenReturn(invalidResponseJson);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockHttpResponse);

        // Create a Gson instance with the custom LocalDate deserializer
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
                .create();

        //Assertion expecting an exception
        assertThrows(com.google.gson.JsonSyntaxException.class, () -> {
            GetHandler.getOrders(date, url, mockHttpClient, gson);
        });
    }
}
