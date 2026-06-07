package online.afeibaili.file

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import online.afeibaili.LoggerLevel
import online.afeibaili.file.json.JsonConfigMap
import online.afeibaili.logger
import java.io.File
import java.io.FileWriter
import java.nio.charset.StandardCharsets

class ConfigFile(val path: String) {

    lateinit var config: JsonConfigMap
    val scopeIo: CoroutineScope = CoroutineScope(Dispatchers.IO)

    fun store() {
        scopeIo.launch {
            val objectMapper: ObjectMapper = ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
            FileWriter(path, StandardCharsets.UTF_8).use {
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(it, config)
            }
        }
    }

    constructor() : this(System.getProperty("user.dir") + "/config/feifei/config.json") {
        try {
            var file = File(path)
            file.parentFile.mkdirs()
            if (!file.exists()) {
                FileWriter(file).use {
                    val mapper = ObjectMapper()
                    it.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(JsonConfigMap()))
                }
                logger("配置文件已生成在${file.absolutePath}，请修改配置文件并重启", LoggerLevel.ERROR)
            }
            try {
                config = ObjectMapper().readValue(file, JsonConfigMap::class.java)
            } catch (e: Exception) {
                logger("请检查配置文件配置是否正确：${e.message}", LoggerLevel.ERROR)
            }
        } catch (e: Exception) {
            logger("文件创建错误，可能是权限问题：${e.message}", LoggerLevel.ERROR)
        }
    }
}