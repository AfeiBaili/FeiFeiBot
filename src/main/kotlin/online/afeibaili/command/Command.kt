package online.afeibaili.command

import net.mamoe.mirai.event.events.MessageEvent

data class Command(val callback: suspend (Array<String>, MessageEvent) -> String, val level: Int = 0)