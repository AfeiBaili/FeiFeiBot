package online.afeibaili.bot;

public class Bot {
    FeiFeiBot bot;
    String name;
    String setting;

    public Bot(FeiFeiBot bot, String name, String setting) {
        this.bot = bot;
        this.name = name;
        this.setting = setting;
    }

    public Bot() {
    }

    public FeiFeiBot getBot() {
        return bot;
    }

    public void setBot(FeiFeiBot bot) {
        this.bot = bot;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSetting() {
        return setting;
    }

    public void setSetting(String setting) {
        this.setting = setting;
    }
}
