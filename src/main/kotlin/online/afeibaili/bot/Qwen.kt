package online.afeibaili.bot

import online.afeibaili.bot.json.Message
import online.afeibaili.bot.json.RequestBody
import online.afeibaili.bot.json.ResponseBody
import online.afeibaili.bot.json.map.QwenGenerateImageResponse
import online.afeibaili.bot.json.map.QwenImageGenerateImageRequest
import online.afeibaili.bot.json.map.QwenTextGenerateImageRequest
import online.afeibaili.config
import java.net.http.HttpRequest
import java.net.http.HttpResponse


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

    val generateImagesUrl = "https://dashscope.aliyuncs.com/api/v1/services/aigc/multimodal-generation/generation"

    fun sendGenerateImageRequest(promptWords: String, vararg imageUrl: String): String {
        val requestData =
            if (imageUrl.isEmpty()) jsonMapper.writeValueAsString(QwenTextGenerateImageRequest(promptWords))
            else jsonMapper.writeValueAsString(QwenImageGenerateImageRequest(*imageUrl, promptWords = promptWords))
        val request: HttpRequest = builderRequest(generateImagesUrl, key, requestData)
        val response: HttpResponse<String> = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        return jsonMapper.readValue(
            response.body(), QwenGenerateImageResponse::class.java
        ).output.choices[0].message.content[0].image
    }

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