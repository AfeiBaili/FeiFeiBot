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
        queryServer("frp-toe.com", 43360)
    }

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