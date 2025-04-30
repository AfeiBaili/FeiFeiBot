package online.afeibaili.bot

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.events.MessageEvent
import online.afeibaili.config
import online.afeibaili.bot.json.Message
import online.afeibaili.bot.json.RequestBody
import online.afeibaili.bot.json.ResponseBody
import java.io.BufferedReader
import java.io.InputStream
import java.net.http.HttpResponse


class Deepseek : AbstractBot(), Stream, Customizable {
    override val url: String = "https://api.deepseek.com/v1/chat/completions"
    override val key: String = config.deepseek.key
    override val requestBody: RequestBody = RequestBody("deepseek-chat", ArrayList<Message>(), false)

    var isRunning = false

    override fun init(): Deepseek {
        requestBody.messages.add(Message("system", config.deepseek.setting))
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
        val responseBodyMessage: Message = responseBody.choices[0].message
        requestBody.messages.add(responseBodyMessage)
        return responseBodyMessage.content
    }

    override suspend fun sendAsStream(
        message: String,
        event: MessageEvent,
        role: String,
    ) {
        val contact: Contact = event.subject
        if (isRunning) {
            contact.sendMessage("${config.deepseek.name}正在回答中！请稍后再试~")
            return
        }
        isRunning = true
        val responseInputStream: HttpResponse<InputStream> = sendRequestAsStream(requestBody, message, role, url, key)
        val inputStream: BufferedReader = responseInputStream.body().bufferedReader()
        val contentSb: StringBuilder = StringBuilder()
        val reasoningSb: StringBuilder = StringBuilder()
        var line: String?
        val send: Contact = event.subject

        while (inputStream.readLine().also { line = it } != null) {
            if (line == "") continue
            if (line == "\n") continue
            if (line == "data: [DONE]") continue
            if (line == ": keep-alive") {
                send.sendMessage("思索中...")
                continue
            }

            val stream = jsonMapper.readValue(line!!.substring(6), StreamJson::class.java)
            var reasoningContent: String?
            if (stream.choices[0].delta.reasoningContent.also { reasoningContent = it } != null) {
                reasoningSb.append(reasoningContent)
            }
            var content: String?
            if (stream.choices[0].delta.content.also { content = it } != null) {
                contentSb.append(content)
            }
        }
        requestBody.messages.add(Message("assistant", contentSb.toString()))
        if (reasoningSb.isEmpty()) {
            send.sendMessage(contentSb.toString())
        } else send.sendMessage("推理内容：\n$reasoningSb\n\n正式回答：\n$contentSb")
        isRunning = false
    }

    fun getStream(): Boolean {
        return requestBody.stream
    }
}