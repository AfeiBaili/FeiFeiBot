package online.afeibaili.bot

import java.util.*


/**
 * 固定大小的信息队列
 *
 *@author AfeiBaili
 *@version 2025/11/26 12:49
 */

class FixedSizeQueue<E>(val maxSize: Int = 50, val pollIndex: Int = 0) : LinkedList<E>() {
    override fun add(e: E): Boolean {
        if (this.size >= maxSize) {
            this.removeAt(pollIndex)
        }
        return super.add(e)
    }
}