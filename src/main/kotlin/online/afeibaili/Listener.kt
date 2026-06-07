package online.afeibaili

import kotlinx.coroutines.cancel
import net.mamoe.mirai.event.Event
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.Listener
import net.mamoe.mirai.event.events.*
import online.afeibaili.MessageManager.messageScope
import online.afeibaili.module.BotNameMemoryRemind
import online.afeibaili.module.todo.TodoManager

object Listener {
    lateinit var groupMessageEvent: Listener<GroupMessageEvent>
    lateinit var friendMessageEvent: Listener<FriendMessageEvent>
    lateinit var botOnlineEvent: Listener<BotOnlineEvent>
    lateinit var botNameMemoryRemind: BotNameMemoryRemind
    lateinit var nudgeEvent: Listener<NudgeEvent>
    fun loadingListener() {
        loadingOnlineListener()
        loadingGroupListener()
        loadingFriendListener()
        loadingNudgeEventListener()
    }

    fun unloadingListener() {
        groupMessageEvent.cancel()
        friendMessageEvent.cancel()
        botOnlineEvent.cancel()
        nudgeEvent.cancel()
        TodoManager.cancelTimer()
        messageScope.cancel()
        if (::botNameMemoryRemind.isInitialized) {
            botNameMemoryRemind.cancelTimer()
        }
    }

    fun loadingGroupListener() {
        groupMessageEvent = GlobalEventChannel.filter { group(it) }.subscribeAlways<GroupMessageEvent> { event ->
            MessageManager.processMessage(event)
        }
    }

    fun loadingFriendListener() {
        friendMessageEvent = GlobalEventChannel.subscribeAlways<FriendMessageEvent> { event ->
            MessageManager.processMessage(event, isFriend = true)
        }
    }

    fun loadingNudgeEventListener() {
        nudgeEvent = GlobalEventChannel.filter { nudge(it) }.subscribeAlways<NudgeEvent> { event ->
            MessageManager.processNudge(event)
        }
    }

    fun loadingOnlineListener() {
        botOnlineEvent = GlobalEventChannel.subscribeOnce<BotOnlineEvent> { event ->
            online.afeibaili.bot = event.bot
            if (config.setting.startMessage != null) {
                config.groups.forEach { group -> event.bot.getGroup(group)?.sendMessage(config.setting.startMessage) }
            }
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

    private suspend fun group(event: Event): Boolean {
        if (event !is GroupMessageEvent) return false
        else {
            val message: String = event.message.contentToString()
            if (event.sender.id == config.master && message.startsWith("@" + config.setting.commandPrefix)) {
                val string: String? = MessageManager.commandParsing(event, true)
                if (string != null) event.subject.sendMessage(string)
            }
        }
        return config.groups.contains(event.group.id)
    }

    private fun nudge(event: Event): Boolean {
        if (event !is NudgeEvent) return false
        return config.groups.contains(event.subject.id)
    }
}