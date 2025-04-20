package online.afeibaili.translation;


import com.fasterxml.jackson.core.JsonProcessingException;
import online.afeibaili.command.Command;
import online.afeibaili.other.Util;
import online.afeibaili.translation.util.AuthV3Util;
import online.afeibaili.translation.util.HttpUtil;

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
                sb.append(translation).append("\n");
            });
            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        } catch (JsonProcessingException e) {
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
