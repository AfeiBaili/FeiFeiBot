package online.afeibaili;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.BotOnlineEvent;
import net.mamoe.mirai.event.events.GroupEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import online.afeibaili.bot.Bot;
import online.afeibaili.bot.ChatGPT;
import online.afeibaili.bot.Deepseek;
import online.afeibaili.bot.Kimi;
import online.afeibaili.other.Commands;
import online.afeibaili.other.Method;
import online.afeibaili.other.Util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageHandler {
    public static final HashMap<String, Method> COMMANDS = new HashMap<>();
    public static final List<Long> MASTERS = new ArrayList<>();
    public static final List<Long> GROUPS = new ArrayList<>();
    public static final StringBuffer BUFFER = new StringBuffer();
    public static final ChatGPT CHATGPT = FeiFeiFactory.createChatGPT();
    public static final Kimi KIMI = FeiFeiFactory.createKimi();
    public static final Deepseek DEEPSEEK = FeiFeiFactory.createDeepseek();
    public static Bot bot;
    public static Long master;
    public static Long group;
    public static Boolean isAlive = true;
    public static Integer LEVEL = 2; // 1 普通等级, 2管理等级

    static {
        GROUPS.add(group = Long.parseLong(Util.getProperty("Group")));
        MASTERS.add(master = Long.parseLong(Util.getProperty("Master")));
    }

    /**
     * 加载消息处理
     */
    public static void load() {
        Commands.setCommands();
        groupListener();
        onlineListener();
    }

    /**
     * 解析命令
     *
     * @param message 传入进来的消息
     * @return 返回命令集中处理的逻辑
     */
    public static String parsingMessage(String message) {
        String msg = new StringBuffer(message).deleteCharAt(0).toString();
        String[] param = msg.split(" +");
        Method method = COMMANDS.get(param[0]);
        if (method != null) return method.get(param);
        else return "找不到命令喵~";
    }

    /**
     * 群聊消息监听器
     */
    public static void groupListener() {
        GlobalEventChannel.INSTANCE.filter(event -> {
            if (event instanceof GroupEvent) {
                GroupMessageEvent groupEvent = (GroupMessageEvent) event;
                return GROUPS.contains(groupEvent.getGroup().getId());
            }
            return false;
        }).subscribeAlways(GroupMessageEvent.class, e -> {
            String message = e.getMessage().contentToString();
            Group send = e.getSubject();
            if ((MASTERS.contains(e.getSender().getId()) || LEVEL == 1) && message.charAt(0) == '/') {
                send.sendMessage(parsingMessage(message));
            } else if (isAlive) {
                if (message.contains("菲菲") || message.contains("@2664306741")) {
                    try {
                        send.sendMessage(CHATGPT.send("user", message));
                    } catch (URISyntaxException | IOException | InterruptedException ex) {
                        send.sendMessage("向ChatGPT发送请求时出错喵~\n" + "错误原因：" + ex.getMessage());
                        ex.printStackTrace();
                    }
                } else if (message.contains("小鲸鱼")) {
                    try {
                        send.sendMessage(DEEPSEEK.send("user", message));
                    } catch (Exception ex) {
                        send.sendMessage("向Deepseek发送请求时出错\n" + "错误原因：" + ex.getMessage());
                        ex.printStackTrace();
                    }
                } else if (message.contains("kimi") || message.contains("Kimi") || message.contains("KIMI")) {
                    try {
                        send.sendMessage(KIMI.send("user", message));
                    } catch (Exception ex) {
                        send.sendMessage("向Kimi发送请求时出错\n" + "错误原因：" + ex.getMessage());
                        ex.printStackTrace();
                    }
                } else if (bot != null && message.contains(bot.getName())) {
                    try {
                        send.sendMessage(bot.getBot().send("user", message));
                    } catch (IOException | URISyntaxException | InterruptedException ex) {
                        send.sendMessage("向" + bot.getName() + "发送请求时出错\n" + "错误原因：" + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * Bot上线时出发的监听器
     */
    public static void onlineListener() {
        GlobalEventChannel.INSTANCE.subscribeAlways(BotOnlineEvent.class, e -> {
            GROUPS.forEach(g -> {
                Group group = e.getBot().getGroup(g);
                if (group != null) {
                    group.sendMessage("菲菲闪亮登场！");
                }
            });
        });
    }
}
