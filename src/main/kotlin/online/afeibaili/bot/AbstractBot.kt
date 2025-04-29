package online.afeibaili.bot

import online.afeibaili.json.RequestBody

abstract class AbstractBot {
    abstract val url: String
    abstract val key: String
    abstract val requestBody: RequestBody

    abstract fun init(): AbstractBot
    abstract fun reset()
    abstract fun send(message: String, role: String = "user"): String
}