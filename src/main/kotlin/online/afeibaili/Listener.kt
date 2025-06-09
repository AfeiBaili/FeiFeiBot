package online.afeibaili

import net.mamoe.mirai.event.Event
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.Listener
import net.mamoe.mirai.event.events.BotOnlineEvent
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MemberJoinEvent
import online.afeibaili.module.BotNameMemoryRemind
import online.afeibaili.module.todo.TodoTimer

object Listener {
    lateinit var groupMessageEvent: Listener<GroupMessageEvent>
    lateinit var friendMessageEvent: Listener<FriendMessageEvent>
    lateinit var botOnlineEvent: Listener<BotOnlineEvent>
    lateinit var botNameMemoryRemind: BotNameMemoryRemind

    fun loadingListener() {
        loadingOnlineListener()
        loadingGroupListener()
        loadingFriendListener()
    }

    fun unloadingListener() {
        groupMessageEvent.cancel()
        friendMessageEvent.cancel()
        botOnlineEvent.cancel()
        TodoTimer.cancelTimer()
        if (::botNameMemoryRemind.isInitialized) {
            botNameMemoryRemind.cancelTimer()
        }
    }

    fun loadingGroupListener() {
        groupMessageEvent = GlobalEventChannel.filter { group(it) }.subscribeAlways<GroupMessageEvent> { event ->
            Manager.process(event)
        }
    }

    fun loadingFriendListener() {
        friendMessageEvent = GlobalEventChannel.subscribeAlways<FriendMessageEvent> { event ->
            Manager.process(event)
        }
    }

    fun loadingOnlineListener() {
        //test 测试startMessage
        botOnlineEvent = GlobalEventChannel.subscribeOnce<BotOnlineEvent> { event ->
            if (config.setting.startMessage != null) {
                config.groups.forEach { group -> event.bot.getGroup(group)?.sendMessage(config.setting.startMessage) }
            }
            //test 测试openMemoryName是否可用
            if (config.module.isOpenMemoryName) {
                botNameMemoryRemind = BotNameMemoryRemind()
                botNameMemoryRemind.startTimer(event)
            }
        }
    }

    fun loadingMemberJoinListener() {
        GlobalEventChannel.subscribeAlways<MemberJoinEvent> { event ->
            event.group.sendMessage("")
        }
    }

    private fun group(event: Event): Boolean {
        if (event !is GroupMessageEvent) return false
        return config.groups.contains(event.group.id)
    }
}