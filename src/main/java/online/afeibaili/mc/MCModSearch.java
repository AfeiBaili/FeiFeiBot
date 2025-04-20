package online.afeibaili.mc;

import online.afeibaili.command.Command;
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
        COMMANDS.put("搜", new Command.Builder()
                .method((param, event) -> {
                    if (param.length != 2) return "搜 [mod名|关键词]\n使用MC百科搜索";
                    try {
                        Document document = Jsoup.connect("https://search.mcmod.cn/s?key=" + URLEncoder.encode(param[1], StandardCharsets.UTF_8) + "&site=&filter=0&mold=0").get();
                        Element takeTime = document.getElementsByClass("info").get(0);
                        int startTakeChar = takeTime.toString().indexOf('[');
                        int endTakeChar = takeTime.toString().indexOf(']');

                        Elements item = document.getElementsByClass("result-item");
                        item.forEach(element -> {
                            StringBuilder message = new StringBuilder();
                            Element aTag = element.getElementsByClass("head").get(0).lastElementChild();
                            if (aTag != null) {
                                message.append("📌").append(aTag.text()).append('\n')
                                        .append("🔗").append(aTag.attr("href")).append('\n')
                                        .append("📜").append(element.getElementsByClass("body").text());
                            }
                            event.getSubject().sendMessage(message.toString());
                        });
                        try {
                            return "从MC百科查找到" + item.size() + "条结果；" + takeTime.toString().substring(startTakeChar + 1, endTakeChar);
                        } catch (StringIndexOutOfBoundsException e) {
                            return "搜不到任何信息";
                        }
                    } catch (IOException e) {
                        return "无法访问MC百科！";
                    }
                })
                .build());
    }
}
