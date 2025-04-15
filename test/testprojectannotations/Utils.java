package testprojectannotations;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static List<Class<?>> loadAllClasses() throws Exception {
        File rootDir = new File("src");
        if (!rootDir.exists() || !rootDir.isDirectory()) {
            throw new IllegalStateException("The 'src' directory does not exist or is not a directory.");
        }

        List<Class<?>> result = new ArrayList<>();
        for (File f : rootDir.listFiles()) {
            loadClassesRec(f, result, "");
        }
        return result;
    }

    private static void loadClassesRec(File f, List<Class<?>> result, String packageName) throws Exception {
        if (f.isDirectory()) {
            String newPackageName = packageName.isEmpty() ? f.getName() : packageName + "." + f.getName();
            for (File child : f.listFiles()) {
                loadClassesRec(child, result, newPackageName);
            }
        } else if (f.getName().endsWith(".class")) { // Use compiled .class files
            String className = f.getName().substring(0, f.getName().length() - 6); // Remove ".class"
            String fullName = packageName + (packageName.isEmpty() ? "" : ".") + className;
            try {
                result.add(Class.forName(fullName)); // Use Class.forName to load classes
            } catch (ClassNotFoundException e) {
                System.err.println("Class not found: " + fullName);
            }
        }
    }
}