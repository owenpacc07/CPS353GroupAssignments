package productsums.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Class used for file writing operations.
 * Provides an interface to write file contents while handling exceptions
 * and providing meaningful error messages.
 */
public class FileWriterUtil {
	/**
     * writes the entire contents of a file into a String.
     * @param filePath: Path to the file to write
     * @param content: String containing the file contents
     * @throws RuntimeException if there's an error writing the file
     */
    public static void writeFile(String filePath, String content) {
        try {
            Files.write(Paths.get(filePath), content.getBytes());
        } catch (IOException e) {
            System.err.println("Error writing to file: " + filePath);
            throw new RuntimeException("Failed to write file: " + e.getMessage(), e);
        }
    }
}