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
    public final RequestBody BODY = new RequestBody("deepseek-chat", new ArrayList<Message>(), true);
    public final String KEY = getProperty("DeepseekKey");
    public boolean isRunning = false;

    @Override
    public void init() {
        BODY.getMessages().add(new Message("system", "你叫小鲸鱼也叫做Deepseek，你是大家的好朋友，" +
                "你在群里面是小鲸鱼的角色，你回复的地方是一个QQ群聊。" + "不可使用markdown格式。" +
                "你也是大家的百科全书，说话喜欢带上颜文字"));
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

    public void sendAsStream(String role, String message, Group send, String name) throws IOException, URISyntaxException, InterruptedException {
        if (isRunning) {
            send.sendMessage("小鲸鱼正在回答中! 请稍后再试~");
            return;
        }
        isRunning = true;
        HttpClient httpClient = HttpClient.newHttpClient();
        BODY.getMessages().add(new Message(role, message, name));
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
            StringBuilder reasoningContentSB = new StringBuilder();
            StringBuilder contentSB = new StringBuilder();
            int reasoningContentLength = 0;
            int contentLength = 0;
            String valueLine;
            while ((valueLine = reader.readLine()) != null) {
                if (valueLine.equals("")) continue;
                if (valueLine.equals("\n")) continue;
                if (valueLine.equals("data: [DONE]")) continue;
                if (valueLine.equals(": keep-alive")) {
                    send.sendMessage("思索中...");
                    continue;
                }
                try {
                    Stream stream = JSON.readValue(valueLine.substring(6), Stream.class);

                    String reasoningContent;
                    if ((reasoningContent = stream.getChoices().get(0).getDelta().getReasoningContent()) != null) {
                        reasoningContentSB.append(reasoningContent);
                        if (reasoningContent.contains("。")) {
                            send.sendMessage(reasoningContentSB
                                    .substring(reasoningContentLength,
                                            reasoningContentSB.length())
                                    .trim());
                            reasoningContentLength = reasoningContentSB.length();
                        }
                    }

                    String content;
                    if ((content = stream.getChoices().get(0).getDelta().getContent()) != null) {

                        try {
                            if (!reasoningContentSB.substring(reasoningContentLength, reasoningContentSB.length()).trim().equals("")) {
                                send.sendMessage(reasoningContentSB
                                        .substring(reasoningContentLength,
                                                reasoningContentSB.length()));
                                reasoningContentLength = reasoningContentSB.length();
                            }
                        } catch (StringIndexOutOfBoundsException ignored) {
                        }

                        contentSB.append(content);
                        if (content.contains("🐋")) {
                            send.sendMessage(contentSB.substring(contentLength, contentSB.length()).trim());
                            contentLength = contentSB.length();
                        }
                    }
                } catch (IOException e) {
                    BODY.getMessages().remove(BODY.getMessages().size() - 1);
                    send.sendMessage("错误解析:" + e.getMessage() + "\n\n" + valueLine + "\n\n" + msg);
                }
            }

            try {
                if (!contentSB.substring(contentLength, contentSB.length()).trim().equals("")) {
                    send.sendMessage(contentSB.substring(contentLength, contentSB.length()));
                }
            } catch (StringIndexOutOfBoundsException ignored) {
            }
            BODY.getMessages().add(new Message("assistant", contentSB.toString()));
            isRunning = false;
        }
    }

    public Boolean getStream() {
        return BODY.getStream();
    }

    public void setSteam(Boolean isStream) {
        BODY.setStream(isStream);
    }

    public String getModel() {
        return BODY.getModel();
    }

    public String setModel(String model) {
        switch (model) {
            case "2":
            case "deepseek-reasoner":
                BODY.setModel("deepseek-reasoner");
                return "设置推理模型成功！";
            case "1":
            case "deepseek-chat":
                BODY.setModel("deepseek-chat");
                return "设置聊天模型成功！";
        }
        return "请输入正确的模型！[deepseek-chat | deepseek-reasoner]";
    }
}
