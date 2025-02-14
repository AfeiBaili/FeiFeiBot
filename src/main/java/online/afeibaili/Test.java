package online.afeibaili;

import com.fasterxml.jackson.databind.ObjectMapper;
import online.afeibaili.json.Balance;
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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Test {
    private static final String KEY = Util.getProperty("ChatGPTKey");
    private static final RequestBody BODY = new RequestBody(Model.N.getModel(), new ArrayList<Message>(), true);

    public static void main(String[] args) {
        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://api.chatanywhere.org/v1/query/balance"))
                    .POST(HttpRequest.BodyPublishers.ofString("{}"))
                    .setHeader("authorization", KEY)
                    .setHeader("Content-Type", "application/json").build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Balance balance = new ObjectMapper().readValue(new String(response.body().getBytes(StandardCharsets.UTF_8)), Balance.class);
            System.out.println(balance.toString());
        } catch (URISyntaxException | IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}
