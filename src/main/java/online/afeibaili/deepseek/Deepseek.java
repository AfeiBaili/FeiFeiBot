package online.afeibaili.deepseek;

import online.afeibaili.jsonmap.Message;
import online.afeibaili.jsonmap.RequestBody;
import online.afeibaili.jsonmap.ResponseBody;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static online.afeibaili.Util.JSON;
import static online.afeibaili.Util.getProperty;

public class Deepseek {
    public static final String URL = "https://api.siliconflow.cn/v1/chat/completions";
    public static final String KEY = getProperty("DeepseekKey");
    public static final RequestBody BODY = new RequestBody("deepseek-ai/DeepSeek-V3", new ArrayList<Message>());
    public static ResponseBody responseBody;

    public static String sendRequest(String role, String message) throws IOException, InterruptedException, URISyntaxException {
        HttpClient httpClient = HttpClient.newHttpClient();
        BODY.getMessages().add(new Message(role, message));
        String msg = JSON.writeValueAsString(BODY);


        HttpRequest request = HttpRequest.newBuilder()
                .setHeader("Authorization", "Bearer " + KEY)
                .setHeader("Content-Type", "application/json")
                .uri(new URI(URL))
                .POST(HttpRequest.BodyPublishers.ofString(msg)).build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        responseBody = JSON.readValue(response.body(), ResponseBody.class);
        Message responseMessage = responseBody.getChoices().get(responseBody.getChoices().size() - 1).getMessage();
        BODY.getMessages().add(responseMessage);

        return responseMessage.getContent();
    }

    public static String initDeepseek() {
        try {
            return Deepseek.sendRequest("system", "你叫小鲸鱼，你很聪明也很可爱，你来帮助我们回答一些问题吧！");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String newDeepseek() {
        BODY.getMessages().clear();
        return initDeepseek();
    }
}
