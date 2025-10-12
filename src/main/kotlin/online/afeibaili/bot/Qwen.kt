package online.afeibaili.bot

import online.afeibaili.bot.json.Message
import online.afeibaili.bot.json.RequestBody
import online.afeibaili.bot.json.ResponseBody
import online.afeibaili.config


/**
 * 千问机器人模型
 *
 *@author AfeiBaili
 *@version 2025/10/12 15:58
 */

class Qwen : AbstractBot(), Customizable {
    override val url: String = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions"
    override val key: String = config.qwen.key

    override val requestBody: RequestBody = RequestBody("qwen-plus", ArrayList<Message>(), false)


    override fun init(): Qwen {
        requestBody.messages.add(Message("system", config.qwen.setting))
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
        return markdown.parsingText(responseMessage.content)
    }

    override fun customize(setting: String): Customizable {
        return Qwen()
    }
}