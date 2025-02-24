package online.afeibaili;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class Test {
    public static void main(String[] args) throws IOException {
        File file = new File(System.getProperty("user.dir") + "/config/feifei/level-map.properties");
        Properties properties = new Properties();

        try (FileReader fileReader = new FileReader(file);
             FileWriter fileWriter = new FileWriter(file)
        ) {
            properties.load(fileReader);
            properties.put(Integer.toString(1), Integer.toString(2));
            properties.store(fileWriter, "d");
        }
    }
}
