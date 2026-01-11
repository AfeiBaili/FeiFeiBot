package online.afeibaili.bot

import online.afeibaili.config
import java.util.*


/**
 * 固定大小的信息队列
 *
 *@author AfeiBaili
 *@version 2025/11/26 12:49
 */

class FixedSizeQueue<E>(val pollIndex: Int = 1) : LinkedList<E>() {
    override fun add(e: E): Boolean {
        if (this.size >= config.setting.maxChatLength) {
            this.removeAt(pollIndex)
        }
        return super.add(e)
    }
}