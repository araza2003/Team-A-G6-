import java.io.*;
import java.net.*;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;

public class SimpleWebServer {
    private static final String LOG_FILE = "access.log";

    public static void main(String[] args) {
        int port = 8080;

        try (ServerSocket serverSocket = new ServerSocket(port);
             PrintWriter logWriter = new PrintWriter(new BufferedWriter(new FileWriter(LOG_FILE, true)), true)) {
            System.out.println("Server is listening on port " + port);

            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    System.out.println("----------------------------\n");
                    String clientIP = clientSocket.getInetAddress().getHostAddress();
                    System.out.println("New connection accepted from " + clientIP);

                    // Read the HTTP request
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String requestLine = in.readLine();
                    System.out.println("Request: " + requestLine);

                    // Parse the request
                    String response;
                    int statusCode;
                    if (requestLine != null && requestLine.startsWith("GET ")) {
                        String path = requestLine.split(" ")[1];
                        if ("/".equals(path)) {
                            response = generateResponse(200, "Success!");
                            statusCode = 200;
                        } else {
                            String filePath = path.substring(1); // Remove leading '/'
                            File file = new File(filePath);
                            if (file.exists() && file.isFile()) {
                                response = generateFileResponse(file);
                                statusCode = 200;
                            } else {
                                response = generateResponse(404, "File not found...");
                                statusCode = 404;
                            }
                        }
                    } else {
                        response = generateResponse(404, "Invalid request...");
                        statusCode = 404;
                    }

                    // Log the access
                    logAccess(logWriter, clientIP, requestLine, statusCode);

                    // Send the response
                    OutputStream out = clientSocket.getOutputStream();
                    System.out.println(response);
                    out.write(response.getBytes());
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

    private static String generateFileResponse(File file) {
        try {
            String dateTime = getServerTime();

            // Determine content type
            FileNameMap fileNameMap = URLConnection.getFileNameMap();
            String contentType = fileNameMap.getContentTypeFor(file.getName());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            // Read file content
            StringBuilder contentBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    contentBuilder.append(line).append("\n");
                }
            }

            String content = contentBuilder.toString();
            String headers = "HTTP/1.1 200 OK\r\n" +
                             "Date: " + dateTime + "\r\n" +
                             "Content-Type: " + contentType + "\r\n" +
                             "Content-Length: " + content.length() + "\r\n" +
                             "\r\n";

            return headers + content;
        } catch (IOException e) {
            return generateResponse(500, "Internal Server Error...");
        }
    }

    private static String getServerTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss zzz", new DateFormatSymbols(Locale.US));
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        return formatter.format(new Date());
    }

    private static void logAccess(PrintWriter logWriter, String clientIP, String requestLine, int statusCode) {
        if (requestLine == null) {
            requestLine = "-";
        }

        SimpleDateFormat logFormatter = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z", new DateFormatSymbols(Locale.US));
        String dateTime = logFormatter.format(new Date());

        String logEntry = String.format("%s - - [%s] \"%s\" %d -", clientIP, dateTime, requestLine, statusCode);
        System.out.println(logEntry);
        logWriter.println(logEntry);
    }
}
