import online.afeibaili.bot.ChatGPT
import online.afeibaili.bot.Deepseek
import online.afeibaili.config
import online.afeibaili.logger
import kotlin.test.Test

class ConfigTest {
    @Test
    fun testConfig() {
        logger(config)
        val deepseek: Deepseek = Deepseek().init()
        logger(deepseek.send("在吗"))
        logger(deepseek.send("在干么呢"))

        val chatGPT: ChatGPT = ChatGPT().init()
        logger(chatGPT.send("在吗"))
        logger(chatGPT.send("在干么呢"))
    }
}