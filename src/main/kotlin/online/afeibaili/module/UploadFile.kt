package online.afeibaili.module

import kotlinx.coroutines.flow.toList
import net.mamoe.mirai.contact.file.AbsoluteFile
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.FileMessage
import net.mamoe.mirai.message.data.QuoteReply
import net.mamoe.mirai.message.data.SingleMessage
import online.afeibaili.command.Command
import online.afeibaili.command.CommandCollection
import online.afeibaili.command.CommandRegistry
import online.afeibaili.command.ParamType
import online.afeibaili.config
import online.afeibaili.configObject
import online.afeibaili.file.json.UploadFile
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


/**
 * 上传文件模块
 *
 *@author AfeiBaili
 *@version 2026/3/23 12:20
 */

typealias NamePath = UploadFile.NamePath

object UploadFile {
    val client: HttpClient = HttpClient.newHttpClient()

    fun load() {
        val namePathList: MutableList<UploadFile.NamePath> = config.module.uploadFile.pathList
        val uploadFile: UploadFile = config.module.uploadFile
        CommandRegistry.registerWithChild(
            "上传",
            "upload-file",
            1,
            ParamType.NOTHING,
            { p, _ ->
                buildString {
                    appendLine("使用上传文件命令进行上传：")
                    appendLine("上传 路径别名 文件引用")
                    appendLine()
                    appendLine("！！！注意！！！")
                    appendLine("请检查群文件是否有名字相同、大小相同、内容不同的文件")
                    appendLine("如果执意上传的话，可能上传的结果和预期不同")
                    append("*更改文件名*可有效避免这种问题")
                }
            },
            *arrayOf(
                Command(
                    "文件", "upload", 1, ParamType.Multiple(listOf(ParamType.STRING, ParamType.REFERENCE))
                ) { p, e ->
                    fun getNames() = namePathList.joinToString("、") { it.name }
                    val name: String = runCatching { p[0] }.getOrElse { return@Command "请填入路径别名：${getNames()}" }
                    val namePath: UploadFile.NamePath? = namePathList.find { it.name == name }
                    namePath ?: return@Command "找不到可用路径别名：${getNames()}"

                    if (e !is GroupMessageEvent) return@Command "该功能只可在群聊中使用"

                    val message: SingleMessage? = e.message.find { it is QuoteReply }
                    message ?: return@Command "未引用文件"
                    message as QuoteReply
                    val fileMessage: SingleMessage? = message.source.originalMessage.find { it is FileMessage }
                    fileMessage ?: return@Command "引用的不是一个文件消息"
                    fileMessage as FileMessage
                    if (fileMessage.size > uploadFile.maxFileSize) return@Command "文件大小不可超过${uploadFile.maxFileSize}字节"

                    e.subject.files.root.refresh()
                    val absoluteFile: AbsoluteFile? = e.subject.files.root.files().toList()
                        .find { it.name == fileMessage.name && it.size == fileMessage.size }

                    absoluteFile ?: return@Command "在根目录下找不到群文件：${fileMessage.name}"

                    val file = File(namePath.path, fileMessage.name)
                    val url: String? = absoluteFile.getUrl()
                    url ?: return@Command "无法获取URL地址"

                    val bytes: ByteArray = runCatching {
                        client.send(
                            HttpRequest.newBuilder(URI.create(url)).GET().build(),
                            HttpResponse.BodyHandlers.ofInputStream()
                        ).body().readAllBytes()
                    }.getOrElse { return@Command "网络请求出错！" }

                    file.writeBytes(bytes)

                    "上传${fileMessage.name}文件至${name}成功"
                },

                Command(
                    "路径别名", "path-name", 1, ParamType.NOTHING, { p, e ->
                        buildString {
                            if (namePathList.isEmpty()) appendLine("当前无可用路径")
                            else appendLine("可用路径：")
                            namePathList.forEachIndexed { index, it ->
                                appendLine("[ ${index + 1} ]: ${it.name}")
                                appendLine("${it.path}")
                            }
                        }
                    }, CommandCollection.create(
                        Command(
                            "注册", "register", 3, ParamType.Multiple(listOf(ParamType.STRING, ParamType.STRING))
                        ) { p, e ->
                            if (p.size != 2) return@Command "注册路径 路径别名 路径（如果有空格请使用&符号替换空格）"
                            val (name, path) = p[0] to p[1].replace("&", " ")
                            val file = File(path)
                            if (!file.exists()) return@Command "文件夹不存在"
                            if (!file.isDirectory) return@Command "路径地址是一个文件"

                            namePathList.add(NamePath(name, file.canonicalPath))
                            configObject.store()
                            "添加路径：$name\n${file.canonicalPath}"
                        },
                        Command("删除", "delete-path", 3, ParamType.STRING) { p, e ->
                            val name = runCatching { p[0] }.getOrElse { return@Command "请输入路径别名参数" }
                            val findNamePath: UploadFile.NamePath? = namePathList.find { it.name == name }
                            findNamePath ?: return@Command "无可用的路径别名"
                            runCatching { namePathList.removeIf { it.name == name } }.getOrElse { return@Command "删除路径失败" }
                            configObject.store()
                            "删除路径成功：$name\n${findNamePath.path}"
                        },
                    )
                ),
                Command(
                    "限制大小", "max-size", 0, ParamType.NOTHING, { p, e ->
                        "当前文件限制大小为${config.module.uploadFile.maxFileSize}字节"
                    }, CommandCollection.create(
                        Command("更改", "change", 3, ParamType.LONG) { p, e ->
                            val size: Long =
                                runCatching { p[0].toLong() }.getOrElse { return@Command "请输入最大字节数（长整形）" }
                            val lastSize = config.module.uploadFile.maxFileSize
                            config.module.uploadFile.maxFileSize = size
                            configObject.store()
                            "已将最大文件大小${lastSize}更改为${size}字节"
                        })
                ),
                Command("打印群文件", "print") { p, e ->
                    e as GroupMessageEvent
                    e.subject.files.root.files().toList().joinToString("\n") { it.name }
                }
            )
        )
    }
}