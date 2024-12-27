package com.bikematchpro.bikematch;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

public class image{
    public static void main(String[] args) {
        // Path to the image file
        String imagePath = "C:\\Users\\mahar\\OneDrive\\Desktop\\Project\\Images\\KTM\\KTM 390 Adventure\\Rally Orange.jpg";

        // Read the image file into a byte array
        byte[] imageData = readImageFile(imagePath);

        if (imageData != null) {
            // Encode the byte array to Base64
            String base64EncodedImage = encodeImageToBase64(imageData);
            System.out.println("Base64 Encoded Image:\n" + base64EncodedImage);
        } else {
            System.out.println("Failed to read image file.");
        }
    }

    private static byte[] readImageFile(String imagePath) {
        try {
            File file = new File(imagePath);
            FileInputStream fis = new FileInputStream(file);
            byte[] imageData = new byte[(int) file.length()];
            fis.read(imageData);
            fis.close();
            return imageData;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String encodeImageToBase64(byte[] imageData) {
        return Base64.getEncoder().encodeToString(imageData);
    }
}
