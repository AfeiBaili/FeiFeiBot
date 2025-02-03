package online.afeibaili;

import online.afeibaili.bot.ChatGPT;
import online.afeibaili.bot.Deepseek;
import online.afeibaili.bot.Kimi;

public interface FeiFeiFactory {
    static ChatGPT createChatGPT() {
        ChatGPT chatGPT = new ChatGPT();
        chatGPT.init();
        return chatGPT;
    }

    static ChatGPT createChatGPT(String setting) {
        ChatGPT chatGPT = new ChatGPT();
        chatGPT.init(setting);
        return chatGPT;
    }

    static Kimi createKimi() {
        Kimi kimi = new Kimi();
        kimi.init();
        return kimi;
    }

    static Kimi createKimi(String setting) {
        Kimi kimi = new Kimi();
        kimi.init(setting);
        return kimi;
    }

    static Deepseek createDeepseek() {
        Deepseek deepseek = new Deepseek();
        deepseek.init();
        return deepseek;
    }

    static Deepseek createDeepseek(String setting) {
        Deepseek deepseek = new Deepseek();
        deepseek.init(setting);
        return deepseek;
    }
}
