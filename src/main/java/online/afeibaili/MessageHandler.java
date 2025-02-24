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
import online.afeibaili.command.Command;
import online.afeibaili.command.Commands;
import online.afeibaili.mc.MCModSearch;
import online.afeibaili.other.Memory;
import online.afeibaili.other.Util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

public class MessageHandler {
    public static final HashMap<String, Command> COMMANDS = new HashMap<>();
    public static final List<Long> GROUPS = new ArrayList<>();
    public static final Set<Long> IMMERSIVES = new HashSet<>();
    public static final StringBuffer BUFFER = new StringBuffer();
    public static final ChatGPT CHATGPT = FeiFeiFactory.createChatGPT();
    public static final Kimi KIMI = FeiFeiFactory.createKimi();
    public static final Deepseek DEEPSEEK = FeiFeiFactory.createDeepseek();
    public static final HashMap<Long, Integer> LEVEL_MAP = Util.getLevelMap();
    public static Bot bot;
    public static Long master;
    public static Long group;
    public static Boolean isAlive = true;
    public static Boolean isImmersive = false;

    static {
        GROUPS.add(group = Long.parseLong(Util.getProperty("Group")));
        master = Long.parseLong(Util.getProperty("Master"));
        Util.setLevelMap(master, 999);
    }

    /**
     * 加载消息处理
     */
    public static void load() {
        Commands.setCommands();
        MCModSearch.load();
        groupListener();
        onlineListener();
    }

    /**
     * 解析命令，包含消息事件
     *
     * @param message 传入进来的消息
     * @param event   群聊消息事件
     * @return 返回命令集中处理的结果
     */
    public static String parsingMessage(String message, GroupMessageEvent event) {
        String msg = new StringBuffer(message).deleteCharAt(0).toString();
        String[] param = msg.split(" +");
        Integer level = LEVEL_MAP.get(event.getSender().getId());
        Command command = COMMANDS.get(param[0]);
        if (command == null) return "找不到命令喵~";
        if (command.getLevel() <= (level == null ? 0 : level)) {
            return command.getMethod().get(param, event);
        } else {
            return "您的等级是 " + (level == null ? 0 : level) + " 喵~但是您的指令等级为 " + command.getLevel() + " 喵~";
        }
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
            if (message.charAt(0) == '/') {
                send.sendMessage(parsingMessage(message, e));
            } else if (isAlive) {
                if (message.contains("菲菲") || message.contains("@2664306741") || IMMERSIVES.contains(e.getSender().getId())) {
                    try {
                        if (CHATGPT.getStream()) CHATGPT.sendAsStream("user", message, send);
                        else send.sendMessage(CHATGPT.send("user", message));
                    } catch (URISyntaxException | IOException | InterruptedException ex) {
                        send.sendMessage("向ChatGPT发送请求时出错喵~\n" + "错误原因：" + ex.getMessage());
                        ex.printStackTrace();
                    }
                } else if (message.contains("小鲸鱼")) {
                    try {
                        if (DEEPSEEK.getStream()) DEEPSEEK.sendAsStream("user", message, send, e.getSenderName());
                        else send.sendMessage(DEEPSEEK.send("user", message));
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

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    GROUPS.forEach(g -> {
                        Optional<Group> group = Optional.ofNullable(e.getBot().getGroup(g));
                        group.ifPresent(gr -> gr.getBotAsMember().setNameCard("菲菲 | Memory：" + Memory.getMemory() + "%"));
                    });
                }
            }, 10000, 60000);
        });
    }
}
