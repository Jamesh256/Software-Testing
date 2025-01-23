package uk.ac.ed.inf;

import java.net.URI;
import java.net.URISyntaxException;

public class ValidateFormat {
    public static void validateDate(String date) {
        if (date.matches("^\\d{4}-\\d{2}-\\d{2}$")){
        }
        else{
            System.err.println("date invalid");
            System.exit(1);
        }
    }

    public static boolean isValidURI(String urlString) {
        try {
            // Create a URI from the given string
            URI url = new URI(urlString);

            // If no exception is thrown, the URL is valid
            return true;
        } catch (URISyntaxException e) {
            // MalformedURLException indicates an invalid URL
            System.err.println("Invalid URL");
            System.exit(1);
        }
        return false;
    }
    public static String removeTrailingSlash(String url) {
        // Check if the string ends with "/"
        if (url.endsWith("/")) {
            // Remove the trailing slash
            return url.substring(0, url.length() - 1);
        } else {
            // Return the original string if no trailing slash
            return url;
        }
    }
}
