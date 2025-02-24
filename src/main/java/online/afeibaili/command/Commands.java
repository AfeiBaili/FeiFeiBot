package online.afeibaili.command;

import online.afeibaili.FeiFeiFactory;
import online.afeibaili.bot.Bot;
import online.afeibaili.bot.ChatGPT;
import online.afeibaili.bot.Deepseek;
import online.afeibaili.bot.Kimi;
import online.afeibaili.other.Util;

import java.util.List;

import static online.afeibaili.MessageHandler.*;

public class Commands {

    /**
     * 设置命令
     */
    public static void setCommands() {
        COMMANDS.put("开始", new Command.Builder()
                .method((param, event) -> {
                    BUFFER.delete(0, BUFFER.length());
                    long id = event.getSender().getId();
                    long finalLevel = LEVEL_MAP.get(id) == null ? 0L : LEVEL_MAP.get(id);
                    COMMANDS.forEach((k, v) -> {
                        if (finalLevel >= v.getLevel()) {
                            BUFFER.append("等级：").append(v.getLevel()).append("    ").append(k).append('\n');
                        }
                    });
                    BUFFER.deleteCharAt(BUFFER.length() - 1);
                    return BUFFER.toString();
                }).build());
        COMMANDS.put("重置菲菲", new Command.Builder()
                .method((param, event) -> {
                    CHATGPT.reload();
                    return "重置好了喵~";
                })
                .level(1)
                .build());
        COMMANDS.put("重置kimi", new Command.Builder()
                .method((param, event) -> {
                    KIMI.reload();
                    return "已创建新的的Kimi";
                })
                .level(1)
                .build());
        COMMANDS.put("重置小鲸鱼", new Command.Builder()
                .method((param, event) -> {
                    DEEPSEEK.reload();
                    return "小鲸鱼🐋已重置好啦~";
                })
                .level(1)
                .build());
        COMMANDS.put("重置所有", new Command.Builder()
                .method((param, event) -> {
                    CHATGPT.reload();
                    DEEPSEEK.reload();
                    KIMI.reload();
                    return "菲菲、小鲸鱼、Kimi已重置好啦~";
                })
                .level(1)
                .build());
        COMMANDS.put("开启聊天", new Command.Builder()
                .method((param, event) -> {
                    isAlive = true;
                    return "菲菲出现了喵~";
                })
                .level(1)
                .build());
        COMMANDS.put("关闭聊天", new Command.Builder()
                .method((param, event) -> {
                    isAlive = false;
                    return "菲菲先退下了喵~";
                })
                .level(1)
                .build());
        COMMANDS.put("设置等级", new Command.Builder()
                .method((param, event) -> {
                    if (param.length != 3) return "设置等级 [QQ] [等级]";
                    try {
                        return Util.setLevelMap(Long.parseLong(param[1]), Integer.parseInt(param[2])) ?
                                "设置成功了喵~" : "设置失败了，不能写入文件，请联系阿飞！";
                    } catch (NumberFormatException e) {
                        return "格式是数字喵~";
                    }
                })
                .level(2)
                .build());
        COMMANDS.put("查看菲菲模型", new Command.Builder()
                .method((param, event) -> CHATGPT.getModel())
                .build());
        COMMANDS.put("切换菲菲模型", new Command.Builder()
                .method((param, event) -> {
                    if (param.length == 1) {
                        return forEach(ChatGPT.MODELS);
                    } else {
                        return CHATGPT.setModel(param[1]);
                    }
                })
                .level(2)
                .build());
        COMMANDS.put("获取菲菲聊天记录", new Command.Builder()
                .method((param, event) -> CHATGPT.getHistory())
                .level(1)
                .build());
        COMMANDS.put("新设定", new Command.Builder()
                .method((param, event) -> {
                    if (param.length != 4) {
                        return "新设定 [名称] [设定长句] [ chatgpt|kimi|deepseek ]";
                    } else {
                        switch (param[3]) {
                            case "chatgpt":
                                ChatGPT chatGPT = FeiFeiFactory.createChatGPT(param[2]);
                                bot = new Bot(chatGPT, param[1], param[2]);
                                return "ChatGPT模型设定成功啦!";
                            case "kimi":
                                Kimi kimi = FeiFeiFactory.createKimi(param[2]);
                                bot = new Bot(kimi, param[1], param[2]);
                                return "Kimi模型设定成功啦!";
                            case "deepseek":
                                Deepseek deepseek = FeiFeiFactory.createDeepseek(param[2]);
                                bot = new Bot(deepseek, param[1], param[2]);
                                return "Deepseek模型设定成功啦!";
                            default:
                                return "请选择标示的模型";
                        }
                    }
                })
                .level(1)
                .build());
        COMMANDS.put("查看群", new Command.Builder()
                .method((param, event) -> forEach(GROUPS))
                .build());
        COMMANDS.put("添加群", new Command.Builder()
                .method((param, event) -> {
                    if (param.length == 1) return "添加群 [群] [...]";
                    BUFFER.delete(0, BUFFER.length());
                    for (int i = 1; i < param.length; i++) {
                        try {
                            GROUPS.add(Long.parseLong(param[i]));
                        } catch (NumberFormatException e) {
                            return "QQ格式不正确！";
                        }
                        BUFFER.append("已添加群：").append(param[i]).append('\n');
                    }
                    BUFFER.deleteCharAt(BUFFER.length() - 1);
                    return BUFFER.toString();
                })
                .level(2)
                .build());
        COMMANDS.put("删除群", new Command.Builder()
                .method((param, event) -> {
                    if (param.length == 1) return "删除群 [群] [...]";
                    BUFFER.delete(0, BUFFER.length());
                    for (int i = 1; i < param.length; i++) {
                        try {
                            long number = Long.parseLong(param[i]);
                            if (number == group) {
                                BUFFER.append("不允许删除主群：").append(param[i]).append('\n');
                                continue;
                            }
                            if (GROUPS.remove(number)) BUFFER.append("已删除群：").append(param[i]).append('\n');
                        } catch (NumberFormatException e) {
                            return "QQ格式不正确！";
                        }
                    }
                    BUFFER.deleteCharAt(BUFFER.length() - 1);
                    return BUFFER.toString();
                })
                .level(2)
                .build());
        COMMANDS.put("开启流", new Command.Builder()
                .method((param, event) -> {
                    if (param.length != 2) {
                        return "开启流 [菲菲|小鲸鱼]";
                    }
                    switch (param[1]) {
                        case "菲菲":
                            CHATGPT.setSteam(true);
                            return "菲菲已开启流式传输";
                        case "小鲸鱼":
                            DEEPSEEK.setSteam(true);
                            return "小鲸鱼已开启流式传输";
                    }
                    return "没有此机器人!";
                })
                .level(1)
                .build());
        COMMANDS.put("关闭流", new Command.Builder()
                .method((param, event) -> {
                    if (param.length != 2) {
                        return "关闭流 [菲菲|小鲸鱼]";
                    }
                    switch (param[1]) {
                        case "菲菲":
                            CHATGPT.setSteam(false);
                            return "菲菲已关闭流式传输";
                        case "小鲸鱼":
                            DEEPSEEK.setSteam(false);
                            return "小鲸鱼已关闭流式传输";
                    }
                    return "没有此机器人!";
                })
                .level(1)
                .build());
        COMMANDS.put("开启沉浸式对话", new Command.Builder()
                .method((param, event) -> {
                    IMMERSIVES.add(event.getSender().getId());
                    return event.getSender().getNick() + "已开启沉浸式对话！";
                })
                .build());
        COMMANDS.put("终止沉浸式对话", new Command.Builder()
                .method((param, event) -> {
                    IMMERSIVES.remove(event.getSender().getId());
                    return event.getSender().getNick() + "已关闭沉浸式对话！";
                })
                .build());
        COMMANDS.put("查看小鲸鱼模型", new Command.Builder()
                .method((param, event) -> DEEPSEEK.getModel())
                .build());
        COMMANDS.put("切换小鲸鱼模型", new Command.Builder()
                .method((param, event) -> {
                    if (param.length == 1) {
                        return "切换小鲸鱼模型 [deepseek-chat | deepseek-reasoner]";
                    }
                    return DEEPSEEK.setModel(param[1]);
                })
                .level(2)
                .build());
        COMMANDS.put("查看所有人等级", new Command.Builder()
                .method(((param, event) -> {
                    StringBuilder message = new StringBuilder();
                    LEVEL_MAP.forEach((k, v) -> {
                        message.append("QQ:").append(k).append(" 等级：").append(v).append("\n");
                    });
                    return message.toString();
                }))
                .build());
    }

    /**
     * 用于将一个列表做成一个消息
     *
     * @param list 列表
     * @param <T>  列表泛型
     * @return 一个消息
     */
    public static <T> String forEach(List<T> list) {
        BUFFER.delete(0, BUFFER.length());
        list.forEach(value -> BUFFER.append(value).append("\n"));
        BUFFER.deleteCharAt(BUFFER.length() - 1);
        return BUFFER.toString();
    }

}
