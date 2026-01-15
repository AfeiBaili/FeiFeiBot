package online.afeibaili.module.music

import com.fasterxml.jackson.databind.ObjectMapper
import net.mamoe.mirai.message.data.ForwardMessageBuilder
import net.mamoe.mirai.message.data.PlainText
import online.afeibaili.command.CommandRegistry
import online.afeibaili.command.ParamType
import online.afeibaili.module.music.map.Music
import online.afeibaili.module.music.map.Song
import java.net.URL
import java.net.URLConnection
import java.net.URLEncoder

/**
 * 搜索网易云音乐id
 *
 *@author AfeiBaili
 *@version 2025/9/28 12:33
 */

object SearchMusicId {
    const val wyyUrl = "https://apis.netstart.cn/music/search?keywords="

    val json = ObjectMapper()

    fun load() {
        CommandRegistry.register("网易云Id", "wyyid", 0, ParamType.STRING) { p, e ->
            val text: String = p.joinToString { " " }.also { if (it.isEmpty()) return@register "关键字不可为空" }

            runCatching {
                val openConnection: URLConnection =
                    URL(wyyUrl + URLEncoder.encode(text, "utf-8")).openConnection()

                val responseJson: String = openConnection.inputStream.reader().use {
                    it.readText()
                }

                var music = Music()

                runCatching {
                    music = json.readValue(responseJson, Music::class.java)
                }.onFailure { error ->
                    error.printStackTrace()
                    return@register "映射错误，请联系管理员"
                }

                if (music.code != 200) return@register "网络错误代码：${music.code}"

                val forwardMessageBuilder = ForwardMessageBuilder(e.subject)
                val songs: List<Song> = music.result.songs
                songs.forEach { song ->
                    val sb: StringBuilder = StringBuilder()
                    song.artists.forEach { artist ->
                        sb.append(artist.name).append("、")
                    }

                    val isEmpty: Boolean = sb.isEmpty()

                    forwardMessageBuilder.add(
                        e.subject.bot.id,
                        song.name,
                        PlainText(
                            """
                        ID：${song.id}
                        
                        🎵：${song.name}
                        作家‍️：${if (isEmpty) "无" else sb.deleteCharAt(sb.length - 1).toString()}
                    """.trimIndent()
                        )
                    )
                }

                if (forwardMessageBuilder.isEmpty()) return@register "搜不到任何信息"
                e.subject.sendMessage(forwardMessageBuilder.build())

                "已发送网易云搜索到的音乐ID"
            }.getOrElse { return@register "无法连接服务器" }
        }
    }
}