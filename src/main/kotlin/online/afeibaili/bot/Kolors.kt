package online.afeibaili.bot

import online.afeibaili.bot.json.Image
import online.afeibaili.bot.json.ImageResponse
import online.afeibaili.config
import online.afeibaili.logger
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class Kolors {
    val uri = "https://api.siliconflow.cn/v1/images/generations"

    private fun base(image: Image): ImageResponse {
        val body: String = jsonMapper.writeValueAsString(image)
        val request: HttpRequest = builderRequest(uri, config.kolors.key, body)
        val response: HttpResponse<String> = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        try {
            return jsonMapper.readValue(response.body(), ImageResponse::class.java)
        } catch (e: Exception) {
            logger(response.body())
            throw e
        }
    }

    fun send(prompt: String): ImageResponse {
        val image = Image(prompt)
        return base(image)
    }

    fun send(prompt: String, batchSize: Int): ImageResponse {
        if (batchSize !in 1..4) throw Exception("Batch size must be between 1 and 4")
        val image = Image(prompt, batchSize)
        return base(image)
    }

    companion object {
    }
}