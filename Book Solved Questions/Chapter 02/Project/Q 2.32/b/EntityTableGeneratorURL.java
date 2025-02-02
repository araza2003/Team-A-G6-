package b;
import java.io.*;
import java.net.*;
import java.util.regex.*;

public class EntityTableGeneratorURL {
    public static void main(String[] args) {
        try {
            // Input URL and output file
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter the URL of the entity set: ");
            String urlString = reader.readLine();
            System.out.print("Enter the output XHTML file path: ");
            String outputFile = reader.readLine();

            // Fixing the deprecated constructor
            URI uri = URI.create(urlString);
            URL url = uri.toURL();
            URLConnection connection = url.openConnection();
            BufferedReader urlReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuilder xhtmlContent = new StringBuilder();

            // XHTML document header
            xhtmlContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            xhtmlContent.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\n");
            xhtmlContent.append("\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n");
            xhtmlContent.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n<head>\n");
            xhtmlContent.append("<title>Entity Test Table</title>\n</head>\n<body>\n");
            xhtmlContent.append("<h1>XHTML Entity Test</h1>\n");
            xhtmlContent.append("<table border=\"1\">\n<tr><th>Entity Name</th><th>Entity Reference</th></tr>\n");

            // Regular expression to extract entity name
            Pattern pattern = Pattern.compile("^<!ENTITY\\s+(\\S+)\\s+");

            String line;
            while ((line = urlReader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String entityName = matcher.group(1);
                    xhtmlContent.append("<tr><td>&amp;").append(entityName).append(";</td>");
                    xhtmlContent.append("<td>&amp;").append(entityName).append(";</td></tr>\n");
                }
            }
            urlReader.close();

            // Close table and add validation link
            xhtmlContent.append("</table>\n");
            xhtmlContent.append("<p><a href=\"http://validator.w3.org/check/referer\">Validate XHTML</a></p>\n");
            xhtmlContent.append("</body>\n</html>");

            // Write XHTML content to output file
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
            writer.write(xhtmlContent.toString());
            writer.close();

            System.out.println("XHTML document generated successfully!");

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
