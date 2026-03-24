package online.afeibaili.command

import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.QuoteReply
import online.afeibaili.*
import online.afeibaili.bot.*
import online.afeibaili.bot.Robots.chatgpt
import online.afeibaili.bot.Robots.customized
import online.afeibaili.bot.Robots.deepseek
import online.afeibaili.bot.Robots.kimi
import online.afeibaili.bot.Robots.kolors
import online.afeibaili.bot.Robots.qwen
import online.afeibaili.bot.json.ImageResponse
import online.afeibaili.module.todo.LocalDateTimeSerializer
import online.afeibaili.module.todo.Todo
import online.afeibaili.module.todo.TodoManager
import java.time.LocalDateTime
import java.time.LocalTime

object Commands {
    val cc get() = CommandRegistry.commandCollection

    fun load() {

    }

    val mune: Command = CommandRegistry.registerWithChild("菜单", "help", 0, ParamType.NOTHING, { _, _ ->
        buildString {
            cc.forEach { it ->
                appendLine(it)
            }
            appendLine()
            append("查看详细参数和子命令请使用 看 <命令>")
        }
    }, Command("详细", "detail") { _, _ ->
        buildString {
            cc.forEach { it ->
                append(it.toDetailString())
            }
        }.removeSuffix("\n").removePrefix("\n")
    }, Command("平展", "flat") { _, _ ->
        val commandPrefix = config.setting.commandPrefix

        fun getChildName(command: Command, prefix: String = ""): String {
            val prefix = "$prefix${command.name}"
            return if (command.childCommand == null) buildString {
                appendLine(prefix)
            } else {
                return buildString {
                    appendLine(prefix)
                    command.childCommand.forEach { append(getChildName(it, "$prefix ")) }
                }
            }
        }

        buildString {
            cc.forEach { it ->
                append(getChildName(it, "$commandPrefix"))
            }
        }.removeSuffix("\n")
    })

    val see: Command = CommandRegistry.register("看", "see", ParamType.STRING) { p, _ ->
        if (p.isEmpty()) return@register "请使用 <看 | see> <命令>\n  示例1：/看 菜单\n  示例2：/see Bot聊天"
        val command: Command? = cc[p[0]]
        command ?: return@register "未知的命令：${p[0]}"

        buildString {
            append(command.toDetailString())
        }.removeSuffix("\n").removePrefix("\n")
    }

    val setLevel = CommandRegistry.registerWithChild("命令等级", "level", 0, ParamType.NOTHING, { p, e ->
        childCommand!!["look"]!!.action.invoke(this, p, e)
    }, Command("查看", "look", 0, ParamType.NOTHING) { _, _ ->
        buildString {
            levelMap.forEach {
                append(it.component1()).append(" （").append(bot.getFriend(it.component1())?.nick ?: "未知好友")
                    .append("）").append(" 为 [").append(it.component2()).append("] 级").append("\n")
            }
        }
    }, Command("设置", "set", 4, ParamType.Multiple(listOf(ParamType.AT, ParamType.INT))) { p, e ->
        if (p.size != 2) return@Command "请使用“看”命令查看详细指令\n  示例1：/看 菜单\n  示例2：/see Bot聊天"
        val level: Int = runCatching {
            p[1].toInt()
        }.getOrElse {
            return@Command "无法将等级解析为数字"
        }
        runCatching {
            levelObject.setLevelMap(getNoAt(1, e).target, level)
        }.onFailure {
            return@Command "请使用@QQ"
        }
        "设置等级成功"
    })

