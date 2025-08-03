package online.afeibaili.module.password.game.draw.data

import online.afeibaili.module.password.game.*
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage


/**
 * 句子类，里面包含多个词
 *
 *@author AfeiBaili
 *@version 2025/7/29 13:19
 */

class Sentence(val text: String, val disruptedText: String, val context: Context) : ArrayList<Word>() {
    val list = this
    val messingWordList = mutableListOf<Word>()

    val image = BufferedImage(width - padding, mainContainerHeight, BufferedImage.TYPE_INT_ARGB)

    val rowWordList = mutableListOf<Word>()

    init {
        var index = 0
        val words: List<String> = text.split("\\s".toRegex())
        val disruptedWords: List<String> = disruptedText.split("\\s".toRegex())
        words.zip(disruptedWords).forEach { pair ->
            if (pair.first.isNotEmpty()) {
                val word = Word(pair, let {
                    index.also { index += pair.first.length }
                }, context)
                this.add(word)
                if (pair.second.indexOf("*") != -1) messingWordList.add(word)
            }
        }

        draw(image.createGraphics())
    }


    fun clearCanvas(graphics2D: Graphics2D) {
        graphics2D.background = Color(0, 0, 0, 0)
        graphics2D.clearRect(0, 0, image.width, image.height)
    }

    fun draw(graphics2D: Graphics2D) = with(graphics2D) {
        rowWordList.clear()

        clearCanvas(graphics2D)
        val intervalHeight = 20
        val intervalWidth = charWidth
        var currentY = intervalHeight

        fun drawCenter() {
            val totalRowWidth = (rowWordList.sumOf { it.size } + rowWordList.size - 1) * charWidth
            val startX = ((width - padding * 2) - totalRowWidth) / 2

            var currentX = startX
            rowWordList.forEach { word ->
                drawImage(word.image, currentX, currentY, null)
                currentX += word.size * charWidth + intervalWidth
            }
            rowWordList.clear()
        }

        var tempWordLetterSize = 0
        list.forEach { word ->
            if (tempWordLetterSize + word.size >= charLineCount) {
                drawCenter()
                tempWordLetterSize = 0
                currentY += intervalHeight + charHeight
            }
            tempWordLetterSize += word.size + 1
            rowWordList.add(word)
        }
        drawCenter()

        dispose()
    }

    /** 待重绘的字母列表 */
    val lastUpdateLetterList = mutableListOf<Letter>()
    fun update(index: Int, char: Char, gameSetting: Game.Companion.GameSetting): Pair<Result, Result> {
        lastUpdateLetterList.forEach { it.draw(it.image.createGraphics(), it.charPair.second) }
        lastUpdateLetterList.clear()

        var isCorrect = false
        var beDeletedLetterCount = 0
        messingWordList.forEach { word ->
            val pair: Pair<Result, Int> = word.update(index, char)
            beDeletedLetterCount += pair.second

            if (pair.first == Result.LetterCorrect) {
                isCorrect = true
            }
            //全局逻辑
            if (pair.first == Result.LetterCorrect && gameSetting.globalLetterMode) {
                for (word in messingWordList) {
                    val beDeletedLetterList = mutableListOf<Letter>()
                    word.forEach {
                        if (it.charPair.first != char) return@forEach
                        if (it.charPair.second != '*') return@forEach
                        it.charPair = Pair(char, char)
                        it.update(char)
                        lastUpdateLetterList.add(it)
                        beDeletedLetterList.add(it)
                    }
                    word.messingLetterList.removeAll(beDeletedLetterList)
                    word.draw(word.image.createGraphics())
                }
                return@forEach
            }
        }
        draw(image.createGraphics())

        fun result() = if (isCorrect) Result.LetterCorrect else Result.LetterError

        if (beDeletedLetterCount == 0) {
            return Pair(Result.GameVictory, result())
        }
        return Pair(Result.GameContinue, result())
    }
}