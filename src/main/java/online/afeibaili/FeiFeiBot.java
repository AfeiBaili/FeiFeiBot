package online.afeibaili;

import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.BotOnlineEvent;
import net.mamoe.mirai.event.events.GroupEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.utils.MiraiLogger;
import online.afeibaili.chat.ChatGPT;
import online.afeibaili.deepseek.Deepseek;
import online.afeibaili.kimi.Kimi;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public final class FeiFeiBot extends JavaPlugin {
    public static final FeiFeiBot INSTANCE = new FeiFeiBot();
    public static final List<Long> GROUP = new ArrayList<>();
    public static final List<Long> MASTER = new ArrayList<>();
    public static final List<String> MENU = new ArrayList<>();
    public static MiraiLogger LOGGER;
    public static Boolean isAlive = true;
    public static Boolean isCommand = false;

    static {
        GROUP.add(975709430L);
        MASTER.add(2411718391L);

        MENU.add("菜单|功能|功能菜单");
        MENU.add("新会话|开启新会话");
        MENU.add("新Kimi");
        MENU.add("新小鲸鱼");
        MENU.add("对话 [次数]");
        MENU.add("新设定 [模型]");
        MENU.add("开启菲菲");
        MENU.add("关闭菲菲");
        MENU.add("切换Key");
        MENU.add("查看Key");
        MENU.add("查看模型|当前模型");
        MENU.add("切换模型");
        MENU.add("切换模型 [模型名称]");
        MENU.add("查看群");
        MENU.add("添加群 [群聊名称]");
        MENU.add("移除群|删除群 [群聊名称]");
        MENU.add("查看主人");
        MENU.add("添加主人 [QQ]");
        MENU.add("移除主人|删除主人 [QQ]");
        MENU.add("获取当前聊天记录");
        MENU.add("最大模型额度");
        MENU.add("最小模型额度");
    }

    private FeiFeiBot() {
        super(new JvmPluginDescriptionBuilder("online.afeibaili.feifeibot", "1.6.0").name("FeiFeiBot").author("AfeiBaili").build());
    }

    public static void parsingCommand(String message, Group send) {
        switch (message) {
            case "菜单":
            case "功能":
            case "功能菜单":
                StringBuffer buffer = new StringBuffer();
                MENU.forEach(model -> {
                    buffer.append(model).append("\n");
                });
                buffer.deleteCharAt(buffer.length() - 1);
                send.sendMessage(buffer.toString());
                break;
            case "新会话":
            case "开启新会话":
                ChatGPT.clearChat();
                ChatGPT.initChat();
                send.sendMessage("创建好了喵~");
                break;
            case "新kimi":
            case "新Kimi":
            case "新KIMI":
                //todo
                send.sendMessage(Kimi.newKimi());
                isCommand = true;
                break;
            case "新小鲸鱼":
                //todo
                send.sendMessage(Deepseek.newDeepseek());
                isCommand = true;
            case "开启菲菲":
                isAlive = true;
                send.sendMessage("菲菲出现了喵~");
                break;
            case "关闭菲菲":
                send.sendMessage("菲菲先退下了喵~");
                isAlive = false;
                break;
            case "查看模型":
            case "当前模型":
                send.sendMessage(ChatGPT.getModel());
                break;
            case "切换模型":
                buffer = new StringBuffer();
                ChatGPT.MODELS.forEach(model -> {
                    buffer.append(model).append("\n");
                });
                buffer.deleteCharAt(buffer.length() - 1);
                send.sendMessage(buffer.toString());
                break;
            case "查看群":
                buffer = new StringBuffer();
                GROUP.forEach(number -> {
                    buffer.append(number).append("\n");
                });
                buffer.deleteCharAt(buffer.length() - 1);
                send.sendMessage(buffer.toString());
                break;
            case "查看主人":
                buffer = new StringBuffer();
                MASTER.forEach(number -> {
                    buffer.append(number).append("\n");
                });
                buffer.deleteCharAt(buffer.length() - 1);
                send.sendMessage(buffer.toString());
                break;
            case "查看key":
            case "查看Key":
            case "查看KEY":
                send.sendMessage(ChatGPT.getKey());
                break;
            case "获取当前聊天记录":
                send.sendMessage(ChatGPT.getNowChatHistory());
                break;
            case "最大模型":
            case "最大额度":
            case "最大额度模型":
            case "最大模型额度":
                send.sendMessage(ChatGPT.maxBalanceModel());
                break;
            case "最小模型":
            case "最小额度":
            case "最小额度模型":
            case "最小模型额度":
                send.sendMessage(ChatGPT.minBalanceModel());
                break;
        }
        String[] strings = message.split(" ");
        if (strings.length > 1) switch (strings[0]) {
            case "新设定":
            case "开启新设定":
                send.sendMessage(ChatGPT.newChat(strings[1]));
                break;
            case "切换模型":
                send.sendMessage(ChatGPT.setModel(strings[1]));
                break;
            case "添加群":
                try {
                    if (GROUP.contains(Long.parseLong(strings[1]))) {
                        send.sendMessage("已经存在了喵~");
                        break;
                    }
                    GROUP.add(Long.parseLong(strings[1]));
                    send.sendMessage("添加" + strings[1] + "群成功了喵~");
                } catch (NumberFormatException ex) {
                    send.sendMessage("群名不正确喵~");
                }
                break;
            case "移除群":
            case "删除群":
                if (Long.parseLong(strings[1]) == 975709430L) {
                    send.sendMessage("无法移除主群喵~");
                } else {
                    try {
                        GROUP.remove(Long.parseLong(strings[1]));
                        send.sendMessage("移除" + strings[1] + "群成功了喵~");
                    } catch (NumberFormatException ex) {
                        send.sendMessage("群名不正确喵~");
                    }
                }
                break;
            case "添加主人":
                try {
                    if (MASTER.contains(Long.parseLong(strings[1]))) {
                        send.sendMessage("已经存在了喵~");
                        break;
                    }
                    MASTER.add(Long.parseLong(strings[1]));
                    send.sendMessage(strings[1] + "也是一位主人了喵~");
                } catch (NumberFormatException ex) {
                    send.sendMessage("QQ不正确喵~");
                }
                break;
            case "移除主人":
            case "删除主人":
                if (Long.parseLong(strings[1]) == 2411718391L) {
                    send.sendMessage("无法移除阿飞喵~");
                } else {
                    try {
                        MASTER.remove(Long.parseLong(strings[1]));
                        send.sendMessage(strings[1] + "现在已经不是我的主人了喵~");
                    } catch (NumberFormatException ex) {
                        send.sendMessage("QQ不正确喵~");
                    }
                }
                break;
            case "对话":
                //todo
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    final int count = Integer.parseInt(strings[1]);
                    int i = 0;
                    String msg = "hi 小鲸鱼，" + ((strings.length > 2) ? strings[2] : "");

                    @Override
                    public void run() {
                        try {
                            i++;
                            if (i < count) {
                                msg = Deepseek.sendRequest("user", msg);
                                send.sendMessage("小鲸鱼: " + msg);
                                msg = ChatGPT.sendRequest("user", msg);
                                send.sendMessage("菲菲: " + msg);
                            } else timer.cancel();

                        } catch (NumberFormatException e) {
                            send.sendMessage("请输入对话次数");
                            timer.cancel();
                        } catch (IOException | InterruptedException | URISyntaxException e) {
                            send.sendMessage("遇到错误: " + e.getMessage());
                            timer.cancel();
                        }
                    }
                }, 0, 0);
                break;
        }
    }

    public void listener() {
        GlobalEventChannel.INSTANCE.filter(event -> {
            if (event instanceof GroupEvent) {
                GroupMessageEvent groupEvent = (GroupMessageEvent) event;
                return GROUP.contains(groupEvent.getGroup().getId());
            }
            return false;
        }).subscribeAlways(GroupMessageEvent.class, e -> {
            String message = e.getMessage().contentToString();
            Group send = e.getSubject();
            if (MASTER.contains(e.getSender().getId())) {
                parsingCommand(message, send);
            }
            if (isCommand) {
                isCommand = false;
                return;
            }
            if ((message.contains("菲菲") || message.contains("@2664306741")) && isAlive) {
                try {
                    send.sendMessage(ChatGPT.sendRequest("user", message));
                } catch (URISyntaxException | IOException | InterruptedException ex) {
                    send.sendMessage("向ChatGPT发送请求时出错喵~\n" + "错误原因：" + ex.getMessage());
                    ex.printStackTrace();
                }
            } else if (message.contains("小鲸鱼") && isAlive) {
                try {
                    send.sendMessage(Deepseek.sendRequest("user", message));
                } catch (Exception ex) {
                    send.sendMessage("向Deepseek发送请求时出错\n" + "错误原因：" + ex.getMessage());
                    ex.printStackTrace();
                }
            } else if (message.contains("kimi") || message.contains("Kimi") || message.contains("KIMI") && isAlive) {
                try {
                    send.sendMessage(Kimi.sendRequest("user", message));
                } catch (Exception ex) {
                    send.sendMessage("向Kimi发送请求时出错\n" + "错误原因：" + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
        GlobalEventChannel.INSTANCE.subscribeAlways(BotOnlineEvent.class, e -> {
            GROUP.forEach(g -> {
                Group group = e.getBot().getGroup(g);
                if (group != null) {
                    group.sendMessage("菲菲闪亮登场！");
                }
            });
        });
    }

    @Override
    public void onEnable() {
        LOGGER = getLogger();
        ChatGPT.initChat();
        Kimi.initKimi();
        Deepseek.initDeepseek();
        listener();
        LOGGER.info("菲菲插件加载成功");
    }
}