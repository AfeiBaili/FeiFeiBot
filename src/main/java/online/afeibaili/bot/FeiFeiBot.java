package online.afeibaili.bot;

import java.io.IOException;
import java.net.URISyntaxException;

public interface FeiFeiBot {
    void init();
    void init(String setting);

    void reload();

    String send(String role, String message) throws IOException, URISyntaxException, InterruptedException;
}
