package online.afeibaili.module.terraria

import online.afeibaili.command.Command
import online.afeibaili.command.CommandRegistry
import online.afeibaili.command.ParamType
import java.net.Socket


/**
 * 泰拉瑞亚类
 *
 *@author AfeiBaili
 *@version 2026/1/29 19:03
 */

object Terraria {
    fun load() {
        CommandRegistry.registerWithChild(
            "泰拉瑞亚", "terraria", 0, ParamType.NOTHING, { _, _ ->
                val count: Int = runCatching {
                    sendCommandStream("/command/count", 1, "afeibaili.cn", 7779)
                }.getOrElse { return@registerWithChild "无法连接到目标地址" }

                toCountString(count)
            },
            Command("服务器查询", "server") { p, _ ->
                val hostStr: String =
                    runCatching { p[0] }.getOrElse { return@Command "请指定服务器参数，示例：127.0.0.1:7778" }
                val strings: List<String> = hostStr.split("[:：]".toRegex())

                val host: String = runCatching { strings[0] }.getOrElse { return@Command "未指定地址" }
                val port: Int =
                    runCatching { strings[1].toInt() }.getOrElse { return@Command "无法获取端口${strings[1]}" }

                val count: Int = runCatching { sendCommandStream("/command/count", 1, host, port) }.getOrElse {
                    return@Command "无法连接此服务器"
                }
                toCountString(count)
            }
        )
    }

    fun sendCommandStream(command: String, size: Int, host: String, port: Int): Int {
        val resultBytes = ByteArray(size)
        val commandBytes = command.toByteArray()
        val socket = Socket(host, port)
        socket.outputStream.write(commandBytes)
        socket.outputStream.flush()
        socket.inputStream.read(resultBytes)
        socket.close()
        return resultBytes[0].toInt()
    }

    fun toCountString(count: Int): String {
        if (count == 0)
            return "🔴服务器当前无人在线"
        return "🟢服务器当前人数有${count}人"
    }
}