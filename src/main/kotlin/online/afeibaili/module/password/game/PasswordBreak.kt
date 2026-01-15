package online.afeibaili.module.password.game

import net.mamoe.mirai.event.events.GroupMessageEvent
import online.afeibaili.command.Command
import online.afeibaili.command.CommandRegistry
import online.afeibaili.command.ParamType

object PasswordBreak {
    fun load() {
        CommandRegistry.registerWithChild(
            "密文破译小游戏", "password-break", 0, ParamType.NOTHING, { _, e ->
                if (e !is GroupMessageEvent) return@registerWithChild "不支持在私聊中游玩"
                val groupEvent: GroupMessageEvent = e
                return@registerWithChild ManageGame.play(groupEvent.group.id)
            },
            Command("认输", "concede") { _, e ->
                if (e !is GroupMessageEvent) return@Command "不支持在私聊中游玩"
                val groupEvent: GroupMessageEvent = e
                return@Command ManageGame.remove(groupEvent.group.id)
            },
            Command("录入", "write-in", 0, ParamType.STRING) { p, _ ->
                val text: String = p.joinToString(" ").also { if (it.isEmpty()) return@Command "句子不可为空" }
                if (text.indexOf("*") != -1) return@Command "句子中不允许包含星号字符：“*”"
                logsSentence(text)
                "已录入句子：$text"
            }
        )
    }
}