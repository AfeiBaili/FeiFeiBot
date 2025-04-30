package online.afeibaili.file

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import online.afeibaili.LoggerLevel
import online.afeibaili.file.json.JsonConfigMap
import online.afeibaili.logger
import java.io.File
import java.io.FileWriter

class ConfigFile(val path: String) {

    lateinit var config: JsonConfigMap

    fun store() {
        val objectMapper: ObjectMapper = ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
        FileWriter(path).use {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(it, config)
        }
        config = ObjectMapper().readValue(File(path), JsonConfigMap::class.java)
    }

    constructor() : this(System.getProperty("user.dir") + "/config/feifei/config.json") {
        try {
            var file = File(path)
            file.parentFile.mkdirs()
            if (!file.exists()) {
                FileWriter(file).use {
                    it.write(
                        """
                            {
                              "master": 2411718391,
                              "groups": [
                                975709430
                              ],
                              "bot": {
                                "qq": 2664306741,
                                "name": "机器人名字"
                              },
                              "module": {
                                "openMemoryName": true,
                                "mcModSearch": true,
                                "translation": true
                              },
                              "setting": {
                                "atByTargetBot": "<chatgpt | deepseek>",
                                "immersiveByTargetBot": "<chatgpt | deepseek>",
                                "startMessage": "加载群后发送的提示消息",
                                "commandPrefix": "/"
                              },
                              "chatgpt": {
                                "name": "chatgpt 称呼",
                                "key": "chatgpt key",
                                "setting": "机器人设定"
                              },
                              "deepseek": {
                                "name": "deepseek 称呼",
                                "key": "deepseek key",
                                "setting": "机器人设定"
                              },
                              "kimi": {
                                "key": "kimi key",
                                "setting": "机器人设定"
                              },
                              "youDao": {
                                "key": "有道云翻译Key",
                                "appId": "有道云翻译应用Id"
                              }
                            }
                    """.trimIndent()
                    )
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