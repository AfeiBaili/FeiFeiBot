package online.afeibaili

import online.afeibaili.bot.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass


/**
 * # 机器人管理器
 *
 * @author AfeiBaili
 * @version 2026/6/7 15:05
 */

object BotManager {
    var isBotAlive = true
    val immersiveMap = HashMap<Long, String>()
    val friendBots = ConcurrentHashMap<Long, Robots>()
    val groupBots = ConcurrentHashMap<Long, Robots>()
    val mianBots = Robots()
    val keyWorldList = HashMap<String, List<Long>>()

    fun getRobotsOrCreate(isFriend: Boolean, id: Long): Robots {
        val rb: Robots = if (isFriend) {
            val robots: Robots? = friendBots[id]
            if (robots == null) {
                val bots = Robots()
                friendBots[id] = bots
                bots
            } else robots
        } else {
            val robots: Robots? = groupBots[id]
            if (robots == null) {
                val bots = Robots()
                groupBots[id] = bots
                bots
            } else robots
        }
        return rb
    }

    fun getCustomizedBot(isFriend: Boolean, id: Long): CustomizedBot? {
        val bot = if (config.module.isMultiInstance) {
            getRobotsOrCreate(isFriend, id).customized
        } else mianBots.customized
        return bot
    }

    fun getCurrentBot(isFriend: Boolean, id: Long): AbstractBot {
        val currentBot = if (config.module.isMultiInstance) {
            getRobotsOrCreate(isFriend, id).currentBot
        } else mianBots.currentBot
        return currentBot
    }

    fun getDeepseekBot(isFriend: Boolean, id: Long) = getBot(Deepseek::class, isFriend, id) as Deepseek
    fun getChatGPTBot(isFriend: Boolean, id: Long) = getBot(ChatGPT::class, isFriend, id) as ChatGPT
    fun getQwenBot(isFriend: Boolean, id: Long) = getBot(Qwen::class, isFriend, id) as Qwen
    fun getKimiBot(isFriend: Boolean, id: Long) = getBot(Kimi::class, isFriend, id) as Kimi

    fun <Bot : AbstractBot> getBot(botClass: KClass<Bot>, isFriend: Boolean, id: Long): AbstractBot {
        fun getBotInstance(botClass: KClass<Bot>, robots: Robots): AbstractBot {
            val bot = when (botClass) {
                Deepseek::class -> robots.deepseek
                ChatGPT::class -> robots.chatgpt
                Qwen::class -> robots.qwen
                Kimi::class -> robots.kimi
                else -> throw RuntimeException("找不到bot class：$botClass")
            }
            return bot
        }

        val abstractBot = if (config.module.isMultiInstance) {
            if (isFriend) {
                val robots: Robots? = friendBots[id]
                if (robots == null) {
                    val bots = Robots()
                    friendBots[id] = bots
                    getBotInstance(botClass, bots)
                } else getBotInstance(botClass, robots)
            } else {
                val robots: Robots? = groupBots[id]
                if (robots == null) {
                    val bots = Robots()
                    groupBots[id] = bots
                    getBotInstance(botClass, bots)
                } else getBotInstance(botClass, robots)
            }
        } else getBotInstance(botClass, mianBots)

        return abstractBot
    }
}