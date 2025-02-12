package productsums.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileWriterUtil {
    public static void writeFile(String filePath, String content) {
        try {
            Files.write(Paths.get(filePath), content.getBytes());
        } catch (IOException e) {
            System.err.println("Error writing to file: " + filePath);
            throw new RuntimeException("Failed to write file: " + e.getMessage(), e);
        }
    }
}