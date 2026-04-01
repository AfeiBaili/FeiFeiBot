import online.afeibaili.module.password.game.charLineCount
import java.net.URI
import java.net.URL
import java.net.URLConnection
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import kotlin.test.Test

/**
 * 测试音乐
 *
 *@author AfeiBaili
 *@version 2025/9/28 12:16
 */

class MusicTest {
    @Test
    fun test2() {
        val client: HttpClient = HttpClient.newBuilder().build()
        val text: String = URLEncoder.encode("麻雀", StandardCharsets.UTF_8)
        val request: HttpRequest =
            HttpRequest.newBuilder(URI.create("https://luren.online:2345/proxy/userMusicSearch?name=$text&t=0&l=20"))
                .setHeader("Authorization", "7uSi+uq1GB9syMnwzVPKvCK6n+yCe9Y0t4wk/zFS3KM=")
                .GET()
                .build()

        println(client.send(request, HttpResponse.BodyHandlers.ofString()).body())

        val request1: HttpRequest =
            HttpRequest.newBuilder(URI.create("https://luren.online:2345/proxy/musicUrl?id=1407551413&level=standard"))
                .setHeader("Authorization", "7uSi+uq1GB9syMnwzVPKvCK6n+yCe9Y0t4wk/zFS3KM=")
                .GET()
                .build()

        println(client.send(request1, HttpResponse.BodyHandlers.ofString()).body())
    }

    @Test
    fun test1() {
        val name = "拂大苏打阿松大阿松大阿松大阿萨的撒的阿松大阿萨大晓"

        val url: String = "https://apis.netstart.cn/music/search?keywords=${URLEncoder.encode(name, "utf-8")}"
        val url1: URL = URL(url)

        val connection: URLConnection = url1.openConnection()
        val json: String = connection.inputStream.reader().readText()
    }
}