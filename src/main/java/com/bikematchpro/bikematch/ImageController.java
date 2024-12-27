package com.bikematchpro.bikematch;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import jakarta.servlet.http.HttpServletResponse;
import com.mongodb.client.*;
import org.bson.Document;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ImageController {

    @GetMapping("/displayAllImages")
    public void displayAllImages(HttpServletResponse response) {
        // Connect to MongoDB
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("bikes");
        MongoCollection<Document> collection = database.getCollection("KTM");

        try {
            // Set content type to HTML
            response.setContentType("text/html");

            // Start writing HTML response
            response.getWriter().println("<html><body>");

            // Iterate through all documents in the collection
            MongoCursor<Document> cursor = collection.find().iterator();
            while (cursor.hasNext()) {
                Document document = cursor.next();
                String imagePath = document.getString("image_id");

                // Encode the image path to handle special characters
                String encodedPath = encodePath(imagePath);

                // Embed each image in an <img> tag
                response.getWriter().println("<img src='/getImage/" + encodedPath + "'/><br/>");
            }

            // End HTML response
            response.getWriter().println("</body></html>");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Close MongoDB connection
            mongoClient.close();
        }
    }

    @GetMapping("/getImage/{imageName:.+}")
    public void getImage(@PathVariable String imageName, HttpServletResponse response) {
        // Connect to MongoDB
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("bikes");
        MongoCollection<Document> collection = database.getCollection("KTM");

        try {
            // Decode the image path to get the original path
            String decodedPath = decodePath(imageName);

            // Find document containing the image path
            Document query = new Document("image_id", decodedPath);
            Document document = collection.find(query).first();
            if (document != null) {
                String imagePath = document.getString("image_id");

                // Set content type
                response.setContentType("image/jpeg");

                // Stream image data directly to response output stream
                Files.copy(Paths.get(imagePath), response.getOutputStream());
                response.getOutputStream().flush();
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Close MongoDB connection
            mongoClient.close();
        }
    }

    private String encodePath(String path) {
        try {
            return URLDecoder.decode(path, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String decodePath(String path) {
        try {
            return URLDecoder.decode(path, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
