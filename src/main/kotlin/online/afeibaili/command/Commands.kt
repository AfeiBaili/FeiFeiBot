package online.afeibaili.command

import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.contact.NormalMember
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.QuoteReply
import online.afeibaili.*
import online.afeibaili.BotManager.getChatGPTBot
import online.afeibaili.BotManager.getDeepseekBot
import online.afeibaili.BotManager.getKimiBot
import online.afeibaili.BotManager.getQwenBot
import online.afeibaili.BotManager.getRobotsOrCreate
import online.afeibaili.bot.*
import online.afeibaili.bot.json.ImageResponse
import online.afeibaili.file.AdminManagerJsonFile
import online.afeibaili.file.AdminManagerJsonFile.Companion.adminManager
import online.afeibaili.file.KeyWordDataJsonFile
import online.afeibaili.module.todo.LocalDateTimeSerializer
import online.afeibaili.module.todo.Todo
import online.afeibaili.module.todo.TodoManager
import online.afeibaili.util.TimeParser
import online.afeibaili.util.TimeParser.toDateTimeString
import java.time.LocalDateTime

object Commands {
    val cc get() = CommandRegistry.commandCollection

    fun load() {

    }

    fun getId(event: MessageEvent): Long = event.subject.id

    fun getIsFriend(event: MessageEvent): Boolean {
        return when (event) {
            is GroupMessageEvent -> false
            is FriendMessageEvent -> true
            else -> throw RuntimeException("未知的消息时间：$event")
        }
    }

