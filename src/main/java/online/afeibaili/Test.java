package online.afeibaili;

import com.fasterxml.jackson.databind.ObjectMapper;
import online.afeibaili.json.Message;
import online.afeibaili.json.RequestBody;
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
import java.util.ArrayList;

public class Test {
    private static final String KEY = Util.getProperty("DeepseekKey");
    private static final RequestBody BODY = new RequestBody("deepseek-reasoner", new ArrayList<Message>(), true);

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        BODY.getMessages().add(new Message("system", "在吗"));
        BODY.getMessages().add(new Message("user", "你好呀"));

        HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://api.deepseek.com/chat/completions"))
                .setHeader("Content-Type", "application/json")
                .setHeader("Authorization", "Bearer " + KEY)
                .POST(HttpRequest.BodyPublishers.ofString(new ObjectMapper().writeValueAsString(BODY)))
                .build();
        HttpResponse<InputStream> inputStreamHttpResponse = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

        InputStreamReader inputStreamReader = new InputStreamReader(inputStreamHttpResponse.body());
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String message;
        while ((message = bufferedReader.readLine()) != null) {
            System.out.println(message);
        }
    }
}
