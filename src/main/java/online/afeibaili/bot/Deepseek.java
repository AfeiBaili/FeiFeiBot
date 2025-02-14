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
import java.time.Duration;
import java.util.ArrayList;

import static online.afeibaili.other.Util.JSON;
import static online.afeibaili.other.Util.getProperty;

public class Deepseek implements FeiFeiBot {
    public final RequestBody BODY = new RequestBody("deepseek-ai/DeepSeek-V3", new ArrayList<Message>());
    public final String KEY = getProperty("DeepseekKey");

    @Override
    public void init() {
        try {
            send("system", "你叫小鲸鱼也叫做Deepseek，是由中国的深度求索（DeepSeek）公司开发的智能助手DeepSeek-V3");
        } catch (IOException | InterruptedException | URISyntaxException e) {
            online.afeibaili.FeiFeiBot.LOGGER.info("小鲸鱼初始化错误");
        }
    }

    @Override
    public void init(String setting) {
        try {
            send("system", setting);
        } catch (IOException | InterruptedException | URISyntaxException e) {
            online.afeibaili.FeiFeiBot.LOGGER.info("小鲸鱼初始化错误");
        }
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
                .timeout(Duration.ofSeconds(5))
                .uri(new URI("https://api.siliconflow.cn/v1/chat/completions")).setHeader("Authorization", "Bearer " + KEY).setHeader("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(msg)).build();

        HttpResponse<String> response = null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (IOException | InterruptedException e) {
            return "连接超时！";
        }
        ResponseBody responseBody = JSON.readValue(response.body(), ResponseBody.class);
        Message responseMessage = responseBody.getChoices().get(responseBody.getChoices().size() - 1).getMessage();
        BODY.getMessages().add(responseMessage);

        return responseMessage.getContent();
    }
}
