import online.afeibaili.bot.*
import online.afeibaili.bot.json.Message
import online.afeibaili.bot.json.RequestBody
import online.afeibaili.command.getChatGPTModuleAsString
import online.afeibaili.command.getChatgptBalance
import online.afeibaili.command.getDeepseekBalance
import online.afeibaili.config
import online.afeibaili.file.ConfigFile
import online.afeibaili.logger
import java.io.BufferedReader
import java.io.InputStream
import java.net.http.HttpResponse
import kotlin.test.Test

class ConfigTest {
    @Test
    fun testConfig() {
        logger(config)
        val deepseek: Deepseek = Deepseek().init()
        logger(deepseek.send("在吗"))
        logger(deepseek.send("在干么呢"))

        val chatGPT: ChatGPT = ChatGPT().init()
        logger(chatGPT.send("在吗"))
        logger(chatGPT.send("在干么呢"))
    }

    @Test
    fun testStream() {
        config = ConfigFile().config
        val requestBody: RequestBody = Deepseek().init().requestBody
        requestBody.stream = true
        val responseInputStream: HttpResponse<InputStream> = sendRequestAsStream(
            requestBody,
            "在吗",
            "user",
            "https://api.deepseek.com/v1/chat/completions",
            "sk-419bcfe0088f473ebbeec9a68606cc32"
        )
        val inputStream: BufferedReader = responseInputStream.body().bufferedReader()
        val contentSb: StringBuilder = StringBuilder()
        val reasoningSb: StringBuilder = StringBuilder()
        var line: String? = null

        while (inputStream.readLine().also { line = it } != null) {
            if (line == "") continue
            if (line == "\n") continue
            if (line == "data: [DONE]") continue
            if (line == ": keep-alive") {
                println("思索中...")
                continue
            }

            val stream = jsonMapper.readValue(line!!.substring(6), StreamJson::class.java)
            println(stream)
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
            println(contentSb.toString())
        } else println("推理内容：\n$reasoningSb\n\n正式回答：\n$contentSb")
    }

    @Test
    fun testGetChatgptBalance() {
        config = ConfigFile().config
        println(getChatgptBalance(ChatGPT().init()))
    }

    @Test
    fun testDeepseekBalance() {
        config = ConfigFile().config
        println(getDeepseekBalance(Deepseek().init()))
    }

    @Test
    fun testChatgptModule() {
        config = ConfigFile().config
        println(getChatGPTModuleAsString())

    }
}