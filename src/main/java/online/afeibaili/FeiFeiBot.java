package online.afeibaili;

import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.utils.MiraiLogger;

public final class FeiFeiBot extends JavaPlugin {
    public static final FeiFeiBot INSTANCE = new FeiFeiBot();
    public static MiraiLogger LOGGER;

    //插件接口
    private FeiFeiBot() {
        super(new JvmPluginDescriptionBuilder("online.afeibaili.feifeibot", "2.6.0")
                .name("FeiFeiBot")
                .author("AfeiBaili")
                .build());
    }

    //接口所调用的方法
    @Override
    public void onEnable() {
        LOGGER = getLogger();   //获取日志器
        MessageHandler.load();  //加载消息总处理
        LOGGER.info("菲菲插件加载成功");
    }
}