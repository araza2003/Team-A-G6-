import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class SimpleWebBrowser {

    public static void main(String[] args) {
        try {
            // Prompt user for a URL
            System.out.print("Enter a URL: ");
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            String urlString = consoleReader.readLine();

            // Create a URL object
            URL url = new URL(urlString);

            // Open a connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method to GET
            connection.setRequestMethod("GET");

            // Connect to the server
            connection.connect();

            // Prepare output file
            PrintWriter fileWriter = new PrintWriter("output.txt", "UTF-8");

            // Print the HTTP response code and message
            fileWriter.println("HTTP Response Code: " + connection.getResponseCode());
            fileWriter.println("HTTP Response Message: " + connection.getResponseMessage());

            // Print HTTP response headers
            fileWriter.println("\nHTTP Headers:");
            for (int i = 0; ; i++) {
                String headerKey = connection.getHeaderFieldKey(i);
                String headerValue = connection.getHeaderField(i);
                if (headerKey == null && headerValue == null) {
                    break;
                }
                fileWriter.println(headerKey + ": " + headerValue);
            }

            // Print the body of the HTTP response
            fileWriter.println("\nHTTP Body:");
            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                fileWriter.println(line);
            }

            // Close streams and connection
            reader.close();
            fileWriter.close();
            connection.disconnect();

        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}