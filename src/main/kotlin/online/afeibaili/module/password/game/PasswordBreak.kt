package online.afeibaili.module.password.game

import net.mamoe.mirai.event.events.GroupMessageEvent
import online.afeibaili.command.Command
import online.afeibaili.command.Commands

object PasswordBreak {
    fun load() {
        Commands.register("密文破译", Command({ param, event ->
            if (event !is GroupMessageEvent) return@Command "不支持在私聊中游玩"
            val groupEvent: GroupMessageEvent = event
            return@Command ManageGame.play(groupEvent.group.id)
        }))
        Commands.register("破译认输", Command({ param, event ->
            if (event !is GroupMessageEvent) return@Command "不支持在私聊中游玩"
            val groupEvent: GroupMessageEvent = event
            return@Command ManageGame.remove(groupEvent.group.id)
        }))
        Commands.register("密文录入", Command({ param, event ->
            if (param.size == 1) return@Command "密文录入 <句子>"
            val sentenceStringBuilder = StringBuilder()
            param.drop(1).forEach { word ->
                sentenceStringBuilder.append(word).append(" ")
            }
            if (sentenceStringBuilder.indexOf("*") != -1) return@Command "句子中不允许包含星号字符：“*”"
            if (sentenceStringBuilder.isNotEmpty())
                sentenceStringBuilder.deleteAt(sentenceStringBuilder.length - 1)
            logsSentence(sentenceStringBuilder.toString())
            "已录入句子：$sentenceStringBuilder"
        }))
    }
}