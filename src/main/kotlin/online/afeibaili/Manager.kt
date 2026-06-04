package online.afeibaili

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.events.MemberJoinEvent
import net.mamoe.mirai.event.events.MemberLeaveEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.event.events.NudgeEvent
import net.mamoe.mirai.message.data.*
import online.afeibaili.bot.ChatGPT
import online.afeibaili.bot.Deepseek
import online.afeibaili.bot.Kimi
import online.afeibaili.bot.Qwen
import online.afeibaili.bot.Robots.chatgpt
import online.afeibaili.bot.Robots.currentBot
import online.afeibaili.bot.Robots.customized
import online.afeibaili.bot.Robots.deepseek
import online.afeibaili.bot.Robots.kimi
import online.afeibaili.bot.Robots.qwen
import online.afeibaili.command.Command
import online.afeibaili.command.CommandRegistry

object Manager {
    var isBotAlive = true
    var isPutChat = true
    val immersiveMap = HashMap<Long, String>()
    val messageScope = CoroutineScope(Dispatchers.Default)

    fun processMessage(event: MessageEvent, isFriend: Boolean = false) = messageScope.launch {
        val message = event.message.contentToString()
        val contact: Contact = event.subject

        if (message.startsWith(config.setting.commandPrefix)) {
            commandParsing(event)?.let {
                contact.sendMessage(it)
            }
        } else if (isBotAlive) botProcess(event, isFriend)
    }

    internal suspend fun commandParsing(event: MessageEvent, isFilter: Boolean = false): String? {
        val message = if (isFilter) event.message.contentToString().removePrefix("@" + config.setting.commandPrefix)
        else event.message.contentToString().removePrefix(config.setting.commandPrefix)
        val split: List<String> = message.split("\\s+".toRegex())
        val level: Int? = levelMap[event.sender.id]
        val command: Command? = CommandRegistry.commandCollection[split[0]]

        var params: Array<String> = split.filter { it.isNotEmpty() }.toTypedArray()
        suspend fun processCommand(command: Command?): String? {
            params = params.drop(1).toTypedArray()
            if (command == null) {
                if (config.setting.printNotFoundCommand) return "找不到命令~"
                return null
            }

            return if (command.childCommand == null) {
                if (command.level <= (level ?: 0)) command.action(command, params, event)
                else "您的等级是${level ?: "0"}，但是此指令等级为${command.level}级！"
            } else {
                val commandName: String = params.getOrElse(0) {
                    return if (command.level <= (level ?: 0)) command.action(command, params, event)
                    else "您的等级是${level ?: "0"}，但是此指令等级为${command.level}级！"
                }
                processCommand(command.childCommand[commandName])
            }
        }

        return processCommand(command)
    }

    ////  Message Process  ////////////////////////////////////////////////////

    suspend fun sendMessageToContact(contact: Contact, message: MessageChain): Unit = when (currentBot) {
        is Deepseek -> {
            val deepseek: Deepseek = currentBot as Deepseek
            sendByDeepseek(contact, deepseek, message, null)
        }

        is ChatGPT -> {
            val chatgpt: ChatGPT = currentBot as ChatGPT
            sendByChatGPT(contact, chatgpt, message, null)
        }

        is Qwen -> {
            val qwen: Qwen = currentBot as Qwen
            sendByQwen(contact, qwen, message, null)
        }

        else -> {}
    }


    suspend fun processJoinGroup(event: MemberJoinEvent) {
        if (!config.module.isOpenJoinLeaveMessage) return

        val contact: Contact = event.group
        val message: MessageChain = PlainText(event.user.nick + "加入了群聊").toMessageChain()
        sendMessageToContact(contact, message)
    }

    suspend fun processLeaveGroup(event: MemberLeaveEvent) {
        if (!config.module.isOpenJoinLeaveMessage) return

        sendMessageToContact(event.group, PlainText(event.user.nick + "离开了群聊").toMessageChain())
    }


    suspend fun processNudge(event: NudgeEvent) {
        val contact: Contact = event.subject
        val message: MessageChain = PlainText(event.from.nick + "戳了戳你的脸").toMessageChain()
        if (event.target.id != bot.id) return
        sendMessageToContact(contact, message)
    }

