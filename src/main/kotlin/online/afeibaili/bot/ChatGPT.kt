package online.afeibaili.bot

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.events.MessageEvent
import online.afeibaili.config
import online.afeibaili.json.Message
import online.afeibaili.json.RequestBody
import online.afeibaili.json.ResponseBody
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
        return responseMessage.content
    }

    override suspend fun sendAsStream(
        message: String,
        event: MessageEvent,
        role: String,
    ) {
        val send: Contact = event.subject
        val responseInputStream: HttpResponse<InputStream> = sendRequestAsStream(requestBody, message, role, url, key)
        val inputStream: BufferedReader = responseInputStream.body().bufferedReader()
        val contentSb: StringBuilder = StringBuilder()
        var line: String
        while (inputStream.readLine().also { line = it } != null) {
            if (line.isEmpty()) continue
            val stream: StreamJson = jsonMapper.readValue(line, StreamJson::class.java)
            var content: String
            if (stream.choices[0].delta.content.also { content = it } != null) {
                contentSb.append(content)
            }
            requestBody.messages.add(Message("assistant", contentSb.toString()))
            send.sendMessage(contentSb.toString())
        }
    }

    fun getStream(): Boolean {
        return requestBody.stream
    }
}