package online.afeibaili.file.json;

import java.util.Arrays;

public class JsonConfigMap {
    long master;
    long[] groups;
    Bot bot;
    Module module;
    Setting setting;
    ChatGPT chatgpt;
    Deepseek deepseek;
    Kimi kimi;
    YouDao youDao;

    public JsonConfigMap() {
    }

    @Override
    public String toString() {
        return "JsonConfigMap{" +
                "master=" + master +
                ", groups=" + Arrays.toString(groups) +
                ", bot=" + bot +
                ", module=" + module +
                ", setting=" + setting +
                ", chatgpt=" + chatgpt +
                ", deepseek=" + deepseek +
                ", kimi=" + kimi +
                ", youDao=" + youDao +
                '}';
    }

    public long getMaster() {
        return master;
    }

    public void setMaster(long master) {
        this.master = master;
    }

    public long[] getGroups() {
        return groups;
    }

    public void setGroups(long[] groups) {
        this.groups = groups;
    }

    public Bot getBot() {
        return bot;
    }

    public void setBot(Bot bot) {
        this.bot = bot;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public Setting getSetting() {
        return setting;
    }

    public void setSetting(Setting setting) {
        this.setting = setting;
    }

    public ChatGPT getChatgpt() {
        return chatgpt;
    }

    public void setChatgpt(ChatGPT chatgpt) {
        this.chatgpt = chatgpt;
    }

    public Deepseek getDeepseek() {
        return deepseek;
    }

    public void setDeepseek(Deepseek deepseek) {
        this.deepseek = deepseek;
    }

    public Kimi getKimi() {
        return kimi;
    }

    public void setKimi(Kimi kimi) {
        this.kimi = kimi;
    }

    public YouDao getYouDao() {
        return youDao;
    }

    public void setYouDao(YouDao youDao) {
        this.youDao = youDao;
    }
}