package online.afeibaili.command

import online.afeibaili.*
import online.afeibaili.bot.AbstractBot
import online.afeibaili.bot.ChatGPT
import online.afeibaili.bot.CustomizedBot
import online.afeibaili.bot.Deepseek
import online.afeibaili.bot.Robots.chatgpt
import online.afeibaili.bot.Robots.customized
import online.afeibaili.bot.Robots.deepseek
import online.afeibaili.bot.Robots.kimi

object Commands {
    val commands = ArrayList<Pair<String, Command>>()
    val commandsMap: MutableMap<String, Command> = HashMap()
    val chatgptName: String? = config.chatgpt.name
    val deepseekName: String? = config.deepseek.name

    fun loadCommands() {
        register("菜单", Command({ p, event ->
            val sb = StringBuilder()
            val level: Int = levelMap[event.sender.id] ?: 0
            val filteredList: List<Pair<String, Command>> = commands.filter { level >= it.component2().level }
            val highLevelList: List<Pair<String, Command>> = commands.filter { it !in filteredList }
            if (highLevelList.isEmpty()) {
                filteredList.forEach {
                    sb.append(it.component1()).append("  等级：").append(it.component2().level).append("\n")
                }
            } else {
                filteredList.forEach {
                    sb.append(it.component1()).append("  等级：").append(it.component2().level).append("\n")
                }
                sb.append("\n其他等级命令：\n")
                highLevelList.forEach {
                    sb.append(it.component1()).append("  等级：").append(it.component2().level).append("\n")
                }
            }
            sb.removeSuffix("\n").toString()
        }))
        register("聊天功能", Command({ param, e ->
            if (param.size != 2) return@Command "聊天功能 <开启 | 关闭>"
            return@Command when (param[1]) {
                "开启" -> {
                    if (Manager.isBotAlive) return@Command "当前为开启"
                    Manager.isBotAlive = true
                    "聊天功能已开启"
                }

                "关闭" -> {
                    if (!Manager.isBotAlive) return@Command "当前为关闭  "
                    Manager.isBotAlive = true
                    "聊天功能已关闭"
                }

                else -> "聊天功能 <开启 | 关闭>"
            }
        }, level = 1))
        register("设置等级", Command({ param, e ->
            if (param.size != 3) return@Command "设置等级 <QQ> <等级>"
            try {
                val qq: Long = if (param[1].startsWith("@")) param[1].removePrefix("@").toLong() else param[1].toLong()
                val level: Int = param[2].toInt()
                levelObject.setLevelMap(qq, level)
                "设置等级成功"
            } catch (e: NumberFormatException) {
                return@Command "参数中包含字母：${e.message}"
            }
        }, level = 4))
        register("查看所有人等级", Command({ p, e ->
            val sb = StringBuilder()
            levelMap.forEach { sb.append(it.component1()).append("=").append(it.component2()).append("\n") }
            sb.removeSuffix("\n").toString()
        }))
        register("查看机器人当前模型", Command({ param, e ->
            if (param.size != 2) return@Command "查看机器人当前模型 <${chatgptName} | $deepseekName | kimi>"
            return@Command "模型为：" + when (param[1]) {
                chatgptName -> getModel(chatgpt)
                deepseekName -> getModel(deepseek)
                "kimi" -> getModel(kimi)
                else -> return@Command "未知机器人"
            }
        }))
        register("查看机器人所有模型", Command({ param, e ->
            if (param.size != 2) return@Command "查看机器人所有模型 <${chatgptName} | $deepseekName>"
            return@Command when (param[1]) {
                chatgptName -> getChatGPTModuleAsString()
                deepseekName -> getDeepseekModuleAsString()
                "kimi" -> "不支持Kimi模型"
                else -> "未知机器人"
            }
        }))
        register("切换机器人模型", Command({ param, e ->
            if (param.size != 3) return@Command "/切换机器人模型 <${chatgptName} | $deepseekName> <模型>"
            val model = param[2]

            return@Command when (param[1]) {
                chatgptName -> {
                    setModel(chatgpt, model)
                    "设置${chatgptName}模型成功！"
                }

                deepseekName -> {
                    setModel(deepseek, model)
                    "设置${deepseekName}模型成功！"
                }

                "kimi" -> "不支持Kimi模型"
                else -> "未知机器人"
            }

        }, level = 2))
        register("查看机器人API余额", Command({ param, event ->
            if (param.size != 2) return@Command "查看机器人API余额 <${chatgptName} | ${deepseekName}>"
            return@Command when (param[1]) {
                chatgptName -> getChatgptBalance(chatgpt)
                deepseekName -> getDeepseekBalance(deepseek)
                else -> "未知的机器人"
            }
        }))
        register("重置$chatgptName", Command({ p, e ->
            chatgpt.reset()
            "${chatgptName}已经重置好了哦"
        }, level = 2))
        register("重置$deepseekName", Command({ p, e ->
            deepseek.reset()
            "${deepseekName}已经重置好啦"
        }, level = 2))
        register("重置kimi", Command({ p, e ->
            kimi.reset()
            "Kimi已经重置好了惹"
        }, level = 2))
        register("重置所有", Command({ p, e ->
            chatgpt.reset()
            deepseek.reset()
            kimi.reset()
            "${chatgptName}、${deepseekName}、Kimi都重置好啦"
        }, level = 3))
        register("获取机器人聊天记录", Command({ param, event ->
            if (param.size != 2) return@Command "获取机器人聊天记录 <${chatgptName} | ${deepseekName}>"
            when (param[1]) {
                chatgptName -> uploadChatHistory(chatgpt, event)
                deepseekName -> uploadChatHistory(deepseek, event)
                else -> return@Command "未知的机器人"
            }
            "已发送聊天记录"
        }, level = 1))
        register("新设定", Command({ param, e ->
            if (param.size < 4) return@Command "新设定 <chatgpt | deepseek> <机器人名称> <setting>"
            val botName = param[2]
            val setting = StringBuilder().apply {
                for (i in 3..param.size - 1) append(param[i])
            }
            return@Command when (param[1]) {
                "chatgpt" -> {
                    customized = CustomizedBot(ChatGPT().customize(setting.toString()) as AbstractBot, botName)
                    "新chatgpt机器人设定成功"
                }

                "deepseek" -> {
                    customized = CustomizedBot(Deepseek().customize(setting.toString()) as AbstractBot, botName)
                    "新deepseek机器人设定成功"
                }

                else -> {
                    "未知的机器人架构"
                }
            }
        }, level = 1))
        register("查看群", Command({ p, event ->
            val sb = StringBuilder()
            config.groups.forEach { it ->
                sb.append(it).append(" ").append(event.bot.getGroup(it)?.name).append("\n")
            }
            sb.removeSuffix("\n").toString()
        }))
        register("添加群", Command({ param, e ->
            if (param.size == 1) return@Command "添加群 <群号> [群号..]"
            for (i in 1..param.size - 1) {
                try {
                    config.groups = config.groups.plus(param[i].toLong())
                } catch (e: NumberFormatException) {
                    return@Command "无法转换的群号${param[i]}：${e.message}"
                }
            }
            configObject.store()
            "添加群成功"
        }, level = 2))
        register("删除群", Command({ param, e ->
            if (param.size == 1) return@Command "删除群 <群号> [群号..]"
            var filtered: List<Long> = listOf()
            for (i in 1..param.size - 1) {
                try {
                    filtered = config.groups.filter { it != param[i].toLong() }
                    config.groups = filtered.toLongArray()
                } catch (e: NumberFormatException) {
                    return@Command "无法转换的群号${param[i]}：${e.message}"
                }
            }
            configObject.store()
            "删除群成功"
        }, level = 3))
        register("开启流", Command({ param, e ->
            if (param.size == 1) return@Command "开启流 <${chatgptName} | ${deepseekName}>"
            return@Command when (param[1]) {
                config.chatgpt.name -> {
                    chatgpt.requestBody.stream = true
                    "${config.chatgpt.name}开启成功"
                }

                config.deepseek.name -> {
                    deepseek.requestBody.stream = true
                    "${config.deepseek.name}开启成功"
                }

                else -> "无法开启的机器人"
            }
        }, level = 1))
        register("关闭流", Command({ param, e ->
            if (param.size == 1) return@Command "关闭流 <${chatgptName} | ${deepseekName}>"
            return@Command when (param[1]) {
                config.chatgpt.name -> {
                    chatgpt.requestBody.stream = false
                    "${config.chatgpt.name}关闭成功"
                }

                config.deepseek.name -> {
                    deepseek.requestBody.stream = false
                    "${config.deepseek.name}关闭成功"
                }

                else -> "无法关闭的机器人"
            }
        }, level = 1))
        register("开启沉浸式对话", Command({ p, e ->
            "0"
        }))
        register("关闭沉浸式对话", Command({ p, e ->
            "0"
        }))
        register("设置命令前缀", Command({ param, e ->
            if (param.size != 2) return@Command "设置命令前缀 <命令前缀>"
            config.setting.commandPrefix = param[1]
            "设置${param[1]}前缀成功"
        }, level = 3))
        register("重载配置文件", Command({ p, e ->
            FeiFeiBot.reloadConfigFile()
            "重载配置文件成功"
        }, level = 4))
    }

    fun register(name: String, command: Command) {
        commands.add(name to command)
        commandsMap.putAll(commands.toMap())
    }
}