package online.afeibaili.module.password.game

import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.event.Event
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.Listener
import net.mamoe.mirai.event.events.GroupMessageEvent
import online.afeibaili.bot
import online.afeibaili.module.password.game.ManageGame.random
import online.afeibaili.module.password.game.draw.BottomBar
import online.afeibaili.module.password.game.draw.MainContainer
import online.afeibaili.module.password.game.draw.TopBar
import online.afeibaili.module.password.game.draw.data.Context
import online.afeibaili.module.password.game.draw.data.Result
import online.afeibaili.module.password.game.draw.data.Sentence
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO


/**
 * 游戏类，一场游戏对应一个Game类
 *
 *@author AfeiBaili
 *@version 2025/7/27 18:52
 */

class Game(val groupId: Long, val sentenceText: String) {
    companion object {
        class GameSetting(var globalLetterMode: Boolean = false)
    }

    val listener: Listener<GroupMessageEvent> =
        GlobalEventChannel.filter { filterGroupId(it) }.subscribeAlways<GroupMessageEvent> { event ->
            parsMassage(event.message.contentToString(), event)
        }

    val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    lateinit var graphics: Graphics2D

    /** 所有的字母 */
    val letterSet = LinkedHashSet<Char>()

    /** 随机值 */
    val letterRandomNumber = mutableSetOf<Int>()

    /** 字母作为Key的随机值 */
    val letterRandomMap: Map<Char, Int>

    val context: Context
    val sentence: Sentence
    val mainContainer: MainContainer
    val topBar: TopBar
    val bottomBar: BottomBar

    val gameSetting = GameSetting()
    val allNumber = 5

    init {
        //初始化上下文
        sentenceText.uppercase().forEach { letter ->
            if (letter in 'A'..'Z') letterSet.add(letter)
        }
        while (letterRandomNumber.size != letterSet.size) letterRandomNumber.add(random.nextInt(1, letterSet.size * 2))
        letterRandomMap = letterSet.zip(letterRandomNumber).toMap()

        //处理打乱逻辑
        context = Context(letterRandomMap)
        sentence = Sentence(
            sentenceText.uppercase(),
            disruption(
                sentenceText,
                random.nextInt(1, 10)
            ), context
        )

        topBar = TopBar(width, topHeight, allNumber)

        mainContainer = MainContainer(
            width, mainContainerHeight, sentence
        )

        bottomBar = BottomBar(width, bottomHeight, letterSet)

        //游戏创建时发送
        drawImage()
        sendImage()
    }

    fun parsMassage(message: String, event: GroupMessageEvent) {
        fun parsCommand(message: String) {
            if (!listOf("!", "！").contains(message[0].toString())) return
            val command = message.substring(1)
            when (command) {
                "开启全局模式" -> {
                    gameSetting.globalLetterMode = true
                    ManageGame.sendMassageScope.launch {
                        event.subject.sendMessage("已开启全局模式")
                    }
                }

                "关闭全局模式" -> {
                    gameSetting.globalLetterMode = false
                    ManageGame.sendMassageScope.launch {
                        event.subject.sendMessage("已关闭全局模式")
                    }
                }
            }
        }

        parsCommand(message)
        val indexAndChar: List<String> = message.split("\\s".toRegex()).filter { it.isNotEmpty() }
        if (indexAndChar.size != 2) return
        if (indexAndChar[1].length != 1) return
        runCatching {
            update(indexAndChar[0].toInt(), indexAndChar[1].uppercase()[0])
        }
    }

    var availableNumber = 5
    var gameOverJob: Job? = null
    fun update(index: Int, char: Char) {
        val result: Pair<Result, Result> = mainContainer.update(index, char, gameSetting)

        /** 游戏胜利时 */
        if (result.first == Result.GameVictory) {
            gameOverJob = ManageGame.sendMassageScope.launch { bot.getGroup(groupId)?.sendMessage("Game Wins!") }
            drawImage()
            sendImage()
            over()
        }

        if (result.second == Result.LetterError) {
            topBar.update(--availableNumber)
            /** 游戏结束时 */
            if (availableNumber == 0) {
                gameOverJob = ManageGame.sendMassageScope.launch { bot.getGroup(groupId)?.sendMessage("Game Over!") }

                over()
            }
        }
        if (result.first == Result.GameContinue) {
            drawImage()
            sendImage()
        }
    }

    /** 打乱 */
    fun disruption(text: String, level: Int): String {
        val charArray: CharArray = text.uppercase().toCharArray()
        random.nextInt()
        for (i in 0 until charArray.size - level) {
            val nextInt: Int = random.nextInt(0, charArray.size)
            val ch: Char = charArray[nextInt]
            if (ch in 'A'..'Z') charArray[nextInt] = '*'
        }
        return String(charArray)
    }

    fun clearCanvas(graphics2D: Graphics2D) = with(graphics2D) {
        background = Color(0, 0, 0)
        clearRect(0, 0, width, height)
    }

    fun drawImage() {
        graphics = image.createGraphics().apply {
            clearCanvas(this)
            color = theme.capital
            fillRect(0, 0, width, height)
            drawImage(topBar.image, 0, 0, null)
            //绘制
            drawImage(
                mainContainer.image, 0, topHeight, null
            )
            drawImage(bottomBar.image, 0, topHeight + mainContainerHeight, null)
        }
        graphics.dispose()
    }

    fun sendImage() {
        val outputStream = ByteArrayOutputStream()
        ImageIO.write(image, "jpg", outputStream)
        ManageGame.sendMassageScope.launch {
            bot.getGroup(groupId)?.sendImage(ByteArrayInputStream(outputStream.toByteArray()))
        }
    }

    //游戏结束时调用
    fun over() {
        ManageGame.remove(groupId)
        listener.cancel()
    }

    fun filterGroupId(event: Event): Boolean {
        if (event !is GroupMessageEvent) return false
        if (event.group.id != groupId) return false
        return true
    }
}