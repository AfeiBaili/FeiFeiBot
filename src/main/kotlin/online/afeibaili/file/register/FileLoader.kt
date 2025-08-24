package online.afeibaili.file.register

import online.afeibaili.file.register.loader.Loader
import online.afeibaili.logger
import java.io.File


/**
 * 文件注册器，用来获取文件内容
 *
 *@author AfeiBaili
 *@version 2025/8/24 10:45
 */

class FileLoader() {
    fun <T> load(filePath: String, loader: Loader<T>): T {
        val file: File = File(filePath)
        if (!file.exists()) {
            logger("$filePath 文件不存在，已创建文件")
            file.parentFile.mkdirs()
            file.createNewFile()
        }

        val text: String = file.readText(Charsets.UTF_8)
        return loader.load(text)
    }

    fun <T> loadFilesAsStringToList(directory: String, process: (File) -> T): List<T> {
        val file: File = File(directory)
        if (!file.exists()) {
            logger("${directory}目录不存在，已创建文件")
            file.mkdirs()
            return emptyList()
        }
        val mutableList: MutableList<T> = mutableListOf()
        file.listFiles()!!.forEach { file ->
            val t: T = process.invoke(file)
            mutableList.add(t)
        }
        return mutableList
    }
}