    val botCommand = CommandRegistry.registerWithChild(
        "Bot", "bot", 0, ParamType.NOTHING,
        { p, e ->
            childCommand!!["model"]!!.childCommand!!["current"]!!.action.invoke(this, p, e)
        },
        //子命令
        Command(
            "内核模型", "model", 0, ParamType.NOTHING, { p, e ->
                childCommand!!["current"]!!.action.invoke(this, p, e)
            }, CommandCollection.create(Command("当前模型", "current", 0, ParamType.NOTHING) { _, _ ->
                val model = when (config.setting.currentBot) {
                    "chatgpt" -> getModel(chatgpt)
                    "deepseek" -> getModel(deepseek)
                    "kimi" -> getModel(kimi)
                    "qwen" -> getModel(qwen)
                    else -> return@Command "未知机器人：${config.setting.currentBot}"
                }
                "当前${config.setting.currentBot}模型为：${model}"
            }, Command("全部", "all") { _, _ ->
                return@Command when (config.setting.currentBot) {
                    "chatgpt" -> getChatGPTModuleAsString()
                    "deepseek" -> getDeepseekModuleAsString()
                    else -> "不支持的机器人：${config.setting.currentBot}"
                }
            }, Command("切换", "switch", 2, ParamType.STRING) { p, _ ->
                val model: String = runCatching {
                    p[0]
                }.getOrElse {
                    return@Command "请填入模型"
                }

                return@Command when (config.setting.currentBot) {
                    "chatgpt" -> {
                        setModel(chatgpt, model)
                        "设置chatgpt模型成功！"
                    }

                    "deepseek" -> {
                        setModel(deepseek, model)
                        "设置deepseek模型成功！"
                    }

                    else -> "不支持的机器人：${config.setting.currentBot}"
                }
            }, Command("余额", "balance") { _, _ ->
                return@Command when (config.setting.currentBot) {
                    "chatgpt" -> getChatgptBalance(chatgpt)
                    "deepseek" -> getDeepseekBalance(deepseek)
                    else -> "不支持的机器人：${config.setting.currentBot}"
                }
            })
        ),
    )

