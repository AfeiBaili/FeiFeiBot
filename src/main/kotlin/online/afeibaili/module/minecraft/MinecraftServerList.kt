package online.afeibaili.module.minecraft

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

            fun writeVarInt(value: Int) {
                var value = value
                while ((value and -128) != 0) {
                    handshake.writeByte(value and 127 or 128)
                    value = value ushr 7
                }
                handshake.writeByte(value)
            }

            fun readVarInt(): Int {
                var numRead = 0
                var result = 0
                var read: Byte

                do {
                    read = dataInputStream.readByte()
                    val value = (read.toInt() and 127)
                    result = result or (value shl (7 * numRead))
                    numRead++
                } while ((read.toInt() and 128) != 0)

                return result
            }

            fun writeString(str: String) {
                val bytes: ByteArray = str.toByteArray()
                writeVarInt(bytes.size)
                handshake.write(bytes)
            }

            fun readString(): String {
                val length: Int = readVarInt()
                val bytes = ByteArray(length)
                dataInputStream.readFully(bytes)
                return String(bytes)
            }

            writeVarInt(0x00)
            writeVarInt(754)
            writeString(host)
            handshake.writeShort(port)
            writeVarInt(1)

            writeVarInt(handshakeBytes.size())
            dataOutputStream.write(handshakeBytes.toByteArray())

            dataOutputStream.write(0x01)
            dataOutputStream.write(0x00)

            readVarInt()
            readVarInt()
            val json = readString()

            socket.close()

            val msljm = JSON.readValue(json, MinecraftServerListJsonMapper::class.java)

            var isEmptyPlayer = true
            if (msljm.players.online != 0) isEmptyPlayer = false

            fun playerToString(players: List<MinecraftServerListJsonMapper.Players.Player>): String {
                val builder = StringBuilder()
                players.dropLast(1).forEach { player ->
                    builder.append(
                        """
                              |  玩家名字：${player.name}
                              |    |--uuid：${player.id + "\n"}
                        """.trimIndent()
                    )
                }

                val lastPlayer = players.last()
                builder.append(
                    """
                          |  玩家名字：${lastPlayer.name}
                          |----|--uuid：${lastPlayer.id}
                    """.trimIndent()
                )
                return builder.toString()
            }

            """
                服务器地址💻：${"$host:$port"}
                服务器信息：
                  |  描述信息：${msljm.description}
                  |  版本号：${msljm.version.name}
                  |--协议号：${msljm.version.protocol}
                
                最大人数：${msljm.players.max}
                当前人数${if (isEmptyPlayer) "🔴" else "🟢"}：${msljm.players.online}
                ${playerToString(msljm.players.sample)}
            """.trimIndent()
        }))
    }
}

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