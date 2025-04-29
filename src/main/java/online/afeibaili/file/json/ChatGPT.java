package online.afeibaili.file.json;

public class ChatGPT {
    String name;
    String key;
    String setting;

    public ChatGPT() {
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
        return "ChatGPT{" +
                "name='" + name + '\'' +
                ", key='" + key + '\'' +
                ", setting='" + setting + '\'' +
                '}';
    }
}
