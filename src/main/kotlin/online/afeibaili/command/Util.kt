package online.afeibaili.command

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.ForwardMessageBuilder
import net.mamoe.mirai.message.data.PlainText
import online.afeibaili.bot.AbstractBot
import online.afeibaili.bot.httpClient
import online.afeibaili.bot.json.ChatGPTBalance
import online.afeibaili.bot.json.DeepseekBalance
import online.afeibaili.bot.jsonMapper
import online.afeibaili.bot.model.json.ModelList
import online.afeibaili.config
import java.net.URI
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

fun getModel(bot: AbstractBot): String {
    return bot.requestBody.model
}

fun setModel(bot: AbstractBot, module: String) {
    bot.requestBody.model = module
}

fun getChatGPTModule(): String {
    val request: HttpRequest = HttpRequest.newBuilder()
        .uri(URI.create("https://api.chatanywhere.tech/v1/models"))
        .setHeader("Authorization", "Bearer ${config.chatgpt.key}")
        .GET()
        .build()
    val response: HttpResponse<String> = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
    return response.body()
}

fun getChatGPTModuleAsString(): String {
    val modelList: ModelList = jsonMapper.readValue(getChatGPTModule(), ModelList::class.java)
    val sb: StringBuilder = StringBuilder()
    modelList.data.forEach { it ->
        sb.append(it.id).append("\n")
    }
    return sb.removeSuffix("\n").toString()
}

fun getDeepseekModule(): String {
    val sb: StringBuilder = StringBuilder()
    sb.append("deepseek-chat").append("\n")
    sb.append("deepseek-reasoner").append("\n")
    return sb.removeSuffix("\n").toString()
}

fun getDeepseekModuleAsString(): String {
    return getDeepseekModule()
}

suspend fun uploadChatHistory(bot: AbstractBot, event: MessageEvent) {
    val contact: Contact = event.subject

    val forwardMessageBuilder = ForwardMessageBuilder(contact)
    bot.requestBody.messages.forEach { message ->
        val qq: Long = when (message.role) {
            "assistant" -> contact.bot.id
            "user" -> event.sender.id
            else -> event.sender.id
        }
        forwardMessageBuilder.add(qq, "记录", PlainText(message.content))
    }
    contact.sendMessage(forwardMessageBuilder.build())
}

fun getDeepseekBalance(bot: AbstractBot): String {
    runCatching {
        val request = HttpRequest.newBuilder()
            .uri(URI("https://api.deepseek.com/user/balance"))
            .GET()
            .setHeader("Authorization", "Bearer ${bot.key}")
            .setHeader("Accept", "application/json")
            .build()
        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        response.body()
        val deepseekBalance: DeepseekBalance = jsonMapper.readValue(response.body(), DeepseekBalance::class.java)
        deepseekBalance.toString()
    }.fold(
        onSuccess = { return it },
        onFailure = { return "接口查询异常请稍后：${it.message}" }
    )
}

fun getChatgptBalance(bot: AbstractBot): String {
    runCatching {
        val request = HttpRequest.newBuilder()
            .uri(URI("https://api.chatanywhere.org/v1/query/balance"))
            .POST(HttpRequest.BodyPublishers.ofString(""))
            .setHeader("Authorization", bot.key)
            .timeout(Duration.ofSeconds(2))
            .setHeader("Content-Type", "application/json").build()
        val future = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
        val response: HttpResponse<String> = future.get()
        val balance: ChatGPTBalance = jsonMapper.readValue(response.body(), ChatGPTBalance::class.java)
        balance.toString()
    }.fold(
        onSuccess = { return it },
        onFailure = { return "请求被拦截无法访问服务器，请自行访问：https://api.chatanywhere.org/#/" }
    )
}
