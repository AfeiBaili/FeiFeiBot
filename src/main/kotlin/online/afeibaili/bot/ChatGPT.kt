package online.afeibaili.bot

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.events.MessageEvent
import online.afeibaili.bot.json.Message
import online.afeibaili.bot.json.RequestBody
import online.afeibaili.bot.json.ResponseBody
import online.afeibaili.config
import java.io.BufferedReader
import java.io.InputStream
import java.net.http.HttpResponse

class ChatGPT : AbstractBot(), Stream, Customizable {
    override val url: String = "https://api.chatanywhere.tech/v1/chat/completions"
    override val key: String = config.chatgpt.key
    override val requestBody: RequestBody = RequestBody("gpt-4o-mini", ArrayList<Message>(), false)

    override fun init(): ChatGPT {
        requestBody.messages.add(Message("system", config.chatgpt.setting))
        return this
    }

    override fun customize(setting: String): Customizable {
        requestBody.messages.add(Message("system", setting))
        return this
    }

    override fun reset() {
        requestBody.messages.clear()
        init()
    }

    override fun send(message: String, role: String): String {
        val responseBody: ResponseBody = sendRequest(requestBody, message, role, url, key)
        val responseMessage: Message = responseBody.choices[0].message
        if (responseMessage.content == null) {
            return "${config.chatgpt.name}不能回答~，服务器过滤了惹！"
        }
        requestBody.messages.add(responseMessage)
        return markdown.parsingText(responseMessage.content)
    }

    override suspend fun sendAsStream(
        message: String,
        event: MessageEvent,
        role: String,
    ) {
        val contact: Contact = event.subject
        val responseInputStream: HttpResponse<InputStream> = sendRequestAsStream(requestBody, message, role, url, key)
        val inputStream: BufferedReader = responseInputStream.body().bufferedReader()
        val contentSb: StringBuilder = StringBuilder()
        var line: String?
        val mergedSb = StringBuilder()

        while (inputStream.readLine().also { line = it } != null) {
            val newLine = line!!
            if (newLine.isEmpty()) continue
            try {
                val stream: StreamJson = jsonMapper.readValue(newLine.substring(6), StreamJson::class.java)
                var content: String? = ""
                if (stream.choices.isNotEmpty() && stream.choices[0].delta.content.also { content = it } != null) {
                    contentSb.append(content)
                    mergedSb.append(content)
                    val line = parsingLine(mergedSb)
                    if (line != null) contact.sendMessage(line)
                }
            } catch (ignore: Exception) {
            }
        }
        if (message.isNotEmpty()) contact.sendMessage(mergedSb.toString())
        requestBody.messages.add(Message("assistant", contentSb.toString()))
    }

    fun getStream(): Boolean {
        return requestBody.stream
    }
}