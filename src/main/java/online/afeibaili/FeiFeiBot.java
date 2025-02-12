package online.afeibaili;

import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.utils.MiraiLogger;

public final class FeiFeiBot extends JavaPlugin {
    public static final FeiFeiBot INSTANCE = new FeiFeiBot();
    public static MiraiLogger LOGGER;

    private FeiFeiBot() {
        super(new JvmPluginDescriptionBuilder("online.afeibaili.feifeibot", "2.1.1")
                .name("FeiFeiBot")
                .author("AfeiBaili")
                .build());
    }

    @Override
    public void onEnable() {
        LOGGER = getLogger();
        MessageHandler.load();
        LOGGER.info("菲菲插件加载成功");
    }
}