    val chat = CommandRegistry.registerWithChild(
        "聊天", "chat", 1, ParamType.NOTHING, { _, _ ->
            Manager.isBotAlive = !Manager.isBotAlive
            "已设置当前聊天为：${if (Manager.isBotAlive) "开启状态" else "关闭状态"}"
        }, Command("开启", "open", 1, ParamType.NOTHING) { _, _ ->
            Manager.isBotAlive = true
            "已开启聊天"
        }, Command("关闭", "close", 1, ParamType.NOTHING) { _, _ ->
            Manager.isBotAlive = false
            "已关闭聊天"
        }, Command(
            "重置模型", "reset", 2, ParamType.NOTHING, CommandCollection.create(
                Command("所有", "all", 2, ParamType.NOTHING) { _, _ ->
                    chatgpt.reset()
                    deepseek.reset()
                    kimi.reset()
                    qwen.reset()
                    "所有模型都重置好了~"
                })
        ) { _, _ ->
            when (config.setting.currentBot) {
                "chatgpt" -> chatgpt.reset()
                "deepseek" -> deepseek.reset()
                "kimi" -> kimi.reset()
                "qwen" -> qwen.reset()
                else -> return@Command "不支持的机器人：${config.setting.currentBot}"
            }
            return@Command "已重置${config.setting.currentBot}机器人"
        }, Command("获取记录", "history") { _, e ->
            when (config.setting.currentBot) {
                "chatgpt" -> uploadChatHistory(chatgpt, e)
                "deepseek" -> uploadChatHistory(deepseek, e)
                "kimi" -> uploadChatHistory(kimi, e)
                "qwen" -> uploadChatHistory(qwen, e)
                else -> return@Command "不支持的机器人：${config.setting.currentBot}"
            }
            return@Command "已发送${config.setting.currentBot}聊天记录"
        }, Command("切换模型", "switch", 1, ParamType.STRING) { p, _ ->
            val model: String = runCatching {
                p[0]
            }.getOrElse { return@Command "请填入模型<chatgpt | deepseek | qwen | kimi>" }

            when (model) {
                "chatgpt" -> config.setting.currentBot = "chatgpt"
                "deepseek" -> config.setting.currentBot = "deepseek"
                "kimi" -> config.setting.currentBot = "kimi"
                "qwen" -> config.setting.currentBot = "qwen"
                else -> return@Command "未知模型，请填入模型<chatgpt | deepseek | qwen | kimi>"
            }
            configObject.store()
            "已切换模型${model}"
        }, Command("当前模型", "current") { _, _ ->
            "当前模型为：${config.setting.currentBot}"
        }, Command(
            "新设定", "new-set", 0, ParamType.Multiple(listOf(ParamType.STRING, ParamType.STRING, ParamType.STRING))
        ) { p, _ ->
            val triple: Triple<String, String, String> = runCatching {
                Triple(p[0], p[1], p[2])
            }.getOrElse {
                return@Command "请使用“机器人、称呼、设定”三个参数: <chatgpt | deepseek | qwen> <name> <setting>"
            }
            customized = when (triple.first) {
                "chatgpt" -> CustomizedBot(ChatGPT().customize(triple.third) as AbstractBot, triple.second)

                "deepseek" -> CustomizedBot(Deepseek().customize(triple.third) as AbstractBot, triple.second)

                "qwen" -> CustomizedBot(Qwen().customize(triple.third) as AbstractBot, triple.second)

                else -> return@Command "未知或不支持的机器人"
            }

            return@Command """
                设定${triple.first}成功！
                机器人称呼：${triple.second}
            """.trimIndent()
        }, Command(
            "流", "stream", 1, ParamType.NOTHING, { _, _ ->
                return@Command when (config.setting.currentBot) {
                    "chatgpt" -> {
                        chatgpt.requestBody.stream = !chatgpt.requestBody.stream
                        "当前chatgpt流为：${if (chatgpt.requestBody.stream) "开启状态" else "关闭状态"}"
                    }

                    "deepseek" -> {
                        deepseek.requestBody.stream = !deepseek.requestBody.stream
                        "当前deepseek流为：${if (deepseek.requestBody.stream) "开启状态" else "关闭状态"}"
                    }

                    else -> {
                        "不支持的机器人：${config.setting.currentBot}"
                    }
                }
            }, CommandCollection.create(
                Command("开启", "open", 1, ParamType.STRING) { p, _ ->
                    val botName: String = runCatching {
                        p[0]
                    }.getOrElse {
                        return@Command "请填入值：<chatgpt | deepseek>"
                    }
                    when (botName) {
                        "chatgpt" -> chatgpt.requestBody.stream = true
                        "deepseek" -> deepseek.requestBody.stream = true
                        else -> return@Command "不支持的机器人：$botName"
                    }
                    "已开启${botName}流"
                },
                Command("关闭", "close", 1, ParamType.STRING) { p, _ ->
                    val botName: String = runCatching {
                        p[0]
                    }.getOrElse {
                        return@Command "请填入值：<chatgpt | deepseek>"
                    }
                    when (botName) {
                        "chatgpt" -> chatgpt.requestBody.stream = false
                        "deepseek" -> deepseek.requestBody.stream = false
                        else -> return@Command "不支持的机器人：$botName"
                    }
                    "已关闭${botName}流"
                },
            )
        ), Command(
            "沉浸式", "immersive", 0, ParamType.NOTHING, { _, e ->
                val qq = e.sender.id
                val bots = listOf("chatgpt", "deepseek", "qwen")
                return@Command if (Manager.immersiveMap.containsKey(qq)) {
                    Manager.immersiveMap.remove(qq)
                    "${e.sender.nick}已关闭沉浸式对话"
                } else {
                    if (config.setting.currentBot in bots) {
                        Manager.immersiveMap.put(qq, config.setting.currentBot)
                        "${e.sender.nick}已开启沉浸式对话"
                    } else "不支持的机器人：${config.setting.currentBot}"
                }
            }, CommandCollection.create(
                Command("开启", "open") { _, e ->
                    val qq = e.sender.id
                    if (Manager.immersiveMap.containsKey(qq)) {
                        return@Command "${e.sender.nick}已经是沉浸式对话了"
                    }
                    when (config.setting.currentBot) {
                        "chatgpt" -> Manager.immersiveMap.put(qq, "chatgpt")
                        "deepseek" -> Manager.immersiveMap.put(qq, "deepseek")
                        "qwen" -> Manager.immersiveMap.put(qq, "qwen")
                        else -> return@Command "不支持的机器人"
                    }

                    "${e.sender.nick}开启了沉浸式对话"
                },
                Command("关闭", "close") { _, e ->
                    val qq = e.sender.id
                    if (!Manager.immersiveMap.containsKey(qq)) return@Command "${e.sender.nick}并不在沉浸式列表"
                    Manager.immersiveMap.remove(qq)
                    "${e.sender.nick}关闭了沉浸式对话"
                },
            )
        ), Command(
            "记录限制", "limit", 0, ParamType.NOTHING, { p, e ->
                childCommand!!["look"]!!.action(this, p, e)
            }, CommandCollection.create(
                Command("查看", "look") { _, _ ->
                    "当前聊天记录限制为${config.setting.maxChatLength}条"
                },
                Command("更改", "change", 2, ParamType.INT) { p, _ ->
                    val length: Int =
                        runCatching { p[0].toInt() }.getOrElse { return@Command "请检查或输入要更新的长度参数" }
                    config.setting.maxChatLength = length
                    "已将聊天记录限制设置为：${config.setting.maxChatLength}条"
                },
            )
        )
    )

