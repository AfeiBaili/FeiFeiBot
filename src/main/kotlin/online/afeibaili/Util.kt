package online.afeibaili

import online.afeibaili.LoggerLevel.*
import online.afeibaili.command.Command
import online.afeibaili.file.ConfigFile
import online.afeibaili.file.LevelMapFile
import online.afeibaili.file.json.JsonConfigMap

private const val isLoggerPrint = false

lateinit var config: JsonConfigMap
lateinit var configObject: ConfigFile
lateinit var levelMap: MutableMap<Long, Int>
lateinit var commandsMap: MutableMap<String, Command>
lateinit var levelObject: LevelMapFile

fun logger(message: Any, level: LoggerLevel = INFO) {
    if (isLoggerPrint) {
        when (level) {
            INFO -> FeiFeiBot.miraiLogger.info(message.toString())
            DEBUG -> FeiFeiBot.miraiLogger.debug(message.toString())
            WARN -> FeiFeiBot.miraiLogger.warning(message.toString())
            ERROR -> FeiFeiBot.miraiLogger.error(message.toString())
        }
    } else println(message.toString())
}

enum class LoggerLevel {
    INFO,
    DEBUG,
    WARN,
    ERROR
}