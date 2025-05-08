package productsums.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException; 
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

public class DataStorageWrapper {
    
    /**
     * Creates a new file at the specified path if it doesn't exist
     * @param filePath Path where the file should be created
     * @return true if file exists or was created successfully, false otherwise
     */
    public boolean createFile(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.getParentFile().mkdirs(); // Create parent directories if they don't exist
                return file.createNewFile();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Reads integers from a file with specified delimiters
     * @param filePath Path to the input file
     * @param delimiters String containing delimiter characters
     * @return Optional list of integers if successful, empty Optional if failed
     */
    public Optional<List<Integer>> readIntegers(String filePath, String delimiters) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            List<Integer> numbers = new ArrayList<>();
            StringBuilder currentNumber = new StringBuilder();
            int character;

            while ((character = reader.read()) != -1) {
                char ch = (char) character;
                if (delimiters.indexOf(ch) != -1) {
                    if (currentNumber.length() > 0) {
                        numbers.add(Integer.parseInt(currentNumber.toString()));
                        currentNumber = new StringBuilder();
                    }
                } else {
                    currentNumber.append(ch);
                }
            }
            
            // Handle last number if exists
            if (currentNumber.length() > 0) {
                numbers.add(Integer.parseInt(currentNumber.toString()));
            }
            
            return Optional.of(numbers);
        } catch (IOException | NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Writes map entries to a file in the format "key:value;"
     * @param filePath Path to the output file
     * @param data Map entries to write
     * @return true if write was successful, false otherwise
     */
    public boolean writeResults(String filePath, String data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(data);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Checks if a file exists at the specified path
     * @param filePath Path to check
     * @return true if file exists, false otherwise
     */
    public boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }
}