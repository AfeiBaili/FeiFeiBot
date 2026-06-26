package online.afeibaili.message

import net.mamoe.mirai.event.events.MessageEvent


/**
 * 消息处理器接口
 *
 * @author AfeiBaili
 * @version 2026/6/27 02:02
 */

interface MessageProcesser {
    suspend fun process(event: MessageEvent)
}