    val group = CommandRegistry.registerWithChild(
        "群聊", "group", 0, ParamType.NOTHING,
        { p, e ->
            childCommand!!["look"]!!.action.invoke(this, p, e)
        },
        Command("查看可用", "look") { _, e ->
            buildString {
                config.groups.forEach { appendLine("$it ${e.bot.getGroup(it)?.name}") }
            }.removeSuffix("\n")
        },
        Command("添加", "add", 1, ParamType.LONG) { p, _ ->
            val groupId: Long = runCatching {
                p[0].toLong()
            }.getOrElse {
                return@Command "请传入或检查群聊的参数"
            }
            if (config.groups.contains(groupId)) return@Command "已包含此群聊"
            else config.groups = config.groups.plus(groupId)
            configObject.store()
            "执行添加群成功"
        },
        Command("删除", "delete", 1, ParamType.LONG) { p, _ ->
            val groupId: Long = runCatching {
                p[0].toLong()
            }.getOrElse {
                return@Command "请传入或检查群聊的参数"
            }

            if (config.groups.contains(groupId)) {
                config.groups = config.groups.filter { it != groupId }.toLongArray()
            } else return@Command "不存在的群聊"
            configObject.store()
            "执行删除群成功"
        },
        Command("禁言", "ban-speak", 3, ParamType.Multiple(listOf(ParamType.AT, ParamType.INT))) { p, e ->
            val at: At = runCatching { getNoAt(1, e) }.getOrElse { return@Command "请输入@QQ参数和秒数参数" }
            val seconds: Int = runCatching { p[1].toInt() }.getOrElse { 600 }

            if (e is GroupMessageEvent) {
                runCatching {
                    e.subject.members[at.target]?.run {
                        mute(seconds)
                        return@Command "已禁言${e.subject.members[at.target]?.nick}${seconds}秒"
                    }
                }.onFailure { return@Command "请检查机器人是否有权限，且时长是否正确0-30天" }
            } else return@Command "你不在群聊中"
            "群里是否包含${at.target}此人？"
        },
        Command("解除禁言", "lift-ban", 3, ParamType.AT) { _, e ->
            val at: At = runCatching { getNoAt(1, e) }.getOrElse { return@Command "请输入@QQ参数" }
            if (e is GroupMessageEvent) {
                runCatching {
                    e.subject.members[at.target]?.run {
                        unmute()
                        return@Command "已尝试解除${e.subject.members[at.target]?.nick}禁言"
                    }
                }.onFailure { return@Command "请检查机器人是否有权限" }
            } else return@Command "你不在群聊中"

            "群里是否包含${at.target}此人？"
        },
        Command("踢出", "kick-out", 4, ParamType.AT) { _, e ->
            val at: At = runCatching { getNoAt(1, e) }.getOrElse { return@Command "请输入@QQ参数" }
            if (e is GroupMessageEvent) {
                runCatching {
                    e.subject.members[at.target]?.run {
                        val name: String? = e.subject.members[at.target]?.nick
                        kick("你被踢出去了")
                        return@Command "${name}已被踢出群聊"
                    }
                }.onFailure { return@Command "请检查机器人是否有权限" }
            } else return@Command "你不在群聊中"
            "群里是否包含此人？"
        },
        Command("启用此群", "enable", 3, ParamType.NOTHING) { _, e ->
            if (e !is GroupMessageEvent) return@Command "不在群聊中"

            if (config.groups.contains(e.group.id)) return@Command "已包含此群聊"
            else config.groups = config.groups.plus(e.group.id)
            configObject.store()
            "执行启用群成功"
        },
        Command("禁用此群", "disable", 3, ParamType.NOTHING) { _, e ->
            if (e !is GroupMessageEvent) return@Command "不在群聊中"

            if (config.groups.contains(e.group.id)) {
                config.groups = config.groups.filter { it != e.group.id }.toLongArray()
            } else return@Command "不存在的群聊"
            configObject.store()
            "执行禁用群成功"
        },
    )

