package online.afeibaili.module.minecraft

import com.fasterxml.jackson.databind.ObjectMapper
import net.mamoe.mirai.message.data.ForwardMessageBuilder
import net.mamoe.mirai.message.data.PlainText
import online.afeibaili.command.Command
import online.afeibaili.command.CommandRegistry
import online.afeibaili.command.ParamType
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.function.Consumer
import javax.naming.directory.InitialDirContext

object Minecraft {
    private val JSON = ObjectMapper()

    fun load() {
        CommandRegistry.registerWithChild(
            "Minecraft", "mc", 0, ParamType.NOTHING, { _, _ ->
                "请使用其他子命令"
            }, Command("搜（遗弃的）", "search", 0, ParamType.STRING) { p, e ->
                val text: String =
                    runCatching { p.joinToString(" ") }.getOrElse { return@Command "请输入词条和mod名称" }
                runCatching {
                    val document = Jsoup.connect(
                        "https://search.mcmod.cn/s?key=" + URLEncoder.encode(
                            text,
                            StandardCharsets.UTF_8
                        ) + "&site=&filter=0&mold=0"
                    ).get()
                    val takeTime = document.getElementsByClass("info")[0]
                    val startTakeChar = takeTime.toString().indexOf('[')
                    val endTakeChar = takeTime.toString().indexOf(']')

                    val item = document.getElementsByClass("result-item")
                    val forwardMessageBuilder = ForwardMessageBuilder(e.subject)
                    item.forEach(Consumer { element: Element? ->
                        val message = StringBuilder()
                        val aTag = element!!.getElementsByClass("head")[0].lastElementChild()
                        if (aTag != null) {
                            val string: String = element.getElementsByClass("body").text()
                            val take: String = string.take((string.length * 0.7).toInt())
                            message.append("📌").append(aTag.text()).append('\n')
                                .append("🔗").append(aTag.attr("href")).append('\n')
                                .append("📜").append(take).append("...")
                        }
                        forwardMessageBuilder.add(e.subject.bot.id, "查询结果", PlainText(message.toString()))
                    })
                    if (forwardMessageBuilder.isEmpty()) {
                        return@Command "搜不到任何信息"
                    } else e.subject.sendMessage(forwardMessageBuilder.build())

                    try {
                        return@Command "从MC百科查找到" + item.size + "条结果；" + takeTime.toString()
                            .substring(startTakeChar + 1, endTakeChar)
                    } catch (_: StringIndexOutOfBoundsException) {
                        return@Command "搜不到任何信息"
                    }
                }.getOrElse { return@Command "无法链接MCMod百科" }
            }, Command("服务器查询", "select-server", 0, ParamType.STRING) { p, _ ->
                val (host, port) = runCatching {
                    val split: List<String> = p[0].split(":")

                    if (split.size != 2) {
                        val srv: Pair<String, Int>? = querySrv(split[0])
                        if (srv != null) {
                            return@runCatching srv
                        }
                    }

                    val host: String = split[0]
                    val port: Int = runCatching { split[1].toInt() }.getOrElse { 25565 }


                    host to port
                }.getOrElse { return@Command "请输入服务器地址" }

                runCatching {
                    val socket = Socket()
                    socket.connect(InetSocketAddress(host, port), 5000)
                    val dataOutputStream = DataOutputStream(socket.getOutputStream())
                    val dataInputStream = DataInputStream(socket.getInputStream())

                    val handshakeBytes = ByteArrayOutputStream()
                    val handshake = DataOutputStream(handshakeBytes)

                    fun writeVarInt(dataOut: DataOutputStream, value: Int) {
                        var value = value
                        while ((value and -128) != 0) {
                            dataOut.writeByte(value and 127 or 128)
                            value = value ushr 7
                        }
                        dataOut.writeByte(value)
                    }

                    fun readVarInt(dataIn: DataInputStream): Int {
                        var numRead = 0
                        var result = 0
                        var read: Byte

                        do {
                            read = dataIn.readByte()
                            val value = (read.toInt() and 127)
                            result = result or (value shl (7 * numRead))
                            numRead++
                        } while ((read.toInt() and 128) != 0)

                        return result
                    }

                    fun writeString(dataOut: DataOutputStream, str: String) {
                        val bytes: ByteArray = str.toByteArray()
                        writeVarInt(dataOut, bytes.size)
                        dataOut.write(bytes)
                    }

                    fun readString(dataIn: DataInputStream): String {
                        val length: Int = readVarInt(dataIn)
                        val bytes = ByteArray(length)
                        dataIn.readFully(bytes)
                        return String(bytes)
                    }

                    writeVarInt(handshake, 0x00)
                    writeVarInt(handshake, 754)
                    writeString(handshake, host)
                    handshake.writeShort(port)
                    writeVarInt(handshake, 1)

                    writeVarInt(dataOutputStream, handshakeBytes.size())
                    dataOutputStream.write(handshakeBytes.toByteArray())

                    dataOutputStream.write(0x01)
                    dataOutputStream.write(0x00)

                    readVarInt(dataInputStream)
                    readVarInt(dataInputStream)
                    val json = readString(dataInputStream)

                    socket.close()


                    val mcInfo = runCatching {
                        JSON.readValue(json, MinecraftServerListJsonMapperNew::class.java)
                    }.getOrElse { JSON.readValue(json, MinecraftServerListJsonMapperOld::class.java) }

                    fun playerToString(players: List<Players.Player>): String {
                        val builder = StringBuilder()
                        players.dropLast(1).forEach { player ->
                            builder.appendLine("  ︱  玩家名字：${player.name}")
                        }

                        val lastPlayer = players.last()
                        builder.append("  ︱--玩家名字：${lastPlayer.name}")
                        return builder.toString()
                    }

                    fun getServerInfo(): String {
                        return buildString {
                            appendLine("服务器信息：")
                            if (mcInfo is MinecraftServerListJsonMapperNew)
                                appendLine("  ︱  描述信息：${mcInfo.description}")
                            else if (mcInfo is MinecraftServerListJsonMapperOld)
                                appendLine("  ︱  描述信息：${mcInfo.description.translate}")
                            appendLine("  ︱  版本号：${mcInfo.version.name}")
                            appendLine("  ︱--协议号：${mcInfo.version.protocol}")
                        }
                    }

                    fun getZeroPlayerOnlineMessage(): String {
                        return buildString {
                            appendLine("服务器地址🔴：$host:$port")
                            append(getServerInfo())
                            append("当前没有人在线")
                        }
                    }

                    fun getMultiPlayerOnlineMessage(): String {
                        return buildString {
                            appendLine("服务器地址🟢：$host:$port")
                            append(getServerInfo())
                            appendLine("当前人数：${mcInfo.players.online}")
                            appendLine(playerToString(mcInfo.players.sample))
                            append("最大人数：${mcInfo.players.max}")
                        }
                    }

                    if (mcInfo.players.online == 0) {
                        return@Command getZeroPlayerOnlineMessage()
                    }

                    return@Command getMultiPlayerOnlineMessage()
                }.getOrElse { exception ->
                    return@Command "网络流错误：${exception.message}"
                }
            }
        )
    }

    val env = Hashtable<String, String>().apply {
        put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory")
        put("java.naming.provider.url", "dns:")
    }

    private fun querySrv(domain: String, srvPrefix: String = "_minecraft._tcp."): Pair<String, Int>? {
        return runCatching {
            val ctx = InitialDirContext(env)
            val attrs = ctx.getAttributes(srvPrefix + domain, arrayOf("SRV"))
            val srvAttr = attrs.get("SRV")
            srvAttr ?: return null
            val split: List<String> = srvAttr.get(0).toString().split("\\s+".toRegex())
            split[3] to split[2].toInt()
        }.getOrElse {
            null
        }
    }
}