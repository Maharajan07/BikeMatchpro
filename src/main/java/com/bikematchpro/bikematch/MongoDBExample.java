package com.bikematchpro.bikematch;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.google.gson.Gson;

public class MongoDBExample {
    private static Gson gson = new Gson();

    public static void main(String[] args) throws IOException {
        // Suppress MongoDB driver logs
        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.SEVERE); // Set the level to SEVERE to suppress all logs
        
        // Now your MongoDB code
        MongoDBConnector connector = new MongoDBConnector("mongodb://localhost:27017", "bike");
        final MongoDatabase database = connector.getDatabase(); // Make it final
        
        // Load HTML content from file
        final String htmlContent = new String(Files.readAllBytes(Paths.get("index.html")));
        
        // Print HTML content to console
        System.out.println("HTML Content:");
        System.out.println(htmlContent);
        
        // Start HTTP server and serve HTML content
        int port = 8080;
        HttpServer server = HttpServer.create(new java.net.InetSocketAddress(port), 0);
        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                byte[] response = htmlContent.getBytes();
                exchange.sendResponseHeaders(200, response.length);
                OutputStream os = exchange.getResponseBody();
                os.write(response);
                os.close();
            }
        });
     // HTTP handler for search by brand
        server.createContext("/search", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                // Get the search query from the URI
                String query = exchange.getRequestURI().getQuery();
                String brand = query.substring(query.indexOf("=") + 1);

                System.out.println("Received search request. Brand: " + brand);

                // Find documents with matching brand
                String jsonData = "[";
                MongoIterable<String> collectionNames = database.listCollectionNames();
                for (String collectionName : collectionNames) {
                    System.out.println("Processing collection: " + collectionName);
                    MongoCollection<Document> collection = database.getCollection(collectionName);
                    FindIterable<Document> documents = collection.find(new Document("Brand", brand));
                    // Convert documents to JSON
                    for (Document doc : documents) {
                        jsonData += gson.toJson(doc) + ",";
                    }
                }
                // Remove the last comma and close the JSON array
                jsonData = jsonData.replaceAll(",$", "") + "]";

                System.out.println("Sending JSON data: " + jsonData);

                // Send the JSON data as the response
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, jsonData.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(jsonData.getBytes());
                os.close();
            }
        });




        

     // HTTP handler for filter selection
        server.createContext("/filter", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                // Get the query string from the URI
                String query = exchange.getRequestURI().getQuery();

                // Parse query parameters
                String[] queryParams = query.split("&");
                String bodyStyle = null;
                String brand = null;
                String displacement = null;
                String price = null;
                for (String param : queryParams) {
                    String[] keyValue = param.split("=");
                    if (keyValue.length == 2) {
                        String key = keyValue[0];
                        String value = keyValue[1];
                        if (key.equals("bodyStyle")) {
                            bodyStyle = value;
                        } else if (key.equals("brand")) {
                            brand = value;
                        } else if (key.equals("displacement")) {
                            displacement = value;
                        } else if (key.equals("price")) {
                            price = value;
                        }
                    }
                }

                System.out.println("Received filter request. Body Style: " + bodyStyle + ", Brand: " + brand + ", Displacement: " + displacement + ", Price: " + price);

                // Filter documents based on the selected filter values
                String jsonData = "[";
                MongoIterable<String> collectionNames = database.listCollectionNames();
                for (String collectionName : collectionNames) {
                    System.out.println("Processing collection: " + collectionName);
                    MongoCollection<Document> collection = database.getCollection(collectionName);
                    FindIterable<Document> documents;
                    if (bodyStyle.equals("All")) {
                        if (brand.equals("All")) {
                            if (displacement.equals("All")) {
                                if (price.equals("All")) {
                                    documents = collection.find();
                                } else {
                                    documents = collection.find(new Document("Price", getPriceFilter(price)));
                                }
                            } else {
                                if (price.equals("All")) {
                                    documents = collection.find(new Document("Displacement", getDisplacementFilter(displacement)));
                                } else {
                                    documents = collection.find(new Document("Displacement", getDisplacementFilter(displacement)).append("Price", getPriceFilter(price)));
                                }
                            }
                        } else {
                            if (displacement.equals("All")) {
                                if (price.equals("All")) {
                                    documents = collection.find(new Document("Brand", brand));
                                } else {
                                    documents = collection.find(new Document("Brand", brand).append("Price", getPriceFilter(price)));
                                }
                            } else {
                                if (price.equals("All")) {
                                    documents = collection.find(new Document("Brand", brand).append("Displacement", getDisplacementFilter(displacement)));
                                } else {
                                    documents = collection.find(new Document("Brand", brand).append("Displacement", getDisplacementFilter(displacement)).append("Price", getPriceFilter(price)));
                                }
                            }
                        }
                    } else {
                        if (brand.equals("All")) {
                            if (displacement.equals("All")) {
                                if (price.equals("All")) {
                                    documents = collection.find(new Document("Body Style", bodyStyle));
                                } else {
                                    documents = collection.find(new Document("Body Style", bodyStyle).append("Price", getPriceFilter(price)));
                                }
                            } else {
                                if (price.equals("All")) {
                                    documents = collection.find(new Document("Body Style", bodyStyle).append("Displacement", getDisplacementFilter(displacement)));
                                } else {
                                    documents = collection.find(new Document("Body Style", bodyStyle).append("Displacement", getDisplacementFilter(displacement)).append("Price", getPriceFilter(price)));
                                }
                            }
                        } else {
                            if (displacement.equals("All")) {
                                if (price.equals("All")) {
                                    documents = collection.find(new Document("Brand", brand).append("Body Style", bodyStyle));
                                } else {
                                    documents = collection.find(new Document("Brand", brand).append("Body Style", bodyStyle).append("Price", getPriceFilter(price)));
                                }
                            } else {
                                if (price.equals("All")) {
                                    documents = collection.find(new Document("Brand", brand).append("Body Style", bodyStyle).append("Displacement", getDisplacementFilter(displacement)));
                                } else {
                                    documents = collection.find(new Document("Brand", brand).append("Body Style", bodyStyle).append("Displacement", getDisplacementFilter(displacement)).append("Price", getPriceFilter(price)));
                                }
                            }
                        }
                    }
                    // Convert documents to JSON
                    for (Document doc : documents) {
                        jsonData += gson.toJson(doc) + ",";
                    }
                }
                // Remove the last comma and close the JSON array
                jsonData = jsonData.replaceAll(",$", "") + "]";

                System.out.println("Sending JSON data: " + jsonData);

                // Send the JSON data as the response
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, jsonData.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(jsonData.getBytes());
                os.close();
            }

            // Method to get the Displacement filter based on user selection
            private Document getDisplacementFilter(String displacement) {
                switch (displacement) {
                    case "below100cc":
                        return new Document("$lt", 100);
                    case "100-150cc":
                        return new Document("$gte", 100).append("$lt", 150);
                    case "150-200cc":
                        return new Document("$gte", 150).append("$lt", 200);
                    case "200-300cc":
                        return new Document("$gte", 200).append("$lt", 300);
                    case "above300cc":
                        return new Document("$gte", 300);
                    default:
                        return new Document();
                }
            }

            // Method to get the Price filter based on user selection
            private Document getPriceFilter(String price) {
                switch (price) {
                    case "below1Lakh":
                        return new Document("$lt", 100000);
                    case "1-1.5Lakhs":
                        return new Document("$gte", 100000).append("$lt", 150000);
                    case "1.5-2Lakhs":
                        return new Document("$gte", 150000).append("$lt", 200000);
                    case "2-3Lakhs":
                        return new Document("$gte", 200000).append("$lt", 300000);
                    case "above3Lakhs":
                        return new Document("$gte", 300000);
                    default:
                        return new Document();
                }
            }
        });




        server.start();
        
        System.out.println("Server is running on port " + port);
    }
}