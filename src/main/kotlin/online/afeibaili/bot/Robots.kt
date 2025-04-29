package online.afeibaili.bot

import online.afeibaili.config

object Robots {
    val deepseek = Deepseek().init()
    val kimi = Kimi().init()
    val chatgpt = ChatGPT().init()
    var customized: CustomizedBot? = null
    val atByTargetBot: AbstractBot
        get() {
            return when (config.setting.atByTargetBot) {
                "deepseek" -> deepseek
                "chatgpt" -> chatgpt
                else -> chatgpt
            }
        }
}