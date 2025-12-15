package online.afeibaili.module.minecraft

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.ObjectMapper
import online.afeibaili.command.Command
import online.afeibaili.command.Commands
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket


/**
 *获取服务器信息
 *
 *@author AfeiBaili
 *@version 2025/12/15 19:28
 */

object MinecraftServerList {
    val JSON = ObjectMapper()

    fun load() {
        Commands.register("mus", Command({ param, event ->
            if (param.size != 2) return@Command "mus xxx.xx:25565"
            val split: List<String> = param[1].split(":")
            if (split.size != 2) return@Command "格式不对"

            val host: String = split[0]
            var port: Int = 0
            runCatching {
                port = split[1].toInt()
            }.onFailure {
                return@Command "端口错误"
            }

            val socket = Socket(host, port)
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

            val msljm = JSON.readValue(json, MinecraftServerListJsonMapper::class.java)

            var isEmptyPlayer = true
            if (msljm.players.online != 0) isEmptyPlayer = false

            fun playerToString(players: List<MinecraftServerListJsonMapper.Players.Player>): String {
                val builder = StringBuilder()
                players.dropLast(1).forEach { player ->
                    builder.appendLine("  ︱  玩家名字：${player.name}")
                }

                val lastPlayer = players.last()
                builder.append("  ︱--玩家名字：${lastPlayer.name}")
                return builder.toString()
            }

            buildString {
                appendLine("服务器地址${if (isEmptyPlayer) "🔴" else "🟢"}：$host:$port")
                appendLine("服务器信息：")
                appendLine("  ︱  描述信息：${msljm.description.text}")
                appendLine("  ︱  版本号：${msljm.version.name}")
                appendLine("  ︱--协议号：${msljm.version.protocol}")
                appendLine("当前人数：${msljm.players.online}")
                appendLine(playerToString(msljm.players.sample))
                append("最大人数：${msljm.players.max}")
            }
        }))
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class MinecraftServerListJsonMapper() {
    var favicon: String = ""
    var description: Description = Description()
    var players: Players = Players()
    var version: Version = Version()

    class Players() {
        var max = 0
        var online = 0
        var sample = listOf<Player>()

        class Player {
            var id: String = ""
            var name: String = ""
        }
    }

    class Description {
        var text = ""
    }

    class Version {
        var name = ""
        var protocol = 0
    }
}