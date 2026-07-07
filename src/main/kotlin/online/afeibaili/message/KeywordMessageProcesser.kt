package online.afeibaili.message

import net.mamoe.mirai.contact.ContactList
import net.mamoe.mirai.contact.NormalMember
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.*
import online.afeibaili.file.KeyWordDataJsonFile


/**
 * 处理关键字消息
 *
 * @author AfeiBaili
 * @version 2026/6/27 02:02
 */

object KeywordMessageProcesser : MessageProcesser {
    val keywords get() = KeyWordDataJsonFile.keywords

    override suspend fun process(event: MessageEvent) {
        if (event !is GroupMessageEvent) return
        val message: MessageChain = event.message
        val words: MutableSet<String> = keywords.words.keys
        val members: ContactList<NormalMember> = event.group.members
        val messageString: String = message.contentToString()
        val containKey: List<String> = words.filter { messageString.contains(it) }
        val messageList = mutableListOf<Message>(PlainText("以下成员触发关键词！\n"))
        var atCount = 0
        containKey.forEach { keyWord ->
            val longs: MutableSet<Long>? = keywords.words[keyWord]
            if (longs == null) return@forEach
            messageList.add(PlainText("关键词[$keyWord]\n"))
            longs.forEach { long ->
                if (!members.contains(long)) {
                    keywords.remove(keyWord, long)
                    messageList.add(PlainText("不在群内的成员：$long"))
                } else {
                    messageList.add(PlainText("  "))
                    messageList.add(At(long))
                    atCount++
                }
                messageList.add(PlainText("\n"))
            }
        }

        messageList.add(PlainText("通知成功至${atCount}位成员！"))
        if (atCount > 0) runCatching {
            event.subject.sendMessage(messageList.toMessageChain().plus(QuoteReply(message)))
        }.onFailure {
            event.subject.sendMessage("发送消息失败: ${it.message}")
        }
    }
}