    fun chatgpt(event: MessageEvent) = getChatGPTBot(getIsFriend(event), getId(event))
    fun deepseek(event: MessageEvent) = getDeepseekBot(getIsFriend(event), getId(event))
    fun qwen(event: MessageEvent) = getQwenBot(getIsFriend(event), getId(event))
    fun kimi(event: MessageEvent) = getKimiBot(getIsFriend(event), getId(event))
    fun robots(event: MessageEvent) = getRobotsOrCreate(getIsFriend(event), getId(event))

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
            }, CommandCollection.create(Command("当前模型", "current", 0, ParamType.NOTHING) { _, e ->
                val currentBotName: String = robots(e).currentBotName

                val model = when (currentBotName) {
                    "chatgpt" -> getModel(chatgpt(e))
                    "deepseek" -> getModel(deepseek(e))
                    "kimi" -> getModel(kimi(e))
                    "qwen" -> getModel(qwen(e))
                    else -> return@Command "未知机器人：$currentBotName"
                }
                "当前${currentBotName}模型为：${model}"
            }, Command("全部", "all") { _, e ->
                val currentBotName: String = robots(e).currentBotName

                return@Command when (currentBotName) {
                    "chatgpt" -> getChatGPTModuleAsString()
                    "deepseek" -> getDeepseekModuleAsString()
                    else -> "不支持的机器人：$currentBotName"
                }
            }, Command("切换", "switch", 2, ParamType.STRING) { p, e ->
                val model: String = runCatching {
                    p[0]
                }.getOrElse {
                    return@Command "请填入模型"
                }
                return@Command when (model) {
                    "chatgpt" -> {
                        setModel(chatgpt(e), model)
                        "设置chatgpt模型成功！"
                    }

                    "deepseek" -> {
                        setModel(deepseek(e), model)
                        "设置deepseek模型成功！"
                    }

                    else -> "不支持的机器人：$model"
                }
            }, Command("余额", "balance") { _, e ->
                val currentBotName: String = robots(e).currentBotName
                return@Command when (currentBotName) {
                    "chatgpt" -> getChatgptBalance(chatgpt(e))
                    "deepseek" -> getDeepseekBalance(deepseek(e))
                    else -> "不支持的机器人：$currentBotName"
                }
            })
        ),
    )

    val chat = CommandRegistry.registerWithChild(
        "聊天", "chat", 3, ParamType.NOTHING, { _, _ ->
            BotManager.isBotAlive = !BotManager.isBotAlive
            "已设置当前聊天为：${if (BotManager.isBotAlive) "开启状态" else "关闭状态"}"
        }, Command("开启", "open", 3, ParamType.NOTHING) { _, _ ->
            BotManager.isBotAlive = true
            "已开启聊天"
        }, Command("关闭", "close", 3, ParamType.NOTHING) { _, _ ->
            BotManager.isBotAlive = false
            "已关闭聊天"
        }, Command(
            "重置模型", "reset", 2, ParamType.NOTHING, CommandCollection.create(
                Command("所有", "all", 2, ParamType.NOTHING) { _, e ->
                    val robots: Robots = robots(e)
                    robots.chatgpt.reset()
                    robots.deepseek.reset()
                    robots.kimi.reset()
                    robots.qwen.reset()
                    "所有模型都重置好了~"
                })
        ) { _, e ->
            val robots: Robots = robots(e)
            val currentBotName: String = robots.currentBotName

            when (currentBotName) {
                "chatgpt" -> robots.chatgpt.reset()
                "deepseek" -> robots.deepseek.reset()
                "kimi" -> robots.kimi.reset()
                "qwen" -> robots.qwen.reset()
                else -> return@Command "不支持的机器人：${currentBotName}"
            }
            return@Command "已重置${currentBotName}机器人"
        }, Command("获取记录", "history") { _, e ->
            return@Command runCatching {
                val currentBotName: String = robots(e).currentBotName

                when (currentBotName) {
                    "chatgpt" -> uploadChatHistory(chatgpt(e), e)
                    "deepseek" -> uploadChatHistory(deepseek(e), e)
                    "kimi" -> uploadChatHistory(kimi(e), e)
                    "qwen" -> uploadChatHistory(qwen(e), e)
                    else -> return@Command "不支持的机器人：${currentBotName}"
                }
                "已发送${currentBotName}聊天记录"
            }.getOrElse { "获取聊天记录不可用，可能是信息太多" }
        }, Command("切换模型", "switch", 1, ParamType.STRING) { p, e ->
            val model: String = runCatching {
                p[0]
            }.getOrElse { return@Command "请填入模型<chatgpt | deepseek | qwen | kimi>" }
            val robots: Robots = robots(e)

            when (model) {
                "chatgpt" -> robots.currentBotName = "chatgpt"
                "deepseek" -> robots.currentBotName = "deepseek"
                "kimi" -> robots.currentBotName = "kimi"
                "qwen" -> robots.currentBotName = "qwen"
                else -> return@Command "未知模型，请填入模型<chatgpt | deepseek | qwen | kimi>"
            }
            "已切换模型${model}"
        }, Command("当前模型", "current") { _, e ->
            "当前模型为：${robots(e).currentBotName}"
        }, Command(
            "新设定", "new-set", 0, ParamType.Multiple(listOf(ParamType.STRING, ParamType.STRING, ParamType.STRING))
        ) { p, e ->
            val triple: Triple<String, String, String> = runCatching {
                Triple(p[0], p[1], p[2])
            }.getOrElse {
                return@Command "请使用“机器人、称呼、设定”三个参数: <chatgpt | deepseek | qwen> <name> <setting>"
            }
            robots(e).customized = when (triple.first) {
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
            "流", "stream", 1, ParamType.NOTHING, { _, e ->
                val currentBotName: String = robots(e).currentBotName

                return@Command when (currentBotName) {
                    "chatgpt" -> {
                        chatgpt(e).requestBody.stream = !chatgpt(e).requestBody.stream
                        "当前chatgpt流为：${if (chatgpt(e).requestBody.stream) "开启状态" else "关闭状态"}"
                    }

                    "deepseek" -> {
                        deepseek(e).requestBody.stream = !deepseek(e).requestBody.stream
                        "当前deepseek流为：${if (deepseek(e).requestBody.stream) "开启状态" else "关闭状态"}"
                    }

                    else -> {
                        "不支持的机器人：$currentBotName"
                    }
                }
            }, CommandCollection.create(
                Command("开启", "open", 1, ParamType.STRING) { p, e ->
                    val botName: String = runCatching {
                        p[0]
                    }.getOrElse {
                        return@Command "请填入值：<chatgpt | deepseek>"
                    }
                    when (botName) {
                        "chatgpt" -> chatgpt(e).requestBody.stream = true
                        "deepseek" -> deepseek(e).requestBody.stream = true
                        else -> return@Command "不支持的机器人：$botName"
                    }
                    "已开启${botName}流"
                },
                Command("关闭", "close", 1, ParamType.STRING) { p, e ->
                    val botName: String = runCatching {
                        p[0]
                    }.getOrElse {
                        return@Command "请填入值：<chatgpt | deepseek>"
                    }
                    when (botName) {
                        "chatgpt" -> chatgpt(e).requestBody.stream = false
                        "deepseek" -> deepseek(e).requestBody.stream = false
                        else -> return@Command "不支持的机器人：$botName"
                    }
                    "已关闭${botName}流"
                },
            )
        ), Command(
            "沉浸式", "immersive", 0, ParamType.NOTHING, { _, e ->
                val qq = e.sender.id
                val bots = listOf("chatgpt", "deepseek", "qwen")
                val currentBotName: String = robots(e).currentBotName

                return@Command if (BotManager.immersiveMap.containsKey(qq)) {
                    BotManager.immersiveMap.remove(qq)
                    "${e.sender.nick}已关闭沉浸式对话"
                } else {
                    if (currentBotName in bots) {
                        BotManager.immersiveMap.put(qq, currentBotName)
                        "${e.sender.nick}已开启沉浸式对话"
                    } else "不支持的机器人：${currentBotName}"
                }
            }, CommandCollection.create(
                Command("开启", "open") { _, e ->
                    val currentBotName: String = robots(e).currentBotName

                    val qq = e.sender.id
                    if (BotManager.immersiveMap.containsKey(qq)) {
                        return@Command "${e.sender.nick}已经是沉浸式对话了"
                    }
                    when (currentBotName) {
                        "chatgpt" -> BotManager.immersiveMap.put(qq, "chatgpt")
                        "deepseek" -> BotManager.immersiveMap.put(qq, "deepseek")
                        "qwen" -> BotManager.immersiveMap.put(qq, "qwen")
                        else -> return@Command "不支持的机器人"
                    }

                    "${e.sender.nick}开启了沉浸式对话"
                },
                Command("关闭", "close") { _, e ->
                    val qq = e.sender.id
                    if (!BotManager.immersiveMap.containsKey(qq)) return@Command "${e.sender.nick}并不在沉浸式列表"
                    BotManager.immersiveMap.remove(qq)
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
        "群聊", "group", 0, ParamType.NOTHING, { p, e ->
            childCommand!!["look"]!!.action.invoke(this, p, e)
        }, Command("查看可用", "look") { _, e ->
            buildString {
                config.groups.forEach { appendLine("$it ${e.bot.getGroup(it)?.name}") }
            }.removeSuffix("\n")
        }, Command("添加", "add", 1, ParamType.LONG) { p, _ ->
            val groupId: Long = runCatching {
                p[0].toLong()
            }.getOrElse {
                return@Command "请传入或检查群聊的参数"
            }
            if (config.groups.contains(groupId)) return@Command "已包含此群聊"
            else config.groups = config.groups.plus(groupId)
            configObject.store()
            "执行添加群成功"
        }, Command("删除", "delete", 1, ParamType.LONG) { p, _ ->
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
        }, Command("禁言", "ban-speak", 3, ParamType.Multiple(listOf(ParamType.AT, ParamType.INT))) { p, e ->
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
        }, Command("解除禁言", "lift-ban", 3, ParamType.AT) { _, e ->
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
        }, Command("踢出", "kick-out", 4, ParamType.AT) { _, e ->
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
        }, Command("启用此群", "enable", 3, ParamType.NOTHING) { _, e ->
            if (e !is GroupMessageEvent) return@Command "不在群聊中"

            if (config.groups.contains(e.group.id)) return@Command "已包含此群聊"
            else config.groups = config.groups.plus(e.group.id)
            configObject.store()
            "执行启用群成功"
        }, Command("禁用此群", "disable", 3, ParamType.NOTHING) { _, e ->
            if (e !is GroupMessageEvent) return@Command "不在群聊中"

            if (config.groups.contains(e.group.id)) {
                config.groups = config.groups.filter { it != e.group.id }.toLongArray()
            } else return@Command "不存在的群聊"
            configObject.store()
            "执行禁用群成功"
        }, Command("开启加退群消息", "open-join-leave-message", 2, ParamType.NOTHING) { _, _ ->
            if (config.module.isOpenJoinLeaveMessage) return@Command "当前已开启加群退群消息"
            config.module.isOpenJoinLeaveMessage = true
            "已开启加群消息"
        }, Command("关闭加退群消息", "close-join-leave-message", 2, ParamType.NOTHING) { _, _ ->
            if (!config.module.isOpenJoinLeaveMessage) return@Command "当前已关闭加群退群消息"
            config.module.isOpenJoinLeaveMessage = false
            "已关闭加群消息"
        }, Command("设置头衔", "set-card", 0, ParamType.STRING) { p, e ->
            val text: String = runCatching {
                val string: String = p[0]
                if (string == "null") return@runCatching ""
                string
            }.getOrElse { return@Command "如果为值为null将清空头衔" }

            if (e !is GroupMessageEvent) return@Command "只支持群聊使用"
            val sender: Member = e.sender
            val senderId: Long = e.sender.id
            val member: NormalMember? = e.group.members.find { it.id == senderId }
            member ?: return@Command "找不到该成员：${senderId}"
            runCatching {
                member.specialTitle = text
            }.getOrElse {
                return@Command "机器人权限不足"
            }

            return@Command if (text == "") "成功将${sender.nick}的头衔清空"
            else "成功将${sender.nick}的头衔设置为${text}"
        }, Command("跟踪聊天", "track-chat", 0, ParamType.NOTHING, { p, e ->
            config.module.isPutChat = !config.module.isPutChat
            configObject.store()
            if (config.module.isPutChat) "当前跟踪聊天为开启状态" else "当前跟踪聊天为关闭状态"
        }, CommandCollection.create(Command("开启", "open") { p, e ->
            if (config.module.isPutChat) return@Command "当前已经是开启状态"
            config.module.isPutChat = true
            configObject.store()
            "已开启跟踪聊天"
        }, Command("关闭", "close") { p, e ->
            if (!config.module.isPutChat) return@Command "当前已经是关闭状态"
            config.module.isPutChat = false
            configObject.store()
            "已关闭跟踪聊天"
        })), Command(
            "关键提醒", "keyword", 0, ParamType.NOTHING, { p, e ->
                "使用添加命令添加一个关键字，群里触发关键字时将At本人"
            }, CommandCollection.create(
                Command("添加", "add", 0, ParamType.STRING) { p, e ->
                    val keyword: String = runCatching {
                        p[0]
                    }.getOrElse { return@Command "请输入关键字" }
                    val id: Long = e.sender.id
                    KeyWordDataJsonFile.keywords.add(keyword, id)
                    KeyWordDataJsonFile.store()

                    "${e.sender.nick}已添加关键字: $keyword"
                },
                Command("删除", "delete", 0, ParamType.STRING) { p, e ->
                    val keyword: String = runCatching {
                        p[0]
                    }.getOrElse { return@Command "请输入关键字" }
                    val id: Long = e.sender.id
                    val isRemoved: Boolean = KeyWordDataJsonFile.keywords.remove(keyword, id)
                    KeyWordDataJsonFile.store()
                    return@Command if (isRemoved) "${e.sender.nick}已删除关键字: $keyword"
                    else "删除失败，可能删除了不存在的关键字"

                },
                Command("清空", "clear", 2, ParamType.NOTHING) { p, e ->
                    KeyWordDataJsonFile.keywords.clear()
                    KeyWordDataJsonFile.store()
                    "已删除所有用户的关键字"
                },
            )
        ), Command(
            "管理员", "admin", 0, ParamType.NOTHING, { p, e ->
                "请使用see命令"
            }, CommandCollection.create(
                Command("给予", "give", 0, ParamType.STRING) { p, e ->
                    if (e !is GroupMessageEvent) return@Command "请在群聊中使用"
                    //获取格式化参数
                    val dateTimeString: String = runCatching {
                        p[0]
                    }.getOrElse { return@Command TimeParser.toString("请添加时间格式来指定时间") }
                    //获取时间
                    val localDateTime: LocalDateTime = runCatching {
                        TimeParser.parse(dateTimeString)
                    }.getOrElse { return@Command "格式异常，无法获取时间日期。详情看：${it.message}" }
                    //判断时间是否已过
                    if (localDateTime.isBefore(LocalDateTime.now()))
                        return@Command "当前时间[${localDateTime.toDateTimeString()}]已过，请换时间"
                    //判断时间是否大于30天
                    val maxDay = 30L
                    if (localDateTime.isAfter(LocalDateTime.now().plusDays(maxDay)))
                        return@Command "当前时间大于${maxDay}天，管理员时间不可超过${maxDay}天"
                    //是否在白名单
                    val bool: Boolean = adminManager.createAdmin(
                        e.group.id, e.sender.id, localDateTime
                    )
                    if (!bool) return@Command "不在管理员白名单中"
                    val member: NormalMember? = e.group.members[e.sender.id]
                    if (member == null) return@Command "找不到成员${e.sender.id}"
                    //成功修改
                    member.modifyAdmin(true)
                    AdminManagerJsonFile.store()
                    "已给予${member.nick}管理员，到期时间为：${localDateTime.toDateTimeString()}"
                }, Command("白名单", "whitelist", 0, ParamType.NOTHING, { p, e ->
                    if (e !is GroupMessageEvent) return@Command "请在群聊中使用"
                    val list: List<Long>? = adminManager.getList(e.group.id)
                    if (list == null || list.isEmpty()) return@Command "此群暂无白名单"
                    return@Command "此群白名单人员\n" + list.joinToString("\n") { it ->
                        "${e.group.members[it]?.nick}(@$it)"
                    }
                }, CommandCollection.create(Command("添加", "add", 4, ParamType.AT) { p, e ->
                    if (e !is GroupMessageEvent) return@Command "请在群聊中使用"
                    val at: At = runCatching { getNoAt(1, e) }.getOrElse { return@Command "请输入@QQ参数" }
                    adminManager.addWhiteList(e.group.id, at.target)
                    AdminManagerJsonFile.store()
                    "已添加${e.group.members[at.target]?.nick}的白名单"
                }, Command("删除", "delete", 4, ParamType.AT) { p, e ->
                    if (e !is GroupMessageEvent) return@Command "请在群聊中使用"
                    val at: At = runCatching { getNoAt(1, e) }.getOrElse { return@Command "请输入@QQ参数" }
                    adminManager.deleteWhiteList(e.group.id, at.target)
                    AdminManagerJsonFile.store()
                    val member: NormalMember? = e.group.members[at.target]
                    member?.modifyAdmin(false)
                    "已删除${e.group.members[at.target]?.nick}的白名单"
                }))
            )
        )
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
            val robots: Robots = robots(e)

            runCatching {
                val image: ImageResponse.Image = robots.kolors.send(text).images[0]
                downloadAndSendImage(image.url, e)
            }.onFailure { return@Command "无法获取图片，可能是服务器出错" }
            "已生成图片"
        },
        Command("千问", "qwen", 1, ParamType.STRING) { p, e ->
            val text: String = p.joinToString(" ")
            val url: String = qwen(e).sendGenerateImageRequest(text)
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
            val url: String = qwen(e).sendGenerateImageRequest(
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
                $TimeParser
            """.trimIndent()
            }

            var localDateTime = if (TimeParser.parseIsInfinite(time)) null
            else runCatching {
                TimeParser.parse(time)
            }.getOrElse { return@Command "解析时间失败：${it.message}" }

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