// Server.java
// HTTP Server handling REST API endpoints and serving web/index.html on port 8081.

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Server {
    // Port set to 8081 as required (Port 8080 is reserved for Jenkins)
    private static final int PORT = 8081;
    // Shared hash table instance to store student records
    private static final StudentTable table = new StudentTable();

    public static void main(String[] args) throws IOException {
        // Create HTTP server listening on port 8081
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // API endpoint to fetch full hash table
        server.createContext("/api/table", exchange -> handleTableRequest(exchange));

        // API endpoint to add or update a student record
        server.createContext("/api/add", exchange -> handleAddRequest(exchange));

        // API endpoint to delete a student record by roll number
        server.createContext("/api/delete", exchange -> handleDeleteRequest(exchange));

        // API endpoint to search for a student record by roll number
        server.createContext("/api/get", exchange -> handleGetRequest(exchange));

        // Serve web/index.html or handle root requests
        server.createContext("/", exchange -> handleRootRequest(exchange));

        // Start the server with default executor
        server.setExecutor(null);
        server.start();

        // Print required startup message
        System.out.println("Server running at http://localhost:8081");
    }

    // Handles GET /api/table
    private static void handleTableRequest(HttpExchange exchange) throws IOException {
        sendJsonResponse(exchange, 200, table.toJson());
    }

    // Handles /api/add?roll=&name=&branch=&cgpa=
    private static void handleAddRequest(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        
        String rollStr = getParam(query, "roll");
        String nameStr = getParam(query, "name");
        String branchStr = getParam(query, "branch");
        String cgpaStr = getParam(query, "cgpa");

        // Validate roll number
        int roll;
        try {
            if (rollStr == null) {
                sendJsonResponse(exchange, 400, "{\"error\":\"Roll number must be a positive integer\"}");
                return;
            }
            roll = Integer.parseInt(rollStr);
            if (roll <= 0) {
                sendJsonResponse(exchange, 400, "{\"error\":\"Roll number must be a positive integer\"}");
                return;
            }
        } catch (Exception e) {
            sendJsonResponse(exchange, 400, "{\"error\":\"Roll number must be a positive integer\"}");
            return;
        }

        // Validate name
        if (nameStr == null || nameStr.trim().isEmpty()) {
            sendJsonResponse(exchange, 400, "{\"error\":\"Name cannot be empty\"}");
            return;
        }

        // Validate branch
        if (branchStr == null || branchStr.trim().isEmpty()) {
            sendJsonResponse(exchange, 400, "{\"error\":\"Branch cannot be empty\"}");
            return;
        }

        // Validate CGPA
        double cgpa;
        try {
            if (cgpaStr == null) {
                sendJsonResponse(exchange, 400, "{\"error\":\"CGPA must be a number between 0 and 10\"}");
                return;
            }
            cgpa = Double.parseDouble(cgpaStr);
            if (cgpa < 0 || cgpa > 10) {
                sendJsonResponse(exchange, 400, "{\"error\":\"CGPA must be a number between 0 and 10\"}");
                return;
            }
        } catch (Exception e) {
            sendJsonResponse(exchange, 400, "{\"error\":\"CGPA must be a number between 0 and 10\"}");
            return;
        }

        // Input is valid; insert or update student record
        Student student = new Student(roll, nameStr.trim(), branchStr.trim(), cgpa);
        table.put(student);

        // Return updated table state
        sendJsonResponse(exchange, 200, table.toJson());
    }

    // Handles /api/delete?roll=
    private static void handleDeleteRequest(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        String rollStr = getParam(query, "roll");

        // Validate roll number
        int roll;
        try {
            if (rollStr == null) {
                sendJsonResponse(exchange, 400, "{\"error\":\"Roll number must be a positive integer\"}");
                return;
            }
            roll = Integer.parseInt(rollStr);
            if (roll <= 0) {
                sendJsonResponse(exchange, 400, "{\"error\":\"Roll number must be a positive integer\"}");
                return;
            }
        } catch (Exception e) {
            sendJsonResponse(exchange, 400, "{\"error\":\"Roll number must be a positive integer\"}");
            return;
        }

        // Perform deletion
        table.remove(roll);

        // Return updated table state
        sendJsonResponse(exchange, 200, table.toJson());
    }

    // Handles /api/get?roll=
    private static void handleGetRequest(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        String rollStr = getParam(query, "roll");

        // Validate roll number
        int roll;
        try {
            if (rollStr == null) {
                sendJsonResponse(exchange, 400, "{\"error\":\"Roll number must be a positive integer\"}");
                return;
            }
            roll = Integer.parseInt(rollStr);
            if (roll <= 0) {
                sendJsonResponse(exchange, 400, "{\"error\":\"Roll number must be a positive integer\"}");
                return;
            }
        } catch (Exception e) {
            sendJsonResponse(exchange, 400, "{\"error\":\"Roll number must be a positive integer\"}");
            return;
        }

        // Perform search
        StudentTable.SearchResult result = table.get(roll);

        // Return search result JSON
        sendJsonResponse(exchange, 200, result.toJson());
    }

    // Serves web/index.html or 404 for unknown static routes
    private static void handleRootRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path.equals("/") || path.equals("/index.html")) {
            File htmlFile = new File("web/index.html");
            if (!htmlFile.exists()) {
                sendJsonResponse(exchange, 404, "{\"error\":\"web/index.html not found\"}");
                return;
            }
            byte[] content = Files.readAllBytes(htmlFile.toPath());
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
            exchange.sendResponseHeaders(200, content.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(content);
            }
        } else {
            sendJsonResponse(exchange, 404, "{\"error\":\"Not found\"}");
        }
    }

    // Helper method to extract query parameter value by key without external libraries
    private static String getParam(String query, String key) {
        if (query == null || query.isEmpty()) {
            return null;
        }
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2 && keyValue[0].equals(key)) {
                try {
                    return URLDecoder.decode(keyValue[1], "UTF-8");
                } catch (Exception e) {
                    return keyValue[1];
                }
            }
        }
        return null;
    }

    // Helper method to write JSON response headers and body
    private static void sendJsonResponse(HttpExchange exchange, int statusCode, String jsonResponse) throws IOException {
        byte[] bytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
