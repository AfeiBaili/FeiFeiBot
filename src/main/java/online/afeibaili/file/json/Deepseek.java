package online.afeibaili.file.json;

public class Deepseek {
    String name;
    String key;
    String setting;

    public Deepseek() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSetting() {
        return setting;
    }

    public void setSetting(String setting) {
        this.setting = setting;
    }

    @Override
    public String toString() {
        return "Deepseek{" +
                "name='" + name + '\'' +
                ", key='" + key + '\'' +
                ", setting='" + setting + '\'' +
                '}';
    }
}
