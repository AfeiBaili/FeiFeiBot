package online.afeibaili;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class Util {
    public static final ObjectMapper JSON = new ObjectMapper();

    /**
     * 获取配置文件的属性
     */
    private static final Properties properties = new Properties();

    static {
        File file = new File(System.getProperty("user.dir") + "/config/feifei.properties");
        try (FileReader fileReader = new FileReader(file)) {
            properties.load(fileReader);
        } catch (IOException e) {
            try {
                if (file.createNewFile()) {
                    Properties p = new Properties();
                    p.put("ChatGPTKey", "");
                    p.put("KimiKey", "");
                    p.put("DeepseekKey", "");
                    FileWriter writer = new FileWriter(file);
                    FileReader reader = new FileReader(file);
                    p.store(writer, "");
                    properties.load(reader);

                    writer.close();
                    reader.close();
                    throw new RuntimeException("已创建配置文件" + file + "请自己更新配置");
                }
            } catch (IOException ex) {
                throw new RuntimeException("无法获取或创建" + file, ex);
            }
        }
    }

    /**
     * 获取配置文件的属性
     *
     * @param name 获取属性Key
     * @return 返回属性值
     */
    public static String getProperty(String name) {
        return properties.getProperty(name);
    }
}
