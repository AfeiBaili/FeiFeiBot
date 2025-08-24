package online.afeibaili.module.echo.cave

import com.fasterxml.jackson.databind.ObjectMapper
import online.afeibaili.command.Command
import online.afeibaili.command.Commands
import online.afeibaili.file.register.FileLoader
import online.afeibaili.file.register.FileWriter
import java.util.Random
import java.util.UUID

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

        Commands.register("回声洞", Command({ param, event ->
            if (dataList.isEmpty()) return@Command "回声洞暂时为空"
            val randomData: Data = dataList[random.nextInt(0, dataList.size)]
            val builder: StringBuilder = StringBuilder()
            builder.append(randomData.message).append("\n——").append(randomData.nick)
            if (showQQNumber) builder.append(" ").append(randomData.id)
            return@Command builder.toString()
        }))

        Commands.register("回声录入", Command({ param, event ->
            if (param.size == 1) return@Command "回声录入 <消息>"
            if (param[1].isEmpty()) return@Command "请输入消息"

            val builder: StringBuilder = StringBuilder()
            param.drop(1).forEach {
                builder.append(it).append(" ")
            }
            val message = builder.removeSuffix(" ").toString()
            val id: Long = event.sender.id
            val nick: String = event.sender.nick

            val data = Data(message, id, nick)
            dataList.add(data)
            FileWriter.writeAndCreateFile(
                directory,
                UUID.randomUUID().toString(),
                jsonMapper.writeValueAsString(data),
            )

            "已录入此消息"
        }))

        Commands.register("显示回声ID", Command({ param, event ->
            if (showQQNumber) return@Command "当前已是显示状态"
            showQQNumber = true
            "接下里的回声将会显示QQ号"
        }, level = 1))

        Commands.register("隐藏回声ID", Command({ param, event ->
            if (!showQQNumber) return@Command "当前已是隐藏状态"
            showQQNumber = false
            "接下里的回声将会隐藏QQ号"
        }, level = 1))

        Commands.register("当前回声数", Command({ param, event ->
            "当前回声数为${dataList.size}句"
        }))
    }


}