package online.afeibaili;

import java.io.IOException;

import static online.afeibaili.translation.Translation.translate;

public class Test {
    public static void main(String[] args) throws IOException {
        System.out.println(translate("早", "zh-CHS", "en"));
    }
}
