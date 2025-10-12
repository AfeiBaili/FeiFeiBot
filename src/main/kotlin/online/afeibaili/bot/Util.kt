package online.afeibaili.bot

import Markdown
import com.fasterxml.jackson.databind.ObjectMapper
import online.afeibaili.LoggerLevel
import online.afeibaili.bot.json.Message
import online.afeibaili.bot.json.RequestBody
import online.afeibaili.bot.json.ResponseBody
import online.afeibaili.bot.json.Stream
import online.afeibaili.logger
import java.io.InputStream
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

typealias StreamJson = Stream

val httpClient: HttpClient = HttpClient.newHttpClient()

val jsonMapper = ObjectMapper()

val markdown = Markdown()

fun builderRequest(url: String, key: String, message: String): HttpRequest {
    return HttpRequest.newBuilder(URI(url)).setHeader("Authorization", "Bearer $key")
        .setHeader("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(message)).build()
}

fun sendRequest(requestBody: RequestBody, message: String, role: String, url: String, key: String): ResponseBody {
    val requestString: String = messageProcessing(requestBody, message, role)
    val request: HttpRequest = builderRequest(url, key, requestString)
    val response: HttpResponse<String> = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
    logger(response.body(), LoggerLevel.DEBUG)
    return jsonMapper.readValue(response.body(), ResponseBody::class.java)
}

fun sendRequestAsStream(
    requestBody: RequestBody,
    message: String,
    role: String,
    url: String,
    key: String,
): HttpResponse<InputStream> {
    val requestString: String = messageProcessing(requestBody, message, role)
    val request: HttpRequest = builderRequest(url, key, requestString)
    return httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream())
}

fun parsingLine(sb: StringBuilder): String? {
    if (sb.trim().isEmpty()) return null
    val indexOf: Int = sb.toString().indexOf('\n')
    return if (indexOf != -1) {
        val substring = sb.substring(0, indexOf)
        sb.delete(0, indexOf + 1)
        val parsing = markdown.parsing(substring)
        if (parsing.trim().isEmpty()) null
        else parsing
    } else null
}

private fun messageProcessing(requestBody: RequestBody, message: String, role: String): String {
    requestBody.messages.add(Message(role, message))
    return jsonMapper.writeValueAsString(requestBody)
}