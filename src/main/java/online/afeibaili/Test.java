package online.afeibaili;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Test {
    public static void main(String[] args) throws IOException {
        Document document = Jsoup.connect("https://search.mcmod.cn/s?key=" +
                URLEncoder.encode("冰与火", StandardCharsets.UTF_8) +
                "&site=&filter=0&mold=0").get();

        Elements item = document.getElementsByClass("result-item");


        item.forEach(element -> {
            StringBuilder message = new StringBuilder();
            Element aTag = element.getElementsByClass("head").get(0).lastElementChild();
            message.append("📌").append(aTag.text()).append('\n')
                    .append("🔗").append(aTag.attr("href")).append('\n')
                    .append("📜").append(element.getElementsByClass("body").text());
            System.out.println(message.toString()+"="+item.size());
        });

    }
}
