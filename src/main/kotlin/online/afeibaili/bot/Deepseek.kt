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
        val sb = StringBuilder()
        val responseBody: ResponseBody = sendRequest(requestBody, message, role, url, key)
        val responseBodyMessage: Message = responseBody.choices[0].message
        if (responseBodyMessage.reasoningContent != null) {
            val reasoningContent: String = responseBodyMessage.reasoningContent
            sb.append("推理内容：\n").append(reasoningContent).append("\n\n")
            sb.append("正式回答：\n")
        }
        requestBody.messages.add(responseBodyMessage)
        sb.append(responseBodyMessage.content)
        return markdown.parsingText(sb.toString())
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
        val mergedSb = StringBuilder()
        var hasReason = false
        var isChange = false

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
            var content: String?
            if (stream.choices[0].delta.reasoningContent.also { reasoningContent = it } != null) {
                if (!hasReason) mergedSb.append("推理内容：").append('\n')
                hasReason = true
                reasoningSb.append(reasoningContent)
                mergedSb.append(reasoningContent)
            }
            if (stream.choices[0].delta.content.also { content = it } != null) {
                if (hasReason && !isChange) {
                    isChange = true
                    mergedSb.append('\n').append("正式内容：").append('\n')
                }
                contentSb.append(content)
                mergedSb.append(content)
            }
            val line = parsingLine(mergedSb)
            if (line != null) contact.sendMessage(line)
        }
        if (message.isNotEmpty()) contact.sendMessage(mergedSb.toString())

        requestBody.messages.add(Message("assistant", contentSb.toString()))
        isRunning = false
    }

    fun getStream(): Boolean {
        return requestBody.stream
    }
}