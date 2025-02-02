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

            PrintWriter fileWriter = new PrintWriter("output.txt", "UTF-8");

            while (true) {
                // Create a URL object
                URL url = new URL(urlString);

                // Open a connection
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Set the request method to GET
                connection.setRequestMethod("GET");

                // Disable automatic redirection
                connection.setInstanceFollowRedirects(false);

                // Connect to the server
                connection.connect();

                int responseCode = connection.getResponseCode();

                // Check if the response is a redirect (status code 3xx)
                if (responseCode >= 300 && responseCode < 400) {
                    String redirectUrl = connection.getHeaderField("Location");
                    fileWriter.println("Redirected to: " + redirectUrl);
                    System.out.println("Redirected to: " + redirectUrl);
                    urlString = redirectUrl; // Update URL to the new location
                    connection.disconnect();
                } else {
                    // Print the HTTP response code and message
                    fileWriter.println("HTTP Response Code: " + responseCode);
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
                    connection.disconnect();
                    break;
                }
            }

            fileWriter.close();

        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
