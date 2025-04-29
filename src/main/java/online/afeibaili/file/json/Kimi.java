package online.afeibaili.file.json;

public class Kimi {
    String key;
    String setting;

    public Kimi() {
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
        return "Kimi{" +
                "key='" + key + '\'' +
                ", setting='" + setting + '\'' +
                '}';
    }
}
