package online.afeibaili.module

import com.fasterxml.jackson.databind.ObjectMapper
import net.mamoe.mirai.contact.Friend
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import online.afeibaili.bot
import online.afeibaili.bot.httpClient
import online.afeibaili.command.CommandRegistry
import online.afeibaili.command.ParamType
import java.io.InputStream
import java.net.URI
import java.net.http.HttpRequest
import java.net.http.HttpResponse


/**
 * 电棍语音命令
 *
 *@author AfeiBaili
 *@version 2026/1/16 17:09
 */

object Otto {
    val json = ObjectMapper()

    fun load() {
        CommandRegistry.register("OTTO", "otto", 0, ParamType.STRING) { p, e ->
            val text: String = runCatching { p[0] }.getOrElse { return@register "请输入要转换的文字" }
            val contactId: Long = runCatching { p[1].toLong() }.getOrElse { e.subject.id }

            val form = mapOf<String, Any>(
                "text" to text,
                "inYsddMode" to true,
                "norm" to false,
                "reverse" to false,
                "speedMult" to 1,
                "pitchMult" to 1,
            ).map { (key, value) -> "$key=$value" }.joinToString("&")

            val request: HttpRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://ottohzys.wzq02.top//make"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build()
            val id: String = runCatching {
                json.readTree(
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body()
                ).get("id").asText()
            }.getOrElse { return@register "无法连接至服务器" }

            val audioRequest: HttpRequest =
                HttpRequest.newBuilder(URI.create("https://ottohzys.wzq02.top//get/$id.ogg"))
                    .GET()
                    .build()

            val audioInputStream: InputStream = runCatching {
                httpClient.send(audioRequest, HttpResponse.BodyHandlers.ofInputStream()).body()
            }.getOrElse { return@register "无法连接至语音服务器" }

            val group: Group? = bot.getGroup(contactId)
            val friend: Friend? = bot.getFriend(contactId)

            audioInputStream.use {
                group?.let {
                    it.sendMessage(it.uploadAudio(audioInputStream.toExternalResource()))
                }
                friend?.let {
                    it.sendMessage(it.uploadAudio(audioInputStream.toExternalResource()))
                }
            }

            "已发送语音："
        }
    }
}