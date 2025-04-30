package online.afeibaili.bot

import online.afeibaili.config
import online.afeibaili.bot.json.Message
import online.afeibaili.bot.json.RequestBody
import online.afeibaili.bot.json.ResponseBody

class Kimi : AbstractBot() {
    override val url: String = "https://api.moonshot.cn/v1/chat/completions"
    override val key: String = config.kimi.key
    override val requestBody: RequestBody = RequestBody("moonshot-v1-8k", ArrayList<Message>())

    override fun init(): Kimi {
        requestBody.messages.add(Message("system", config.kimi.setting))
        return this
    }

    override fun reset() {
        requestBody.messages.clear()
        init()
    }

    override fun send(message: String, role: String): String {
        val responseBody: ResponseBody = sendRequest(requestBody, message, role, url, key)
        val responseMessage: Message = responseBody.choices[0].message
        requestBody.messages.add(responseMessage)
        return responseMessage.content
    }
}