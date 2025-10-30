import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlin.test.Test

/**
 * NovelAI测试
 *
 *@author AfeiBaili
 *@version 2025/10/29 11:39
 */

class NovelAITest {
    val client: HttpClient = HttpClient.newHttpClient()

    @Test
    fun test02() {
        println(
            sendPostRequest(
                "https://p0.kamiya.dev/api/image/generate", """

            """.trimIndent()
            )
        )
    }

    @Test
    fun test1() = println(
        sendGetRequest("https://p0.kamiya.dev/api/session/getDetails").body().toString()
    )

    fun sendGetRequest(url: String): HttpResponse<String> {
        val request: HttpRequest = HttpRequest.newBuilder()
            .GET()
            .setHeader("Authorization", "Bearer sk-2AzMKajCyMNf2RirOYeQGSqCi2tdmKiyDpj7W2nbhji4IZJm")
            .uri(URI(url))
            .build()

        return client.send(request, HttpResponse.BodyHandlers.ofString())
    }

    fun sendPostRequest(url: String, data: String): HttpResponse<String> {
        val request: HttpRequest = HttpRequest.newBuilder()
            .POST(HttpRequest.BodyPublishers.ofString(data))
            .setHeader("Authorization", "Bearer sk-2AzMKajCyMNf2RirOYeQGSqCi2tdmKiyDpj7W2nbhji4IZJm")
            .uri(URI(url))
            .build()

        return client.send(request, HttpResponse.BodyHandlers.ofString())
    }
}