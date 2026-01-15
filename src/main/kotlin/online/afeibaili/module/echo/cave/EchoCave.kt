package online.afeibaili.module.echo.cave

import com.fasterxml.jackson.databind.ObjectMapper
import online.afeibaili.command.Command
import online.afeibaili.command.CommandRegistry
import online.afeibaili.command.ParamType
import online.afeibaili.file.register.FileLoader
import online.afeibaili.file.register.FileWriter
import java.util.*

/**
 * 回声洞逻辑
 *
 *@author AfeiBaili
 *@version 2025/8/24 10:28
 */

object EchoCave {
    private val fileLoader = FileLoader()
    private val random = Random()
    private val directory = "${System.getProperty("user.dir")}/data/feifei/EchoCave"
    private var showQQNumber = false
    private val jsonMapper = ObjectMapper()

    fun load() {
        val dataList =
            fileLoader.loadFilesAsStringToList(directory) { file ->
                val text: String = file.readText()
                jsonMapper.readValue(text, Data::class.java)
            }.toMutableList()

        CommandRegistry.registerWithChild("回声洞", "echo-cave", 0, ParamType.NOTHING, { _, _ ->
            if (dataList.isEmpty()) return@registerWithChild "回声洞暂时为空"
            val randomData: Data = dataList[random.nextInt(0, dataList.size)]
            val builder: StringBuilder = StringBuilder()
            builder.append(randomData.message).append("\n——").append(randomData.nick)
            if (showQQNumber) builder.append(" ").append(randomData.id)
            return@registerWithChild builder.toString()
        }, Command("录入", "write-in", 0, ParamType.STRING) { p, e ->
            val text: String = p.joinToString(" ").also { if (it.isEmpty()) return@Command "参数不可为空" }
            val id: Long = e.sender.id
            val nick: String = e.sender.nick

            val data = Data(text, id, nick)
            dataList.add(data)
            FileWriter.writeAndCreateFile(
                directory,
                UUID.randomUUID().toString(),
                jsonMapper.writeValueAsString(data),
            )

            "已录入此消息"
        }, Command("显示id", "show-id") { _, _ ->
            if (showQQNumber) return@Command "当前已是显示状态"
            showQQNumber = true
            "接下里的回声将会显示QQ号"
        }, Command("隐藏id", "hide-id") { _, _ ->
            if (!showQQNumber) return@Command "当前已是隐藏状态"
            showQQNumber = false
            "接下里的回声将会隐藏QQ号"
        }, Command("当前数量", "current-number") { _, _ -> "当前回声数为${dataList.size}句" }
        )
    }
}