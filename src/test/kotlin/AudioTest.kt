import com.fasterxml.jackson.databind.ObjectMapper
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.createTempFile
import kotlin.test.Test

/**
 * 音频测试
 *
 *@author AfeiBaili
 *@version 2026/1/16 16:40
 */

class AudioTest {
    @Test
    fun test1() {
        val client: HttpClient = HttpClient.newHttpClient()

        val form = mapOf<String, Any>(
            "text" to "为什么",
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

        val json = ObjectMapper()


        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        val id: String = json.readTree(response.body()).get("id").asText()

        val request1: HttpRequest = HttpRequest.newBuilder(URI.create("https://ottohzys.wzq02.top//get/$id.ogg"))
            .GET()
            .build()

        val file: Path = createTempFile()
        println("文件路径：${file.absolutePathString()}")
        val httpResponse: HttpResponse<Path> = client.send(request1, HttpResponse.BodyHandlers.ofFile(file))

        println(id)
    }
}