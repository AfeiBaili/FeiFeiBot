package online.afeibaili.bot;


import online.afeibaili.json.Message;
import online.afeibaili.json.RequestBody;
import online.afeibaili.json.ResponseBody;
import online.afeibaili.other.Util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static online.afeibaili.other.Util.JSON;

public class Kimi implements FeiFeiBot {
    private final String KEY = Util.getProperty("KimiKey");
    private final RequestBody BODY = new RequestBody("moonshot-v1-8k", new ArrayList<Message>());

    @Override
    public void init() {
        try {
            send("system", "你是 Kimi，由 Moonshot AI 提供的人工智能助手，你更擅长中文和英文的对话。" +
                    "你会为用户提供安全，有帮助，准确的回答。Moonshot AI 为专有名词，不可翻译成其他语言。");
        } catch (IOException | URISyntaxException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void init(String setting) {
        try {
            send("system", setting);
        } catch (IOException | URISyntaxException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void reload() {
        BODY.getMessages().clear();
        init();
    }

    @Override
    public String send(String role, String message) throws IOException, URISyntaxException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        BODY.getMessages().add(new Message(role, message));
        String msg = JSON.writeValueAsString(BODY);


        HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://api.moonshot.cn/v1/chat/completions"))
                .setHeader("Authorization", "Bearer " + KEY)
                .setHeader("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(msg)).build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        ResponseBody responseBody = JSON.readValue(response.body(), ResponseBody.class);
        Message responseMessage = responseBody.getChoices().get(responseBody.getChoices().size() - 1).getMessage();
        BODY.getMessages().add(responseMessage);

        return responseMessage.getContent();
    }
}
