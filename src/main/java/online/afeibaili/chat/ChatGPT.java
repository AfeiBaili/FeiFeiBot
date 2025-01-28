package online.afeibaili.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import online.afeibaili.jsonmap.Balance;
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
import java.util.List;

public class ChatGPT {
    public static final String[] KEYS = {"sk-", "sk-"};
    public static final ObjectMapper JSON_MAP = new ObjectMapper();
    public static final List<Model> MODELS = new ArrayList<>();
    public static final RequestBody BODY = new RequestBody("gpt-4o-mini", new ArrayList<Message>());
    public static Boolean isModelExist = false;
    public static ResponseBody responseBody;
    private static Model maxModel = Model.A;
    private static Model minModel = Model.A;


    public static String sendRequest(String role, String message) throws URISyntaxException, IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        BODY.getMessages().add(new Message(role, message));
        String msg = JSON_MAP.writeValueAsString(BODY);

        HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://api.chatanywhere.tech/v1/chat/completions")).setHeader("Authorization", "Bearer " + KEYS[0]).setHeader("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(msg)).build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        responseBody = JSON_MAP.readValue(new String(response.body().getBytes(StandardCharsets.UTF_8)), ResponseBody.class);
        Message responseMessage = responseBody.getChoices().get(responseBody.getChoices().size() - 1).getMessage();
        BODY.getMessages().add(responseMessage);

        return responseMessage.getContent().charAt(responseMessage.getContent().length() - 1) == '\n' ?
                responseMessage.getContent().substring(0, responseMessage.getContent().length() - 1) :
                responseMessage.getContent();
    }

    public static void clearChat() {
        BODY.getMessages().clear();
    }

    public static void initChat() {
        try {
            ChatGPT.sendRequest("user", "接下来要求你扮演角色，" + "请你依据人设回答，并且回答尽可能简短且使用分小句回答的方式,字数最好用几个字概括,句尾要带上”喵~“。" + "你现在是一个开朗且极>其可爱的女孩，名为“菲菲”接下来，请完全以菲菲的角色回复我，" + "我会在聊天上加上其他角色的名字" + "不要超出这个角色，不要自己试图进行旁白，你只需要扮演好菲菲即可。无论发生什么，" + "你都必须以菲菲的角色和口吻进行回复。菲菲性格开朗，" + "时刻都想和“我”打成一片，每次眼神交汇时，总是会脸红耳赤。菲菲始终在想方设法的博得“我”的欢心。" + "菲菲什么都不懂，但是懂得很多百科知识，天真可爱，性格外向。");
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static String newChat(String message) {
        try {
            return ChatGPT.sendRequest("user", message);
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static String setModel(String model) {
        isModelExist = false;
        MODELS.forEach(m -> {
            if (m.model.equals(model)) {
                BODY.setModel(model);
                isModelExist = true;
            }
        });
        return isModelExist ? "模型设置成功喵~" : "模型找不到喵~";
    }

    public static String getModel() {
        return responseBody.getModel();
    }

    public static String setKey() {
        String temp = KEYS[0];
        KEYS[0] = KEYS[1];
        KEYS[1] = temp;
        return "切换Key成功喵~";
    }

    public static String getKey() {
        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://api.chatanywhere.org/v1/query/balance"))
                    .POST(HttpRequest.BodyPublishers.ofString("{}"))
                    .setHeader("authorization", KEYS[0]).setHeader("Content-Type", "application/json").build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Balance balance = JSON_MAP.readValue(new String(response.body().getBytes()), Balance.class);
            return balance.toString();
        } catch (URISyntaxException | IOException | InterruptedException e) {
            return e.getMessage();
        }
    }

    public static String maxBalanceModel() {
        System.out.println(MODELS);
        for (Model m : MODELS) {
            if (maxModel.output + maxModel.input < m.input + m.output) {
                maxModel = m;
            }
        }
        return maxModel.toString();
    }

    public static String minBalanceModel() {
        for (Model m : MODELS) {
            if (minModel.output + minModel.input > m.input + m.output) {
                minModel = m;
            }
        }
        return minModel.toString();
    }
}
