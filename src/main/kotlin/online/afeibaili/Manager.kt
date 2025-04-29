package online.afeibaili

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.events.MessageEvent
import online.afeibaili.bot.ChatGPT
import online.afeibaili.bot.Deepseek
import online.afeibaili.bot.Robots.atByTargetBot
import online.afeibaili.bot.Robots.chatgpt
import online.afeibaili.bot.Robots.customized
import online.afeibaili.bot.Robots.deepseek
import online.afeibaili.bot.Robots.kimi
import online.afeibaili.command.Command

object Manager {
    var isBotAlive = true

    suspend fun process(event: MessageEvent) {
        val message = event.message.contentToString()
        val contact: Contact = event.subject

        if (message.startsWith("/")) contact.sendMessage(commandParsing(event))
        else if (isBotAlive) botProcess(event)
    }

    private suspend fun commandParsing(event: MessageEvent): String {
        val message = event.message.contentToString().removePrefix("/")
        val param: Array<String> = message.split("\\s+".toRegex()).toTypedArray()
        val level: Int? = levelMap[event.sender.id]
        val command: Command? = commandsMap[param.first()]
        if (command == null) return "找不到命令~"
        return if (command.level <= (level ?: 0)) {
            command.callback(param, event)
        } else "您的等级是${level ?: "0"}，但是此指令等级为${command.level}级！"
    }

    private suspend fun botProcess(event: MessageEvent) {
        val message: String = event.message.contentToString()
        val contact: Contact = event.subject

        with(message) {
            when {
                contains("@${config.bot.qq}") -> {
                    when (atByTargetBot) {
                        is Deepseek -> {
                            val atDeepseek: Deepseek = atByTargetBot as Deepseek
                            if (atDeepseek.getStream()) atDeepseek.sendAsStream(message, event)
                            else contact.sendMessage(atDeepseek.send(message))
                        }

                        is ChatGPT -> {
                            val atChatGPT: ChatGPT = atByTargetBot as ChatGPT
                            if (atChatGPT.getStream()) atChatGPT.sendAsStream(message, event)
                            else contact.sendMessage(atChatGPT.send(message))
                        }

                        else -> {}
                    }
                }

                contains(config.chatgpt.name) -> if (chatgpt.getStream()) chatgpt.sendAsStream(message, event)
                else contact.sendMessage(chatgpt.send(message))

                contains(config.deepseek.name) -> if (deepseek.getStream()) deepseek.sendAsStream(message, event)
                else contact.sendMessage(deepseek.send(message))

                customized != null && contains(customized!!.name) -> contact.sendMessage(customized!!.bot.send(message))
                contains("kimi") || contains("Kimi") || contains("KIMI") -> contact.sendMessage(kimi.send(message))

                else -> {}
            }
        }
    }
}