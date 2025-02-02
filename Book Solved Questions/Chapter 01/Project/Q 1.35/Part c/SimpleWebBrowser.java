import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SimpleWebBrowser {

    public static void main(String[] args) {
        try {
            // Prompt user for a URL
            System.out.print("Enter a URL: ");
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            String urlString = consoleReader.readLine();

            // Open a file to write output
            PrintWriter fileWriter = new PrintWriter(new FileWriter("output.txt"));

            while (true) {
                // Create a URL object
                URL url = new URL(urlString);

                // Extract host, port, and path
                String host = url.getHost();
                int port;
                boolean isHttps = url.getProtocol().equalsIgnoreCase("https");
                if (isHttps) {
                    port = (url.getPort() == -1) ? 443 : url.getPort();
                } else {
                    port = (url.getPort() == -1) ? 80 : url.getPort();
                }
                String path = (url.getPath().isEmpty()) ? "/" : url.getPath();

                // Open a socket connection
                Socket socket;
                if (isHttps) {
                    SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                    socket = sslSocketFactory.createSocket(host, port);
                } else {
                    socket = new Socket(host, port);
                }

                // Create the HTTP GET request
                String request = "GET " + path + " HTTP/1.1\r\n" +
                                 "Host: " + host + "\r\n" +
                                 "Connection: close\r\n\r\n";

                // Send the request
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(request.getBytes());
                outputStream.flush();

                // Read the response
                InputStream inputStream = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                boolean isRedirect = false;
                String redirectUrl = null;

                while ((line = reader.readLine()) != null) {
                    fileWriter.println(line);
                    System.out.println(line);

                    // Check for redirect (status code 3xx)
                    if (line.startsWith("HTTP/1.1 3")) {
                        isRedirect = true;
                    }

                    // Extract "Location" header for redirect URL
                    if (isRedirect && line.toLowerCase().startsWith("location: ")) {
                        redirectUrl = line.split(" ", 2)[1].trim();
                    }

                    // End of headers
                    if (line.isEmpty()) {
                        break;
                    }
                }

                if (isRedirect && redirectUrl != null) {
                    fileWriter.println("Redirected to: " + redirectUrl);
                    System.out.println("Redirected to: " + redirectUrl);
                    urlString = redirectUrl;
                } else {
                    // End the loop if not a redirect
                    break;
                }

                reader.close();
                socket.close();
            }

            fileWriter.close();
            System.out.println("The output has been written to output.txt");

        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
