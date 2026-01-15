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

    val formatter1: DateTimeFormatter = DateTimeFormatter.ofPattern("H:m")
    val formatter2: DateTimeFormatter = DateTimeFormatter.ofPattern("H:m:s")
    val formatter3: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy年M月d日-H:m")
    val formatter4: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy年M月d日-H:m:s")

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
            else if (todo.dateTime.isAfter(LocalDateTime.now())) map.put(todo.uuid, todo)
            else deleteFile(todo.uuid)
        }
    }

    fun createTodo(message: String, event: MessageEvent) {
        val uuid: String = UUID.randomUUID().toString().replace("-", "")
        val todo = Todo(uuid, event.sender.id, message, null)
        saveFile(todo)
        map.put(todo.uuid, todo)
    }

    fun createTask(dateTime: LocalDateTime, message: String, event: MessageEvent): Todo {
        val date: Date = Date.from(dateTime.toInstant(ZoneOffset.ofHours(8)))
        val uuid = UUID.randomUUID().toString().replace("-", "")
        val todo = Todo(uuid, event.sender.id, message, dateTime)
        saveFile(todo)

        todoTimer.schedule(date) {
            val messages = MessageChainBuilder()
                .append("任务时间已到")
                .append(At(event.sender.id))
                .append("\n")
                .append("任务ID：${todo.uuid}")
                .append("\n")
                .append("任务内容：")
                .append(message)
                .build()

            CoroutineScope(Dispatchers.Default).launch {
                event.subject.sendMessage(messages)
            }
            map.remove(uuid)
        }
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