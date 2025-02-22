package online.afeibaili.bot;

import net.mamoe.mirai.contact.Group;
import online.afeibaili.json.Message;
import online.afeibaili.json.RequestBody;
import online.afeibaili.json.ResponseBody;
import online.afeibaili.json.Stream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

import static online.afeibaili.other.Util.JSON;
import static online.afeibaili.other.Util.getProperty;

public class Deepseek implements FeiFeiBot {
    //小鲸鱼默认开启流
    public final RequestBody BODY = new RequestBody("deepseek-reasoner", new ArrayList<Message>(), true);
    public final String KEY = getProperty("DeepseekKey");
    public boolean isRunning = false;

    @Override
    public void init() {
        BODY.getMessages().add(new Message("system", "你叫小鲸鱼也叫做Deepseek，你是大家的好朋友，" +
                "你在每一句后面都会加上🐋这个emoji作为你的后缀，你也是大家的百科全书"));
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

        HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
        try (
                InputStream body = response.body();
                InputStreamReader inputStreamReader = new InputStreamReader(body);
                BufferedReader reader = new BufferedReader(inputStreamReader);
        ) {
            String content;
            while ((content = reader.readLine()) != null) {
                if (!content.equals("")) {
                    ResponseBody responseBody = JSON.readValue(content, ResponseBody.class);
                    Message responseMessage = responseBody.getChoices().get(0).getMessage();

                    BODY.getMessages().add(responseMessage);
                    String reasoningContent = responseMessage.getReasoningContent();
                    responseMessage.setReasoningContent(null);
                    return reasoningContent + "\n\n\n" + responseMessage.getContent();
                }
            }
        }
        BODY.getMessages().remove(BODY.getMessages().size() - 1);
        return "小鲸鱼服务器断开连接";
    }

    public void sendAsStream(String role, String message, Group send) throws IOException, URISyntaxException, InterruptedException {
        if (isRunning) {
            send.sendMessage("小鲸鱼正在回答中! 请稍后再试~");
            return;
        }
        isRunning = true;
        HttpClient httpClient = HttpClient.newHttpClient();
        BODY.getMessages().add(new Message(role, message));
        String msg = JSON.writeValueAsString(BODY);

        HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://api.deepseek.com/v1/chat/completions"))
                .setHeader("Authorization", "Bearer " + KEY)
                .setHeader("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(msg)).build();

        HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
        try (
                InputStream body = response.body();
                InputStreamReader inputStreamReader = new InputStreamReader(body);
                BufferedReader reader = new BufferedReader(inputStreamReader);
        ) {
            StringBuilder sb = new StringBuilder();
            StringBuilder contentMessage = new StringBuilder();
            String s;
            int once = 0;
            while ((s = reader.readLine()) != null) {
                if (s.equals("")) continue;
                if (s.equals("\n")) continue;
                if (s.equals("data: [DONE]")) continue;
                if (s.equals(": keep-alive")) if (once == 0) {
                    send.sendMessage("思考中.");
                    once++;
                    continue;
                } else {
                    send.sendMessage(". ".repeat(once));
                    once++;
                    continue;
                }
                try {
                    Stream stream = JSON.readValue(s.substring(6), Stream.class);
                    String reasoningContent;
                    if ((reasoningContent = stream.getChoices().get(0).getDelta().getReasoningContent()) != null) {
                        sb.append(reasoningContent);
                        if (reasoningContent.contains("。")) {
                            send.sendMessage(sb.toString().trim());
                            sb.delete(0, sb.length());
                        }
                    }
                    String content;
                    if ((content = stream.getChoices().get(0).getDelta().getContent()) != null) {
                        sb.append(content);
                        if (content.contains("🐋")) {
                            send.sendMessage(sb.toString().trim());
                            sb.delete(0, sb.length());
                        }
                        contentMessage.append(content);
                    }
                } catch (IOException e) {
                    BODY.getMessages().remove(BODY.getMessages().size() - 1);
                    send.sendMessage("错误解析:" + e.getMessage() + "\n\n" + s + "\n\n" + msg);
                }
            }
            if (!sb.toString().equals("")) {
                send.sendMessage(sb.toString());
            }
            BODY.getMessages().add(new Message("assistant", contentMessage.toString()));
            isRunning = false;
        }
    }

    public Boolean getStream() {
        return BODY.getStream();
    }

    public void setSteam(Boolean isStream) {
        BODY.setStream(isStream);
    }
}
