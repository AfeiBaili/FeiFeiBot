package online.afeibaili.other;

import com.fasterxml.jackson.databind.ObjectMapper;
import online.afeibaili.FeiFeiBot;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

public class Util {
    public static final ObjectMapper JSON = new ObjectMapper();
    public static final HashMap<Long, Integer> LEVEL_MAP = new HashMap<>();
    private static final Properties levelMap = new Properties();
    private static final Properties properties = new Properties();

    static {
        File file = new File(System.getProperty("user.dir") + "/config/feifei/feifei.properties");
        try (FileReader fileReader = new FileReader(file)) {
            properties.load(fileReader);
        } catch (IOException e) {
            try {
                if (file.getParentFile().mkdirs()) if (file.createNewFile()) {
                    Properties p = new Properties();
                    p.put("ChatGPTKey", "");
                    p.put("KimiKey", "");
                    p.put("DeepseekKey", "");
                    p.put("Group", "");
                    p.put("Master", "");
                    p.put("YouDaoAppKey", "");
                    p.put("YouDaoAppSecret", "");
                    FileWriter writer = new FileWriter(file);
                    FileReader reader = new FileReader(file);
                    p.store(writer, "菲菲的配置属性");
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

    static {
        refresh();
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

    /**
     * 获取等级映射文件
     *
     * @return 返回一个HashMap
     */
    public static HashMap<Long, Integer> getLevelMap() {
        return LEVEL_MAP;
    }

    /**
     * 添加等级映射到等级映射文件中
     *
     * @param qq    QQ号
     * @param level 等级
     * @return 是否添加成功
     */
    public static boolean setLevelMap(Long qq, Integer level) {
        File file = new File(System.getProperty("user.dir") + "/config/feifei/level-map.properties");
        levelMap.remove(qq);
        levelMap.put(Long.toString(qq), Long.toString(level));
        try (FileWriter writer = new FileWriter(file)) {
            levelMap.store(writer, "");
            refresh();
            return true;
        } catch (IOException e) {
            refresh();
            return false;
        }
    }

    /**
     * 私有方法用于刷新和获取等级表格
     */
    private static void refresh() {
        File file = new File(System.getProperty("user.dir") + "/config/feifei/level-map.properties");
        try (FileReader fileReader = new FileReader(file)) {
            levelMap.load(fileReader);
            levelMap.forEach((qq, level) -> {
                try {
                    LEVEL_MAP.put(Long.parseLong(qq.toString()), Integer.parseInt(level.toString()));
                } catch (NumberFormatException e) {
                    throw new RuntimeException("请检查level-map.properties配置文件是否包含数字以外的符号", e);
                }
            });
        } catch (IOException e) {
            try {
                if (file.getParentFile().mkdirs()) {
                    FeiFeiBot.LOGGER.info("已创建等级映射路径");
                }
                if (file.createNewFile()) {
                    FeiFeiBot.LOGGER.info("已创建等级映射文件");
                }
            } catch (IOException ex) {
                throw new RuntimeException("无法创建等级映射文件，可能是权限不足");
            }
        }
    }
}
