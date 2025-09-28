package online.afeibaili.module.music

import com.fasterxml.jackson.databind.ObjectMapper
import net.mamoe.mirai.message.data.ForwardMessageBuilder
import net.mamoe.mirai.message.data.PlainText
import online.afeibaili.command.Command
import online.afeibaili.command.Commands
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
        Commands.register("网易云id", Command({ param, event ->
            if (param.size < 2) return@Command "网易云id <关键词>"

            val keyWords: List<String> = param.drop(1)

            val stringBuilder = StringBuilder()
            keyWords.forEach {
                stringBuilder.append(it).append(" ")
            }
            val keyWord: String = stringBuilder.deleteCharAt(stringBuilder.length - 1).toString()

            val openConnection: URLConnection =
                URL(wyyUrl + URLEncoder.encode(keyWord, "utf-8")).openConnection()

            val responseJson: String = openConnection.inputStream.reader().use {
                it.readText()
            }

            var music = Music()

            runCatching {
                music = json.readValue(responseJson, Music::class.java)
            }.onFailure { error ->
                error.printStackTrace()
                return@Command "映射错误，请联系管理员"
            }

            if (music.code != 200) return@Command "网络错误代码：${music.code}"

            val forwardMessageBuilder = ForwardMessageBuilder(event.subject)
            val songs: List<Song> = music.result.songs
            songs.forEach { song ->
                val sb: StringBuilder = StringBuilder()
                song.artists.forEach { artist ->
                    sb.append(artist.name).append("、")
                }

                val isEmpty: Boolean = sb.isEmpty()

                forwardMessageBuilder.add(
                    event.subject.bot.id,
                    song.name,
                    PlainText(
                        """
                        id：${song.id}
                        
                        歌名：${song.name}
                        艺术家：${if (isEmpty) "无" else sb.deleteCharAt(sb.length - 1).toString()}
                    """.trimIndent()
                    )
                )
            }

            if (forwardMessageBuilder.isEmpty()) return@Command "搜不到任何信息"
            event.subject.sendMessage(forwardMessageBuilder.build())

            "已发送网易云搜索到的音乐ID"
        }))
    }
}