package online.afeibaili.bot

import com.fasterxml.jackson.databind.ObjectMapper
import online.afeibaili.json.Message
import online.afeibaili.json.RequestBody
import online.afeibaili.json.ResponseBody
import java.io.InputStream
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

typealias StreamJson = online.afeibaili.json.Stream

val httpClient: HttpClient = HttpClient.newHttpClient()

val jsonMapper = ObjectMapper()

fun builderRequest(url: String, key: String, message: String): HttpRequest {
    return HttpRequest.newBuilder(URI(url))
        .setHeader("Authorization", "Bearer $key")
        .setHeader("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(message))
        .build()
}

fun sendRequest(requestBody: RequestBody, message: String, role: String, url: String, key: String): ResponseBody {
    val requestString: String = messageProcessing(requestBody, message, role)
    val request: HttpRequest = builderRequest(url, key, requestString)
    val response: HttpResponse<String> = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
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

private fun messageProcessing(requestBody: RequestBody, message: String, role: String): String {
    requestBody.messages.add(Message(role, message))
    return jsonMapper.writeValueAsString(requestBody)
}