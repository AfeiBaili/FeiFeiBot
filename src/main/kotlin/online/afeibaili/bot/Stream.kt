package online.afeibaili.bot

import net.mamoe.mirai.event.events.MessageEvent

interface Stream {
    suspend fun sendAsStream(message: String, event: MessageEvent, role: String = "user")
}