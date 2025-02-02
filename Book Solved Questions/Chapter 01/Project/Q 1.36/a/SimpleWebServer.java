import java.io.*;
import java.net.*;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;

public class SimpleWebServer {
    public static void main(String[] args) {
        int port = 3002;
        
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    System.out.println("----------------------------- \n");
                    System.out.println("New connection accepted");

                    // Read the HTTP request
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String requestLine = in.readLine();
                    System.out.println("Request: " + requestLine);

                    // Parse the request
                    String response;
                    if (requestLine != null && requestLine.startsWith("GET ")) {
                        String path = requestLine.split("")[1];
                        if ("/".equals(path)) {
                            response = generateResponse(200, "Success!");
                        } else {
                            response = generateResponse(404, "Failure...");
                        }
                    } else {
                        response = generateResponse(404, "Failure...");
                    }

                    // Send the response
                    OutputStream out = clientSocket.getOutputStream();
                    out.write(response.getBytes());
                    System.out.println(response);
                    out.flush();
                } catch (IOException e) {
                    System.err.println("Error handling client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    private static String generateResponse(int statusCode, String message) {
        String statusLine;
        if (statusCode == 200) {
            statusLine = "HTTP/1.1 200 OK\r\n";
        } else {
            statusLine = "HTTP/1.1 404 Not Found\r\n";
        }

        String dateTime = getServerTime();
        String headers = "Date: " + dateTime + "\r\n" +
                         "Content-Type: text/plain\r\n" +
                         "Content-Length: " + message.length() + "\r\n" +
                         "\r\n";

        return statusLine + headers + message;
    }

    private static String getServerTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss zzz", new DateFormatSymbols(Locale.US));
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        return formatter.format(new Date());
    }
}
