package online.afeibaili;

import com.fasterxml.jackson.databind.ObjectMapper;
import online.afeibaili.json.Message;
import online.afeibaili.json.RequestBody;
import online.afeibaili.other.Model;
import online.afeibaili.other.Util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class Test {
    private static final String KEY = Util.getProperty("DeepseekKey");
    private static final RequestBody BODY = new RequestBody("deepseek-chat", new ArrayList<Message>(), true);

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        BODY.getMessages().add(new Message("system","在吗"));
        BODY.getMessages().add(new Message("user","在吗"));

        HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://api.deepseek.com/chat/completions"))
                .setHeader("Content-Type", "application/json")
                .setHeader("Authorization", "Bearer " + KEY)
                .POST(HttpRequest.BodyPublishers.ofString("{\n  \"messages\": [\n    {\n      \"content\": \"You are a helpful assistant\",\n      \"role\": \"system\"\n    },\n    {\n      \"content\": \"Hi\",\n      \"role\": \"user\"\n    }\n  ],\n  \"model\": \"deepseek-chat\",\n  \"frequency_penalty\": 0,\n  \"max_tokens\": 2048,\n  \"presence_penalty\": 0,\n  \"response_format\": {\n    \"type\": \"text\"\n  },\n  \"stop\": null,\n  \"stream\": false,\n  \"stream_options\": null,\n  \"temperature\": 1,\n  \"top_p\": 1,\n  \"tools\": null,\n  \"tool_choice\": \"none\",\n  \"logprobs\": false,\n  \"top_logprobs\": null\n}"))
                .build();

        System.out.println(client.send(request, HttpResponse.BodyHandlers.ofString()).body());
    }
}
