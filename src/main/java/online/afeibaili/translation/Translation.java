package online.afeibaili.translation;

import com.fasterxml.jackson.databind.ObjectMapper;
import online.afeibaili.LoggerLevel;
import online.afeibaili.UtilKt;
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

/**
 * 有道翻译Dome改
 */

public class Translation {
    private static final HttpClient CLIENT = HttpClient.newBuilder().build();

    public static String parseResult(String body) {
        try {
            ObjectMapper jsonMapper = new ObjectMapper();
            Result result = jsonMapper.readValue(body, Result.class);
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
            UtilKt.logger(e, LoggerLevel.INFO);
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
            AuthV3Util.addAuthParams(UtilKt.config.getYouDao().getKey(), UtilKt.config.getYouDao().getAppId(), params);
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
