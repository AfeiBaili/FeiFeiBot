package online.afeibaili.bot

import online.afeibaili.config

object Robots {
    val deepseek = Deepseek().init()
    val kimi = Kimi().init()
    val chatgpt = ChatGPT().init()
    val qwen = Qwen().init()
    var customized: CustomizedBot? = null
    val currentBot: AbstractBot
        get() {
            return when (config.setting.currentBot) {
                "deepseek" -> deepseek
                "chatgpt" -> chatgpt
                "qwen" -> qwen
                "kimi" -> kimi
                else -> deepseek
            }
        }
    val kolors = Kolors()
}