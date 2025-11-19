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
import java.io.InputStream
import java.util.*
import javax.imageio.ImageIO

object Coffee {
    val data = System.getProperty("user.dir") + "/data/feifei/coffee/forms"
    val coffeePng = System.getProperty("user.dir") + "/data/feifei/coffee/coffee/coffee.png"
    val backgroundWellPng = System.getProperty("user.dir") + "/data/feifei/coffee/background/well.png"
    val backgroundCityPng = System.getProperty("user.dir") + "/data/feifei/coffee/background/morning_bg.png"
    val backgroundCoffeePng =
        System.getProperty("user.dir") + "/data/feifei/coffee/background/mainmenu_coffeemachine.png"

    const val BACKGROUND_WIDTH: Int = 1580
    const val BACKGROUND_HEIGHT: Int = 720
    var gameBackground = BufferedImage(BACKGROUND_WIDTH, BACKGROUND_HEIGHT, BufferedImage.TYPE_INT_ARGB).apply {
        graphics.drawImage(ImageIO.read(File(backgroundWellPng)), 196, 0, null)
        graphics.drawImage(ImageIO.read(File(backgroundCityPng)), 0, 0, null)
        graphics.drawImage(ImageIO.read(File(backgroundCoffeePng)), 400, 169, null)
        graphics.dispose()
    }
    val drinkFileList = File(System.getProperty("user.dir") + "/data/feifei/coffee/coffee/drink").listFiles()!!.toList()
    val groupMap = mutableMapOf<Long, Game>()
    val random = Random()
    val tipList: List<String> = listOf(
        "你为什么不试试%s呢？",
        "你试试%s怎么样？",
        "你要不要考虑一下%s？",
        "我觉得%s可能不错？",
        "或许可以试试%s？",
        "我推荐你试试%s",
        "强烈安利%s给你",
        "%s这个怎么样",
    )
    lateinit var dataMap: Map<String, File>

    fun getTip() =
        String.format(tipList[random.nextInt(tipList.size)], dataMap.keys.toList()[random.nextInt(dataMap.keys.size)])

    fun load() {
        if (!File(data).exists()) return logger("咖啡机数据不存在", LoggerLevel.ERROR)
        dataMap = File(data).listFiles()!!.toList().associateBy { it.name }

        Commands.register("咖啡机", Command({ param, event ->
            if (param.size > 1) {
                when (param[1]) {
                    "提示" -> return@Command getTip()

                    "游戏列表" -> return@Command dataMap.toString()
                }
                return@Command "咖啡机 [提示]"
            }

            val game: Game? = groupMap[event.subject.id]

            if (game != null && game.playerIsExists(event.sender.id)) return@Command "当前正在游戏中，请输入结束以结束游戏"
            if (game != null && !game.playerIsExists(event.sender.id)) {
                game.addPlayer(event.sender.id)
                return@Command "已加入游戏"
            }

            groupMap[event.subject.id] = Game(
                GlobalEventChannel.filter {
                    it is GroupMessageEvent && it.group.id == event.subject.id
                }.subscribeAlways<GroupMessageEvent> { e ->
                    val message: String = e.message.contentToString().lowercase()
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
                            if (list.isEmpty()) {
                                event.subject.sendMessage("好像没有什么变化？")
                                return@subscribeAlways
                            }
                            contact.sendImage(game.drawGame(list[random.nextInt(list.size)], Gender.Girl))
                            game.currentWord = null
                            return@subscribeAlways
                        }

                        "我", "i", "I" -> {
                            if (game.currentWord == null) return@subscribeAlways
                            val list: List<File> = getImageFile("boy")
                            if (list.isEmpty()) {
                                event.subject.sendMessage("好像没有什么变化？")
                                return@subscribeAlways
                            }
                            contact.sendImage(game.drawGame(list[random.nextInt(list.size)], Gender.Boy))
                            game.currentWord = null
                            return@subscribeAlways
                        }

                        "提示" -> {
                            event.subject.sendMessage(getTip())
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
                        )
                        contact.sendMessage(messageList[random.nextInt(messageList.size)])
                        return@subscribeAlways
                    }

                    contact.sendMessage("...已制取咖啡")
                    val bufferedImage: BufferedImage = ImageIO.read(File(coffeePng))
                    val graphics2D: Graphics2D = bufferedImage.createGraphics()
                    var bPng = File(dir.path, "b.png")
                    if (!bPng.exists()) bPng = drinkFileList[random.nextInt(drinkFileList.size)]
                    val tPng = File(dir.path, "t.png")
                    if (bPng.exists()) graphics2D.drawImage(ImageIO.read(bPng), 0, 0, null)
                    if (tPng.exists()) graphics2D.drawImage(ImageIO.read(tPng), 0, 0, null)
                    graphics2D.dispose()
                    val arrayOutputStream = ByteArrayOutputStream()
                    ImageIO.write(bufferedImage, "PNG", arrayOutputStream)
                    contact.sendImage(ByteArrayInputStream(arrayOutputStream.toByteArray()))
                    game.currentWord = Pair(true, message)
                    contact.sendMessage("请问你要给谁喝？她or我")
                }).apply {
                addPlayer(event.sender.id)
            }

            "已创建游戏"
        }, 0))
    }

    class Game(val listener: Listener<GroupMessageEvent>) {
        val players = mutableSetOf<Long>()

        //第一个：是否在回答中；第二个：关键字
        var currentWord: Pair<Boolean, String>? = null
        var boyRoleImage: BufferedImage? = null
        var girlRoleImage: BufferedImage? = null

        fun stop() {
            listener.cancel()
            players.clear()
        }

        fun drawGame(file: File, gender: Gender): InputStream {
            fun imageToStream(image: BufferedImage): InputStream {
                val arrayOutputStream = ByteArrayOutputStream()
                ImageIO.write(image, "PNG", arrayOutputStream)
                return ByteArrayInputStream(arrayOutputStream.toByteArray())
            }

            when (gender) {
                Gender.Boy -> {
                    val bufferedImage: BufferedImage =
                        BufferedImage(BACKGROUND_WIDTH, BACKGROUND_HEIGHT, BufferedImage.TYPE_INT_ARGB).apply {
                            graphics.drawImage(gameBackground, 0, 0, null)
                            if (girlRoleImage != null) graphics.drawImage(girlRoleImage, -100, 0, null)
                            graphics.drawImage(ImageIO.read(file), 500, 0, null)
                            boyRoleImage = ImageIO.read(file)
                            graphics.dispose()
                        }
                    return imageToStream(bufferedImage)
                }

                Gender.Girl -> {
                    val bufferedImage: BufferedImage =
                        BufferedImage(BACKGROUND_WIDTH, BACKGROUND_HEIGHT, BufferedImage.TYPE_INT_ARGB).apply {
                            graphics.drawImage(gameBackground, 0, 0, null)
                            if (boyRoleImage != null) graphics.drawImage(boyRoleImage, 500, 0, null)
                            graphics.drawImage(ImageIO.read(file), -100, 0, null)
                            girlRoleImage = ImageIO.read(file)
                            graphics.dispose()
                        }
                    return imageToStream(bufferedImage)
                }
            }
        }

        fun playerIsExists(id: Long): Boolean = players.contains(id)

        fun addPlayer(id: Long) {
            players.add(id)
        }
    }

    enum class Gender {
        Boy,
        Girl,
    }
}