package online.afeibaili.module

import net.mamoe.mirai.event.events.BotOnlineEvent
import online.afeibaili.config
import oshi.SystemInfo
import java.text.DecimalFormat
import java.util.*

class BotNameMemoryRemind {
    val systemInfo = SystemInfo()
    val timer = Timer()
    lateinit var timerTask: TimerTask
    fun startTimer(bot: BotOnlineEvent) {
        timerTask = object : TimerTask() {
            override fun run() {
                config.groups.forEach { group ->
                    bot.bot.getGroup(group)?.botAsMember?.nameCard = "${config.bot.name} | 内存占用比：${getMemory()}%"
                }
            }
        }
        //十分钟
        timer.schedule(timerTask, 10000, 60000 * 10)
    }

    fun cancelTimer() {
        if (::timerTask.isInitialized) {
            timerTask.cancel()
        }
        timer.cancel()
    }

    fun getMemory(): String {
        val totalMemorySize = systemInfo.hardware.memory.total
        val freeMemorySize = systemInfo.hardware.memory.available
        return DecimalFormat("0.00").format(100.0 - (freeMemorySize * 100) / totalMemorySize)
    }
}