package online.afeibaili.bot

import online.afeibaili.config

class Robots {
    val deepseek = Deepseek().init()
    val kimi = Kimi().init()
    val chatgpt = ChatGPT().init()
    val qwen = Qwen().init()
    var customized: CustomizedBot? = null
    var currentBotName: String = config.setting.currentBot
    val currentBot: AbstractBot
        get() {
            return when (currentBotName) {
                "deepseek" -> deepseek
                "chatgpt" -> chatgpt
                "qwen" -> qwen
                "kimi" -> kimi
                else -> deepseek
            }
        }
    val kolors = Kolors()
}