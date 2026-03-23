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
                    it.write(
                        """
                            {
                              "master": 2411718391,
                              "groups": [
                                975709430
                              ],
                              "bot": {
                                "name": "机器人名字"
                              },
                              "module": {
                                "openMemoryName": true,
                                "enableMinecraft": true,
                                "translation": true,
                                "echoCave": true,
                                "passwordBreakGame": {
                                  "isOpen": true,
                                  "fontPath": "字体相对于mirai主目录的相对路径"
                                },
                                "uploadFile": {
                                  "isOpen": true,
                                  "maxFileSize": 5242880
                                  "pathList":[
                                    {
                                      "name": "路径别名",
                                      "path": "/路径"
                                    }
                                  ]
                                }
                              },
                              "setting": {
                                "currentBot": "deepseek",
                                "startMessage": "加载群后发送的提示消息",
                                "commandPrefix": "/",
                                "maxChatLength": 100,
                                "printNotFoundCommand": true
                              },
                              "chatgpt": {
                                "key": "chatgpt key",
                                "setting": "机器人设定"
                              },
                              "deepseek": {
                                "key": "deepseek key",
                                "setting": "机器人设定"
                              },
                              "kimi": {
                                "key": "kimi key",
                                "setting": "机器人设定"
                              },
                              "qwen": {
                                "key": "qwen key",
                                "setting": "机器人设定"
                              }
                              "youDao": {
                                "appKey": "有道云翻译应用Id",
                                "appSecret": "有道云翻译Key"
                              },
                              "kolors": {
                                "key": "kolors key"
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