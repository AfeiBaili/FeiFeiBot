package online.afeibaili.module

import com.sun.management.OperatingSystemMXBean
import net.mamoe.mirai.event.events.BotOnlineEvent
import online.afeibaili.config
import java.lang.management.ManagementFactory
import java.text.DecimalFormat
import java.util.*

class BotNameMemoryRemind {
    val timer = Timer()
    fun startTimer(bot: BotOnlineEvent) {
        timer.schedule(object : TimerTask() {
            override fun run() {
                config.groups.forEach { group ->
                    bot.bot.getGroup(group)?.botAsMember?.nameCard = "${config.bot.name} | Memory：${getMemory()}%"
                }
            }
        }, 10000, 60000)
    }

    fun getMemory(): String {
        val bean = ManagementFactory.getOperatingSystemMXBean() as OperatingSystemMXBean
        val totalMemorySize = bean.totalMemorySize
        val freeMemorySize = bean.freeMemorySize
        return DecimalFormat("0.00").format(((freeMemorySize.toFloat() / totalMemorySize) * 100).toDouble())
    }
}