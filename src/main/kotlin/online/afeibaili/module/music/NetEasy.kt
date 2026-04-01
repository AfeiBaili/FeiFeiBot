package online.afeibaili.module.music

import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.ForwardMessageBuilder
import net.mamoe.mirai.message.data.OfflineAudio
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.utils.ExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import online.afeibaili.command.Command
import online.afeibaili.command.CommandRegistry
import online.afeibaili.command.ParamType
import online.afeibaili.module.music.map.Music
import online.afeibaili.module.music.map.Song
import java.io.InputStream
import java.net.URI


/**
 * 网易云模块
 *
 *@author AfeiBaili
 *@version 2026/3/27 19:40
 */

object NetEasy {
    fun load() {
        CommandRegistry.registerWithChild(
            "网易云", "net-easy", action = { p, e -> "todo" }, commands = arrayOf(search, song, songById)
        )
    }

    private suspend fun sendSong(groupMessageEvent: GroupMessageEvent, stream: InputStream) {
        val resource: ExternalResource = stream.toExternalResource()
        val audioMessage: OfflineAudio = groupMessageEvent.group.uploadAudio(resource)
        groupMessageEvent.subject.sendMessage(audioMessage)
        resource.close()
    }

    val song = Command("点歌", "song", 0, ParamType.STRING) { p, e ->
        if (e !is GroupMessageEvent) return@Command "请在群聊中使用"
        val text: String = p.joinToString(" ").also { if (it.isEmpty()) return@Command "关键字不可为空" }
        val idsMusic: Music? = queryIds(text)
        idsMusic ?: return@Command "网络出错"
        val songInfo: Song = runCatching {
            idsMusic.result.songs.get(0)
        }.getOrElse { return@Command "找不到此歌曲" }
        val songId: String = songInfo.id.toString()
        val songUri: URI? = getSongUriById(songId)
        songUri ?: return@Command "无法拿到歌曲"
        val songStream: InputStream? = getSongStreamByUri(songUri)
        songStream ?: return@Command "无法获取歌曲流"
        sendSong(e, songStream)
        """
            🆔I D：$songId
            🎵歌曲：${songInfo.name}
            🤵作者：${songInfo.artists.joinToString("、") { it.name }}
        """.trimIndent()
    }

    val songById = Command("id点歌", "get-by-id", 0, ParamType.LONG) { p, e ->
        if (e !is GroupMessageEvent) return@Command "请在群聊中使用"
        val id: Long = runCatching { p[0].toLong() }.getOrElse { return@Command "请输入歌曲id" }
        val songUri: URI? = getSongUriById(id.toString())
        songUri ?: return@Command "无法拿到歌曲"
        val songStream: InputStream? = getSongStreamByUri(songUri)
        songStream ?: return@Command "无法获取歌曲流"
        sendSong(e, songStream)
        "已发送id为${id}的音乐"
    }

    val search = Command("获取id", "get-id", 0, ParamType.STRING) { p, e ->
        val text: String = p.joinToString(" ").also { if (it.isEmpty()) return@Command "关键字不可为空" }
        runCatching {
            val music: Music = queryIds(text) ?: return@Command "查询网络出错"

            if (music.code != 200) return@Command "网络错误代码：${music.code}"

            val forwardMessageBuilder = ForwardMessageBuilder(e.subject)
            val songs: List<Song> = music.result.songs
            songs.forEach { song ->
                val sb: StringBuilder = StringBuilder()
                song.artists.forEach { artist ->
                    sb.append(artist.name).append("、")
                }

                val isEmpty: Boolean = sb.isEmpty()

                forwardMessageBuilder.add(
                    e.subject.bot.id, song.name, PlainText(
                        """
                        ID：${song.id}
                        
                        🎵：${song.name}
                        作家‍️：${if (isEmpty) "无" else sb.deleteCharAt(sb.length - 1).toString()}
                    """.trimIndent()
                    )
                )
            }

            if (forwardMessageBuilder.isEmpty()) return@Command "搜不到任何信息"
            e.subject.sendMessage(forwardMessageBuilder.build())

            "已发送网易云搜索到的音乐ID"
        }.getOrElse { return@Command "无法连接服务器" }
    }
}