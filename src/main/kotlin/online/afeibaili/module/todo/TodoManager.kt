package online.afeibaili.module.todo

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.MessageChainBuilder
import online.afeibaili.bot
import online.afeibaili.logger
import java.io.File
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.concurrent.schedule

object TodoManager {
    val todoTimer = Timer()
    val map = LinkedHashMap<String, Todo>()
    val dir = File(System.getProperty("user.dir") + "/data/feifei/todo")

    init {
        dir.mkdirs()
        dir.listFiles()?.forEach { file ->
            if (file.isDirectory) return@forEach
            val todo: Todo = runCatching {
                Json.decodeFromString<Todo>(file.reader().readText())
            }.getOrElse {
                logger("无法解析的Todo文件：${file.canonicalPath}")
                return@forEach
            }
            if (todo.dateTime == null) map.put(todo.uuid, todo)
            else if (todo.dateTime.isAfter(LocalDateTime.now())) {
                startTask(todo)
                map.put(todo.uuid, todo)
            } else deleteFile(todo.uuid)
        }
    }

    fun createTodo(message: String, event: MessageEvent) {
        val uuid: String = UUID.randomUUID().toString().replace("-", "")
        val todo = Todo(uuid, event.sender.id, message, null, event.subject.id)
        saveFile(todo)
        map.put(todo.uuid, todo)
    }

    fun startTask(todo: Todo) {
        if (todo.dateTime == null) return
        val date: Date = Date.from(todo.dateTime.toInstant(ZoneOffset.ofHours(8)))
        todoTimer.schedule(date) {
            val messages = MessageChainBuilder()
                .append("任务时间已到")
                .append(At(todo.at))
                .append("\n")
                .append("任务ID：${todo.uuid}")
                .append("\n")
                .append("任务内容：")
                .append(todo.message)
                .build()

            CoroutineScope(Dispatchers.Default).launch {
                bot.getGroup(todo.contact)?.sendMessage(messages)
                bot.getFriend(todo.contact)?.sendMessage(messages)
            }
            map.remove(todo.uuid)
        }
    }

    fun createTask(dateTime: LocalDateTime, message: String, event: MessageEvent): Todo {
        val uuid = UUID.randomUUID().toString().replace("-", "")
        val todo = Todo(uuid, event.sender.id, message, dateTime, event.subject.id)
        saveFile(todo)
        startTask(todo)
        map.put(todo.uuid, todo)
        return todo
    }

    fun cancelTimer() {
        todoTimer.cancel()
    }

    fun saveFile(todo: Todo) {
        File(dir, "${todo.uuid}.todo").printWriter().use { out ->
            out.println(Json.encodeToString(todo))
        }
    }

    fun deleteFile(uuid: String) {
        File(dir, "$uuid.todo").delete()
    }

    fun deleteTask(uuid: String): Todo? {
        deleteFile(uuid)
        return map.remove(uuid)
    }
}