    val commandPrefix = CommandRegistry.register("更改命令前缀", "change-command-prefix", 3, ParamType.STRING) { p, _ ->
        val commandPrefix: String = runCatching { p[0] }.getOrElse { return@register "请传入要更新的前缀参数" }
        val tc = config.setting.commandPrefix
        config.setting.commandPrefix = commandPrefix
        configObject.store()
        "已将前缀${tc}更新为${commandPrefix}"
    }

    val reloadConfig = CommandRegistry.register("重载配置", "reload-config", 3, ParamType.NOTHING) { _, _ ->
        FeiFeiBot.reloadConfigFile()
        "已执行重载配置文件"
    }

    val generateImage = CommandRegistry.registerWithChild(
        "图片生成", "image", 0, ParamType.STRING,
        { _, _ ->
            "请使用kolors和qwen来生成图片"
        },
        Command("Kolors", "kolors", 0, ParamType.STRING) { p, e ->
            val text: String = p.joinToString(" ")
            if (text.isEmpty()) return@Command "未输入描述词"

            runCatching {
                val image: ImageResponse.Image = kolors.send(text).images[0]
                downloadAndSendImage(image.url, e)
            }.onFailure { return@Command "无法获取图片，可能是服务器出错" }
            "已生成图片"
        },
        Command("千问", "qwen", 1, ParamType.STRING) { p, e ->
            val text: String = p.joinToString(" ")
            val url: String = qwen.sendGenerateImageRequest(text)
            downloadAndSendImage(url, e)
            "已创建，提示词：$text"
        },
        Command("图生图", "image2image", 1, ParamType.STRING) { p, e ->
            val text: String = p.joinToString(" ")
            val mutableListOf = mutableListOf<String>()
            e.message.forEach { message ->
                if (message is QuoteReply) {
                    message.source.originalMessage.forEach { it ->
                        if (it is Image) mutableListOf.add(it.imageId)
                    }
                }
            }
            val url: String = qwen.sendGenerateImageRequest(
                text,
                *mutableListOf.toTypedArray(),
            )
            downloadAndSendImage(url, e)
            "提示词：$text" + "\n图片数量：${mutableListOf.size}"
        },
    )

