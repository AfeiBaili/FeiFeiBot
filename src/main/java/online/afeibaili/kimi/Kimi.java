package online.afeibaili.kimi;

import online.afeibaili.jsonmap.Message;
import online.afeibaili.jsonmap.RequestBody;
import online.afeibaili.jsonmap.ResponseBody;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static online.afeibaili.Util.JSON;

public class Kimi {
    public static final String KEY = "sk-";
    public static final RequestBody BODY = new RequestBody("moonshot-v1-8k", new ArrayList<Message>());
    public static ResponseBody responseBody;

    public static String sendRequest(String role, String message) throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        BODY.getMessages().add(new Message(role, message));
        String msg = JSON.writeValueAsString(BODY);


        HttpRequest request = HttpRequest.newBuilder()
                .setHeader("Authorization", "Bearer " + KEY)
                .setHeader("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(msg)).build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        responseBody = JSON.readValue(response.body(), ResponseBody.class);
        Message responseMessage = responseBody.getChoices().get(responseBody.getChoices().size() - 1).getMessage();
        BODY.getMessages().add(responseMessage);

        return responseMessage.getContent();
    }

    public static String initKimi() {
        try {
            return Kimi.sendRequest("system", "你是 Kimi，由 Moonshot AI 提供的人工智能助手，你更擅长中文和英文的对话。你会为用户提供安全，有帮助，准确的回答。Moonshot AI 为专有名词，不可翻译成其他语言。");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static String newKimi() {
        BODY.getMessages().clear();
        return initKimi();
    }
}