    private suspend fun botProcess(event: MessageEvent, isFriend: Boolean) {
        val singleMessages: MessageChain = event.message
        val contact: Contact = event.subject
        val senderName: String = event.sender.remark.ifEmpty {
            event.sender.nick
        }

        for (singleMessage in singleMessages) {
            if ((singleMessage is At && singleMessage.target == bot.id) || isFriend) {
                when (currentBot) {
                    is Deepseek -> {
                        val deepseek: Deepseek = currentBot as Deepseek
                        sendByDeepseek(contact, deepseek, event.message, senderName)
                        return
                    }

                    is ChatGPT -> {
                        val chatgpt: ChatGPT = currentBot as ChatGPT
                        sendByChatGPT(contact, chatgpt, event.message, senderName)
                        return
                    }

                    is Qwen -> {
                        val qwen: Qwen = currentBot as Qwen
                        sendByQwen(contact, qwen, event.message, senderName)
                        return
                    }

                    else -> {}
                }
            }
        }

        val message: String =
            MessageProcessor(event.message).start().filterAtMessage().addSenderName(senderName).endAndToString()

        with(message) {
            try {
                when {
                    immersiveMap.contains(event.sender.id) -> when (immersiveMap[event.sender.id]) {
                        "deepseek" -> sendByDeepseek(contact, deepseek, event.message, senderName)
                        "chatgpt" -> sendByChatGPT(contact, chatgpt, event.message, senderName)
                        "qwen" -> sendByQwen(contact, qwen, event.message, senderName)
                        else -> {}
                    }

                    contains(config.bot.name) -> {
                        when (currentBot) {
                            is Deepseek -> sendByDeepseek(contact, deepseek, event.message, senderName)
                            is ChatGPT -> sendByChatGPT(contact, chatgpt, event.message, senderName)
                            is Qwen -> sendByQwen(contact, qwen, event.message, senderName)
                            is Kimi -> contact.sendMessage(kimi.send(message))
                            else -> {}
                        }
                    }

                    customized != null && contains(customized!!.name) -> contact.sendMessage(
                        customized!!.bot.send(
                            message
                        )
                    )

                    else -> {
                        if (isPutChat) currentBot.putChat(message)
                        else {}
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                contact.sendMessage("消息异常：${contact.sendMessage(e.message!!)}")
            }
        }
    }

    class MessageProcessor(val messageChain: MessageChain) {
        class Start(var messageChain: MessageChain) {
            fun filterAtMessage(): Start {
                val messageChainBuilder = MessageChainBuilder()
                messageChain.forEach { message ->
                    if (message !is At) messageChainBuilder.add(message)
                }
                messageChain = messageChainBuilder.build()
                return this
            }

            fun addSenderName(name: String?, infix: String = "："): Start {
                name?.let {
                    messageChain = MessageChainBuilder().apply {
                        +(name + infix)
                        +messageChain
                    }.build()
                }

                return this
            }

            fun end() = messageChain
            fun endAndToString() = messageChain.contentToString()
        }

        fun start() = Start(messageChain)
    }


    private suspend fun sendByChatGPT(
        contact: Contact,
        chatgpt: ChatGPT,
        message: MessageChain,
        senderName: String?,
    ) {
        val message: String =
            MessageProcessor(message).start().filterAtMessage().addSenderName(senderName).end().contentToString()
        if (chatgpt.getStream()) chatgpt.sendAsStream(message, contact)
        else contact.sendMessage(chatgpt.send(message))
    }

    private suspend fun sendByQwen(
        contact: Contact,
        qwen: Qwen,
        message: MessageChain,
        senderName: String?,
    ) {
        val message: String =
            MessageProcessor(message).start().filterAtMessage().addSenderName(senderName).end().contentToString()
        contact.sendMessage(qwen.send(message))
    }

    private suspend fun sendByDeepseek(
        contact: Contact,
        deepseek: Deepseek,
        message: MessageChain,
        senderName: String?,
    ) {
        val message: String =
            MessageProcessor(message).start().filterAtMessage().addSenderName(senderName).end().contentToString()
        if (deepseek.getStream()) deepseek.sendAsStream(message, contact)
        else contact.sendMessage(deepseek.send(message))
    }
}