    val task = CommandRegistry.registerWithChild(
        "TODO", "todo", 0, ParamType.NOTHING,
        { p, e ->
            childCommand!!["list"]!!.action(this, p, e)
        },
        Command("创建任务", "create", 0, ParamType.Multiple(listOf(ParamType.STRING, ParamType.STRING))) { p, e ->
            val (time, message) = runCatching { p[0] to p[1] }.getOrElse {
                return@Command """
                传入时间参数和信息参数 时间日期参数 打印消息参数
                时间日期参数格式：
                1.根据时间提醒
                +无限
                +12h
                +30m
                +10s
                2.根据日期提醒
                20:00
                20:00:10
                2005年05月16日-20:00
                2005年05月16日-20:00:00
            """.trimIndent()
            }

            val localDateTime: LocalDateTime? = if (time.startsWith("+")) when {
                time.endsWith("无限") -> {
                    null
                }

                time.endsWith("h") -> {
                    val t: String = time.removeSuffix("h").removePrefix("+")
                    val hours: Long = runCatching { t.toLong() }.getOrElse { return@Command "无法格式化小时：$t" }
                    LocalDateTime.now().plusHours(hours)
                }

                time.endsWith("m") -> {
                    val t: String = time.removeSuffix("m").removePrefix("+")
                    val minute: Long = runCatching { t.toLong() }.getOrElse { return@Command "无法格式化分钟：$t" }
                    LocalDateTime.now().plusMinutes(minute)
                }

                time.endsWith("s") -> {
                    val t: String = time.removeSuffix("s").removePrefix("+")
                    val seconds: Long = runCatching { t.toLong() }.getOrElse { return@Command "无法格式化秒：$t" }
                    LocalDateTime.now().plusSeconds(seconds)
                }

                else -> return@Command "不支持的格式：${time}"
            } else {
                var localDateTime = runCatching {
                    val localTime: LocalTime = LocalTime.from(TodoManager.formatter1.parse(time))
                    LocalDateTime.now().withHour(localTime.hour).withMinute(localTime.minute).withSecond(0)
                }.getOrElse { null }

                if (localDateTime == null) {
                    localDateTime = runCatching {
                        val localTime: LocalTime = LocalTime.from(TodoManager.formatter2.parse(time))
                        LocalDateTime.now().withHour(localTime.hour).withMinute(localTime.minute)
                            .withSecond(localTime.second)
                    }.getOrElse { null }
                }

                if (localDateTime == null) {
                    localDateTime = runCatching {
                        LocalDateTime.from(TodoManager.formatter3.parse(time))
                    }.getOrElse { null }
                }

                if (localDateTime == null) {
                    localDateTime = runCatching {
                        LocalDateTime.from(TodoManager.formatter4.parse(time))
                    }.getOrElse { null }
                }

                localDateTime ?: return@Command """
                                请检查日期是否准确，支持的日期：
                                20:00
                                20:00:10
                                2005年05月16日-20:00
                                2005年05月16日-20:00:00
                            """.trimIndent()
            }

            if (localDateTime == null) {
                TodoManager.createTodo(message, e)
                return@Command "已创建TODO：${message}"
            }

            if (localDateTime.isBefore(LocalDateTime.now())) return@Command "设置的时间已过：${
                LocalDateTimeSerializer.formatter.format(localDateTime)
            }"

            val todo: Todo = TodoManager.createTask(localDateTime, message, e)

            buildString {
                appendLine("已创建任务")
                appendLine("ID：${todo.uuid}")
                appendLine("时间：${LocalDateTimeSerializer.formatter.format(todo.dateTime)}")
                appendLine()
                appendLine("事件：${todo.message}")
            }.removeSuffix("\n")
        },
        Command("删除任务", "delete", 0, ParamType.STRING) { p, _ ->
            val uuid: String = runCatching { p[0] }.getOrElse { return@Command "请传入任务id" }
            val todo: Todo? = TodoManager.deleteTask(uuid)
            return@Command if (todo == null) "任务不存在，可能是id错误"
            else "已删除任务：\n$todo"
        },
        Command("列表", "list", 0, ParamType.NOTHING) { _, _ ->
            val text: String = buildString {
                TodoManager.map.forEach { (k, v) ->
                    append(v.toString())
                }
            }.removeSuffix("\n\n")
            if (text.isEmpty()) return@Command "当前列表为空"
            text
        },
    )
}