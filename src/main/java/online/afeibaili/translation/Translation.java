package online.afeibaili.translation;


import online.afeibaili.command.Command;
import online.afeibaili.other.Util;
import online.afeibaili.translation.util.AuthV3Util;
import online.afeibaili.translation.util.HttpUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import static online.afeibaili.FeiFeiBot.LOGGER;
import static online.afeibaili.MessageHandler.COMMANDS;
import static online.afeibaili.other.Util.JSON;

/**
 * 有道翻译Dome改
 */

public class Translation {
    private static final String APP_KEY = Util.getProperty("YouDaoAppKey");
    private static final String APP_SECRET = Util.getProperty("YouDaoAppSecret");
    private static final HttpClient CLIENT = HttpClient.newBuilder().build();

    public static void load() {
        COMMANDS.put("翻译", new Command.Builder()
                .method((param, event) -> {
                    if (param.length == 1) return "请填写要翻译的内容";
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i < param.length; i++) {
                        sb.append(param[i]).append(" ");
                    }
                    sb.deleteCharAt(sb.length() - 1);
                    String q = sb.toString();

                    int[] counts = countLettersVsNonLetters(q);
                    int letterCount = counts[0];
                    int nonLetterCount = counts[1];

                    if (letterCount > nonLetterCount) return parseResult(translate(q, "en", "zh-CHS"));
                    else return parseResult(translate(q, "zh-CHS", "en"));
                })
                .build());
    }

    public static String parseResult(String body) {
        try {
            Result result = JSON.readValue(body, Result.class);
            StringBuilder sb = new StringBuilder();
            result.getTranslation().forEach(translation -> {
                sb.append("释义：").append(translation).append("\n");
            });

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(result.getWebdict().getUrl()))
                    .GET()
                    .build();
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            Element main = Jsoup.parse(response.body()).getElementById("ec");

            if (main != null) {
                sb.append("\n");
                main.getElementsByTag("h2").get(0)
                        .getElementsByTag("div").get(0).getElementsByTag("span")
                        .forEach(it -> {
                            if (!it.attr("class").equals("phonetic")) sb.append(it.text()).append("\n");
                        });

                main.getElementsByTag("li").forEach(it -> {
                    sb.append(it.text()).append("\n");
                });
                main.getElementsByTag("p").forEach(it -> {
                    sb.append(it.text()).append("\n");
                });
            }

            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        } catch (IOException | InterruptedException e) {
            LOGGER.info(e);
            return "无法映射：" + e.getMessage();
        }
    }

    public static int[] countLettersVsNonLetters(String text) {
        int letterCount = 0;
        int nonLetterCount = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                letterCount++;
            } else {
                nonLetterCount++;
            }
        }

        return new int[]{letterCount, nonLetterCount};
    }

    public static String translate(String q, String from, String to) {
        Map<String, String[]> params = createRequestParams(q, from, to);
        try {
            AuthV3Util.addAuthParams(APP_KEY, APP_SECRET, params);
        } catch (NoSuchAlgorithmException ignored) {
        }
        byte[] result = HttpUtil.doPost("https://openapi.youdao.com/api", null, params, "application/json");

        return new String(result, StandardCharsets.UTF_8);
    }

    private static Map<String, String[]> createRequestParams(String q, String from, String to) {
        return new HashMap<String, String[]>() {{
            put("q", new String[]{q});
            put("from", new String[]{from});
            put("to", new String[]{to});
        }};
    }
}
