package productsums.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Utility class for file reading operations.
 * Provides a simple interface to read file contents while handling exceptions
 * and providing meaningful error messages.
 */
public class FileReaderUtil {
    /**
     * Reads the entire contents of a file into a String.
     * @param filePath Path to the file to read
     * @return String containing the file contents
     * @throws RuntimeException if there's an error reading the file
     */
    public static String readFile(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            System.err.println("Error reading file: " + filePath);
            throw new RuntimeException("Failed to read file: " + e.getMessage(), e);
        }
    }
}