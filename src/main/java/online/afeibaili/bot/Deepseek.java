package online.afeibaili.bot;

import online.afeibaili.json.Message;
import online.afeibaili.json.RequestBody;
import online.afeibaili.json.ResponseBody;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static online.afeibaili.other.Util.JSON;
import static online.afeibaili.other.Util.getProperty;

public class Deepseek implements FeiFeiBot {
    public final RequestBody BODY = new RequestBody("deepseek-reasoner", new ArrayList<Message>());
    public final String KEY = getProperty("DeepseekKey");

    @Override
    public void init() {
        BODY.getMessages().add(new Message("system", "你叫小鲸鱼也叫做Deepseek，是由中国的深度求索（DeepSeek）公司开发的智能助手"));
    }

    @Override
    public void init(String setting) {
        BODY.getMessages().add(new Message("system", setting));
    }

    @Override
    public void reload() {
        BODY.getMessages().clear();
        init();
    }

    @Override
    public String send(String role, String message) throws IOException, InterruptedException, URISyntaxException {
        HttpClient httpClient = HttpClient.newHttpClient();
        BODY.getMessages().add(new Message(role, message));
        String msg = JSON.writeValueAsString(BODY);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://api.deepseek.com/v1/chat/completions"))
                .setHeader("Authorization", "Bearer " + KEY)
                .setHeader("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(msg)).build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.body().equals("")) {
            BODY.getMessages().remove(BODY.getMessages().size() - 1);
            return "服务器负载较高，请稍后再试~";
        }
        ResponseBody responseBody = JSON.readValue(response.body(), ResponseBody.class);
        Message responseMessage = responseBody.getChoices().get(0).getMessage();
        BODY.getMessages().add(responseMessage);
        String reasoningContent = responseMessage.getReasoningContent();
        responseMessage.setReasoningContent(null);

        return reasoningContent + "\n\n\n" + responseMessage.getContent();
    }
}
