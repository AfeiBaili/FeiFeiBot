package online.afeibaili.bot;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.mamoe.mirai.contact.Group;
import online.afeibaili.json.*;
import online.afeibaili.other.Model;
import online.afeibaili.other.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static online.afeibaili.other.Util.JSON;

public class ChatGPT implements FeiFeiBot {
    public static final List<Model> MODELS = new ArrayList<>();
    private final String KEY = Util.getProperty("ChatGPTKey");
    private final RequestBody BODY = new RequestBody(Model.N.getModel(), new ArrayList<>());
    private boolean isModelExist = false;

    @Override
    public void init() {
        try {
            send("system", "接下来要求你扮演角色，" +
                    "请你依据人设回答，并且回答尽可能简短且使用分小句回答的方式,字数最好用几个字概括,句尾要带上”喵~“。" +
                    "你现在是一个开朗且极其可爱的女孩，名为“菲菲”接下来，请完全以菲菲的角色回复我，" +
                    "不要超出这个角色，不要自己试图进行旁白，你只需要扮演好菲菲即可。无论发生什么，" +
                    "你都必须以菲菲的角色和口吻进行回复。菲菲性格开朗，" +
                    "时刻都想和“我”打成一片，每次眼神交汇时，总是会脸红耳赤。菲菲始终在想方设法的博得“我”的欢心。" +
                    "菲菲什么都不懂，但是懂得很多百科知识，天真可爱，性格外向。");
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void init(String setting) {
        try {
            send("system", setting);
        } catch (URISyntaxException | IOException | InterruptedException e) {
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

        HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://api.chatanywhere.tech/v1/chat/completions"))
                .setHeader("Authorization", "Bearer " + KEY)
                .setHeader("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(msg)).build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        ResponseBody responseBody = JSON.readValue(response.body(), ResponseBody.class);
        if (responseBody.getChoices().get(0).getMessage().getContent() == null) {
            return "菲菲不能回答喵~！服务器过滤了喵！";
        }
        Message responseMessage = responseBody.getChoices().get(0).getMessage();
        BODY.getMessages().add(responseMessage);

        return responseMessage.getContent();
    }

    public void sendAsStream(String role, String message, Group send) throws IOException, URISyntaxException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        BODY.getMessages().add(new Message(role, message));
        String msg = JSON.writeValueAsString(BODY);

        HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://api.chatanywhere.tech/v1/chat/completions"))
                .setHeader("Authorization", "Bearer " + KEY)
                .setHeader("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(msg)).build();

        HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
        try (
                InputStream body = response.body();
                InputStreamReader inputStreamReader = new InputStreamReader(body);
                BufferedReader reader = new BufferedReader(inputStreamReader);
        ) {
            StringBuffer sb = new StringBuffer();
            String s;
            while ((s = reader.readLine()) != null) {
                if (s.equals("")) continue;
                try {
                    Stream stream = JSON.readValue(s.substring(6), Stream.class);
                    Optional<String> content = Optional.ofNullable(stream.getChoices().get(0).getDelta().getContent());
                    content.ifPresent(value -> {
                        sb.append(value);
                        if (value.contains("！")) {
                            send.sendMessage(sb.toString());
                            sb.delete(0, sb.length());
                        }
                        if (value.contains("？")) {
                            send.sendMessage(sb.toString());
                            sb.delete(0, sb.length());
                        }
                    });
                } catch (IOException ignored) {
                }
            }
        }
    }

    public String getHistory() {
        return BODY.getMessages().toString();
    }

    public String getKey() {
        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://api.chatanywhere.org/v1/query/balance"))
                    .POST(HttpRequest.BodyPublishers.ofString("{}"))
                    .setHeader("authorization", KEY)
                    .setHeader("Content-Type", "application/json").build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Balance balance = new ObjectMapper().readValue(new String(response.body().getBytes(StandardCharsets.UTF_8)), Balance.class);
            return balance.toString();
        } catch (URISyntaxException | IOException | InterruptedException e) {
            return e.getMessage();
        }
    }

    public String setModel(String message) {
        isModelExist = false;
        MODELS.forEach(m -> {
            if (m.getModel().equals(message)) {
                BODY.setModel(message);
                isModelExist = true;
            }
        });
        return isModelExist ? "模型设置成功喵~" : "模型找不到喵~";
    }

    public String getModel() {
        return BODY.getModel();
    }

    public Boolean getStream() {
        return BODY.getStream();
    }

    public void setSteam(Boolean isStream) {
        BODY.setStream(isStream);
    }
}
