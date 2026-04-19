package online.afeibaili.module.group

import net.mamoe.mirai.event.Event
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.MemberJoinEvent
import net.mamoe.mirai.event.events.MemberLeaveEvent
import online.afeibaili.Manager
import online.afeibaili.config


/**
 * 加群消息
 *
 *@author AfeiBaili
 *@version 2026/4/19 22:35
 */

object JoinLeaveGroupListener {
    fun load() {
        GlobalEventChannel.filter { group(it) }.subscribeAlways<MemberJoinEvent> { event ->
            Manager.processJoinGroup(event)
        }

        GlobalEventChannel.filter { group(it) }.subscribeAlways<MemberLeaveEvent> { event ->
            Manager.processLeaveGroup(event)
        }
    }

    private fun group(event: Event): Boolean = when (event) {
        is MemberJoinEvent -> config.groups.contains(event.groupId)
        is MemberLeaveEvent -> config.groups.contains(event.groupId)
        else -> false
    }
}