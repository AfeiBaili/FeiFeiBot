package online.afeibaili.module.coffee

import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.Listener
import net.mamoe.mirai.event.events.GroupMessageEvent
import online.afeibaili.LoggerLevel
import online.afeibaili.command.Command
import online.afeibaili.command.Commands
import online.afeibaili.logger
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import javax.imageio.ImageIO

object Coffee {
    val data = System.getProperty("user.dir") + "/data/feifei/coffee/forms"
    val coffeeImage = System.getProperty("user.dir") + "/data/feifei/coffee/coffee.png"
    val groupMap = mutableMapOf<Long, Game>()
    val random = Random()

    fun load() {
        if (!File(data).exists()) return logger("咖啡机数据不存在", LoggerLevel.ERROR)
        val dataMap: Map<String, File> = File(data).listFiles()!!.toList().associateBy { it.name }

        Commands.register("异常咖啡机", Command({ param, event ->
            if (param.size > 1) {
                val list: List<String> = listOf(
                    "你为什么不试试%s呢？",
                    "你试试%s怎么样？",
                    "你要不要考虑一下%s？",
                    "我觉得%s可能不错？",
                    "或许可以试试%s？",
                    "我推荐你试试%s",
                    "强烈安利%s给你",
                    "%s这个怎么样",
                )
                when (param[1]) {
                    "提示" -> return@Command String.format(
                        list[random.nextInt(list.size)], dataMap.keys.toList()[random.nextInt(dataMap.keys.size)]
                    )
                }
                return@Command "异常咖啡机 [提示]"
            }

            val listener: Listener<GroupMessageEvent> =
                GlobalEventChannel.filter {
                    it is GroupMessageEvent && it.group.id == event.subject.id
                }.subscribeAlways<GroupMessageEvent> { e ->
                    val message: String = e.message.contentToString()
                    val contact = event.subject
                    val game: Game = groupMap[e.subject.id]!!
                    fun getImageFile(gender: String): List<File> {
                        val list = mutableListOf<File>()
                        val dir = File("$data/${game.currentWord!!.component2()}")
                        dir.listFiles()!!.forEach { file ->
                            if (file.name.contains(gender)) {
                                list.add(file)
                            }
                        }
                        return list
                    }
                    when (message) {
                        "结束" -> {
                            if (game.playerIsExists(e.sender.id)) {
                                event.subject.sendMessage("没想到这么快你们就结束了")
                                game.stop()
                                groupMap.remove(e.subject.id)
                                return@subscribeAlways
                            } else {
                                event.subject.sendMessage("我可不能擅自同意你的请求")
                                return@subscribeAlways
                            }
                        }


                        "她", "she", "She" -> {
                            if (game.currentWord == null) return@subscribeAlways
                            val list: List<File> = getImageFile("girl")
                            contact.sendImage(list[random.nextInt(list.size)])
                            game.currentWord = null
                            return@subscribeAlways
                        }

                        "我", "i", "I" -> {
                            if (game.currentWord == null) return@subscribeAlways
                            val list: List<File> = getImageFile("boy")
                            contact.sendImage(list[random.nextInt(list.size)])
                            game.currentWord = null
                            return@subscribeAlways
                        }
                    }
                    if (!"\\w+".toRegex().matches(message)) return@subscribeAlways
                    val dir: File? = dataMap[message]
                    if (dir == null) {
                        val messageList: List<String> = listOf(
                            "你到底在输入什么玩意？",
                            "你这是在乱打吗？",
                            "你就不能好好输入？",
                            "你知道该输入什么吗？",
                            "能不能认真点输入？",
                            "输入前能不能动动脑子？",
                        )
                        contact.sendMessage(messageList[random.nextInt(messageList.size)])
                        return@subscribeAlways
                    }

                    contact.sendMessage("...已制取咖啡")
                    val bufferedImage: BufferedImage = ImageIO.read(File(coffeeImage))
                    val graphics2D: Graphics2D = bufferedImage.createGraphics()
                    val bPng = File(dir.path, "b.png")
                    val tPng = File(dir.path, "t.png")
                    if (bPng.exists()) graphics2D.drawImage(ImageIO.read(bPng), 0, 0, null)
                    if (tPng.exists()) graphics2D.drawImage(ImageIO.read(tPng), 0, 0, null)
                    val arrayOutputStream = ByteArrayOutputStream()
                    ImageIO.write(bufferedImage, "PNG", arrayOutputStream)
                    contact.sendImage(ByteArrayInputStream(arrayOutputStream.toByteArray()))
                    game.currentWord = Pair(true, message)
                    contact.sendMessage("请问你要给谁喝？她or我")
                }

            val game: Game? = groupMap[event.subject.id]

            if (game != null && game.playerIsExists(event.sender.id)) return@Command "当前正在游戏中，请输入结束以结束游戏"
            if (game != null && !game.playerIsExists(event.sender.id)) {
                game.addPlayer(event.sender.id)
                return@Command "已加入游戏"
            }

            groupMap[event.subject.id] = Game(listener).apply {
                addPlayer(event.sender.id)
            }

            "已创建游戏"
        }, 0))
    }

    class Game(val listener: Listener<GroupMessageEvent>) {
        val players = mutableSetOf<Long>()

        //第一个：是否在回答中；第二个：关键字
        var currentWord: Pair<Boolean, String>? = null
        fun stop() {
            listener.cancel()
            players.clear()
        }

        fun playerIsExists(id: Long): Boolean = players.contains(id)

        fun addPlayer(id: Long) {
            players.add(id)
        }
    }
}