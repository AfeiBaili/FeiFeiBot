package online.afeibaili.file

import online.afeibaili.config
import online.afeibaili.logger
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.*

class LevelMapFile {
    val levelMap: MutableMap<Long, Int> = HashMap()
    private val levelMapProperties = Properties()

    val file = File(System.getProperty("user.dir") + "/data/feifei/level-map.properties")

    constructor() {
        file.parentFile.mkdirs()
        if (!file.exists()) {
            val writer = FileWriter(file)
            levelMapProperties.put(config.master.toString(), 999.toString())
            levelMapProperties.store(writer, null)
            logger("已创建等级映射文件")
        }
        refresh()
    }

    fun setLevelMap(qq: Long, level: Int) {
        levelMapProperties.remove(qq.toString())
        levelMapProperties[qq.toString()] = level
        FileWriter(file).use {
            levelMapProperties.store(it, null)
        }
        refresh()
    }

    fun refresh() {
        val reader = FileReader(file)
        levelMapProperties.load(reader)
        levelMap.clear()
        levelMapProperties.forEach { it ->
            levelMap.put(it.key.toString().toLong(), it.value.toString().toInt())
        }
    }
}