package online.afeibaili.mc;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static online.afeibaili.MessageHandler.COMMANDS;

public class MCModSearch {
    public static void load() {
        COMMANDS.put("搜", (param, event) -> {
            if (param.length != 2) return "搜 [mod名|关键词]\n使用MC百科搜索";
            StringBuilder message = new StringBuilder();
            try {
                Document document = Jsoup.connect("https://search.mcmod.cn/s?key=" + URLEncoder.encode(param[1], StandardCharsets.UTF_8) + "&site=&filter=0&mold=0").get();
                Element takeTime = document.getElementsByClass("info").get(0);
                int startTakeChar = takeTime.toString().indexOf('[');
                int endTakeChar = takeTime.toString().indexOf(']');
                message.append("已查找到结果；" + takeTime.toString(), startTakeChar + 1, endTakeChar).append("\n\n");

                Elements item = document.getElementsByClass("result-item");

                item.forEach(element -> {
                    Element aTag = element.getElementsByClass("head").get(0).lastElementChild();
                    message.append("📌").append(aTag.text()).append('\n')
                            .append("🔗").append(aTag.attr("href")).append('\n')
                            .append("📜").append(element.getElementsByClass("body").text()).append('\n').append('\n');
                });
            } catch (IOException e) {
                return "无法访问MC百科！";
            }
            return message.toString();
        });
    }
}
