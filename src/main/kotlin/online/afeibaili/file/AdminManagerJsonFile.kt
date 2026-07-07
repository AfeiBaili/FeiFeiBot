package online.afeibaili.file

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.NormalMember
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.buildMessageChain
import online.afeibaili.bot
import online.afeibaili.file.AdminManagerJsonFile.Companion.adminManager
import online.afeibaili.util.TimeParser.toDateTimeString
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.concurrent.schedule

/**
 * # 管理管理员Json配置文件
 *
 * @author AfeiBaili
 * @version 2026/7/7 11:58
 */

class AdminManagerJsonFile {
    companion object {
        lateinit var adminManager: AdminManager
        val om: ObjectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())
        val path = "${System.getProperty("user.dir")}/data/feifei"
        val bot get() = online.afeibaili.bot
        fun load() {
            val file = File(path, "admin-manager.json")
            if (!file.exists()) {
                file.parentFile.mkdirs()
                file.createNewFile()
                file.writeText(om.writerWithDefaultPrettyPrinter().writeValueAsString(AdminManager()))
            }

            adminManager = om.readValue(file, AdminManager::class.java)
            adminManager.groupMap.forEach { (_, group) ->
                group.adminTimer.forEach { it ->
                    if (it.getLocalDateTime().isBefore(LocalDateTime.now())) {
                        println("定时器时间已过，正在重新设置定时器")
                        it.endTimeMillis =
                            LocalDateTime.now().plusSeconds(10)
                                .toInstant(ZoneOffset.ofHours(8)).toEpochMilli()
                        it.start() //修改、启动、删除
                    } else it.start()
                }
            }
            store()

            Runtime.getRuntime().addShutdownHook(Thread {
                store()
            })
        }

        fun store() {
            synchronized(this) {
                om.writerWithDefaultPrettyPrinter()
                    .writeValue(File(path, "admin-manager.json"), adminManager)
            }
        }
    }
}

class AdminManager {
    val groupMap = mutableMapOf<Long, GroupAdmin>()

    fun addWhiteList(groupId: Long, adminId: Long) {
        if (groupMap[groupId] == null) groupMap[groupId] = GroupAdmin(groupId)
        groupMap[groupId]?.whitelist?.add(adminId)
    }

    fun deleteWhiteList(groupId: Long, adminId: Long) {
        if (groupMap[groupId] == null) groupMap[groupId] = GroupAdmin(groupId)
        groupMap[groupId]?.whitelist?.remove(adminId)
    }

    fun getList(groupId: Long): List<Long>? {
        val admin: GroupAdmin? = groupMap[groupId]
        if (admin == null) return null
        return admin.whitelist.toList()
    }

    fun createAdmin(groupId: Long, adminId: Long, time: LocalDateTime): Boolean {
        val groupAdmin: GroupAdmin? = groupMap[groupId]
        if (groupAdmin == null) groupMap[groupId] = GroupAdmin(groupId)
        if (groupAdmin != null && groupAdmin.whitelist.contains(adminId)) {
            groupAdmin.adminTimer.add(
                AdminTimer(
                    groupId, adminId, time.toInstant(ZoneOffset.ofHours(8)).toEpochMilli()
                ).also { it.start() }
            )
            return true
        }
        return false
    }
}

class GroupAdmin(
    val groupId: Long,
    val whitelist: MutableSet<Long> = mutableSetOf(),
    val adminTimer: MutableSet<AdminTimer> = mutableSetOf(),
)

data class AdminTimer(
    val groupId: Long,
    val adminId: Long,
    var endTimeMillis: Long,
) {
    @JsonIgnore
    fun getLocalDateTime(): LocalDateTime {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(endTimeMillis), ZoneOffset.ofHours(8))
    }

    fun start() {
        val localDateTime: LocalDateTime = getLocalDateTime()
        val timer = Timer()

        if (!localDateTime.isBefore(LocalDateTime.now())) {
            println("加载计时器：$this")
            timer.schedule(Date.from(Instant.ofEpochMilli(endTimeMillis))) {
                val group: Group? = bot.getGroup(groupId)
                if (group == null) return@schedule
                val member: NormalMember? = group.members[adminId]

                adminManager.groupMap[groupId]?.adminTimer?.removeAll {
                    it.groupId == groupId && it.adminId == adminId
                }

                AdminManagerJsonFile.store()
                CoroutineScope(Dispatchers.Default).launch {
                    if (member == null) {
                        group.sendMessage("找不到成员：$adminId")
                    } else {
                        runCatching {
                            member.modifyAdmin(false)
                        }.getOrElse {
                            group.sendMessage("权限不足")
                            return@launch
                        }
                        val messages: MessageChain = buildMessageChain {
                            +PlainText("管理员期限已到：")
                            +At(adminId)
                        }
                        group.sendMessage(messages)
                    }
                }
                println("管理员时间已到：${this@AdminTimer}")
            }
        } else println("计时器时间已过: ${localDateTime.toDateTimeString()}")
    }
}