import com.fasterxml.jackson.databind.ObjectMapper
import online.afeibaili.module.minecraft.MinecraftServerListJsonMapperNew
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.charset.StandardCharsets
import kotlin.test.Test


/**
 *
 *
 *@author AfeiBaili
 *@version 2025/12/15 19:11
 */

class MinecraftTest {
    @Test
    fun testPing() {
//        println(testServer(arrayOf("u:25565")))
    }

    val jsonMapper = ObjectMapper()

//    fun testServer(p: Array<String>): String {
//        val (host, port) = runCatching {
//            val split: List<String> = p[0].split(":")
//            val host: String = split[0]
//            val port: Int = runCatching { split[1].toInt() }.getOrElse { 25565 }
//            host to port
//        }.getOrElse { return "请输入服务器地址" }
//
//        runCatching {
//            val socket = Socket()
//            socket.connect(InetSocketAddress(host, port), 5000)
//            val dataOutputStream = DataOutputStream(socket.getOutputStream())
//            val dataInputStream = DataInputStream(socket.getInputStream())
//
//            val handshakeBytes = ByteArrayOutputStream()
//            val handshake = DataOutputStream(handshakeBytes)
//
//            fun writeVarInt(dataOut: DataOutputStream, value: Int) {
//                var value = value
//                while ((value and -128) != 0) {
//                    dataOut.writeByte(value and 127 or 128)
//                    value = value ushr 7
//                }
//                dataOut.writeByte(value)
//            }
//
//            fun readVarInt(dataIn: DataInputStream): Int {
//                var numRead = 0
//                var result = 0
//                var read: Byte
//
//                do {
//                    read = dataIn.readByte()
//                    val value = (read.toInt() and 127)
//                    result = result or (value shl (7 * numRead))
//                    numRead++
//                } while ((read.toInt() and 128) != 0)
//
//                return result
//            }
//
//            fun writeString(dataOut: DataOutputStream, str: String) {
//                val bytes: ByteArray = str.toByteArray()
//                writeVarInt(dataOut, bytes.size)
//                dataOut.write(bytes)
//            }
//
//            fun readString(dataIn: DataInputStream): String {
//                val length: Int = readVarInt(dataIn)
//                val bytes = ByteArray(length)
//                dataIn.readFully(bytes)
//                return String(bytes)
//            }
//
//            writeVarInt(handshake, 0x00)
//            writeVarInt(handshake, 754)
//            writeString(handshake, host)
//            handshake.writeShort(port)
//            writeVarInt(handshake, 1)
//
//            writeVarInt(dataOutputStream, handshakeBytes.size())
//            dataOutputStream.write(handshakeBytes.toByteArray())
//
//            dataOutputStream.write(0x01)
//            dataOutputStream.write(0x00)
//
//            readVarInt(dataInputStream)
//            readVarInt(dataInputStream)
//            val json = readString(dataInputStream)
//
//            socket.close()
//
//            val mcInfo = jsonMapper.readValue(json, MinecraftServerListJsonMapperNew::class.java)
//
//            fun playerToString(players: List<MinecraftServerListJsonMapperNew.Players.Player>): String {
//                val builder = StringBuilder()
//                players.dropLast(1).forEach { player ->
//                    builder.appendLine("  ︱  玩家名字：${player.name}")
//                }
//
//                val lastPlayer = players.last()
//                builder.append("  ︱--玩家名字：${lastPlayer.name}")
//                return builder.toString()
//            }
//
//            if (mcInfo.players.online == 0) {
//                return buildString {
//                    appendLine("服务器地址🔴：$host:$port")
//                    appendLine("服务器信息：")
//                    appendLine("  ︱  描述信息：${mcInfo.description}")
//                    appendLine("  ︱  版本号：${mcInfo.version.name}")
//                    appendLine("  ︱--协议号：${mcInfo.version.protocol}")
//                    appendLine("当前没有人在线")
//                }
//            }
//
//            return buildString {
//                appendLine("服务器地址🟢：$host:$port")
//                appendLine("服务器信息：")
//                appendLine("  ︱  描述信息：${mcInfo.description}")
//                appendLine("  ︱  版本号：${mcInfo.version.name}")
//                appendLine("  ︱--协议号：${mcInfo.version.protocol}")
//                appendLine("当前人数：${mcInfo.players.online}")
//                appendLine(playerToString(mcInfo.players.sample))
//                append("最大人数：${mcInfo.players.max}")
//            }
//        }.getOrElse { exception ->
//            exception.printStackTrace()
//            return "网络流错误：${exception.message}"
//        }
//    }

    @Throws(Exception::class)
    fun queryServer(host: String, port: Int) {
        val socket: Socket = Socket()
        socket.connect(InetSocketAddress(host, port), 3000)

        val out = DataOutputStream(socket.getOutputStream())
        val `in` = DataInputStream(socket.getInputStream())

        // Handshake
        val handshakeBytes = ByteArrayOutputStream()
        val handshake = DataOutputStream(handshakeBytes)

        writeVarInt(handshake, 0x00) // packet id
        writeVarInt(handshake, 754) // 协议版本（1.20.1 可用）
        writeString(handshake, host)
        handshake.writeShort(port)
        writeVarInt(handshake, 1) // 状态

        writeVarInt(out, handshakeBytes.size())
        out.write(handshakeBytes.toByteArray())

        // 状态请求
        out.writeByte(0x01)
        out.writeByte(0x00)

        readVarInt(`in`) // size
        readVarInt(`in`) // packet id
        val json = readString(`in`)

        println("服务器返回信息：")
        println(json)

        socket.close()
    }


    // ===== 工具方法 =====
    @Throws(IOException::class)
    private fun writeVarInt(out: DataOutputStream, value: Int) {
        var value = value
        while ((value and -128) != 0) {
            out.writeByte(value and 127 or 128)
            value = value ushr 7
        }
        out.writeByte(value)
    }

    @Throws(IOException::class)
    private fun readVarInt(`in`: DataInputStream): Int {
        var numRead = 0
        var result = 0
        var read: Byte
        do {
            read = `in`.readByte()
            val value = (read.toInt() and 127)
            result = result or (value shl (7 * numRead))
            numRead++
        } while ((read.toInt() and 128) != 0)

        return result
    }

    @Throws(IOException::class)
    private fun writeString(out: DataOutputStream, str: String) {
        val bytes: ByteArray = str.toByteArray()
        writeVarInt(out, bytes.size)
        out.write(bytes)
    }

    @Throws(IOException::class)
    private fun readString(`in`: DataInputStream): String {
        val length = readVarInt(`in`)
        val bytes = ByteArray(length)
        `in`.readFully(bytes)
        return String(bytes, StandardCharsets.UTF_8)
    }
}