package online.afeibaili.other;

import online.afeibaili.FeiFeiFactory;
import online.afeibaili.bot.Bot;
import online.afeibaili.bot.ChatGPT;
import online.afeibaili.bot.Deepseek;
import online.afeibaili.bot.Kimi;

import java.util.List;

import static online.afeibaili.MessageHandler.*;

public class Commands {

    /**
     * 设置命令
     */
    public static void setCommands() {
        COMMANDS.put("开始", param -> {
            BUFFER.delete(0, BUFFER.length());
            COMMANDS.forEach((k, v) -> BUFFER.append(k).append('\n'));
            BUFFER.deleteCharAt(BUFFER.length() - 1);
            return BUFFER.toString();
        });
        COMMANDS.put("重置菲菲", param -> {
            CHATGPT.reload();
            return "重置好了喵~";
        });
        COMMANDS.put("重置kimi", param -> {
            CHATGPT.reload();
            return "已创建新的的Kimi";
        });
        COMMANDS.put("重置小鲸鱼", param -> {
            CHATGPT.reload();
            return "小鲸鱼🐋已重置好啦~";
        });
        COMMANDS.put("开启聊天", param -> {
            isAlive = true;
            return "菲菲出现了喵~";
        });
        COMMANDS.put("关闭聊天", param -> {
            isAlive = false;
            return "菲菲先退下了喵~";
        });
        COMMANDS.put("切换普通命令等级", param -> {
            LEVEL = 1;
            return "设置等级成功喵~";
        });
        COMMANDS.put("切换管理命令等级", param -> {
            LEVEL = 2;
            return "设置等级成功喵~";
        });
        COMMANDS.put("查看菲菲模型", param -> CHATGPT.getModel());
        COMMANDS.put("切换菲菲模型", param -> {
            if (param.length == 1) {
                return forEach(ChatGPT.MODELS);
            } else {
                return CHATGPT.setModel(param[1]);
            }
        });
        COMMANDS.put("查看key", param -> CHATGPT.getKey());
        COMMANDS.put("获取菲菲聊天记录", param -> CHATGPT.getHistory());
        COMMANDS.put("新设定", param -> {
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
        });
        COMMANDS.put("查看群", param -> forEach(GROUPS));
        COMMANDS.put("查看主人", param -> forEach(MASTERS));
        COMMANDS.put("添加群", param -> {
            BUFFER.delete(0, BUFFER.length());
            for (int i = 1; i < param.length; i++) {
                GROUPS.add(Long.parseLong(param[i]));
                BUFFER.append("已添加群：").append(param[i]).append('\n');
            }
            BUFFER.deleteCharAt(BUFFER.length() - 1);
            return BUFFER.toString();
        });
        COMMANDS.put("添加主人", param -> {
            BUFFER.delete(0, BUFFER.length());
            for (int i = 1; i < param.length; i++) {
                MASTERS.add(Long.parseLong(param[i]));
                BUFFER.append("已添加主人：").append(param[i]).append('\n');
            }
            BUFFER.deleteCharAt(BUFFER.length() - 1);
            return BUFFER.toString();
        });
        COMMANDS.put("删除群", param -> {
            BUFFER.delete(0, BUFFER.length());
            for (int i = 1; i < param.length; i++) {
                long number = Long.parseLong(param[i]);
                if (number == group) {
                    BUFFER.append("不允许删除主群：").append(param[i]).append('\n');
                    continue;
                }
                if (GROUPS.remove(number)) BUFFER.append("已删除群：").append(param[i]).append('\n');
            }
            BUFFER.deleteCharAt(BUFFER.length() - 1);
            return BUFFER.toString();
        });
        COMMANDS.put("删除主人", param -> {
            BUFFER.delete(0, BUFFER.length());
            for (int i = 1; i < param.length; i++) {
                long number = Long.parseLong(param[i]);
                if (number == master) {
                    BUFFER.append("不允许删除主人：").append(param[i]).append('\n');
                    continue;
                }
                if (MASTERS.remove(number)) BUFFER.append("已删除主人：").append(param[i]).append('\n');
            }
            BUFFER.deleteCharAt(BUFFER.length() - 1);
            return BUFFER.toString();
        });

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
