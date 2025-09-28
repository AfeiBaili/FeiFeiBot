import com.fasterxml.jackson.databind.ObjectMapper
import online.afeibaili.module.music.map.Music
import java.net.URL
import java.net.URLConnection
import java.net.URLEncoder
import kotlin.test.Test

/**
 * 测试音乐
 *
 *@author AfeiBaili
 *@version 2025/9/28 12:16
 */

class MusicTest {
    @Test
    fun test1() {
        val name = "拂大苏打阿松大阿松大阿松大阿萨的撒的阿松大阿萨大晓"

        val url: String = "https://apis.netstart.cn/music/search?keywords=${URLEncoder.encode(name, "utf-8")}"
        val url1: URL = URL(url)

        val connection: URLConnection = url1.openConnection()
        val json: String = connection.inputStream.reader().readText()
    }
}