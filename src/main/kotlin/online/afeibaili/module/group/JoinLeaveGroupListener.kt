package online.afeibaili.module.group

import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.MemberJoinEvent
import net.mamoe.mirai.event.events.MemberLeaveEvent
import online.afeibaili.Manager


/**
 * 加群消息
 *
 *@author AfeiBaili
 *@version 2026/4/19 22:35
 */

object JoinLeaveGroupListener {
    fun load() {
        GlobalEventChannel.subscribeAlways<MemberJoinEvent> { event ->
            Manager.processJoinGroup(event)
        }

        GlobalEventChannel.subscribeAlways<MemberLeaveEvent> { event ->
            Manager.processLeaveGroup(event)
        }
    }
}