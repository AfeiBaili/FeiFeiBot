package online.afeibaili.file.register

import java.io.File


/**
 * 写入配置文件之类的内容
 *
 *@author AfeiBaili
 *@version 2025/8/24 22:07
 */

object FileWriter {
    fun write(filePath: String, text: String) {
        val file = File(filePath)
        check(file.exists()) { "${filePath}文件不存在！" }
        file.writeText(text)
    }

    fun writeAndCreateFile(directory: String, fileName: String, text: String) {
        val file = File(directory, fileName)
        file.createNewFile()
        file.writeText(text)
    }
}