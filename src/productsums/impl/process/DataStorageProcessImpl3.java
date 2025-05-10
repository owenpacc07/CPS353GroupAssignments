package productsums.impl.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;
import java.util.stream.Collectors;

public class DataStorageProcessImpl3 extends DataStorageProcessImpl2{
	public Optional<Integer> writeToFile(String name, String contents) {
		if (name.contains("/") || name.contains("\\")) {
			return Optional.of(3);
		}
		File file = new File("src/resources/temp/"+name);
		try {
			file.createNewFile();
		} catch (IOException e) {
			return Optional.of(2);
		}
		if (!file.exists()) {
			return Optional.of(1); // file doesn't exist
		}
		try (PrintWriter pw = new PrintWriter(file)){
			pw.print(contents);
			return Optional.empty();
		} catch (IOException e) {
			return Optional.of(2); // Other IOException
		}
	}
	public record DataStorageRead(Optional<String> message, Optional<Integer> error){};
	public DataStorageRead readFromFile(String name) {
		if (name.contains("/") || name.contains("\\")) {
			return new DataStorageRead(Optional.of("Bad file name, no slashes allowed"), Optional.of(3));
		}
		File file = new File("src/resources/temp/"+name);
		if (!file.exists()) {
			return new DataStorageRead(Optional.of("Tried to read from file that doesn't exist"), Optional.of(1));
		}
		try (BufferedReader br = new BufferedReader(new FileReader(file))){
			return new DataStorageRead(
					Optional.of(
							br.lines()
							.collect(Collectors.joining("\n"))),
					Optional.empty()
					);
		} catch (IOException e) {
			return new DataStorageRead(Optional.of(e.toString()), Optional.of(2));
		}
	}
}
