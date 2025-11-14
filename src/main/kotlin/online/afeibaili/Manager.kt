package online.afeibaili

import net.mamoe.mirai.contact.Contact
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

object Manager {
    var isBotAlive = true
    val immersiveMap = HashMap<Long, String>()

    suspend fun processMessage(event: MessageEvent, isFriend: Boolean = false) {
        val message = event.message.contentToString()
        val contact: Contact = event.subject

        if (message.startsWith(config.setting.commandPrefix)) contact.sendMessage(commandParsing(event))
        else if (isBotAlive) botProcess(event, isFriend)
    }

    private suspend fun commandParsing(event: MessageEvent): String {
        val message = event.message.contentToString().removePrefix(config.setting.commandPrefix)
        val param: Array<String> = message.split("\\s+".toRegex()).toTypedArray()
        val level: Int? = levelMap[event.sender.id]
        val command: Command? = commandsMap[param.first()]
        if (command == null) return "找不到命令~"
        return if (command.level <= (level ?: 0)) {
            command.callback(param, event)
        } else "您的等级是${level ?: "0"}，但是此指令等级为${command.level}级！"
    }

    suspend fun processNudge(event: NudgeEvent) {
        val contact: Contact = event.subject
        val message: MessageChain = PlainText(event.from.nick + "戳了戳你的脸").toMessageChain()
        if (event.target.id != config.bot.qq) return
        when (currentBot) {
            is Deepseek -> {
                val deepseek: Deepseek = currentBot as Deepseek
                sendByDeepseek(contact, deepseek, message)
            }

            is ChatGPT -> {
                val chatgpt: ChatGPT = currentBot as ChatGPT
                sendByChatGPT(contact, chatgpt, message)
            }

            is Qwen -> {
                val qwen: Qwen = currentBot as Qwen
                sendByQwen(contact, qwen, message)
            }
        }
    }

    private suspend fun botProcess(event: MessageEvent, isFriend: Boolean) {
        val singleMessages: MessageChain = event.message
        val contact: Contact = event.subject

        for (singleMessage in singleMessages) {
            if ((singleMessage is At && singleMessage.target == config.bot.qq) || isFriend) {
                when (currentBot) {
                    is Deepseek -> {
                        val deepseek: Deepseek = currentBot as Deepseek
                        sendByDeepseek(contact, deepseek, event.message)
                        return
                    }

                    is ChatGPT -> {
                        val chatgpt: ChatGPT = currentBot as ChatGPT
                        sendByChatGPT(contact, chatgpt, event.message)
                        return
                    }

                    is Qwen -> {
                        val qwen: Qwen = currentBot as Qwen
                        sendByQwen(contact, qwen, event.message)
                        return
                    }

                    else -> {}
                }
            }
        }

        val message: String = filterAtMessage(event.message).contentToString()


        with(message) {
            try {
                when {
                    immersiveMap.contains(event.sender.id) -> when (immersiveMap[event.sender.id]) {
                        "deepseek" -> sendByDeepseek(contact, deepseek, event.message)
                        "chatgpt" -> sendByChatGPT(contact, chatgpt, event.message)
                        "qwen" -> sendByQwen(contact, qwen, event.message)
                        else -> {}
                    }

                    contains(config.bot.name) -> {
                        when (currentBot) {
                            is Deepseek -> sendByDeepseek(contact, deepseek, event.message)
                            is ChatGPT -> sendByChatGPT(contact, chatgpt, event.message)
                            is Qwen -> sendByQwen(contact, qwen, event.message)
                            is Kimi -> contact.sendMessage(kimi.send(message))
                            else -> {}
                        }
                    }

                    customized != null && contains(customized!!.name) -> contact.sendMessage(
                        customized!!.bot.send(
                            message
                        )
                    )

                    else -> {}
                }
            } catch (e: Exception) {
                e.printStackTrace()
                contact.sendMessage("消息异常，请检查配置文件或信息：${contact.sendMessage(e.message!!)}")
            }
        }
    }

    private fun filterAtMessage(messages: MessageChain): MessageChain {
        val messageChainBuilder = MessageChainBuilder()
        messages.forEach { message ->
            if (message !is At) messageChainBuilder.add(message)
        }
        return messageChainBuilder.build()
    }

    private suspend fun sendByChatGPT(contact: Contact, chatgpt: ChatGPT, message: MessageChain) {
        val message: String = filterAtMessage(message).contentToString()
        if (chatgpt.getStream()) chatgpt.sendAsStream(message, contact)
        else contact.sendMessage(chatgpt.send(message))
    }

    private suspend fun sendByQwen(contact: Contact, qwen: Qwen, message: MessageChain) {
        val message: String = filterAtMessage(message).contentToString()
        contact.sendMessage(qwen.send(message))
    }

    private suspend fun sendByDeepseek(contact: Contact, deepseek: Deepseek, message: MessageChain) {
        val message: String = filterAtMessage(message).contentToString()
        if (deepseek.getStream()) deepseek.sendAsStream(message, contact)
        else contact.sendMessage(deepseek.send(message))
    }
}